# Voices Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Now and then, a god appears as a vision in the sky and asks a question with two answers. The knob answers, and a right answer gives one obol.

**Architecture:** A new `Voices` class in `game/` holds the questions, their order, the count of the player's turns to the next vision, and the vision on screen. `Game` feeds it the player's own turns, holds Sisyphus while a vision asks, reads the answer from the knob, pays the obol and saves it all. The question and the reply use the panel of the wise words through `QuoteBox.announce`. A new `scene/Visions.kt` draws the god and the answers over the scene; `UiRenderer` frames the panel and draws the arrows by the knob. A chime, made in code, plays when a vision comes.

**Tech Stack:** Kotlin Multiplatform, Compose Multiplatform, kotlin.test. All art is drawn in code into the `Pixels` framebuffer.

**Spec:** `docs/voices.md`

## Global Constraints

- Only the player's own turns count toward a vision: `abs(turn) / (2 * PI)` from the knob, without `shop.bonus`, and never the work of the gods.
- The count to the next vision: a random number of turns from 600 to 1800. When the saved key is missing (a new game or an old save): from 300 to 600.
- No vision while `summit != null` (the final climb), at the top, or in the ending. The count does not move then.
- While a vision comes or asks, the knob does not move Sisyphus: no `position`, no `effort`, no work from the knob. The gods keep working.
- When a vision comes, the knob stops at once (no coasting). An answer counts only from a new touch that begins after the answers show. Then 1/8 of a turn picks a side: clockwise (a positive turn) is the right answer, counterclockwise (negative) the left.
- The answers show when the question is fully typed in the panel and the vision is fully in (light 1).
- No answer in 20 seconds of asking: the god's farewell line, and the vision leaves.
- A vision ends without an answer when the app pauses, when the Agora, the Worlds screen or the help opens, when the final climb begins, and when the player moves to another world.
- A right answer: `shop.add(1.0)`, then `onObol(1)`, so the coin sounds and "+1" rises as for any obol. A wrong answer, or none: nothing.
- Fade times: the vision comes in over 1.0 s and leaves over 1.5 s.
- New saved keys: `nextVision`, `visionSeed`, `visionsDrawn`, `rightAnswers`. The count and the order are saved as soon as a vision begins, so a restart never shows the same vision twice.
- Text drawn with `PixelFont` uses only its glyphs. A question or a reply fits 3 lines of 119 pixels; an answer fits 2 lines of 46 pixels (the smallest phone, 135 virtual pixels wide).
- Match the style of the surrounding code: short names, few comments, one-line KDoc in Simple English where the code around it has one.
- Every task ends with `./gradlew :composeApp:jvmTest :composeApp:testAndroidHostTest` passing.
- Do not commit. The user reviews and commits.

## Review Focus

- The player flicks the knob hard just before a vision comes. Expect the knob to stop at once, Sisyphus to stay, and no answer until a new touch. Test: Task 3, `aFlickBeforeAVisionPicksNoAnswer`.
- The app goes to the background in the middle of a vision, and comes back. Expect no dark scene left over, and the knob to push again. Test: Task 3, `pausingEndsTheVision`.
- The player opens the Agora during a vision and buys the 15th item. Expect the vision to end, and no vision in the final climb. Tests: Task 4, `openingTheAgoraEndsTheVision`; Task 3, `theFinalClimbEndsTheVision`.
- An old save from before the voices: no keys. Expect the first vision after 300 to 600 turns, and no crash. Test: Task 3, `anOldSaveMeetsTheVoicesSoon`.
- The smallest phone, with the longest answers on two lines. Expect both answers inside the scene, apart from each other. Test: Task 5, `theLongestAnswersFitTheSmallestPhone`.

---

### Task 1: The questions

**Files:**
- Create: `composeApp/src/commonMain/kotlin/dev/oblac/sisyphus/game/Voices.kt`
- Test: `composeApp/src/commonTest/kotlin/dev/oblac/sisyphus/VoicesTest.kt`
- Modify: `composeApp/src/commonTest/kotlin/dev/oblac/sisyphus/QuotesTest.kt` (use the new `NARROWEST_TEXT` instead of its own)

