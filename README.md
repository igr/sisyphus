# Sisyphus

A small pixel-art game for iOS and Android, made with Kotlin Multiplatform and Compose Multiplatform.
Turn the knob, and Sisyphus pushes the rock up the hill. The knob clicks at each step.
The sun moves only while he works.
Each day has its own weather. Wise words appear under the display as you push.
Earn obols as you push, and spend them in the Agora Market: better rocks, helpers, blessings and beauty.
When everything is bought, a final climb leads to the top. There the rock rolls down, and everything begins again, in the same world or in a new one.
Tap the day to choose another world: Corinth, Mount Etna or The Underworld. There the climb starts over.
The same screen lists the labors: honors for all your climbs, such as Tina Turner for 10,000 turns of the knob.
Corinth is free. The other worlds are bought once, for $0.99.

Website, support and privacy policy: https://igr.github.io/sisyphus/ (from the `gh-pages` branch).

## Run

- iOS: open `iosApp/iosApp.xcodeproj`, select the **Sisyphus** scheme and an iPhone simulator, then Run.
  For a real iPhone, set your signing team in the target settings.
- Android: `./gradlew :androidApp:installDebug` with a phone or emulator connected.
  The Android SDK path goes in `local.properties` (`sdk.dir=...`).
  The Google Play bundle: `./gradlew :androidApp:bundleRelease` (see `playstore/listing.md`).
- Desktop window on a Mac, for quick checks: `just run` (or `./gradlew :composeApp:run`)
- Test builds: `just beta` uploads the current commit to Google Play internal testing and to TestFlight
  (`just beta-android`, `just beta-ios` for one of them). These builds have the dev screen.
  Commit first: the build number is the commit count, so every upload needs its own commit.
  Android needs the Play service account key in `~/.android/sisyphus-play.json`; iOS uses the account signed in to Xcode.
- Store releases: `just release` uploads the current commit without the dev screen: a production draft on Google Play
  (roll it out in Play Console) and a build in App Store Connect (submit it for review there).
  Never send a test build to the stores: do not promote internal builds to production, or submit TestFlight builds from `just beta`.
- Dev screen: in debug builds (Run in Xcode, `installDebug`, `just run`), tap the "Agora Market" title
  to add or take away obols, to reset the shop, to start the climb again, to go three clicks before the next obol,
  to go near the top, or to open every world without buying.
  Release builds do not have it.
- Tests: `./gradlew :composeApp:jvmTest`. The tests also write PNG snapshots and `clicks.wav`
  to `composeApp/build/snapshots`.

## Purchases

RevenueCat sells the worlds, in the project "Sisyphus: Push the Rock". Each world after Corinth has one id,
`Place.product`, for its entitlement and for its package in the `worlds` offering. The app knows only these two.
The stores sell each world as a product of the same name, except Google Play, which sells The Underworld as
`world_underworld2`: a store never gives back a product id, even after a delete.

- Debug builds buy in RevenueCat's Test Store: a test sheet, and no money. All other builds buy in the App Store
  and Google Play. TestFlight and the Play license testers buy in the sandbox.
- The iOS SDK does not stop a release build that has the Test Store key, so the apps choose the key:
  `SisyphusApp.swift` and `MainActivity.kt` set `testStore` only in debug builds.
- The keys are public. The Test Store key is in `RevenueCatBilling.kt`, the store keys in `RevenueCatBilling.android.kt`
  and `RevenueCatBilling.ios.kt`.
- Support: the Worlds screen shows the end of the player's RevenueCat id, and a tap copies all of it.
  Find the player in the RevenueCat dashboard by that id.
- The RevenueCat CLI `rc` shows and changes the products: `rc products list`, `rc offerings verify`.
  `rc setup apple app7651e8d161` and `rc setup google app62a1b9ad7a` connect the stores, with two-factor sign-in.

## Code

All game code is in `composeApp/src/commonMain`. The screen is one small framebuffer (about 150 pixels wide),
scaled up by a whole number, so every pixel stays sharp. There are no image files: all art is made in code.
The knob click, the click of every other tap, the sound of buying, the fanfare of a new world, the coin of a new obol and the spell of a vision are sound files in
`composeApp/src/commonMain/composeResources/files`. The speaker in the Agora Market turns all of them on and off.

- `audio` — reads the game's sounds from their WAV files
- `gfx` — framebuffer, dithering, pixel font
- `game` — knob physics, days and weather, quotes
- `scene` — the display: sky, far view, slope, rock, Sisyphus, clouds, birds, rain. `Look.kt` holds what differs
  from world to world. `Etna.kt` and `Underworld.kt` draw those worlds. To add a world, follow `.claude/skills/new-world/SKILL.md`.
- `ui` — layout, frame, Greek key band, quote panel, knob
- `Screen.kt` maps touches to virtual pixels; `App.kt` shows the framebuffer in Compose
- `mobileMain` — the purchases of worlds, through RevenueCat, for both phones. The desktop has every world.
- `iosMain`, `androidMain`, `jvmMain` — saving, sound and haptics for each platform; `androidApp` is the Android app

The data that the game saves on the device is listed in [docs/saved-data.md](docs/saved-data.md).
