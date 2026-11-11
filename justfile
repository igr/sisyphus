set allow-duplicate-recipes

scheme := "Sisyphus"

_default:
    @just --list

# runs the game in a desktop window
run:
    ./gradlew :composeApp:run

# shows the price of each world on Google Play; with --apply, sets it from Config.WORLD_PRICE in every country
play-price *args:
    python3 stores/playstore/price.py {{args}}

# the release and beta recipes of the games: see ~/prj/igr/gamekit/README.md
import '~/prj/igr/gamekit/release.just'

# uploads this commit, with the dev screen, to Google Play internal testing
beta-android: _committed
    ./gradlew :androidApp:publishReleaseBundle -PversionCode={{build}} -Pbeta

# uploads this commit to TestFlight, as a store build: Apple rejects an app with a hidden dev screen
beta-ios: release-ios

# checks that the archive has no dev screen and no Test Store
_check-archive:
    python3 -I stores/appstore/check.py build/{{scheme}}.xcarchive