**Interfaces:**
- Produces:
  - `enum class God(val title: String, val farewell: String) { SPHINX, HERMES, HECATE }` with titles `"the Sphinx"`, `"Hermes"`, `"Hecate"`.
  - `class Question(val god: God, val text: String, val right: String, val wrong: String, val yes: String, val no: String)`: `yes` is the reply to a right answer, `no` to a wrong one.
  - `val RIDDLES: List<Question>` (all `God.SPHINX`) and `val ROADS: List<Question>` (all `God.HECATE`). In this task, each holds the three examples below; Task 7 fills them to 20.
  - `const val NARROWEST_TEXT = 119` and `const val NARROWEST_ANSWER = 46`.
  - `val SAYINGS: List<Quote>`: the wise words Hermes asks about. `val PEOPLE: List<String>`: the authors he can name.
  - `fun hermes(saying: Quote, other: String): Question`: text `"Who said this? \"${saying.text}\""`, right `saying.author`, wrong `other`, yes `"Yes. These are the words of ${saying.author}."`, no `"No. These are the words of ${saying.author}."`.

Rules for `SAYINGS` and `PEOPLE`: an author is a person unless the author is one of these 25, the voices of Sisyphus's world and the proverbs: Sisyphus, the Rock, the Hill, the Sun, the Moon, the Column, Zeus, Hades, Hermes, a Goat, a Bird, Gravity, the Olive Tree, the Wind, the Cypress, the Rain, the Sea, Proverb, Delphic maxim, Japanese proverb, Latin proverb, French proverb, Chinese proverb, Persian proverb, Zen saying. `PEOPLE` holds each person whose name fits 2 lines of `NARROWEST_ANSWER`, once. `SAYINGS` holds each quote of `QUOTES` whose author is in `PEOPLE` and whose Hermes question fits 3 lines of `NARROWEST_TEXT`. Today that is 413 sayings by 65 authors, and 72 people.

The three examples of each list, as the tone for the rest:

| God | Text | Right | Wrong | Yes | No |
|---|---|---|---|---|---|
| Sphinx | What walks on four feet at dawn, on two at noon, and on three at dusk? | A man | A horse | Yes: a man. A child crawls, a man walks, and the old lean on a staff. | No: a man. A child crawls, a man walks, and the old lean on a staff. |
| Sphinx | I have no legs, yet I run to the sea. What am I? | A river | A road | Yes, a river. Even water does not rest. | No, a river. It runs, and it never rests. |
| Sphinx | The more you take, the more you leave behind. What are they? | Steps | Obols | Yes: steps. You leave many on this hill. | No: steps. You leave many on this hill. |
| Hecate | Two roads: one short and easy, one long and steep. Which does a wise man take? | The steep one | The easy one | Yes. The road to virtue is steep at first, then it grows easy. | The easy road is short, but it leads nowhere worth going. |
| Hecate | When does a wise man begin? | Today | Tomorrow | Yes. Tomorrow is only a word. | Today. Tomorrow never comes on its own. |
| Hecate | What does a wise man want? | Little | Much | Yes. He who wants little has much. | Little. He who wants much is always poor. |

Farewells: the Sphinx `"You are silent. I will ask again another day."`, Hermes `"No answer? Then I fly on."`, Hecate `"The roads will wait for you."`

- [ ] **Step 1: Write the failing tests in `VoicesTest`**

