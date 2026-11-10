"""Stops an upload to Apple when the app in the archive holds the dev screen or the key of RevenueCat's Test Store.

Apple rejects an app with hidden features (guideline 5.6), so App Store and TestFlight builds leave both out.
Kotlin/Native keeps the texts of the code in UTF-16, and the names that Swift can call in UTF-8.
Usage: python3 -I stores/appstore/check.py build/Sisyphus.xcarchive
"""
import re
import sys
from pathlib import Path

BILLING = Path(__file__).parents[2] / "composeApp/src/mobileMain/kotlin/dev/oblac/sisyphus/RevenueCatBilling.kt"
DEV_SCREEN = ["Open all", "Worlds and jokers", "Voices and labors", "openWorlds", "devOpen", "nearTheTop", "initWithTestStore"]

app = Path(sys.argv[1]) / "Products/Applications/Sisyphus.app/Sisyphus"
data = app.read_bytes()
key = re.search(r'TEST_STORE_KEY = "([^"]+)"', BILLING.read_text()).group(1)
marks = [("the key of the Test Store", key)] + [(f'"{text}"', text) for text in DEV_SCREEN]
found = [name for name, text in marks if text.encode() in data or text.encode("utf-16-le") in data]
if found:
    sys.exit(f"The app holds the dev screen or the Test Store, so it cannot go to Apple: {', '.join(found)}.")
print("The app holds no dev screen and no Test Store.")
