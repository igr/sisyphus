# Voices of Each World Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Each world has its own two gods who ask riddles from one shared pool of 60, and Hermes asks "Who said this?" in every world.

**Architecture:** The god leaves the question: a `Question` is only its texts, and the `Vision` holds the god who asks it. `God` gets the world it belongs to (none for Hermes), and `Voices` takes the world to choose the three gods of a round. One `RIDDLES` pool replaces the riddles and the crossroads; the two gods of the world take riddles from it in turn. `scene/Visions.kt` gets four new sprites.

**Tech Stack:** Kotlin Multiplatform, Compose Multiplatform, kotlin.test. All art is drawn in code into the `Pixels` framebuffer.

**Spec:** `docs/voices.md`

## Global Constraints

- The gods: Corinth has the Sphinx and Hecate, Mount Etna has Hephaestus and Empedocles, The Underworld has Charon and Persephone. Hermes comes in every world and asks only "Who said this?".
- One pool of 60 riddles for all worlds: the 20 riddles, the 20 crossroads, and 20 new ones. No question or reply names or describes the god who asks it.
- A riddle does not come again until all 60 have come, in all worlds together. A saying does not come again until all of Hermes' sayings have come.
- The saved keys stay the same: `nextVision`, `visionSeed`, `visionsDrawn`, `rightAnswers`. A move to another world keeps them.
- Text drawn with `PixelFont` uses only its glyphs. A question or a reply, with "Correct. " or "Wrong. " before it, fits 3 lines of 119 pixels; an answer fits 2 lines of 46 pixels.
- Match the style of the surrounding code: short names, few comments, one-line KDoc in Simple English where the code around it has one.
- Every task ends with `./gradlew :composeApp:jvmTest :composeApp:testAndroidHostTest` passing.
- Do not commit. The user reviews and commits.

## Review Focus

- A player moves to Mount Etna while a vision is coming. Expect the vision to end with its god's farewell, and the next vision from a god of Etna. Test: Task 1, `aMoveBringsTheGodsOfTheNewWorld`.
- A player moves between worlds many times. Expect no riddle twice before all 60 have come. Test: Task 1, `noRiddleComesAgainBeforeAllHaveComeInAllWorlds`.
- A save from before this change, in the middle of the order. Expect no crash, and visions that go on. Test: Task 1, `anOlderSaveGoesOnAsking`.
- The longest new title, "Persephone", and the longest farewell on the smallest phone. Expect them to fit. Test: Task 2, the fit tests.
- A new riddle whose wrong answer is also right. Expect none: the riddles are read once more before the build ends.

---

### Task 1: The gods of each world

**Files:**
- Modify: `game/Voices.kt`, `game/Game.kt`, `scene/Visions.kt` (the god comes from the vision)
- Test: `VoicesTest`, `GameTest`, `ScreenTest`

**Interfaces:**
- Produces: `enum class God(val title: String, val farewell: String, val place: Place?)` with SPHINX, HERMES, HECATE, HEPHAESTUS, EMPEDOCLES, CHARON, PERSEPHONE; `fun godsOf(place: Place): List<God>`; `class Question(val text, val right, val wrong, val yes, val no)` with `reply(right)`; `class Vision(val question: Question, val god: God, val rightOnLeft: Boolean)`; `val RIDDLES: List<Question>` (the 40 for now; `ROADS` goes); `Voices.visionAt(k: Int, place: Place)`, `Voices.pushed(turns: Double, place: Place)`, `Voices.summon(place: Place)`.