```kotlin
private val all = RIDDLES + ROADS + SAYINGS.map { hermes(it, "Seneca") }

@Test fun everyVoiceHasGlyphs()  // every text, right, wrong, yes, no and farewell: PixelFont.hasGlyph for each char
@Test fun everyQuestionAndReplyFitsThePanel()  // text, yes, no, farewell: PixelFont.wrap(it, NARROWEST_TEXT).size <= 3, every line width <= NARROWEST_TEXT
@Test fun everyAnswerFitsItsCorner()  // right, wrong, and every name in PEOPLE: wrap(it, NARROWEST_ANSWER).size <= 2, every line width <= NARROWEST_ANSWER
@Test fun hermesAsksOnlyAboutPeople() {
    assertTrue(SAYINGS.none { it.author == "Sisyphus" || it.author == "Proverb" || it.author == "the Rock" })
    assertTrue(SAYINGS.all { it.author in PEOPLE })
    assertTrue(SAYINGS.size >= 400)
    assertEquals(PEOPLE.size, PEOPLE.toSet().size)
}
@Test fun riddlesBelongToTheSphinxAndRoadsToHecate()  // RIDDLES.all { it.god == God.SPHINX }, ROADS.all { it.god == God.HECATE }
@Test fun noQuestionComesTwice()  // RIDDLES and ROADS: texts are distinct, and right != wrong in every question
```

- [ ] **Step 2: Run them and see them fail**

Run: `./gradlew :composeApp:jvmTest --tests 'dev.oblac.sisyphus.VoicesTest'`
Expected: compile failure, `God`, `RIDDLES` and the rest do not exist.

- [ ] **Step 3: Write `game/Voices.kt` with the interfaces above, and point `QuotesTest` at `NARROWEST_TEXT`**

`SAYINGS` and `PEOPLE` are computed once from `QUOTES` with `PixelFont.wrap` and `PixelFont.width`, by the rules above.

- [ ] **Step 4: Run the tests and see them pass**

Run: `./gradlew :composeApp:jvmTest :composeApp:testAndroidHostTest`
Expected: PASS.

---

### Task 2: The order and the count

**Files:**
- Modify: `composeApp/src/commonMain/kotlin/dev/oblac/sisyphus/game/Voices.kt`
- Test: `composeApp/src/commonTest/kotlin/dev/oblac/sisyphus/VoicesTest.kt`

**Interfaces:**
- Consumes: `God`, `Question`, `RIDDLES`, `ROADS`, `SAYINGS`, `PEOPLE`, `hermes` from Task 1.
- Produces:
  - `class Vision(val question: Question, val rightOnLeft: Boolean)` with `enum class Phase { COMING, ASKING, LEAVING }`, `var phase: Phase` (private set, starts `COMING`), `var light: Double` (private set, 0 to 1, starts 0), `val left: String`, `val right: String` (the answers on each side), `var asked: Double` (private set, seconds of `ASKING`), `val over: Boolean` (true when `LEAVING` and `light` is 0), `fun ask()` (to `ASKING`), `fun leave()` (to `LEAVING`), and `fun update(dt: Double)`: `light` rises by `dt / 1.0` in `COMING` up to 1, falls by `dt / 1.5` in `LEAVING` down to 0, and `asked` grows by `dt` in `ASKING`.
  - `class Voices(private val seed: Int, drawn: Int, count: Double?, rightAnswers: Int, private val random: Random)`:
    - `var drawn: Int` (private set): the visions so far.
    - `var count: Double` (private set): the player's turns until the next vision. A null `count` starts it from 300 to 600.
    - `var rightAnswers: Int` (private set).
    - `var vision: Vision?` (private set).
    - `fun visionAt(k: Int): Vision`: the k-th vision, the same for the same `seed` and `k`.
    - `fun pushed(turns: Double): Vision?`: counts `count` down. When it reaches 0, the next vision begins: `vision = visionAt(drawn)`, `drawn++`, `count` from 600 to 1800. Returns the new vision, or null.
    - `fun summon(): Vision`: the next vision at once, for the dev screen. The count starts again too.
    - `fun answer(right: Boolean)`: counts a right answer, and the vision leaves.
    - `fun end()`: no vision any more.
  - Constants in `Voices`' companion: `FIRST = 300.0..600.0`, `NEXT = 600.0..1800.0`, `ASK_TIME = 20.0`, `ANSWER_TURN = 1.0 / 8`.

