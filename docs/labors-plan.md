# Labors Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** A Labors tab on the Worlds screen with 20 labors in 4 categories. Hermes brings the news of a labor that is done, and a dot stands by the day until the tab opens. The number of climbs no longer shows under the day.

**Architecture:** A new `game/Labors.kt` holds the categories, the 20 labors, their texts and the short progress. `Game` counts the player's own turns and the days pushed through, saves them, finds the labors that become done, and queues their news; `QuoteBox` takes a queued news in place of its next wise word. `Screen` draws two tabs in the title row of the Worlds screen and a scrolling list of labors; `SceneRenderer` draws a dot after the day instead of the climbs.

**Tech Stack:** Kotlin Multiplatform, Compose Multiplatform, kotlin.test. All art is drawn in code into the `Pixels` framebuffer.

**Spec:** `docs/labors.md`

## Global Constraints

- A labor gives nothing: no obols, no items, no change to the game.
- No count goes down. The top, a move to another world and the Reset of the dev screen keep them all.
- Turns: the `turns` that push in `Game.update`, the same value that moves Sisyphus: zero at the top and while a vision holds the knob. The gods' pushes never count. The final climb counts.
- Days: `daysBefore + day - 1`. `begin()` adds `day - 1` to `daysBefore` before `effort` goes back to 0, so a move on DAY 1 adds nothing.
- Hills: `climbs`. Right answers: `voices.rightAnswers`.
- The news: `Quote("A labor is done: <name>.", "Hermes")`. It takes the place of the next wise word; it never uses `announce`. It waits while a screen covers the panel or a vision shows. Labors that are done when the game starts get no news.
- New saved keys: `turnsPushed`, `daysBefore`, `laborsSeen`. All start at 0 when missing.
- Text drawn with `PixelFont` uses only its glyphs and fits the smallest phone (135 virtual pixels wide). The news fits 3 lines of 119 pixels.
- Match the style of the surrounding code: short names, few comments, one-line KDoc in Simple English where the code around it has one.
- Every task ends with `./gradlew :composeApp:jvmTest :composeApp:testAndroidHostTest` passing.
- Do not commit. The user reviews and commits.

## Review Focus

- An older save with `climbs` 1 and `rightAnswers` 3, and none of the new keys. Expect the dot, no news, and counts that start from there. Tests: Task 2, `anOlderSaveShowsTheDot`; Task 3, `laborsDoneAtTheStartBringNoNews`.
- The first top does "Top of the Morning", and `begin()` replaces the `QuoteBox` with the tutorial. Expect the news after the tutorial, not lost. Test: Task 3, `theNewsOfATopSurvivesTheNewClimb`.
- The first right answer does "Lucky Guess" while the reply is in the panel. Expect the whole reply first, then the news. Test: Task 3, `theNewsWaitsForTheReply`.
- A player who owns two worlds moves back and forth on DAY 1. Expect no new days. Test: Task 2, `aMoveOnTheFirstDayAddsNoDay`.
- The smallest phone with the longest name and the longest progress. Expect them on one line, and the plain line in at most two. Test: Task 5, `everyLaborFitsTheSmallestPhone`.

---

### Task 1: The labors

**Files:**
- Create: `composeApp/src/commonMain/kotlin/dev/oblac/sisyphus/game/Labors.kt`
- Test: `composeApp/src/commonTest/kotlin/dev/oblac/sisyphus/LaborsTest.kt`

**Interfaces:**
- Produces: `enum class Category(val title: String) { HILLS("Hills climbed"), TURNS("Knob turns"), DAYS("Days"), ANSWERS("Right answers") }`; `class Labor(val category: Category, val goal: Long, val name: String, val line: String)`; `val LABORS: List<Labor>` in category order, goals rising; `class Counts(val hills: Int, val turns: Double, val days: Int, val answers: Int) { fun of(category: Category): Double }`; `fun Labor.done(counts: Counts): Boolean`; `fun progress(count: Double, goal: Long): String`; `fun laborNews(labor: Labor): Quote`.

