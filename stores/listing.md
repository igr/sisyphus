# Store listing

What the App Store and Google Play share. Copy these into App Store Connect and Play Console; the character counts are within the limits of both stores. What only one store has is in its own folder.

| Path | What it holds |
|---|---|
| `listing.md` | The shared texts: the name, the links, the description and What's New |
| `privacy.md` | The privacy policy, which the website shows at https://igr.github.io/sisyphus/privacy.html |
| `screenshots/` | The screenshots, taken on the iPhone simulator |
| `appstore/` | The fields of App Store Connect |
| `playstore/` | The fields of Play Console, its graphics, its screenshots, and `price.py` for the prices of the worlds |

## App

| Field | Value |
|---|---|
| Name (23/30) | Sisyphus: Push the Rock |
| Website and support | https://igr.github.io/sisyphus/ |
| Privacy policy | https://igr.github.io/sisyphus/privacy.html. Its text is `privacy.md`: publish it there when it changes. |
| Price | Free. Corinth is free; the other four worlds are in-app purchases, $1.99 each (`Config.WORLD_PRICE`). |

If the name is taken, try "Sisyphus: Endless Hill" or "Sisyphus Knob". The name under the icon on the phone stays "Sisyphus".

## Description (1033/4000)

Every day, Sisyphus pushes his rock up the hill. Today, you turn the knob.

Sisyphus is a small, calm pixel-art game. Under a window to a Greek hillside sits a bronze knob. Turn it, and Sisyphus leans into his boulder and climbs. Stop, and he holds on and catches his breath.

- Time moves only when you push. The sun rises over the sea, crosses the sky and sets behind the hill. Then come the moon and the stars.
- Every day has its own weather: clear skies, drifting clouds, windy days with gulls, and rain.
- Wise words appear as you climb: more than 600 lines from Camus, Heraclitus, Seneca, Marcus Aurelius, Homer, Lao Tzu and more, and many from Sisyphus, the Rock and the Hill themselves.
- Olive trees, umbrella pines, cypresses, almond trees in bloom and broken columns pass by on the way up.
- Every step of the knob clicks, with a light tap of haptics. Sound can be turned off.
- Your day, the sun and your place on the hill are saved. Come back anytime.

No ads. No accounts. No tracking. Just the hill, the rock and you.

## What's New in 2.0 (474/500)

Both stores take this text: the What's New of the App Store, and the release notes of Google Play, which allow 500 characters.

- Four new worlds to climb: Mount Etna, The Underworld, Cairo and Mount Fuji.
- The Agora: spend your obols on rocks, helpers, gods, blessings and sights.
- Buy everything, and the top of the hill comes in sight. Then the climb begins again.
- Gods come in visions and ask you questions.
- Stones by the path tell old tales, and jokers tell riddles.
- Labors to do, and cards to collect.
- New sounds: the rock rolls as you push, and the settings turn each sound on and off.

## Screenshots

`screenshots/` holds the screenshots of both stores, taken on the iPhone simulator, five of each size: `1-morning.png` to `5-rain.png`.

- `6.9-inch/`: 1320 × 2868, from the iPhone 16 Pro Max. App Store Connect asks for this size, and can scale it for the smaller iPhones.
- `6.3-inch/`: 1206 × 2622, from the iPhone 16 Pro, for the 6.3" and 6.1" displays.

Google Play takes no picture that is more than twice as tall as it is wide. Its screenshots in `playstore/screenshots/` are the 6.9-inch ones, cut to 1320 × 2640: see `playstore/listing.md`.