`visionAt(k)` decides the god, the question and the side:

```kotlin
val round = k / God.entries.size
val god = God.entries.shuffled(Random(seed + round * 7919))[k % God.entries.size]
// the god's own list, one shuffled round after another, like QuoteBox.quoteAt
val list = when (god) { SPHINX -> RIDDLES; HECATE -> ROADS; HERMES -> SAYINGS }
val order = list.indices.shuffled(Random(seed + god.ordinal * 104729 + round / list.size * 7919))
val i = order[round % list.size]
// Hermes names another person, chosen with Random(seed xor k) from PEOPLE without the right author
// the right answer is on the left when Random(seed + k * 31).nextBoolean()
```

- [ ] **Step 1: Write the failing tests in `VoicesTest`**

```kotlin
@Test fun aNewGameMeetsTheFirstVoiceSoon()  // Voices(1, 0, null, 0, Random(2)).count in 300.0..600.0
@Test fun theCountRunsDownWithTurns() {
    val v = Voices(1, 0, 10.0, 0, Random(2))
    assertNull(v.pushed(9.5))
    assertNotNull(v.pushed(1.0))
    assertEquals(1, v.drawn)
    assertTrue(v.count in 600.0..1800.0)
}
@Test fun theGodsComeInTurn()  // for rounds 0..9: visionAt(3r), visionAt(3r+1), visionAt(3r+2) hold all three gods
@Test fun aRiddleComesAgainOnlyAfterAllRiddles()  // the Sphinx's first RIDDLES.size visions have distinct texts
@Test fun theSameDrawGivesTheSameVision()  // two Voices with the same seed: visionAt(k).question.text and rightOnLeft equal for k in 0..30
@Test fun hermesNamesAnotherPerson()  // every Hermes vision in 0..60: wrong != right, wrong in PEOPLE
@Test fun aVisionComesInAsksAndLeaves() {
    val v = Vision(RIDDLES[0], rightOnLeft = true)
    v.update(0.5); assertEquals(0.5, v.light, 1e-9)
    v.update(1.0); assertEquals(1.0, v.light, 1e-9)
    v.ask(); v.update(3.0); assertEquals(3.0, v.asked, 1e-9)
    v.leave(); v.update(1.5); assertTrue(v.over)
    assertEquals(RIDDLES[0].right, v.left)
}
```

- [ ] **Step 2: Run them and see them fail**

Run: `./gradlew :composeApp:jvmTest --tests 'dev.oblac.sisyphus.VoicesTest'`
Expected: compile failure, `Voices` and `Vision` do not exist.

- [ ] **Step 3: Add `Vision` and `Voices` to `game/Voices.kt`**

- [ ] **Step 4: Run the tests and see them pass**

Run: `./gradlew :composeApp:jvmTest :composeApp:testAndroidHostTest`
Expected: PASS.

---

### Task 3: The game hears the voices

**Files:**
- Modify: `composeApp/src/commonMain/kotlin/dev/oblac/sisyphus/game/Game.kt`
- Test: `composeApp/src/commonTest/kotlin/dev/oblac/sisyphus/GameTest.kt`

