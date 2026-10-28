---
name: new-world
description: Use when the user asks for a new world in the Sisyphus game, or asks to draw or change a world of the Worlds screen, such as Corinth, Mount Etna, The Underworld or another place where Sisyphus pushes his rock.
---

# New world

A world is a new look and new names on the same game. The rules, the economy and the saved keys stay the same. Corinth, Mount Etna and The Underworld are the models. Each world has a `when (place)` branch in the code, so a new entry in `Place` makes the compiler show each place to fill.

## The parts of a world

| Part | Corinth | Code |
|---|---|---|
| Id and position | 0, first in the list | `Place` in `game/Game.kt` |
| Product | None: Corinth is free | `Place.product`, such as `world_etna`: its entitlement and its package in the `worlds` offering of RevenueCat, and usually its product in each store |
| Title | Corinth | `Place.title` |
| Note | the hill by the sea | `Place.note` |
| Myth | Sisyphus was the king of Corinth. The gods punish him on a hill by the sea. | Only for the names and the look |
| Sky over the day | Blue day, orange dusk, deep blue night | `KEYS`, `ETNA_KEYS`, `UNDERWORLD_KEYS` in `scene/Atmosphere.kt`; `Look.sky` |
| Sun, moon, stars | The sun, the moon with its phases, stars, shooting stars | `Look.sun`, with the glow around the sun; the `stars` value of the sky keys |
| Far view and landmark | The sea, with islands on the horizon | `drawSea`, `drawIslands`; `drawSummit`, `drawFields` in `scene/Etna.kt`; `drawRoof`, `drawFarShore`, `drawStyx` in `scene/Underworld.kt` |
| Ground | Grass on brown soil with stones, and flowers | `Look.ground`, `blades`, `grass`, `flowers` |
| Props by the path | Olive, young olive, cypress, stone pine, poplar, almond, lemon, dead tree, column, bush | `Look.props`: `PROPS` in `scene/Props.kt`, `ETNA_PROPS`, `UNDERWORLD_PROPS` |
| Life | Birds by day | `Look.birdUp`, `Look.birdFlat` |
| Weather | Clear, fair, cloudy, windy, rain | `Weather.of`, the same days in every world; `Look.ash` turns rain into ash |
| Small things | Dust at his feet, sweat, splashes of rain | `Look.dust`; `SWEAT`, `SPLASH` in `scene/World.kt` |
| Rocks | River stone, Pumice, Sphere of Daedalus | `items` in `game/Shop.kt`; `Look.rocks`: `ROCKS` in `SceneRenderer.kt`, `ETNA_ROCKS`, `UNDERWORLD_ROCKS` |
| Sidekicks | Merope, Bellerophon, Heracles | `items`; `ICONS` in `ui/Icons.kt`, `ETNA_ICONS`, `UNDERWORLD_ICONS` in `ui/<World>Icons.kt` |
| Gods | Amalthea, Pan, Thanatos | `items`; icons |
| Blessings | Purse of Hermes, Gift of Tyche, Wealth of Plutus | `items`; icons |
| Beauty | Dolphins leap in the sea, Pegasus flies by, the Pleiades shine at night | `items`; icons; `World.update` and `SceneRenderer.render` |
| Voices | The Sphinx and Hecate ask the riddles in the visions | `God` and `godsOf` in `game/Voices.kt`; sprites in `scene/Visions.kt` |
| Picture | The scene at noon, from the top of the rock down to the far view | `worldPreview` in `ui/Previews.kt` |

These are the same in every world: the price of $0.99, Sisyphus, the first rock, the knob, the day length, the final climb and its end, the prices and the effects of the 15 items, the line titles, the notes of the first four lines ("turns count 1.5x"), the wise words, the riddles, Hermes and his "Who said this?" in the visions, the sounds and the screens.

## Steps