- [ ] **Step 1: Write the failing tests** in `LaborsTest`:
  - `twentyLaborsInFourCategories`: the goals of each category are `[1, 10, 50, 100, 1000]`, `[100, 1_000, 10_000, 100_000, 1_000_000]`, `[10, 100, 1_000, 10_000, 100_000]`, `[1, 10, 50, 100, 500]`, and the names, in order: Top of the Morning, Rolling Stone, King of the Hill, Over the Hill, Imagine Him Happy; Turn On, Spin Doctor, Tina Turner, Revolutionary, Perpetual Motion; Carpe Diem, The Hundred Days, Days of Our Lives, Groundhog Day, Eternal Sunshine; Lucky Guess, Teacher's Pet, The Oracle, Know-It-All, Know Thyself.
  - `thePlainLinesSayWhatToDo`: "Reach the top once.", "Reach the top 10 times.", "Turn the knob 1,000,000 times.", "Push through 10 days.", "Answer a vision right.", "Answer 500 visions right.".
  - `progressIsShort`: `progress(7.0, 10) == "7/10"`, `progress(850.0, 1_000) == "850/1k"`, `progress(12_345.0, 100_000) == "12k/100k"`, `progress(123_456.0, 1_000_000) == "123k/1M"`, `progress(0.6, 100) == "0/100"`.
  - `aLaborIsDoneAtItsGoal`: Spin Doctor is not done at 999.9 turns and is done at 1000.
  - `theNewsNamesTheLabor`: `laborNews(Tina Turner)` is "A labor is done: Tina Turner." from "Hermes".
  - `everyLaborTextHasGlyphs`: names, lines, titles and news; and every news fits 3 lines of `NARROWEST_TEXT`.
- [ ] **Step 2: Run** `./gradlew :composeApp:jvmTest --tests 'dev.oblac.sisyphus.LaborsTest'`. Expected: compile failure, `LABORS` unresolved.
- [ ] **Step 3: Implement** `Labors.kt` with the interfaces above. A count below 1,000 shows whole; from 1,000 it shows in thousands, "k", and from 1,000,000 in millions, "M", always rounded down.
- [ ] **Step 4: Run** the same command. Expected: PASS.

### Task 2: The counts

**Files:**
- Modify: `composeApp/src/commonMain/kotlin/dev/oblac/sisyphus/game/Game.kt`
- Modify: `docs/saved-data.md`
- Test: `composeApp/src/commonTest/kotlin/dev/oblac/sisyphus/GameTest.kt`

**Interfaces:**
- Consumes: Task 1 `Counts`, `LABORS`, `done`.
- Produces: `Game.turnsPushed: Double`, `Game.daysBefore: Int`, `Game.days: Int`, `Game.counts: Counts`, `Game.laborsDone: Int`, `Game.newLabors: Boolean`, `Game.seeLabors()`.

- [ ] **Step 1: Write the failing tests** in `GameTest`:
  - `turnsPushedCountTheOwnTurns`: one full turn of the knob adds 1 (within 0.01); time away with Thanatos adds nothing; turns while a vision holds the knob add nothing.
  - `daysAddUpOverTheClimbs`: on DAY 5, `days` is 4; after `startOver()` it is still 4, and `daysBefore` is 4.
  - `aMoveOnTheFirstDayAddsNoDay`: `moveTo` twice on DAY 1 leaves `days` at 0.
  - `theCountsStay`: after the top, a move and `resetClimb()`, `climbs`, `turnsPushed`, `days` and `voices.rightAnswers` are not smaller.
  - `theCountsAreSaved`: a new `Game` on the same store has the same `turnsPushed`, `daysBefore` and `laborsSeen`.
  - `anOlderSaveShowsTheDot`: a store with `climbs` 1 and none of the new keys has `newLabors`; after `seeLabors()` it does not, and the new `laborsSeen` is saved.
