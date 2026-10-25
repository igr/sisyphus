build := `git rev-list --count HEAD`

_default:
    @just --list

# runs the game in a desktop window
run:
    ./gradlew :composeApp:run

# uploads this commit to Google Play internal testing and to TestFlight
beta: beta-android beta-ios

# uploads this commit to Google Play internal testing
beta-android: _committed
    ./gradlew :androidApp:publishReleaseBundle -PversionCode={{build}}

# uploads this commit to TestFlight; Apple's rsync goes first, Homebrew's breaks the export
beta-ios: _committed
    rm -rf build/Sisyphus.xcarchive build/upload
    PATH=/usr/bin:/bin:/usr/sbin:/sbin:$PATH xcodebuild -project iosApp/iosApp.xcodeproj -scheme Sisyphus -configuration Release -destination 'generic/platform=iOS' -archivePath build/Sisyphus.xcarchive -allowProvisioningUpdates archive CURRENT_PROJECT_VERSION={{build}}
    PATH=/usr/bin:/bin:/usr/sbin:/sbin:$PATH xcodebuild -exportArchive -archivePath build/Sisyphus.xcarchive -exportOptionsPlist iosApp/ExportOptions.plist -exportPath build/upload -allowProvisioningUpdates

# the build number is the commit count, so each upload needs its own commit
_committed:
    @test -z "$(git status --porcelain)" || (echo "Commit first: each upload is one commit." && exit 1)
