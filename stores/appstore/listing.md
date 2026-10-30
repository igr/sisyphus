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
| Copyright | 2026 Igor Spasic |
| Age rating | Answer "None" or "No" to every question. The result is 4+. |

## App Privacy

| Question | Answer |
|---|---|
| Data types | Purchases: Purchase History |
| Purposes | App Functionality |
| Linked to the user | No: the id of RevenueCat is anonymous |
| Used for tracking | No |

`iosApp/iosApp/PrivacyInfo.xcprivacy` declares the same.

## Promotional text (161/170)

A tiny pixel-art meditation. Turn the bronze knob, push the boulder up an endless Greek hill, and collect wise words from Seneca, Heraclitus and the Rock itself.

## Keywords (99/100)

pixel art,knob,boulder,hill,greek,myth,stoic,quotes,calm,relax,zen,idle,retro,philosophy,meditation

## Notes for App Review

No account or login is needed. Drag a finger in a circle on the bronze knob at the bottom of the screen to push the rock. The gear in the bottom-right corner opens the settings, where the sounds turn on and off. A tap on the day in the top-left corner opens the Worlds screen: the worlds with a lock are in-app purchases, and Restore is at the bottom.

## In-app purchases

The four worlds are non-consumable, $1.99 each: `world_etna`, `world_underworld`, `world_cairo` and `world_fuji`. Each has its review notes and a screenshot of the question to buy it (see "Purchases" in `README.md`). Apple reviews the first purchases only with a version: on the version page, add them under "In-App Purchases and Subscriptions" before you submit it.

## Screenshots

- iPhone 6.9" Display: `../screenshots/6.9-inch/1-morning.png` to `5-rain.png`
- iPhone 6.3" and 6.1" Display: `../screenshots/6.3-inch/1-morning.png` to `5-rain.png`

## Build

`just release` uploads the current commit to App Store Connect, without the dev screen. Choose that build on the version page, and submit it for review.