- [ ] **Step 2: Run** `./gradlew :composeApp:jvmTest --tests 'dev.oblac.sisyphus.GameTest'`. Expected: compile failure.
- [ ] **Step 3: Implement.** `update` adds its `turns` to `turnsPushed`. `begin()` adds `day - 1` to `daysBefore` first. `save()` writes the three keys. Add the three rows to `docs/saved-data.md`, with the start values for older saves.
- [ ] **Step 4: Run** the suite. Expected: PASS.

### Task 3: The news

**Files:**
- Modify: `composeApp/src/commonMain/kotlin/dev/oblac/sisyphus/game/Quotes.kt` (`QuoteBox`)
- Modify: `composeApp/src/commonMain/kotlin/dev/oblac/sisyphus/game/Game.kt`
- Modify: `composeApp/src/commonMain/kotlin/dev/oblac/sisyphus/Screen.kt` (sets `game.covered`)
- Test: `GameTest`, `QuotesTest`

**Interfaces:**
- Consumes: Task 1 `laborNews`; Task 2 `counts`.
- Produces: `QuoteBox(seed, drawn, firstTime, news: () -> Quote? = { null })`; `Game.covered: Boolean` (var).

- [ ] **Step 1: Write the failing tests:**
  - `QuotesTest.aNewsTakesThePlaceOfTheNextWiseWord`: a box whose `news` gives one quote shows it after the current wise word fades, and `drawn` does not change.
  - `GameTest.aLaborBringsItsNewsOnce`: `turnsPushed` from the store at 99.9; a turn of the knob; when the wise word changes, it is "A labor is done: Turn On." from "Hermes", and it never comes again.
  - `GameTest.laborsDoneAtTheStartBringNoNews`: a store with `climbs` 1 and `rightAnswers` 3: no news in the next 20 wise words.
  - `GameTest.theNewsWaitsWhileThePanelIsCovered`: with `covered` true, the next wise word is not the news; after `covered` is false, the next one is.
  - `GameTest.theNewsWaitsForTheReply`: the first right answer; the panel shows the reply until it fades, and then the news of Lucky Guess.
  - `GameTest.theNewsOfATopSurvivesTheNewClimb`: the first top; after the tutorial of the new climb, the news of Top of the Morning comes.
- [ ] **Step 2: Run** `./gradlew :composeApp:jvmTest --tests 'dev.oblac.sisyphus.GameTest' --tests 'dev.oblac.sisyphus.QuotesTest'`. Expected: compile failure.
- [ ] **Step 3: Implement.** The fading `QuoteBox` takes `news() ?: next()`. `Game` keeps the labors that were done when it started, finds the new ones at the end of each `update`, and queues their news in order. The `news` that `Game` gives every `QuoteBox`, also the one `begin()` makes, returns nothing while `covered` or while a vision shows. `Screen.update` sets `covered` from the Agora, the Worlds screen, the help and the ending.
- [ ] **Step 4: Run** the suite. Expected: PASS.

### Task 4: The day

**Files:**
- Modify: `composeApp/src/commonMain/kotlin/dev/oblac/sisyphus/scene/SceneRenderer.kt` (`drawDay`)
- Modify: `composeApp/src/commonMain/kotlin/dev/oblac/sisyphus/ui/UiRenderer.kt` (`help`)
- Test: `ScreenTest`

**Interfaces:**
- Consumes: Task 2 `Game.newLabors`.
- Produces: `drawDay(px, day: Int, dot: Boolean, banner: Double)`.

- [ ] **Step 1: Write the failing tests** in `ScreenTest`:
  - `noClimbsUnderTheDay`: the scene under the day label is the same with `climbs` 0 and 7.
  - `aDotStandsAfterTheDayWhileALaborIsNew`: the pixels just right of the day label differ with and without a new labor, and are the same again after `seeLabors()`.
  - `theHelpFitsTheSmallestPhone`: with 9999 obols, the notes of the help stay inside the scene, and the help names the labors.
- [ ] **Step 2: Run** the ScreenTest. Expected: failures.
- [ ] **Step 3: Implement.** `drawDay` loses the peak and the climbs, and draws a small outlined bronze dot after "DAY n" when `dot`. Remove the peak sprite if nothing else uses it. The help note says "The sun moves only while you push. Tap the day for the worlds and the labors."; move the notes so that they fit.
- [ ] **Step 4: Run** the suite. Expected: PASS.

