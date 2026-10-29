# Stations Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** In Corinth, each item bought sets a carved stone 30 turns ahead on the path. When Sisyphus walks past it, the next of the 15 parts of the tale of Sisyphus shows in the panel of the wise words.

**Architecture:** A new `game/Tales.kt` holds the tale: 15 parts, each with its place and one or two texts. `Game` places the next stone, notices when he walks past it, puts the part's texts first in the news queue that the labors already use, and counts a part as told when its last text fades. A part cut by any other text comes again after it. `QuoteBox` gets `hurry()`, so a wise word makes room for the part at once. `scene/Steles.kt` draws the stone and its 15 reliefs, and `SceneRenderer` draws the stones like the herms.

**Tech Stack:** Kotlin Multiplatform, Compose Multiplatform, kotlin.test. All art is drawn in code into the `Pixels` framebuffer.

**Spec:** `docs/stations.md`

## Global Constraints

- A stone gives nothing: no obols, and no change to the pace. A climb is never shorter or longer.
- Only `position` reaches a stone, and only the player's own turns move it. The gods never do.
- The texts and places are the table of `docs/stations.md`, copied exactly. The place is the author of each text: `Quote(text, place)`.
- `Config.STELE_TURNS = 30`: a stone stands `STELE_TURNS * PX_PER_TURN` (360) pixels ahead of `position`.
- New saved keys: `stelesTold` (0 to 15) and `nextStele` (pixels walked, -1 for none). In an old save, `stelesTold` starts at `min(shop.bought, tale size)`, and `nextStele` at -1.
- Only Corinth has a tale. In other worlds, nothing changes.
- The stones walked past are not saved. After a restart, only the next stone ahead stands. A stone he walked past goes out of sight within 5 turns.
- Every text fits 3 lines of `NARROWEST_TEXT` (119), and `"— $place"` fits one line of it. Only `PixelFont` glyphs.
- Every snapshot that exists today stays byte-identical. Old saves have `stelesTold == shop.bought`, so they show no stone.
- Match the code around it: short names, few comments, one-line KDoc in Simple English.
- Every task ends with `./gradlew :composeApp:jvmTest :composeApp:testAndroidHostTest` passing.
- Do not commit: ask the user.

## Review Focus

- The 15th item is bought while the 14th part shows. Hermes says "The top is in sight. Push on." Expect the 14th part to come again after it, and then the 15th. Test: Task 3, `aPartCutByAnotherTextComesAgain`.
- The app restarts after he walked past a stone, but before its last text faded. Expect the part to come with a new stone: never lost, never told twice. Test: Task 2, `aPartNotToldBeforeARestartComesWithANewStone`.
- The player moves to another world while a part waits in the news. Expect no part of Corinth in the new world. Test: Task 2, `aMoveDropsTheWaitingPart`.
- A labor is done just as he walks past a stone. Expect the part first and the news of the labor after it, with neither lost. Test: Task 3, `thePartComesBeforeTheNewsOfALabor`.
- The dev Top button buys everything at once and puts him near the top. Expect at most one stone ahead, and nothing to break at the top. Test: Task 2, `nearTheTopPlacesOneStoneAtATime`.

---

### Task 1: The tale

**Files:**
- Create: `composeApp/src/commonMain/kotlin/dev/oblac/sisyphus/game/Tales.kt`
- Modify: `composeApp/src/commonMain/kotlin/dev/oblac/sisyphus/game/Config.kt`
- Test: `composeApp/src/commonTest/kotlin/dev/oblac/sisyphus/TalesTest.kt`

**Interfaces:**
- Produces: `class Part(val place: String, vararg texts: String) { val quotes: List<Quote> }` with `quotes = texts.map { Quote(it, place) }`. `val CORINTH_TALE: List<Part>` holds the 15 parts in the order of the table. `fun taleOf(place: Place): List<Part>?` returns `CORINTH_TALE` for Corinth and null for the other worlds. `class Stele(val at: Double, val part: Int)`. `Config.STELE_TURNS = 30`.

