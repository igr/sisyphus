# Sisyphus

A small pixel-art game for iOS and Android, made with Kotlin Multiplatform and Compose Multiplatform.
Turn the knob, and Sisyphus pushes the rock up the hill. The knob clicks at each step.
The sun moves only while he works.
Each day has its own weather. Wise words appear under the display as you push.

Website, support and privacy policy: https://igr.github.io/sisyphus/ (from the `gh-pages` branch).

## Run

- iOS: open `iosApp/iosApp.xcodeproj`, select the **Sisyphus** scheme and an iPhone simulator, then Run.
  For a real iPhone, set your signing team in the target settings.
- Android: `./gradlew :androidApp:installDebug` with a phone or emulator connected.
  The Android SDK path goes in `local.properties` (`sdk.dir=...`).
  The Google Play bundle: `./gradlew :androidApp:bundleRelease` (see `playstore/listing.md`).
- Desktop window on a Mac, for quick checks: `just run` (or `./gradlew :composeApp:run`)
- Tests: `./gradlew :composeApp:jvmTest`. The tests also write PNG snapshots and `clicks.wav`
  to `composeApp/build/snapshots`.

## Code

All game code is in `composeApp/src/commonMain`. The screen is one small framebuffer (about 150 pixels wide),
scaled up by a whole number, so every pixel stays sharp. There are no image or sound files:
all art and the click sound are made in code.

- `audio` — the knob click
- `gfx` — framebuffer, dithering, pixel font
- `game` — knob physics, days and weather, quotes
- `scene` — the display: sky, sea, slope, rock, Sisyphus, clouds, birds, rain
- `ui` — layout, frame, Greek key band, quote panel, knob
- `Screen.kt` maps touches to virtual pixels; `App.kt` shows the framebuffer in Compose
- `iosMain`, `androidMain`, `jvmMain` — saving, sound and haptics for each platform; `androidApp` is the Android app
