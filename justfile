scheme := "Sisyphus"

_default:
    @just --list

# runs the game in a desktop window
run:
    ./gradlew :composeApp:run

# the release and beta recipes of the games: see ~/prj/igr/gamekit/README.md
import '~/prj/igr/gamekit/release.just'
