# Store listing

What the App Store and Google Play share. Copy these into App Store Connect and Play Console; the character counts are within the limits of both stores. What only one store has is in its own folder.

| Path | What it holds |
|---|---|
| `listing.md` | The shared texts: the name, the links, the description and What's New |
| `privacy.md` | The privacy policy, which the website shows at https://katemariongames.com/sisyphus/privacy-policy/ |
| `screenshots/` | The screenshots, taken on the iPhone simulator |
| `appstore/` | The fields of App Store Connect |
| `playstore/` | The fields of Play Console, its graphics, its screenshots, and `price.py` for the prices of the worlds |

## App

| Field | Value |
|---|---|
| Name (23/30) | Sisyphus: Push the Rock |
| Website and support | https://katemariongames.com/sisyphus/ |
| Privacy policy | https://katemariongames.com/sisyphus/privacy-policy/. Its text is `privacy.md`: publish it there when it changes. The Privacy link in the settings of the game opens it. |
| Price | Free. Corinth is free; the other four worlds are in-app purchases, $1.99 each (`Config.WORLD_PRICE`). |

If the name is taken, try "Sisyphus: Endless Hill" or "Sisyphus Knob". The name under the icon on the phone stays "Sisyphus".

## Description (1095/4000)

Every day, Sisyphus pushes his rock up the hill. Today, you turn the knob.

Sisyphus is a small, calm pixel-art game. Under a window to a Greek hillside sits a bronze knob. Turn it, and Sisyphus leans into his boulder and climbs. Stop, and he holds on and catches his breath.

- Time moves as you push, and as the gods you hire push for you, even while the app is closed. The sun rises over the sea, crosses the sky and sets behind the hill. Then come the moon and the stars.
- Every day has its own weather: clear skies, drifting clouds, windy days with gulls, and rain.
- Wise words appear as you climb: more than 600 lines from Camus, Heraclitus, Seneca, Marcus Aurelius, Homer, Lao Tzu and more, and many from Sisyphus, the Rock and the Hill themselves.
- Olive trees, umbrella pines, cypresses, almond trees in bloom and broken columns pass by on the way up.
- Every step of the knob clicks, with a light tap of haptics. Sound can be turned off.
- Your day, the sun and your place on the hill are saved. Come back anytime.

No ads. No accounts. No tracking. Just the hill, the rock and you.

## What's New in 2.0 (497/500)

Both stores take this text: the What's New of the App Store, and the release notes of Google Play, which allow 500 characters.

- Four new worlds, each an in-app purchase: Mount Etna, The Underworld, Cairo and Mount Fuji.
- The Agora: spend your obols on rocks, helpers, gods, blessings and sights.
- Buy everything, and the top of the hill comes in sight. Then the climb begins again.
- Gods come in visions and ask you questions.
- Stones by the path tell old tales, and jokers tell riddles.
- Labors to do, and cards to collect.
- The rock rolls as you push. The settings turn off all sound, the ticks or the rolling rock.

## Screenshots

`screenshots/` holds the screenshots of both stores, taken on the iPhone simulator, five of each size: `1-morning.png`, `2-vision.png`, `3-sunset.png`, `4-night.png` and `5-diogenes.png`.

- `6.9-inch/`: 1320 × 2868, from the iPhone 17 Pro Max. App Store Connect asks for this size, and can scale it for the smaller iPhones.
- `6.3-inch/`: 1206 × 2622, from the iPhone 17 Pro, for the 6.3" and 6.1" displays.

To take them again, install a Release build on a new simulator, and launch it once for each shot with the saved keys as arguments, such as `xcrun simctl launch <sim> dev.oblac.sisyphus -sisyphus.effort 1762.56`. Each shot starts from a new install, with `-sisyphus.laborsSeen 20 -sisyphus.quotesDrawn 1 -sisyphus.nextStele 50000`, and `-sisyphus.nextJoker 50000` in all but Diogenes. The screenshots must have no alpha channel.

| Shot | Arguments |
|---|---|
| `1-morning` | `-sisyphus.effort 1762.56 -sisyphus.position 420 -sisyphus.quoteSeed 694 -sisyphus.knob 0.6 -sisyphus.obols 4` |
| `2-vision` | `-sisyphus.effort 7214.4 -sisyphus.position 700 -sisyphus.quoteSeed 1070 -sisyphus.knob 1.2 -sisyphus.obols 11 -sisyphus.visionSeed 1 -sisyphus.visionsDrawn 1 -sisyphus.nextVision 0.02`, then a turn of the knob; the shot is taken when the Sphinx asks |
| `3-sunset` | `-sisyphus.effort 9905.76 -sisyphus.position 900 -sisyphus.quoteSeed 805 -sisyphus.knob 4.0 -sisyphus.obols 17` |
| `4-night` | `-sisyphus.effort 10938.24 -sisyphus.position 580 -sisyphus.quoteSeed 312 -sisyphus.knob 5.4 -sisyphus.obols 23` |
| `5-diogenes` | `-sisyphus.effort 7110.72 -sisyphus.position 100 -sisyphus.quoteSeed 1879 -sisyphus.knob 2.3 -sisyphus.obols 8 -sisyphus.joker 0 -sisyphus.met.0 0 -sisyphus.nextJoker 134` |

Google Play takes no picture that is more than twice as tall as it is wide. Its screenshots in `playstore/screenshots/` are the 6.9-inch ones, cut to 1320 × 2640: see `playstore/listing.md`.
