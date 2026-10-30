build := `git rev-list --count HEAD`

_default:
    @just --list

# runs the game in a desktop window
run:
    ./gradlew :composeApp:run

# shows the price of each world on Google Play; with --apply, sets it from Config.WORLD_PRICE in every country
play-price *args:
    python3 stores/playstore/price.py {{args}}

# uploads this commit, with the dev screen, to Google Play internal testing and to TestFlight
beta: beta-android beta-ios

# uploads this commit, with the dev screen, to Google Play internal testing
beta-android: _committed
    ./gradlew :androidApp:publishReleaseBundle -PversionCode={{build}} -Pbeta

# uploads this commit, with the dev screen, to TestFlight
beta-ios: _committed (_ios "BETA")

# uploads this commit, without the dev screen, for the stores: a production draft on Google Play, a build in App Store Connect
release: release-android release-ios

# uploads this commit as a production draft on Google Play; roll it out in Play Console
release-android: _committed
    ./gradlew :androidApp:publishReleaseBundle -PversionCode={{build}} --track production --release-status draft

# uploads this commit to App Store Connect; submit it for review there
release-ios: _committed (_ios "")

# archives and uploads to App Store Connect; Apple's rsync goes first, Homebrew's breaks the export
_ios conditions:
    rm -rf build/Sisyphus.xcarchive build/upload
    PATH=/usr/bin:/bin:/usr/sbin:/sbin:$PATH xcodebuild -project iosApp/iosApp.xcodeproj -scheme Sisyphus -configuration Release -destination 'generic/platform=iOS' -archivePath build/Sisyphus.xcarchive -allowProvisioningUpdates archive CURRENT_PROJECT_VERSION={{build}} SWIFT_ACTIVE_COMPILATION_CONDITIONS="{{conditions}}"
    PATH=/usr/bin:/bin:/usr/sbin:/sbin:$PATH xcodebuild -exportArchive -archivePath build/Sisyphus.xcarchive -exportOptionsPlist iosApp/ExportOptions.plist -exportPath build/upload -allowProvisioningUpdates

# the build number is the commit count, so each upload needs its own commit
_committed:
    @test -z "$(git status --porcelain)" || (echo "Commit first: each upload is one commit." && exit 1)
