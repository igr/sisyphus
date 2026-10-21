# Google Play listing

Copy these into Play Console. Character counts are within Google's limits.

## App details

| Field | Value |
|---|---|
| App name (23/30) | Sisyphus: Push the Rock |
| Package name | dev.oblac.sisyphus |
| Default language | English (United States) |
| App or game | Game |
| Free or paid | Free |
| Category | Casual |
| Contact email | Required and shown on the store page. Choose the address you want to show. |
| Website | https://igr.github.io/sisyphus/ |
| Privacy policy | https://igr.github.io/sisyphus/privacy.html |

## Short description (77/80)

Turn the bronze knob and push the boulder up an endless hill. Calm pixel art.

## Full description (1036/4000)

Every day, Sisyphus pushes his rock up the hill. Today, you turn the knob.

Sisyphus is a small, calm pixel-art game. Under a window to a Greek hillside sits a bronze knob. Turn it, and Sisyphus leans into his boulder and climbs. Stop, and he holds on and catches his breath.

- Time moves only when you push. The sun rises over the sea, crosses the sky and sets behind the hill. Then come the moon and the stars.
- Every day has its own weather: clear skies, drifting clouds, windy days with gulls, and rain.
- Wise words appear as you climb: almost 150 lines from Camus, Heraclitus, Seneca, Marcus Aurelius, Homer, Lao Tzu and more, and many from Sisyphus, the Rock and the Hill themselves.
- Olive trees, umbrella pines, cypresses, almond trees in bloom and broken columns pass by on the way up.
- Every step of the knob clicks, with a light tap of haptics. Sound can be turned off.
- Your day, the sun and your place on the hill are saved. Come back anytime.

No ads. No accounts. No data collected. Just the hill, the rock and you.

## Release notes (29/500)

First release on Google Play.

## App content

| Question | Answer |
|---|---|
| App access | All functionality is available without special access. |
| Ads | No, the app has no ads. |
| Content rating | Category: Game. Answer "No" to every question. The result is Everyone / PEGI 3. |
| Target audience | 13 and older. Younger age groups add the Families policy. |
| Data safety | The app does not collect or share any user data. |
| Advertising ID | No. |
| Government, financial, health, news apps | No. |

## Graphics

- App icon (512 × 512): `icon-512.png`
- Feature graphic (1024 × 500): `feature-graphic.png`
- Phone screenshots (1080 × 2160): `screenshots/1-morning.png` to `5-rain.png`

## Build

- `./gradlew :androidApp:bundleRelease` makes `androidApp/build/outputs/bundle/release/androidApp-release.aab`.
- Version 1.1, version code 1. Every upload needs a higher version code: change `versionCode` in `androidApp/build.gradle.kts`.
- The bundle is signed with the upload key `~/.android/sisyphus-upload.jks`. Its passwords are in `keystore.properties`, which is not in git. Back up both files. Google Play signs the app with its own key; if the upload key is lost, Google support can reset it.
