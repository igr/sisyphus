# Jokers Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** About once a game day or two, one of 10 public-domain jokers stands by the path and tells a joke or a riddle in a bubble; a third tab of the Worlds screen shows their cards, face up once met.

**Architecture:** `game/Jokers.kt` holds the jokers, their lines and the weighted choice. `Game` keeps the joker ahead, the counts of meetings and the cards seen, and tells the bubble text as it tells a stone's. The scene draws the joker and his bubble; the Worlds screen gets a third tab with the cards.

**Tech Stack:** Kotlin Multiplatform, Compose, the pixel framebuffer `Pixels`, kotlin.test.

**Spec:** `docs/jokers.md`

## Global Constraints

- The jokers, weights, ranks, "who he is" texts and lines are the ones in `docs/jokers.md`, word for word. Ids never change: 0 Diogenes … 9 Cthulhu.
- The jokers take no number from `Game.random` or from the tales: `private val jokerRandom = Random(seed + 2)`.
- New saved keys: `nextJoker` (-1 none), `joker` (old saves 0), `jokersSeen` (bits), `met.0` … `met.9`.
- `Config.JOKER_TURNS = Config.TURNS_PER_DAY..2 * Config.TURNS_PER_DAY` (turns of walking ahead), `Config.JOKER_ROOM = 15` (turns from the next stone).
- Only the letters of `PixelFont`. Every text fits the bubble with its note (the name) on the smallest phone.
- Tests: `./gradlew :composeApp:jvmTest :composeApp:testAndroidHostTest`. Builds: `./gradlew :androidApp:assembleDebug :composeApp:compileKotlinIosSimulatorArm64`.
- Ask before committing (user rule). No Co-Authored-By trailer.

## Review Focus

- A stone placed after the joker, near him (a purchase, the part before told): the joker moves past the stone, and the two bubbles never show together.
- A restart in the middle of a bubble: the same joker, the same line, the same text.
- An old save (no joker keys): a joker stands ahead at the first update, and no card is face up.
- A saved `joker` id the game does not have: Diogenes, no crash.
- The dev Stone and Joker buttons used together: no crash.

---

### Task 1: The jokers

**Files:** Create `game/Jokers.kt`; modify `game/Config.kt`. Test: `JokersTest.kt` (commonTest).

**Interfaces — Produces:** `class Joke(vararg val texts: String)`; `class Joker(val id: Int, val name: String, val weight: Int, val rank: String, val about: String, val jokes: List<Joke>)`; `val JOKERS: List<Joker>` in id order; `fun pickJoker(random: Random): Joker`; `Config.JOKER_TURNS`, `Config.JOKER_ROOM`.

- [ ] **Step 1: Failing tests.** `theJokersAreTenInTheOrderOfTheirIds` (ids 0..9; names and ranks as in the spec); `theWeightsAddUpToFiveHundred` (sum 500, first 250, last 1, each lower than the one before); `everyJokerHasLinesOfOneOrTwoTexts`; `everyTextHasOnlyLettersOfTheFont`; `eachJokerComesAsOftenAsHisWeightSays` (500,000 picks of `Random(1)`: each count within 10% of `weight * 1000`).
- [ ] **Step 2: Run them.** Expected: compile errors.
- [ ] **Step 3: Implement** the data, word for word from the spec, and `pickJoker`: a number below 500, walked down the weights.
- [ ] **Step 4: Run them.** Expected: PASS.

### Task 2: Jokers on the path

**Files:** Modify `game/Game.kt`. Test: `JokersOnThePathTest.kt` (commonTest).

**Interfaces — Consumes:** Task 1. **Produces:** `Game.joker: Joker` (the next one); `Game.nextJoker: Double?`; `Game.metJoker: Pair<Double, Joker>?` (the last one met, in this run); `Game.jesting: String?`; `fun Game.met(joker: Joker): Int`; `Game.newJokers: Boolean`; `fun Game.seeJokers()`; `fun Game.nearTheNextJoker()`.

