#!/usr/bin/env python3
"""
Sets the price of every world on Google Play from Config.WORLD_PRICE, in every country, as the convert of Play Console does.

It only reads and tells what it would change, unless --apply is given. It signs in with the service account of the
Gradle Play Publisher, ~/.android/sisyphus-play.json, and uses the one-time products of the Play Developer API.
Run: python3 playstore/price.py [--apply]
"""
import base64
import json
import os
import re
import subprocess
import sys
import time
import urllib.error
import urllib.parse
import urllib.request

PACKAGE = "dev.oblac.sisyphus"
KEY = os.path.expanduser("~/.android/sisyphus-play.json")
CONFIG = os.path.join(os.path.dirname(os.path.abspath(__file__)), "..", "composeApp/src/commonMain/kotlin/dev/oblac/sisyphus/game/Config.kt")
API = f"https://androidpublisher.googleapis.com/androidpublisher/v3/applications/{PACKAGE}"


def b64(data: bytes) -> str:
    return base64.urlsafe_b64encode(data).rstrip(b"=").decode()


def sign_in() -> str:
    """An access token for the Play Developer API. The private key goes to openssl through a pipe, never to a file."""
    key = json.load(open(KEY))
    now = int(time.time())
    head = b64(json.dumps({"alg": "RS256", "typ": "JWT"}).encode())
    claims = b64(json.dumps({
        "iss": key["client_email"], "scope": "https://www.googleapis.com/auth/androidpublisher",
        "aud": key["token_uri"], "iat": now, "exp": now + 600,
    }).encode())
    r, w = os.pipe()
    os.write(w, key["private_key"].encode())
    os.close(w)
    signed = subprocess.run(["openssl", "dgst", "-sha256", "-sign", f"/dev/fd/{r}"], input=f"{head}.{claims}".encode(),
                            capture_output=True, pass_fds=(r,))
    os.close(r)
    if signed.returncode != 0:
        sys.exit("openssl could not sign in")
    body = urllib.parse.urlencode({
        "grant_type": "urn:ietf:params:oauth:grant-type:jwt-bearer",
        "assertion": f"{head}.{claims}.{b64(signed.stdout)}",
    }).encode()
    return json.load(urllib.request.urlopen(key["token_uri"], body))["access_token"]


def call(token, method, path, body=None, params=None):
    url = API + path + ("?" + urllib.parse.urlencode(params) if params else "")
    data = json.dumps(body).encode() if body is not None else None
    request = urllib.request.Request(url, data=data, method=method,
                                     headers={"Authorization": f"Bearer {token}", "Content-Type": "application/json"})
    try:
        return json.load(urllib.request.urlopen(request))
    except urllib.error.HTTPError as e:
        sys.exit(f"{method} {path}: {e.code} {e.read().decode()[:1000]}")


def world_price() -> float:
    return float(re.search(r"const val WORLD_PRICE = ([0-9.]+)", open(CONFIG).read()).group(1))


def money(amount: float, currency: str = "USD") -> dict:
    units = int(amount)
    return {"currencyCode": currency, "units": str(units), "nanos": round((amount - units) * 1e9)}


def shown(m: dict) -> str:
    return f"{int(m.get('units', 0)) + m.get('nanos', 0) / 1e9:.2f} {m['currencyCode']}" if m else "-"


def worlds(token) -> list:
    products, page = [], None
    while True:
        answer = call(token, "GET", "/oneTimeProducts", params={"pageToken": page} if page else None)
        products += answer.get("oneTimeProducts", [])
        page = answer.get("nextPageToken")
        if not page:
            return [p for p in products if p["productId"].startswith("world_")]


def main():
    apply = "--apply" in sys.argv[1:]
    price = world_price()
    token = sign_in()
    converted = call(token, "POST", "/pricing:convertRegionPrices", {"price": money(price)})
    regions = converted.get("convertedRegionPrices", {})
    others = converted.get("convertedOtherRegionsPrice", {})
    version = converted.get("regionVersion", {}).get("version")
    print(f"Config.WORLD_PRICE: {price:.2f} USD, converted for {len(regions)} regions (regions version {version})")
    print("  " + ", ".join(f"{code} {shown(regions[code]['price'])}" for code in ("US", "GB", "DE", "RS", "JP", "IN") if code in regions))
    print(f"  new regions: {shown(others.get('usdPrice', {}))}, {shown(others.get('eurPrice', {}))}")

    updates = []
    for product in worlds(token):
        changes = 0
        for option in product.get("purchaseOptions", []):
            configs = option.get("regionalPricingAndAvailabilityConfigs", [])
            us = next((c for c in configs if c.get("regionCode") == "US"), {})
            print(f"{product['productId']}: option {option.get('purchaseOptionId')} ({option.get('state')}), "
                  f"{len(configs)} regions, US {shown(us.get('price', {}))}")
            for c in configs:
                new = regions.get(c.get("regionCode"), {}).get("price")
                if new and new != c.get("price"):
                    changes += 1
                    if apply:
                        c["price"] = new
            if apply and others:
                option.setdefault("newRegionsConfig", {}).update({"usdPrice": others["usdPrice"], "eurPrice": others["eurPrice"]})
        print(f"  {'changes' if apply else '--apply would change'} {changes} region prices")
        updates.append({"oneTimeProduct": product, "updateMask": "purchaseOptions", "regionsVersion": {"version": version}})
    if apply and updates:
        answer = call(token, "POST", "/oneTimeProducts:batchUpdate", {"requests": updates})
        print(f"Play updated {len(answer.get('oneTimeProducts', []))} worlds")


if __name__ == "__main__":
    main()
