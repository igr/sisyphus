# The Pool of Tales Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Each climb, in every world, tells one tale from a pool of 4 (Sisyphus, Typhon, Orpheus, Isis and Osiris); every world has stones in its own colors.

**Architecture:** `Tale` and `TALES` replace `taleOf(place)` in `game/Tales.kt`. `Game` keeps the tale of the climb and the tales told, and takes a new tale when a climb begins. In the scene, a `Stone` holds the slab and the 60 reliefs in the colors of one world, and each `Look` has its `Stone`.

**Tech Stack:** Kotlin Multiplatform, Compose, the pixel framebuffer `Pixels`, kotlin.test.

**Spec:** `docs/stations.md`

## Global Constraints

- The texts are the ones in `docs/stations.md`, word for word. Tale ids never change: Sisyphus 0, Typhon 1, Orpheus 2, Isis and Osiris 3.
- The choice of a tale takes no number from `Game.random`: the visions and the sights stay the same.
- New saved keys: `tale` (old saves: 0) and `talesTold` (old saves: 0), one bit for each tale id.
- Only the letters of `PixelFont`. Every text fits the popup on the smallest phone (`LookTest`).
- Tests: `./gradlew :composeApp:jvmTest :composeApp:testAndroidHostTest`. Builds: `./gradlew :androidApp:assembleDebug :composeApp:compileKotlinIosSimulatorArm64`.
- Ask before committing (user rule); this plan's commits are approved with the plan.

## Review Focus