**Interfaces:**
- Consumes: `Voices`, `Vision`, `Question`, `God` from Tasks 1 and 2.
- Produces:
  - In `Knob`: `var grabs: Int` (private set), counting every `grab`; and `fun stop()`, which sets `velocity` and the pending turn to 0.
  - In `Game`: `val voices: Voices`; `val vision: Vision? get() = voices.vision`; `var onVision: () -> Unit = {}` (a vision comes); `fun endVision()` (ends a vision without an answer, if one comes or asks); `fun summonVision()` (for dev builds).
  - Saved keys: `nextVision` (`voices.count`), `visionSeed` (start value: the game's `seed`), `visionsDrawn`, `rightAnswers`.

How `update(dt)` changes, in this order:

1. `turn = knob.update(dt)`, as now.
2. While a vision is `COMING` or `ASKING`, the turns that move Sisyphus are 0. The gods' work goes on.
3. With no vision, and `summit == null` and not `atTop`: `voices.pushed(abs(turn) / (2 * PI))`. A new vision: `knob.stop()`, `quotes.announce(Quote(question.text, question.god.title))`, `onVision()`, and save at once.
4. `COMING`: when `light` is 1 and the panel shows the question fully typed (`quotes.quote` is the question and `quotes.phase == SHOWING`), `ask()`, and remember `knob.grabs`.
5. `ASKING`: once `knob.grabs` is greater than the remembered number, add up the signed turns of the knob, in turns, from the start of the newest touch. At `+ANSWER_TURN` the right side is picked, at `-ANSWER_TURN` the left. A pick: right means `shop.add(1.0)` and `onObol(1)`; then `voices.answer(right)` and `quotes.announce(Quote(if (right) yes else no, god.title))`. After `ASK_TIME` seconds with no pick: `quotes.announce(Quote(god.farewell, god.title))` and `leave()`.
6. `vision.update(dt)`. When it is `over`, `voices.end()`.

`pause()`, `beginClimb()` (when the final climb begins) and `begin()` (a new climb or another world) call `endVision()`.

- [ ] **Step 1: Write the failing tests in `GameTest`**

Use `MemoryStore(mapOf("nextVision" to 2.0))` to bring a vision soon, and the file's `turn` helper, which grabs the knob anew each call.

```kotlin
@Test fun aVisionComesWhenTheCountRunsOut()  // turn(2.5 * 2 * PI): vision != null, quotes.quote.text == vision.question.text
@Test fun theBonusDoesNotCountTowardAVision()  // rock tier 3 and nextVision 10: after 5 turns, voices.count == 5 (+-0.01)
@Test fun theGodsDoNotBringAVision()  // Thanatos, nextVision 1, a day away through pause()/resume(): vision == null, voices.count == 1
@Test fun noVisionInTheFinalClimb()  // nearTheTop(), nextVision 1, turn 1.5 turns: vision == null, voices.count == 1
@Test fun aVisionHoldsSisyphus()  // after the vision comes: position, effort and shop.obols stay while turning 3 more turns
@Test fun aFlickBeforeAVisionPicksNoAnswer()  // flick (grab, fast drag, release), the vision comes: knob.velocity == 0; 5 s of update: vision.phase == ASKING and no pick
@Test fun anAnswerNeedsANewTouch()  // the vision asks; the same touch turns 1/4 turn more: no pick; a new touch turns 1/8: a pick
@Test fun aRightAnswerGivesOneObol()  // turn toward the right side: obols + 1, voices.rightAnswers == 1, onObol got 1, quotes.quote.text == question.yes
@Test fun aWrongAnswerGivesNothing()  // turn toward the wrong side: obols the same, quotes.quote.text == question.no
@Test fun noAnswerSaysFarewell()  // 20 s of asking: phase LEAVING, quotes.quote.text == god.farewell
@Test fun theKnobPushesAgainAfterTheAnswer()  // after a pick, turning moves position
@Test fun pausingEndsTheVision()  // pause() during ASKING: vision == null; resume(); turning moves position
@Test fun theFinalClimbEndsTheVision()  // all but one item bought, a vision asks, buy the last: vision == null
@Test fun movingEndsTheVision()  // moveTo(Place.ETNA) during a vision: vision == null
@Test fun theVoicesAreSaved()  // after a vision begins: a new Game on the same store has the same voices.drawn and voices.count, and no vision
@Test fun theCountStaysAtTheTopAndInAnotherWorld()  // startOver() and moveTo(Place.ETNA) keep voices.count, voices.drawn and voices.rightAnswers
@Test fun anOldSaveMeetsTheVoicesSoon()  // a store with effort but no voice keys: voices.count in 300.0..600.0
```

- [ ] **Step 2: Run them and see them fail**

Run: `./gradlew :composeApp:jvmTest --tests 'dev.oblac.sisyphus.GameTest'`
Expected: compile failure, `voices`, `vision`, `endVision` do not exist.

- [ ] **Step 3: Add the voices to `Knob` and `Game` as described**

- [ ] **Step 4: Run the tests and see them pass**

Run: `./gradlew :composeApp:jvmTest :composeApp:testAndroidHostTest`
Expected: PASS.

---

### Task 4: The panel, the arrows and the dev button

**Files:**
- Modify: `composeApp/src/commonMain/kotlin/dev/oblac/sisyphus/ui/UiRenderer.kt`
- Modify: `composeApp/src/commonMain/kotlin/dev/oblac/sisyphus/ui/Layout.kt`
- Modify: `composeApp/src/commonMain/kotlin/dev/oblac/sisyphus/Screen.kt`
- Test: `composeApp/src/commonTest/kotlin/dev/oblac/sisyphus/ScreenTest.kt`

**Interfaces:**
- Consumes: `Game.vision`, `Game.endVision()`, `Game.summonVision()`, `Vision.Phase` from Task 3.
- Produces:
  - `UiRenderer.quote(px: Pixels, r: Rect, box: QuoteBox, clock: Double, voice: Boolean = false)`: with `voice`, a 1-pixel frame in `TERRA_DARK` around the panel, 2 pixels outside it to the left and right and 1 pixel above and below, and the author line in `TERRA_LIGHT` instead of `TERRA`.
  - `UiRenderer.knob`: while the vision is `ASKING`, two curved arrows in `TERRA_LIGHT` at `knobR + 8`: the left one from `-PI / 2 - 0.95` to `-PI / 2 - 1.55`, the right one from `-PI / 2 + 0.95` to `-PI / 2 + 1.55`. The private `arrow` turns its head the way the angle goes, from `from` to `to`.
  - `Layout.devVision`: a button `"Vision"` next to `devWorlds`, in the same row.

`Screen.render` passes `voice = true` while the vision comes or asks. `Screen.press` calls `game.endVision()` when it opens the Agora, the Worlds screen or the help. The dev button calls `game.summonVision()` and closes the dev screen and the Agora.

- [ ] **Step 1: Write the failing tests in `ScreenTest`**

```kotlin
@Test fun aVoiceFramesThePanel()  // a summoned vision, updated 3 s: the pixel at (quote.x - 2, quote.y + 5) is TERRA_DARK; with no vision it is the background
@Test fun openingTheAgoraEndsTheVision()  // vision asks, tap the coin: game.vision == null
@Test fun openingTheHelpEndsTheVision()
@Test fun theDevScreenSummonsAVision()  // dev screen, tap devVision: game.vision != null, agoraOpen == false
@Test fun theVisionButtonFitsTheSmallestPhone()  // devVision.right <= the right edge of the Agora rows on smallest()
```

- [ ] **Step 2: Run them and see them fail**

Run: `./gradlew :composeApp:jvmTest --tests 'dev.oblac.sisyphus.ScreenTest'`
Expected: FAIL, `devVision` does not exist.

- [ ] **Step 3: Draw the frame and the arrows, add the button, end the vision when a screen opens**

- [ ] **Step 4: Run the tests and see them pass**

Run: `./gradlew :composeApp:jvmTest :composeApp:testAndroidHostTest`
Expected: PASS.

---

### Task 5: The vision in the scene

**Files:**
- Create: `composeApp/src/commonMain/kotlin/dev/oblac/sisyphus/scene/Visions.kt`
- Modify: `composeApp/src/commonMain/kotlin/dev/oblac/sisyphus/Screen.kt` (`render` draws the vision on `scene` before it goes to the screen)
- Test: `composeApp/src/commonTest/kotlin/dev/oblac/sisyphus/ScreenTest.kt`, `composeApp/src/jvmTest/kotlin/dev/oblac/sisyphus/SnapshotTest.kt`

**Interfaces:**
- Consumes: `Vision`, `God` from Task 2.
- Produces:
  - Three sprites made with `sprite(palette, art)`, about 25 x 15, drawn at twice their size: the Sphinx (a woman's head, a lion's body and folded wings, facing left), Hermes (the winged hat, a cloak, the caduceus) and Hecate (a woman with a torch in each hand).
  - `fun drawVision(px: Pixels, vision: Vision)`, on the pixels of the scene:
    - Every pixel moves toward `rgb(0x101828)` by `0.6 * vision.light`.
    - The god stands centered at 30% of the scene's height. Around it, a soft dithered light (`bayer`) of `rgb(0xFFF2C8)`; its edge traced in that color; the figure mixed at 80% over the dark scene. All of it scaled by `vision.light`.
    - While `ASKING`: the left answer in the bottom-left corner and the right answer in the bottom-right corner, 6 pixels from the edges, in `CREAM` with a dark outline (`PixelFont.drawOutlined`), wrapped to `(px.width - 12) / 2 - 8` pixels, at most 2 lines, with a small terra triangle beside each that points outward.

- [ ] **Step 1: Write the failing tests**

```kotlin
// ScreenTest
@Test fun theVisionDarkensTheScene()  // the mean brightness of the scene rect with a vision fully in is below 60% of the same frame without it
@Test fun theLongestAnswersFitTheSmallestPhone()  // on smallest(): a vision whose answers are "Marcus Aurelius" and "Publilius Syrus" (Vision(hermes(...), ...)) draws cream pixels only inside the scene rect, and the left and right answers do not overlap
// SnapshotTest
@Test fun voices()  // for each Place: noon, a summoned vision updated 4 s, written as voice-<n>.png; and the same on the smallest phone
```

- [ ] **Step 2: Run them and see them fail**

Run: `./gradlew :composeApp:jvmTest --tests 'dev.oblac.sisyphus.ScreenTest'`
Expected: FAIL, the scene has no vision yet.

- [ ] **Step 3: Write `scene/Visions.kt` and call it from `Screen.render`**

- [ ] **Step 4: Run the tests and look at the snapshots**

Run: `./gradlew :composeApp:jvmTest :composeApp:testAndroidHostTest`
Expected: PASS. Open `composeApp/build/snapshots/voice-*.png`: each god is clear in the dark sky of all three worlds, and the answers are easy to read on the smallest phone.

---

### Task 6: The chime

**Files:**
- Create: `composeApp/src/commonMain/kotlin/dev/oblac/sisyphus/audio/Chime.kt`
- Modify: `composeApp/src/commonMain/kotlin/dev/oblac/sisyphus/Platform.kt`, `composeApp/src/commonMain/kotlin/dev/oblac/sisyphus/App.kt`
- Modify: `composeApp/src/androidMain/kotlin/dev/oblac/sisyphus/TrackSound.kt`, `composeApp/src/iosMain/kotlin/dev/oblac/sisyphus/EngineSound.kt`, `composeApp/src/jvmMain/kotlin/dev/oblac/sisyphus/ClipSound.kt`
- Test: `composeApp/src/commonTest/kotlin/dev/oblac/sisyphus/SoundTest.kt`

**Interfaces:**
- Consumes: `Game.onVision` from Task 3.
- Produces:
  - `fun chimeSamples(): FloatArray`: 1.6 s of mono samples at 44.1 kHz. A soft bell: partials at 880 Hz, 2.76 x and 5.4 x that, with amplitudes 1, 0.5 and 0.25 and decay times 1.2 s, 0.6 s and 0.3 s, a 10 ms rise, and a peak of 0.5.
  - `Sound.load(click, ui, win, fanfare, coin, chime: FloatArray)`, `fun chime() {}` in `Sound`, and `fun Sound.vision(on: Boolean)`, which plays it unless the sound is off.
  - `App` loads the chime and sets `game.onVision = { sound.vision(game.soundOn) }`, and clears it as it clears `onTick`.
  - Each platform keeps the chime and plays it as it plays the coin.

- [ ] **Step 1: Write the failing test in `SoundTest`**

```kotlin
@Test fun theChimeIsSoftAndFadesOut() {
    val s = chimeSamples()
    assertEquals((1.6 * 44100).toInt(), s.size)
    assertEquals(0.5f, s.maxOf { abs(it) }, 0.01f)
    assertTrue(abs(s[0]) < 0.01f)
    assertTrue(s.takeLast(441).all { abs(it) < 0.01f })
}
@Test fun theVisionIsSilentWhenTheSoundIsOff()  // a recording Sound: vision(false) plays nothing, vision(true) plays the chime once
```

- [ ] **Step 2: Run it and see it fail**

Run: `./gradlew :composeApp:jvmTest --tests 'dev.oblac.sisyphus.SoundTest'`
Expected: compile failure, `chimeSamples` does not exist.

- [ ] **Step 3: Make the chime, and play it on every platform**

- [ ] **Step 4: Run the tests and build every platform**

Run: `./gradlew :composeApp:jvmTest :composeApp:testAndroidHostTest :androidApp:assembleDebug :composeApp:compileKotlinIosSimulatorArm64`
Expected: PASS and BUILD SUCCESSFUL.

---

### Task 7: The riddles and the crossroads

**Files:**
- Modify: `composeApp/src/commonMain/kotlin/dev/oblac/sisyphus/game/Voices.kt`

**Interfaces:**
- Consumes: `RIDDLES`, `ROADS` from Task 1, and the tests of Task 1.

Write 17 more riddles and 17 more crossroads, so that each list has 20. They may be written for the game. Each keeps the tone of the examples in Task 1: calm, warm, now and then gently witty, about the hill, the rock, nature, the seasons, the gods and the wise life. The right answer is the only reasonable answer. The wrong answer sounds possible at first, but it is wrong. A reply gives the right answer in a new way, not as a lesson. No two questions are alike, and none asks about a quote of the panel.

- [ ] **Step 1: Write the 34 questions**

- [ ] **Step 2: Run the tests of Task 1**

Run: `./gradlew :composeApp:jvmTest --tests 'dev.oblac.sisyphus.VoicesTest'`
Expected: PASS. Fix any text that is too long or uses a missing glyph by rewording it.

- [ ] **Step 3: The user reads the 40 questions and their replies**

Show them as a table: god, question, right, wrong, yes, no. Change what the user asks for, and run the tests again.

---

### Task 8: The saved data and the docs

**Files:**
- Modify: `docs/saved-data.md`
- Modify: `docs/voices.md` (only if the build changed something the doc says)

- [ ] **Step 1: Add the four keys to the table of `docs/saved-data.md`**

| Key | What it holds | At the top | In another world |
|---|---|---|---|
| `nextVision` | The player's turns until the next vision. | Kept | Kept |
| `visionSeed` | The number that sets the order of the visions. | Kept | Kept |
| `visionsDrawn` | How many visions came so far. | Kept | Kept |
| `rightAnswers` | How many questions the player answered right. | Kept | Kept |

Start values: `nextVision` from 300 to 600, at random; `visionSeed` the seed of the game; `visionsDrawn` and `rightAnswers` 0.

- [ ] **Step 2: Run everything**

Run: `./gradlew :composeApp:jvmTest :composeApp:testAndroidHostTest :androidApp:assembleDebug :composeApp:compileKotlinIosSimulatorArm64`
Expected: PASS and BUILD SUCCESSFUL.

- [ ] **Step 3: See it on the simulator**

Build and run the iOS app on a simulator. On the dev screen, tap Vision: the scene goes dark, the god comes with a chime, the question types out, and the answers show. Lift the finger, touch the knob again, and turn an eighth of a turn to the right side: one obol comes. Do not use `just run` for this: the desktop game has a real save.
