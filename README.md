# Sisyphus

A small pixel-art game for iOS and Android, made with Kotlin Multiplatform and Compose Multiplatform.
Turn the knob, and Sisyphus pushes the rock up the hill. The knob clicks at each step.
The sun moves only while he works.
Each day has its own weather. Wise words appear under the display as you push.
Earn obols as you push, and spend them in the Agora Market: better rocks, helpers, blessings and beauty.
When everything is bought, a final climb leads to the top. There the rock rolls down, and everything begins again.
Tap the day to choose another world, such as Mount Etna; there the climb starts over.

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
  to add or take away obols, or to reset the shop. Release builds do not have it.
- Tests: `./gradlew :composeApp:jvmTest`. The tests also write PNG snapshots and `clicks.wav`
  to `composeApp/build/snapshots`.

## Code

All game code is in `composeApp/src/commonMain`. The screen is one small framebuffer (about 150 pixels wide),
scaled up by a whole number, so every pixel stays sharp. There are no image files: all art is made in code.
The knob click, the click of every other tap and the sound of buying are sound files in
`composeApp/src/commonMain/composeResources/files`. The speaker in the Agora Market turns all of them on and off.

- `audio` — reads the game's sounds from their WAV files
- `gfx` — framebuffer, dithering, pixel font
- `game` — knob physics, days and weather, quotes
- `scene` — the display: sky, far view, slope, rock, Sisyphus, clouds, birds, rain. `Look.kt` holds what differs
  from world to world, and `Etna.kt` draws Mount Etna. To add a world, follow `.claude/skills/new-world/SKILL.md`.
- `ui` — layout, frame, Greek key band, quote panel, knob
- `Screen.kt` maps touches to virtual pixels; `App.kt` shows the framebuffer in Compose
- `iosMain`, `androidMain`, `jvmMain` — saving, sound and haptics for each platform; `androidApp` is the Android app

The data that the game saves on the device is listed in [docs/saved-data.md](docs/saved-data.md).