- A saved `tale` id that the pool does not have (a newer version's save): the game tells the tale of Sisyphus, and does not crash.
- The 15th part told by a jump of the dev screen still marks its tale as told.
- The Reset of the shop keeps the tale of the climb; the Reset of the climb, the top and a move take a new one.
- The relief of a part is `stone.reliefs[tale.id][part]`: the reliefs are in the order of the ids, 15 for each tale.
- The stone of the Underworld is dark on a dark scene: its relief must still show in the snapshot.

---

### Task 1: The pool of tales

**Files:**
- Modify: `composeApp/src/commonMain/kotlin/dev/oblac/sisyphus/game/Tales.kt`, `game/Game.kt`, `scene/SceneRenderer.kt` (`drawTelling` only)
- Test: `TalesTest.kt`, `StationsTest.kt`, `LookTest.kt`, `ScreenTest.kt` (the `CORINTH_TALE` uses)
- Docs: `docs/saved-data.md`

**Interfaces:**
- Produces: `class Part(vararg val texts: String)`; `class Tale(val id: Int, val parts: List<Part>)`; `val SISYPHUS`, `TYPHON`, `ORPHEUS`, `ISIS_AND_OSIRIS`; `val TALES: List<Tale>` in id order. `Game.tale: Tale` (no longer null), `Game.talesTold: Int`. `taleOf` and `CORINTH_TALE` are gone.

- [ ] **Step 1: Write the failing tests.**
  - `TalesTest.thePoolHasFourTalesOfFifteenParts`: `TALES.map { it.id } == listOf(0, 1, 2, 3)`; each has 15 parts; only `SISYPHUS.parts[13]` has 2 texts. First and last texts of each tale as in the spec, and `SISYPHUS.parts[0]` is "Long ago, Sisyphus built the city of Ephyra, and he became its king."
  - `TalesTest.everyTextHasOnlyLettersOfTheFont`: over all tales.
  - `StationsTest.theFirstClimbTellsTheTaleOfSisyphus`: a new game and an old save (no `tale` key) tell `SISYPHUS`, with `talesTold == 0`.
  - `StationsTest.sisyphusComesAgainUntilItIsTold`: `startOver()` with `talesTold` 0 gives `SISYPHUS`.
  - `StationsTest.aNewClimbTakesATaleNotToldYet`: for each `talesTold` with bit 0 set and not all set, `startOver()` gives a tale whose bit is not set.
  - `StationsTest.whenAllAreToldTheTaleBeforeDoesNotComeAtOnce`: `talesTold` 15, `tale` 0 to 3, `climbs` 0 to 4: the new tale differs from the one before.
  - `StationsTest.aTaleIsToldWithItsLastPart`: all 15 items, a `summit`, `stelesTold` 14, a stone near: walking through it sets bit 0 of `talesTold`. The same through a jump of the dev screen.
  - `StationsTest.theChoiceTakesNoRandomNumber`: two games with the same seed, `talesTold` 0 and 1; after `startOver()` and `save()`, their saved `nextVision` is the same.
  - `StationsTest.aClimbKeepsItsTaleAcrossARestart`: `tale` 2 in the store; a new `Game` on that store tells `ORPHEUS`; `save()` writes 2.
  - `StationsTest.anUnknownTaleIdTellsTheTaleOfSisyphus`: `tale` 9 in the store gives `SISYPHUS`.
  - `StationsTest.theResetOfTheShopKeepsTheTale`: `tale` 3; after `resetShop()`, still `ISIS_AND_OSIRIS`, from part 1.
  - `StationsTest.everyWorldTellsATale`: in Etna on day 3, a stone stands ahead.
  - The other tests use `SISYPHUS.parts` where they used `CORINTH_TALE`. `aMoveDropsTheStoneAndItsPopup` keeps its asserts; its reason is now day 1 of the new climb.
- [ ] **Step 2: Run them.** Expected: compile errors for `TALES`, `SISYPHUS`, `Tale`; then, with stubs, FAIL on the asserts.
- [ ] **Step 3: Implement.** `Tales.kt`: the 4 tales, texts word for word from the spec. `Game`: `tale` from `KEY_TALE` (`TALES.getOrNull(id) ?: SISYPHUS`), `talesTold` from `KEY_TALES_TOLD`; `passStele` sets the bit when `stelesTold` reaches `tale.parts.size`; `nextTale()`: `SISYPHUS` while its bit is 0, else a random one of the tales not told, else of `TALES - tale`, from `private val taleRandom = Random(seed + 1)`. `begin()` calls `tale = nextTale()` first, before the shop and the voices; `restartTale()` only clears the parts; `save()` writes both keys. `drawTelling` uses `game.tale.parts.size`.
- [ ] **Step 4: Run the tests.** Expected: PASS, both suites.
- [ ] **Step 5: Docs.** `docs/saved-data.md`: rows for `tale` and `talesTold`, with their old-save values.
- [ ] **Step 6: Commit.** "Tell a tale from a pool of four in every climb"

### Task 2: The stones of the worlds

**Files:**
- Modify: `scene/Steles.kt`, `scene/Look.kt`, `scene/Etna.kt`, `scene/Underworld.kt`, `scene/Cairo.kt`, `scene/SceneRenderer.kt`
- Test: `LookTest.kt`, `SnapshotTest.kt`
- Docs: `.claude/skills/new-world/SKILL.md`

**Interfaces:**
- Consumes: `Game.tale.id`, `TALES`.
- Produces: `internal class Stone(colors: Map<Char, Int>)` with `slab: Pixels` and `reliefs: List<List<Pixels>>` (by tale id, then part); `LIMESTONE`, `LAVA_STONE`, `BLACK_STONE`, `SANDSTONE`; `Look.stone: Stone`. `TALE_STONE` and `RELIEFS` are gone.

- [ ] **Step 1: Write the failing tests.**
  - `LookTest.everyStoneHasTheReliefsOfEveryTale`: for each of the 4 stones, `reliefs.size == TALES.size`, each 15, each 7 by 7, and the slab is 9 by 15.
  - `LookTest.eachWorldHasItsOwnStone`: the 4 slabs differ.
  - `LookTest.thePopupOfAStoneFitsTheSmallestPhone`: over every text of every tale (`tale` key).
  - `LookTest.theStoneAheadIsDrawn`: in every world.
- [ ] **Step 2: Run them.** Expected: FAIL.
- [ ] **Step 3: Implement.** The slab art and the 15 reliefs of Sisyphus stay as they are. Draw the 45 new reliefs, 7 by 7 in `c` and `k`, as the spec's tables list them. Colors (`w W D p C c k`): limestone as now; lava rock, black stone and sandstone darker or warmer, with a relief that shows on the panel. `drawSteles` draws `look.stone.slab` and `look.stone.reliefs[game.tale.id][stele.part]`.
- [ ] **Step 4: Snapshots.** `steles-<world>` for the 4 worlds: 4 rows, one for each tale, of 15 stones. Look at each; the relief shows on every stone.
- [ ] **Step 5: Run all tests and both builds.** Expected: PASS.
- [ ] **Step 6: Skill.** The Tale row of the new-world skill becomes "Stones": the colors of the world's stone, `Look.stone`; the tales are the same in every world. Step 9 says to give the new world its stone. `TalesTest.otherWorldsTellNoTaleYet` is gone from step 11.
- [ ] **Step 7: Commit.** "Give each world its own stones for the tales"