1. Write the sheet: the table of parts, with a column for the new world. Take the names from Greek myth. Where you can, connect them to Sisyphus or to the place. Show the sheet to the user. Draw nothing before the user agrees to the names.
2. Before any change, run `./gradlew :composeApp:jvmTest` and copy `composeApp/build/snapshots` to the scratchpad. At the end, compare the images with `cmp`: the images of the other worlds must be the same. Only the Worlds images change.
3. Add the world last to `Place`, with the next id that was never used, and the product `world_<name>`. Never change or reuse an id or a product: saved games keep the id, and the stores keep the product.
4. Add the code of the world, as Mount Etna and The Underworld do:
   - `scene/<World>.kt`: its `Look`, props, birds, flyer, rocks, far view and sights. Its sky keys go in `scene/Atmosphere.kt`.
   - `game/Shop.kt`: its 15 items, made with `item(...)` in `with(Place.<WORLD>)`, so that they get the prices and the effects of Corinth.
   - `ui/<World>Icons.kt`: its 12 icons.
   - Add the world to the `when (place)` branches: `lookOf`, `items`, `itemIcon`, `SceneRenderer.render` (far view and sights) and `World.update` (sights).
   - Sell the world in RevenueCat with the CLI `rc`. This changes the user's accounts, so ask first. Create the product in the Test Store app (`--type non_consumable`, then `rc products prices set <id> --price USD=0.99`), in the App Store app and in the Play Store app. Create the entitlement of the same name and attach the three products. Create the package of the same name in the `worlds` offering and attach them. Then create the store products: the App Store ones with `rc products store plan` and `apply`, for all 175 App Store countries; the Google Play ones by the user in Play Console, which converts the price for every country. The user finishes their review details there. A store never gives back a product id, even after a delete: if one is lost, give the store product a new id, such as `world_underworld2`, and attach it to the same entitlement and package. `Place.product` stays.
5. Draw the scene: all of the parts from "Sky over the day" to "Small things". The help says that the sun moves only while you push, so each world has a sun that crosses its sky. The landmark shows at the start of a climb and in the picture.
6. Write the 15 items, and draw 12 icons (16 x 16) and 3 rocks. The details first tell what the item is, then what it does, with the effect words of Corinth. Each details text ends as the Corinth item of the same line and tier ends, for example "He takes Merope's place."
7. Make the three beauty sights, one for each tier of the Beauty line. A sight uses the timer of the Corinth sight of the same tier (`nextLeap`, `nextFlight`). Code that all worlds run must not take more random numbers, because then the other worlds change.
8. Choose the two gods who ask the riddles of the world in the visions, from its myths. Add them to `God` with the world as their place, each with a farewell that it says when nobody answers, and draw their sprites (25 x 15) in `scene/Visions.kt`. The riddles stay the same in every world, so no riddle names or describes the god who asks it.
9. Look at the picture on the Worlds screen: `worldPreview` cuts it from the real scene at noon. The landmark must show in it.
10. Update the tests. Each test that has The Underworld in it gets the new world too:
   - `GameTest`: add the new id to the ids that never change, and the product to the products that never change. A save with the new id loads its items.
   - `ShopTest`, `BeautyTest`, `PropsTest`, `RockLookTest` and `LookTest`.
   - `VoicesTest.eachWorldHasHermesAndTwoGodsOfItsOwn`: the gods of the new world. The fit, letter and picture tests already cover every god.
   - `ScreenTest`: its own icons. The fit and letter tests already cover every world.
   - With four worlds or more, they do not fit the smallest phone. `theWorldsFitTheSmallestPhone` must make sure that the list scrolls to the last world. In `theWorldsScrollWhenTheyDoNotFit`, use `Place.entries.last()`.
   - `SnapshotTest`: add the world to `moments`, `beauty`, `iconSheet`, `propSheet` and `rockSheet`. Where a test has a number of worlds in it, such as `worldScroll(3)`, use `Place.entries.size`.
11. Update the docs: the ids in `docs/saved-data.md` (the `place` key and "Ids that never change"), the Worlds section in `docs/shop.md`, the gods of each world in `docs/voices.md`, and `README.md` where it tells about the worlds or the scene.
12. Do the checks:
    - Run `./gradlew :composeApp:jvmTest :composeApp:testAndroidHostTest`. Look at each new image in `composeApp/build/snapshots`, also at the images of the smallest phone.
    - Run `./gradlew :androidApp:assembleDebug :composeApp:compileKotlinIosSimulatorArm64`.
    - Show the world on the simulator: a debug build shows its lock and its price, and buys it in the Test Store. It starts at day 1, with no obols, in the new scene, and it stays there after a restart. The Top button of the dev screen starts the final climb there. Do not use `just run` for this: the desktop game has a real save, and the move deletes its progress.

## Rules for names and texts

- Use only the letters that `PixelFont` has (`PixelFont.hasGlyph`).
- On the smallest phone (135 pixels wide), each name and note fits its row of the Agora, and the details fit in 6 lines of the offer. The title and the note fit their row of the Worlds screen. The tests in `ScreenTest` make sure of this.
