# Google Play

The fields that only Play Console has. The name, the links, the full description and the release notes (What's New) are in `../listing.md`. Character counts are within Google's limits.

## App details

| Field | Value |
|---|---|
| Package name | dev.oblac.sisyphus |
| Default language | English (United States) |
| App or game | Game |
| Free or paid | Free |
| Category | Casual |
| Contact email | Required and shown on the store page. Choose the address you want to show. |

## Short description (77/80)

Turn the bronze knob and push the boulder up an endless hill. Calm pixel art.

## App content

| Question | Answer |
|---|---|
| App access | All functionality is available without special access. |
| Ads | No, the app has no ads. |
| Content rating | Category: Game. Answer "Yes" to fantasy violence (the myths of the tales, told in text, with no blood shown), to mild fear (Dracula, Cthulhu, Death), to references to alcohol (wine in some jokes and tales), and to digital purchases (the worlds). Answer "No" to every other question. Write here the ratings that the summary page gives. |
| Target audience | 13 and older. Younger age groups add the Families policy. |
| Data safety | The app collects one data type, Financial info: Purchase history. It is collected and not shared, not processed ephemerally, and the collection is required. The purposes are App functionality and Analytics: RevenueCat counts the purchases for our sales reports. It is encrypted in transit. Users can ask for its deletion: they email their support ID to contact@katemariongames.com. There are no accounts. |
| Advertising ID | No. |
| Government, financial, health, news apps | No. |

## Graphics

- App icon (512 × 512): `icon-512.png`
- Feature graphic (1024 × 500): `feature-graphic.png`
- Phone screenshots (1320 × 2640): `screenshots/1-morning.png` to `5-diogenes.png`.

Google Play takes no picture that is more than twice as tall as it is wide, so its screenshots are the 6.9-inch iPhone screenshots of `../screenshots/` with 114 rows cut at the top and at the bottom. After new iPhone screenshots, cut them again, from this folder:

```bash
for f in ../screenshots/6.9-inch/*.png; do sips --cropToHeightWidth 2640 1320 "$f" --out "screenshots/$(basename "$f")"; done
```

## In-app products

The four worlds are one-time products, $1.99 each: `world_etna`, `world_underworld2`, `world_cairo` and `world_fuji`, with the purchase options `world-etna`, `world-underworld`, `world-cairo` and `world-fuji`. The Underworld is `world_underworld2`, because Google never gives back a product id. A new product is made by hand in Play Console. `just play-price` shows the price of each world in every country, and `just play-price --apply` sets them from `Config.WORLD_PRICE` (`price.py`).

## Build

- `just release` uploads the current commit as a production draft, without the dev screen: roll it out in Play Console. `just beta` uploads it to internal testing.
- The version code is the number of commits, so every upload needs its own commit.
- The bundle is signed with the upload key `~/.android/sisyphus-upload.jks`. Its passwords are in `keystore.properties`, which is not in git. Back up both files. Google Play signs the app with its own key; if the upload key is lost, Google support can reset it.
