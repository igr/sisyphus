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
| Data types | Purchases: Purchase History |
| Purposes | App Functionality, Analytics: RevenueCat counts the purchases for our sales reports |
| Linked to the user | No: the id of RevenueCat is anonymous |
| Used for tracking | No |

`iosApp/iosApp/PrivacyInfo.xcprivacy` declares the same.

## Promotional text (161/170)

A tiny pixel-art meditation. Turn the bronze knob, push the boulder up an endless Greek hill, and collect wise words from Seneca, Heraclitus and the Rock itself.

## Keywords (99/100)

pixel art,knob,boulder,hill,greek,myth,stoic,quotes,calm,relax,zen,idle,retro,philosophy,meditation

## Notes for App Review (2215/4000)

Sisyphus: Push the Rock is a calm pixel-art game. Turn the knob, and Sisyphus pushes his rock up the hill. No account or login is needed. The game has no ads and no tracking.

How to play: drag a finger in a circle on the bronze knob at the bottom of the screen. One circle is one turn. The sun moves only while you push, so a day passes only when you turn the knob. In a new climb:
- After 20 to 40 turns, a joker stands by the path and tells a riddle in a bubble.
- After 36 turns, the first obol comes. The coin count in the top-right corner shows the obols.
- After 70 to 100 turns, on day 2, a god comes in a vision and asks a question. Turn the knob to the left or to the right to pick an answer.
- From day 3, stones by the path tell old tales.

Screens: the X in the top-right corner closes each screen.
- The coin count opens the Agora. There, obols buy rocks, helpers, gods, blessings and sights. A god pushes on his own, also while the app is closed.
- The day in the top-left corner opens a screen with three tabs: Worlds, Labors and Cards. When a labor is done, or a joker or a tale is new, this tap opens that tab first. Then tap "Worlds" at the top left to see the worlds.
- The gear in the bottom-right corner opens the settings: the sound switches, the link to the privacy policy and the support ID.
- The (?) in the bottom-left corner opens the help.

In-app purchases: the four worlds with a lock on the Worlds tab: Mount Etna, The Underworld, Cairo and Mount Fuji. Each is a non-consumable purchase of $1.99. You can buy one at any time. No progress is needed first. Tap a world with a lock, then Yes, and the payment sheet opens. After the purchase, the new world begins at once, with its own sky, landmark, items and gods. The climb starts over there at day 1. Corinth, the first world, is free. Restore is in the bottom-left corner of the Worlds screen.

Data: the game keeps its progress on the device. RevenueCat handles the purchases with an anonymous ID. The game asks for no personal data.

This version replaces 2.0, which we removed from review before the review started. 2.0.1 adds the X that closes each screen, a new coin sound and small fixes.

Contact: contact@katemariongames.com

## In-app purchases

The four worlds are non-consumable, $1.99 each: `world_etna`, `world_underworld`, `world_cairo` and `world_fuji`. In App Store Connect each has a review screenshot and review notes:

- The screenshot: the question to buy that world, with its price, on the Worlds tab of the simulator. Upload it with `rc products store screenshot <product> --file <png>`.
- The notes, with the name of the world: "Unlocks the world Mount Etna. Tap the day in the top-left corner, then Worlds at the top left. Tap Mount Etna, which has a lock, then Yes. Corinth, the first world, is free."

Apple reviews the first purchases only with a version: on the version page, add them under "In-App Purchases and Subscriptions" before you submit it.

## Screenshots

- iPhone 6.9" Display: `../screenshots/6.9-inch/1-morning.png` to `5-diogenes.png`
- iPhone 6.3" and 6.1" Display: `../screenshots/6.3-inch/1-morning.png` to `5-diogenes.png`

## Build

`just release` uploads the current commit to App Store Connect, without the dev screen. Choose that build on the version page, and submit it for review.