- [ ] **Step 1: Write the failing tests** in `TalesTest`:
  - `corinthTellsTheTaleOfSisyphusInFifteenParts`: `taleOf(Place.CORINTH)!!.size == 15`. Part 9 is at "Peirene's spring". Part 14 has 2 quotes, all the others have 1. The first text is "Long ago, this city was Ephyra. Sisyphus built it, and he became its king." The last is "For his tricks, the gods gave him a rock. He must push it to the top, forever."
  - `otherWorldsTellNoTaleYet`: `taleOf` is null for Mount Etna, The Underworld and Cairo.
  - `everyPartFitsTheSmallestPhone`: every quote of every part fits 3 lines of `NARROWEST_TEXT`, and `PixelFont.width("— ${part.place}") <= NARROWEST_TEXT`. Use the `fits` helper of `VoicesTest`, and check `PixelFont.hasGlyph` for every character.
- [ ] **Step 2: Run them, and see them fail.** `./gradlew :composeApp:jvmTest --tests 'dev.oblac.sisyphus.TalesTest'`. Expected: they do not compile, because `taleOf` is missing.
- [ ] **Step 3: Write `Tales.kt`** with the 15 parts of the table of `docs/stations.md`. Add `Config.STELE_TURNS` with a one-line KDoc: "A stone of the tale stands this many turns of walking ahead of him."
- [ ] **Step 4: Run the tests, and see them pass.**

### Task 2: Stones and parts in the game

**Files:**
- Modify: `composeApp/src/commonMain/kotlin/dev/oblac/sisyphus/game/Quotes.kt` (`QuoteBox`)
- Modify: `composeApp/src/commonMain/kotlin/dev/oblac/sisyphus/game/Game.kt`
- Modify: `docs/saved-data.md`
- Test: `composeApp/src/commonTest/kotlin/dev/oblac/sisyphus/StationsTest.kt`, `composeApp/src/commonTest/kotlin/dev/oblac/sisyphus/QuotesTest.kt`

**Interfaces:**
- Consumes: `taleOf`, `Part.quotes`, `Stele` and `Config.STELE_TURNS`, from Task 1.
- Produces: `QuoteBox.hurry()`. In `Game`: `var stelesTold: Int` (private set), `var nextStele: Double?` (private set), and `val steles: List<Stele>`, which holds the stones he walked past in this run of the app, then the next one.

- [ ] **Step 1: Write the failing tests.**
  - `QuotesTest.hurryFadesAWiseWordButNotANews`: a `QuoteBox` showing a wise word goes to `FADING` after `hurry()`. A `QuoteBox` showing an `announce`d quote, or a news, stays as it was.
  - In `StationsTest`, Corinth games with `MemoryStore(mapOf("obols" to 10.0))`. Turn with the `Game.turn` helper of `GameTest`: move it to a shared test file, `TestKnob.kt`.
    - `aStoneStandsAheadWhenAnItemIsBought`: after `buy(Line.GODS)`, `nextStele == position + 360.0`, and `steles.single().part == 0`.
    - `walkingPastTheStoneTellsThePart`: turn past the stone. `quotes.quote` becomes `CORINTH_TALE[0].quotes[0]` within 1 second. After it fades, `stelesTold == 1`.
    - `thePartsComeInOrderOnceEach`: buy 3 items together. The stones come one at a time, at least 360 pixels apart. Parts 0, 1 and 2 show in this order, each once, and `stelesTold == 3`.
    - `theGodsDoNotWalkHimToAStone`: with the gods bought, `pause()`, then `resume()` 10 hours later on the `wall` clock. `position`, `nextStele` and `stelesTold` stay as they were.
    - `stonesAndPartsSurviveARestart`: a new `Game` on the same store keeps `stelesTold` and `nextStele`.
    - `aPartNotToldBeforeARestartComesWithANewStone`: walk past a stone, `save()`, and start a new `Game` before the text fades. It has `nextStele == position + 360.0` and the same `stelesTold`. The part shows once he walks past the new stone.
    - `aNewClimbStartsTheTaleAgain`: after `startOver()`, `stelesTold == 0`, `nextStele == null` and `steles` is empty.
    - `aMoveDropsTheWaitingPart`: walk past a stone, then `moveTo(Place.ETNA)` at once. No text of `CORINTH_TALE` shows in the next minute of turning. Buying there sets no stone.
    - `anOldSaveCountsItsItemsAsTold`: with the store keys `shop.rock` 2 and `shop.alone` 1, and no `stelesTold`: `stelesTold == 3` and `nextStele == null`.
    - `nearTheTopPlacesOneStoneAtATime`: after `nearTheTop()` and 10 seconds of turning, `steles.size <= 1`, and `atTop` comes without an error.