### Task 5: The Labors tab

**Files:**
- Modify: `ui/Layout.kt`, `ui/UiRenderer.kt`, `Screen.kt`
- Modify: `docs/shop.md` (the Worlds screen)
- Test: `ScreenTest`, `jvmTest/.../SnapshotTest.kt`

**Interfaces:**
- Consumes: Task 1 `LABORS`, `Category`, `progress`; Task 2 `counts`, `newLabors`, `seeLabors()`.
- Produces: `Layout.worldsTab: Rect`, `Layout.laborsTab: Rect`, `Layout.laborScroll: Int`; `UiRenderer.tabs(px, layout, labors: Boolean, dot: Boolean)`, `UiRenderer.labors(px, layout, counts: Counts, scroll: Int)`; `Screen.laborsShown: Boolean`.

- [ ] **Step 1: Write the failing tests** in `ScreenTest`:
  - `theWorldsScreenHasTwoTabs`: a tap on the day shows Worlds; a tap on Labors shows the labors and `seeLabors`; a tap on Labors again closes the screen; a tap on the day shows Labors again.
  - `theDayOpensTheLaborsWhenTheDotShows`.
  - `theTabsFitTheSmallestPhone`: "Worlds", "Labors" and the dot end before the obols.
  - `everyLaborFitsTheSmallestPhone`: each name with its progress at one turn below its goal on one line, and each plain line in at most two lines of the list.
  - `theLaborListScrollsToTheLastLabor`.
  - `restoreAndTheIdStayOnBothTabs`.
  - Change `tappingTheDayOpensTheWorlds` so that a tap on the shown tab closes the screen.
- [ ] **Step 2: Run** the ScreenTest. Expected: failures.
- [ ] **Step 3: Implement.** The title row shows the tabs; the shown one is cream, the other gray, with the dot after "Labors" while a labor is new. A tap on the other tab switches and resets the scroll; any other tap on the title row closes the screen, as today. The list: a header per category in its color, with "n/5"; a done labor has a dark band of its category color with a cream name and line; a labor not done is gray, with its progress at the right. Colors: Hills terra, Turns bronze, Days a dawn gold, Right answers the night blue of the visions, from the colors the game already has. The list drags and coasts as the worlds do, up to `laborScroll`; a tap on it does nothing. Opening the screen shows the tab shown last, or Labors while `newLabors`. Showing Labors calls `seeLabors()`. Write the snapshots `12-labors-small` (none done), `12-labors` (some done) and `12-day-dot`. Update the Worlds screen in `docs/shop.md`.
- [ ] **Step 4: Run** the suite. Expected: PASS. Look at the three snapshots.

### Task 6: The dev button

**Files:**
- Modify: `Game.kt`, `ui/Layout.kt`, `ui/UiRenderer.kt`, `Screen.kt`
- Test: `GameTest`, `ScreenTest`

**Interfaces:**
- Consumes: Task 1 `LABORS`; Task 2 `turnsPushed`.
- Produces: `Game.nearTheNextLabor()`, `Layout.devNextLabor: Rect`.

- [ ] **Step 1: Write the failing tests:**
  - `GameTest.theDevScreenBringsALaborAtTheNextClick`: after `nearTheNextLabor()`, one click of the knob does the next turns labor and queues its news.
  - `ScreenTest.theLaborButtonFitsTheSmallestPhone`, and a tap on it closes the Agora and the dev screen.
- [ ] **Step 2: Run** the tests. Expected: compile failure.
- [ ] **Step 3: Implement.** `nearTheNextLabor()` sets `turnsPushed` to one click below the goal of the first Turns labor that is not done. The button stands in the Voices row of the dev screen, after "Next turn"; the row title names the labors too.
- [ ] **Step 4: Run** the suite, then `./gradlew :androidApp:assembleDebug :composeApp:compileKotlinIosSimulatorArm64`. Expected: PASS and BUILD SUCCESSFUL.