- [ ] **Step 1: Write the failing tests:**
  - `VoicesTest.eachWorldHasHermesAndTwoGodsOfItsOwn`: `godsOf` gives [SPHINX, HERMES, HECATE], [HEPHAESTUS, HERMES, EMPEDOCLES] and [CHARON, HERMES, PERSEPHONE].
  - `VoicesTest.hermesAsksTheQuotesAndTheOthersTheRiddles`: in each world, for the first 30 visions, Hermes asks only "Who said this?", and every other question is in `RIDDLES`.
  - `VoicesTest.noRiddleComesAgainBeforeAllHaveComeInAllWorlds`: the riddles of visions 0 to many, with the world changing every 7 visions, show each riddle once before any comes again.
  - `GameTest.aMoveBringsTheGodsOfTheNewWorld`: a vision summoned on Mount Etna comes from Hephaestus, Empedocles or Hermes.
  - `GameTest.anOlderSaveGoesOnAsking`: a store with `visionsDrawn` 7 and a seed brings a vision on Etna without a crash.
  - Change the existing tests to read the god from the vision, and the pools from `RIDDLES`.
- [ ] **Step 2: Run** `./gradlew :composeApp:jvmTest --tests 'dev.oblac.sisyphus.VoicesTest'`. Expected: compile failure.
- [ ] **Step 3: Implement.** Hermes asks the saying of number `round`; the two other gods of a round ask the riddles of numbers `2 * round` and `2 * round + 1`, in their order in the round. Each pass through a pool has its own shuffle, from the seed. `Game` passes `place`, and reads the title and the farewell from `vision.god`. The four new gods use the sprite of the Sphinx until Task 3.
- [ ] **Step 4: Run** the suite. Expected: PASS.

### Task 2: The riddles

**Files:**
- Modify: `game/Voices.kt`
- Test: `VoicesTest`

- [ ] **Step 1: Write the failing tests:**
  - `VoicesTest.sixtyRiddles`: `RIDDLES.size == 60`, no two questions alike.
  - `VoicesTest.noRiddleNamesAGod`: no question or reply holds the title of a god.
  - The fit and glyph tests read every riddle with its reply, every farewell and every title.
- [ ] **Step 2: Run** the VoicesTest. Expected: `sixtyRiddles` fails.
- [ ] **Step 3: Write.** Reword the replies that only fit their old god, such as "A torch. I have two, if you need one.". Add 20 riddles: classic riddles with answers from the world of the myths, and questions about the myths. Write the farewells of the four new gods.
- [ ] **Step 4: Run** the suite. Expected: PASS. Rebuild the table of all 60 riddles for the user.

### Task 3: The pictures

**Files:**
- Modify: `scene/Visions.kt`
- Test: `jvmTest/.../SnapshotTest.kt`, `ScreenTest`

- [ ] **Step 1: Write the failing test:** `ScreenTest.eachGodHasItsOwnPicture`: the seven sprites differ from each other.
- [ ] **Step 2: Run** it. Expected: FAIL, the new gods share the Sphinx.
- [ ] **Step 3: Draw** Hephaestus (a bearded smith with his hammer), Empedocles (a philosopher with a bronze sandal), Charon (a hooded ferryman with his pole) and Persephone (a crown, and a pomegranate in her hand), 25 by 15 like the others. The snapshot test writes a vision of each god.
- [ ] **Step 4: Run** the suite. Expected: PASS. Look at the seven snapshots.

### Task 4: The world definition

**Files:**
- Modify: `.claude/skills/new-world/SKILL.md`, `docs/shop.md` (the tables of the worlds)

- [ ] **Step 1:** Add a row "Voices" to the parts of a world in the skill: for Corinth, "the Sphinx and Hecate, with Hermes"; the code is `God` and `godsOf` in `game/Voices.kt`, and the sprites in `scene/Visions.kt`. Add a step: choose two gods of the world, write their farewells, and draw their sprites; the riddles stay shared.
- [ ] **Step 2:** Add the gods of each world to the tables of Mount Etna and The Underworld in `docs/shop.md`.
- [ ] **Step 3: Run** the suite, then `./gradlew :androidApp:assembleDebug :composeApp:compileKotlinIosSimulatorArm64`. Expected: PASS and BUILD SUCCESSFUL.