- [ ] **Step 2: Run them, and see them fail.** `./gradlew :composeApp:jvmTest --tests 'dev.oblac.sisyphus.StationsTest' --tests 'dev.oblac.sisyphus.QuotesTest'`
- [ ] **Step 3: Add `QuoteBox.hurry()`.** `QuoteBox` remembers whether its quote is a plain wise word: one that it drew itself, or the tutorial. `hurry()` sends a plain wise word that is typing or showing to `FADING`. It never cuts an `announce`d quote or a news.
- [ ] **Step 4: Add the stations to `Game`.**
  - Add the keys `stelesTold` and `nextStele`, and the fields of the Interfaces, loaded as Global Constraints says. Add `private var tale = taleOf(place)`.
  - Call `passStele(from)`, then `countTold()`, then `placeStele()` in `update`, right after `passHerm(from)`. Also call `placeStele()` at the end of `buy`.
  - In `begin()`: remove the texts of the old tale from `news`, then set `tale = taleOf(place)`, `stelesTold = 0` and `nextStele = null`, and clear the stones walked past.
  - Save the two keys in `save()`. Add two rows to the table of `docs/saved-data.md` ("Back to 0" / "Back to -1" in both columns), and their start values to its list.
  - Use this logic. It is the algorithm, so keep it:

```kotlin
private val part get() = tale?.getOrNull(stelesTold)

/** He walked past the next stone: its part goes first in the news, and a wise word makes room for it. */
private fun passStele(from: Double) {
    val at = nextStele ?: return
    val part = part ?: return
    if (from >= at || position < at) return
    nextStele = null
    passed += Stele(at, stelesTold)
    part.quotes.asReversed().forEach { news.addFirst(it) }
    if (vision == null) quotes.hurry()
    dirty = true
}

/** A part is told when its last text fades. */
private fun countTold() {
    val part = part ?: return
    if (quotes.quote === part.quotes.last() && quotes.phase == QuoteBox.Phase.FADING) {
        stelesTold++
        dirty = true
    }
}

/** The next stone stands ahead when an item is bought for a part not yet told, once the part before it is told. */
private fun placeStele() {
    val part = part ?: return
    if (nextStele != null || atTop || stelesTold >= min(shop.bought, tale!!.size)) return
    if (quotes.quote in part.quotes || news.any { it in part.quotes }) return
    nextStele = position + Config.STELE_TURNS * PX_PER_TURN
    dirty = true
}
```

- [ ] **Step 5: Run the tests, and see them pass.** Then run all tests.

### Task 3: The panel's other voices

**Files:**
- Modify: `composeApp/src/commonMain/kotlin/dev/oblac/sisyphus/game/Game.kt`
- Test: `composeApp/src/commonTest/kotlin/dev/oblac/sisyphus/StationsTest.kt`

**Interfaces:**
- Consumes: `part`, `news` and `stelesTold` of Task 2.
- Produces: `private fun say(quote: Quote)`. Every `quotes.announce(...)` in `Game` becomes `say(...)`.