- [ ] **Step 1: Failing tests.** A new game: `nextJoker` is 72 to 144 turns ahead (`JOKER_TURNS * PX_PER_TURN`), seeds 1..20. Walking: `jesting` is null until 3 turns before the joker, then his first text for 6 turns, then the second; after it, `met(joker)` is 1 and a new joker stands 72 to 144 turns ahead. Lines in turn: `met.0` = 3 in the store gives Diogenes's 4th line. Room: with a stone ahead, the joker is at least 15 turns from it; a purchase that places a stone near the joker moves the joker 15 turns past the stone; no frame shows `telling` and `jesting` together over a long walk with all items bought one by one. A vision hides nothing in the game but stops the walk: `jesting` keeps its text while the vision shows. No bubble at the top. `startOver()` and `moveTo()` put a new joker 72 to 144 turns ahead and keep the counts. Restart: `nextJoker`, `joker`, `met` and `jokersSeen` come back from the store; an unknown `joker` id is Diogenes. The choice takes no number from the game: two games with the same seed, one with a joker met, save the same `nextVision`. `newJokers` after a first meeting; `seeJokers()` clears it. `nearTheNextJoker()` brings the joker after the current one in id order, a turn into his bubble.
- [ ] **Step 2: Run them.** Expected: compile errors, then FAIL.
- [ ] **Step 3: Implement** as the stones are: `placeJoker()` and `passJoker()` in `update` after the stones; `placeStele()` moves a joker that is too near; `begin()` clears the joker ahead; `save()` writes the keys.
- [ ] **Step 4: Run the tests.** Expected: PASS, both suites.

### Task 3: The jokers in the scene

**Files:** Create `scene/Jokers.kt` (sprites); modify `scene/SceneRenderer.kt`. Test: `LookTest.kt`, `SnapshotTest.kt`.

**Interfaces — Consumes:** Task 2. **Produces:** `internal val JOKER_SPRITES: List<Pixels>` by id; `SceneRenderer.jokerLeft(at, p, joker)`.

- [ ] **Step 1: Failing tests.** `LookTest.everyJokerHasASprite` (10, at most 15 by 24); `theBubbleOfAJokerFitsTheSmallestPhone` (every text of every joker, its note the name: the day and the obols stay as they are, and the bubble shows); `aVisionHidesTheBubbleOfAJoker`; `theJokerAheadIsDrawn` (in every world).
- [ ] **Step 2: Run them.** Expected: FAIL.
- [ ] **Step 3: Implement.** Draw the 10 sprites as the spec's Look column says. `drawJokers` after the stones, before the rock: the joker ahead, and the last one met while in the picture, flipped once the rock passed him. `drawJesting` after `drawTelling`: tail at the middle of the joker, kept 3 pixels inside the picture.
- [ ] **Step 4: Snapshots.** `13-joker` and `13-joker-small` (Diogenes's first line), `jokers` (the 10 in a row on the slope at noon) and `jokers-night`. Look at them.
- [ ] **Step 5: Run all tests.** Expected: PASS.

### Task 4: The Jokers tab

**Files:** Modify `ui/Layout.kt`, `ui/UiRenderer.kt`, `Screen.kt`, `scene/SceneRenderer.kt` (the day dot). Test: `ScreenTest.kt`, `SnapshotTest.kt`.

**Interfaces — Consumes:** Task 2, Task 3 (`JOKER_SPRITES`). **Produces:** `Layout.jokersTab`, `Layout.jokerCards: List<Rect>`, `Layout.openCard: Rect`, `Layout.devJoker`.

- [ ] **Step 1: Failing tests.** The three tabs fit the smallest phone; the cards fit the smallest phone; a tap on Jokers shows the cards; a tap on a face-down card opens nothing; a tap on a face-up card opens it, a tap closes it, and back closes it; the dot after Jokers and by the day while a card is new, gone after the tab shows; the Worlds screen opens on Jokers when only a card is new; the dev Joker button brings the next joker and closes the dev screen; its row fits the smallest phone.
- [ ] **Step 2: Run them.** Expected: FAIL.
- [ ] **Step 3: Implement.** A tab enum in `Screen` in place of `laborsShown`; the cards page and the open card in `UiRenderer`; the dev button in the row of the voices.
- [ ] **Step 4: Snapshots.** `14-jokers`, `14-jokers-small`, `14-card`. Look at them.
- [ ] **Step 5: Run all tests and both builds.** Expected: PASS.

### Task 5: Docs

**Files:** `docs/saved-data.md`, `README.md`, `.claude/skills/new-world/SKILL.md`.

- [ ] **Step 1:** `saved-data.md`: rows for the new keys, their start values, and the joker ids in "Ids that never change". README: the jokers in the `game`, `scene` and `ui` bullets. The new-world skill: the jokers are the same in every world.
- [ ] **Step 2:** Run all tests. Ask before committing.
