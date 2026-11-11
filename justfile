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