- [ ] **Step 1: Write the failing tests.**
  - `aPartWaitsForAVision`: walk past a stone while a vision asks (`nextVision` 0.5 and the `asking` steps of `GameTest`). The part shows only after the reply has faded, and `stelesTold` counts it once.
  - `aPartCutByAnotherTextComesAgain`: while part 0 shows, `summonVision()`. Answer it. Part 0 shows again after the reply. Then, with 13 parts told, 14 items and part 13 showing, the 15th `buy` cuts it with "The top is in sight. Push on." Expect both texts of part 13 after it, then the stone of part 14.
  - `thePartComesBeforeTheNewsOfALabor`: when he walks past a stone, a labor's news is already in `news`. The part shows first, then the news.
  - `aPartWithTwoTextsShowsBoth`: with 13 parts told and 14 items, part 13 shows its two texts one after the other. `stelesTold` becomes 14 only when the second fades.
  - `theLastPartComesBeforeTheFirstHerm`: with 14 parts told and 14 items, buying the 15th puts the stone of part 14 within the first 100 turns of the final climb. Its text has faded before `position` reaches `herms.first()`.
- [ ] **Step 2: Run them, and see them fail.** `./gradlew :composeApp:jvmTest --tests 'dev.oblac.sisyphus.StationsTest'`
- [ ] **Step 3: Add `say`.** If the panel shows a text of `part` and its phase is not `FADING`, that text goes first in `news` before the new quote is announced. Replace every `quotes.announce` call in `Game` with `say`.
- [ ] **Step 4: Run the tests, and see them pass.** Then run all tests.

### Task 4: The stones in the scene

**Files:**
- Create: `composeApp/src/commonMain/kotlin/dev/oblac/sisyphus/scene/Steles.kt`
- Modify: `composeApp/src/commonMain/kotlin/dev/oblac/sisyphus/scene/SceneRenderer.kt`
- Test: `composeApp/src/commonTest/kotlin/dev/oblac/sisyphus/LookTest.kt`, `composeApp/src/jvmTest/kotlin/dev/oblac/sisyphus/SnapshotTest.kt`

**Interfaces:**
- Consumes: `Game.steles` of Task 2.
- Produces: `internal val STELE: Pixels`, which is 9 by 15. `internal val RELIEFS: List<Pixels>` holds 15 reliefs, each 7 by 7, in the order of the table.

- [ ] **Step 1: Before any change, copy `composeApp/build/snapshots`** to the scratchpad, after a run of `./gradlew :composeApp:jvmTest`.
- [ ] **Step 2: Write the failing test.** `LookTest.theStoneAheadIsDrawn`: a Corinth game at noon with a stone 2 turns ahead renders different pixels from the same game with no stone. Set it up with the store keys `nextStele`, `stelesTold` 0 and `shop.alone` 1. Then write `SnapshotTest.steles`:
  - `12-stele` and `12-stele-small`: Corinth at noon, the stone of part 8 one turn behind him, and its text ("Peirene's spring") in the panel.
  - `steles-corinth`: a sheet of the 15 stones with their reliefs, as `rockSheet` does.
- [ ] **Step 3: Run it, and see it fail.** `./gradlew :composeApp:jvmTest --tests 'dev.oblac.sisyphus.LookTest'`
- [ ] **Step 4: Draw them.**
  - `STELE`: pale limestone (the light and shade of `PROP_COLORS`), a rounded top, and two rows of carved letter marks in the bottom half.
  - `RELIEFS`: the subjects of the Relief column of the table, carved in two darker tones of the stone.
  - In `SceneRenderer`, add `drawSteles(px, atm, game, p)` right after `drawHerms`, placed as `drawHerms` places a herm. Draw `RELIEFS[stele.part]` at (1, 1) of the stone, with the light of the time of day.
- [ ] **Step 5: Run all tests.** Look at the new snapshots, also the small one. `cmp` every snapshot that existed in Step 1: all must be the same.
- [ ] **Step 6: Check on the simulator** with a debug build. In Corinth, buy an item from the dev screen, then turn past the stone. Show the user.
