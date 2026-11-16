# App Store

The fields that only App Store Connect has. The name, the links, the description, What's New and the screenshots are in `../listing.md`. Character counts are within Apple's limits.

## App information

| Field | Value |
|---|---|
| Subtitle (29/30) | Turn the knob. Push the rock. |
| Bundle ID | dev.oblac.sisyphus |
| SKU | sisyphus |
| Primary language | English (U.S.) |
| Category | Games: Casual, Simulation |
| Copyright | 2026 KateMarionGames |
| Age rating | Alcohol, Tobacco, or Drug Use or References: Infrequent (wine in some jokes and tales, and the Wine of Dionysus). Horror/Fear Themes: Infrequent (Dracula, Cthulhu, Death). Cartoon or Fantasy Violence: Infrequent (the myths of the tales, told in text). Guns or Other Weapons: Infrequent (a sword, a spear, a sickle in the tales). Mature or Suggestive Themes: Infrequent (war and abduction in the myths). Contests: Infrequent (the questions of the visions). "None" or "No" to every other question. The result is 13+. |

## App Privacy

| Question | Answer |
|---|---|
| Data collection | No, we do not collect data from this app |

`iosApp/iosApp/PrivacyInfo.xcprivacy` declares the same: no data types, no tracking.

## Promotional text (161/170)

A tiny pixel-art meditation. Turn the bronze knob, push the boulder up an endless Greek hill, and collect wise words from Seneca, Heraclitus and the Rock itself.

## Keywords (99/100)

pixel art,knob,boulder,hill,greek,myth,stoic,quotes,calm,relax,zen,idle,retro,philosophy,meditation

## Notes for App Review (2274/4000)

Sisyphus: Push the Rock is a calm pixel-art game. Turn the knob, and Sisyphus pushes his rock up the hill. The game is free: it has no in-app purchases, no ads, no accounts and no tracking, and it does not use the network.

How to play: drag a finger in a circle on the bronze knob at the bottom of the screen. One circle is one turn. The sun moves only while you push, so a day passes only when you turn the knob. In a new climb:
- After 20 to 40 turns, a joker stands by the path and tells a riddle in a bubble.
- After 36 turns, the first obol comes. The coin count in the top-right corner shows the obols.
- After 70 to 100 turns, on day 2, a god comes in a vision and asks a question. Turn the knob to the left or to the right to pick an answer.
- From day 3, stones by the path tell old tales.
- When all 15 items of the Agora are bought, a final climb leads to the top. The rock rolls down, and a new climb begins, in the same world or in another one.
Only play brings these. No server, date, region or setting turns anything on.

Screens: the X in the top-right corner closes each screen.
- The coin count opens the Agora. There, obols buy rocks, helpers, gods, blessings and sights. A god pushes on his own, also while the app is closed: when the app opens again, the game adds his turns.
- The day in the top-left corner opens a screen with three tabs: Worlds, Labors and Cards. When a labor is done, or a joker or a tale is new, this tap opens that tab first. Then tap "Worlds" at the top left.
- Worlds: five worlds, all free: Corinth, Mount Etna, The Underworld, Cairo and Mount Fuji. Tap a world, then Yes, and the climb starts over there at day 1, with its own sky, landmark, items and gods. Under the worlds, a note says that more worlds will come.
- The gear in the bottom-right corner opens the settings: the sound switches, the link to the privacy policy and the build number.
- The (?) in the bottom-left corner opens the help.

Data: the game keeps its progress on the device only. It collects no data.

This build replaces build 126 of version 2.0.1. Build 126 sold the four worlds as in-app purchases; now they are free, and the app holds no purchase code. The app has no hidden, dormant or undocumented features.

Contact: contact@katemariongames.com

## In-app purchases

None: every world is free. The four products of the worlds, `world_etna`, `world_underworld`, `world_cairo` and `world_fuji`, stay in App Store Connect as "Ready to Submit". Never add them to a version, and do not delete them: Apple never lets you use a deleted product ID again.

## Screenshots

- iPhone 6.9" Display: `../screenshots/6.9-inch/1-morning.png` to `5-diogenes.png`
- iPhone 6.3" and 6.1" Display: `../screenshots/6.3-inch/1-morning.png` to `5-diogenes.png`

## Build

`just release` uploads the current commit to App Store Connect, without the dev screen. Choose that build on the version page, and submit it for review.
