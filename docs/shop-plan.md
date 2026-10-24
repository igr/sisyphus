# Obols and the Shop Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Players earn obols by pushing and spend them in a shop of 15 items: rocks, helpers, blessings and beauty.

**Architecture:** A new `Shop` class in `game/` holds the catalog, what the player owns, and the obol balance. `Game` feeds it work from the knob and from helpers (also for time away), limits the sun to 1.5×, and saves it. `Screen` adds a coin button and the Agora, a full-screen shop that also holds the sound setting; `UiRenderer` draws them. `SceneRenderer` and `World` draw the rock looks and the beauty items.

**Tech Stack:** Kotlin Multiplatform, Compose Multiplatform, kotlin.test. All art is drawn in code into the `Pixels` framebuffer.

**Spec:** `docs/shop.md`

## Global Constraints

- Prices, effects and names exactly as in the table of Task 1. Total price: 87 obols.
- 720 units of work earn 1 obol; blessings set it to 504, 360 and 216.
- One knob turn is 1 unit of work times `bonus` (rock × helper beside him, 1.0 to 6.0).
- The sun and the walk move by `min(bonus, 1.5)` turns per knob turn. Constant `SUN_CAP = 1.5`.
- Helpers on their own: 6, 12, 24 turns an hour. They are not multiplied by `bonus`. They move the sun and earn obols; they do not move the walk (`position`), do not count as pushing (`moved`), and do not reset `stillFor`.
- Time away has no limit. A missing or future save time gives no time away.
- Beauty items add up. Left-over obols stay.
- No in-app purchases, no ads, no network.
- Text drawn with `PixelFont` uses only its glyphs. There is no `+` and no `×`: write `x`.
- Match the style of the surrounding code: short names, few comments, one-line KDoc where the code around it has one.
- Every task ends with `./gradlew :composeApp:jvmTest :composeApp:testAndroidHostTest` passing.
- Do not commit. The user reviews and commits.

## Review Focus

- Upgrading from 1.1: no `savedAt` and no shop keys in the store. Expect an empty shop, 0 obols, and no time away. Test: Task 3, `anOldSaveWithoutATimeGetsNoAwayTime`.
- The device clock moves back. Expect no time away and nothing lost. Test: Task 3, `aClockMovedBackGivesNothing`.
- A year away with Thanatos and Plutus. Expect exact numbers and a message that still fits the quote panel. Test: Task 3, `aYearAwayStaysSane`.
- The smallest phone, 135 virtual pixels wide. Expect the coin button to win over the knob and all Agora text to fit. Tests: Task 4, `theCoinWinsOverTheKnobOnTheSmallestPhone`, `everyItemFitsItsRowOnTheSmallestPhone`.
- A player with enough obols for a whole line taps once. Expect exactly one item bought. Test: Task 4, `oneTapBuysOneItem`.

---

### Task 1: The shop catalog and its rules

**Files:**
- Create: `composeApp/src/commonMain/kotlin/dev/oblac/sisyphus/game/Shop.kt`
- Test: `composeApp/src/commonTest/kotlin/dev/oblac/sisyphus/ShopTest.kt`

**Interfaces:**
- Produces:
  - `enum class Line { ROCK, BESIDE, ALONE, BLESSING, BEAUTY }` (rows in this order).
  - `class Item(val line: Line, val tier: Int, val name: String, val note: String, val price: Int, val effect: Double)`.
  - `val ITEMS: List<Item>`: 15 items, in `Line` order, tiers 1 to 3.
  - `const val SUN_CAP = 1.5`, `const val WORK_PER_OBOL = 720.0`.
  - `class Shop(tiers: Map<Line, Int> = emptyMap(), obols: Double = 0.0)` with:
    - `var obols: Double` (private set);
    - `fun tier(line: Line): Int` (0 to 3);
    - `fun next(line: Line): Item?` (null when the line is complete);
    - `fun buy(line: Line): Boolean` (true when bought);
    - `val bonus: Double`, `val helpersPerHour: Double`, `val workPerObol: Double`;
    - `val helper: Item?` (the owned ALONE item);
    - `fun addWork(turns: Double)`.

The catalog. The names and notes are the text the shop shows.

| Line | Tier | Name | Note | Price | Effect |
|---|---|---|---|---|---|
| ROCK | 1 | River stone | turns count 1.5x | 2 | 1.5 |
| ROCK | 2 | Pumice | turns count 2x | 5 | 2.0 |
| ROCK | 3 | Sphere of Daedalus | turns count 3x | 9 | 3.0 |
| BESIDE | 1 | Merope | helps push 1.25x | 3 | 1.25 |
| BESIDE | 2 | Bellerophon | helps push 1.5x | 6 | 1.5 |
| BESIDE | 3 | Heracles | helps push 2x | 10 | 2.0 |
| ALONE | 1 | A goat | 6 pushes an hour | 1 | 6.0 |
| ALONE | 2 | Pan | 12 pushes an hour | 4 | 12.0 |
| ALONE | 3 | Thanatos | 24 pushes an hour | 8 | 24.0 |
| BLESSING | 1 | Purse of Hermes | obol per 7 days | 2 | 504.0 |
| BLESSING | 2 | Gift of Tyche | obol per 5 days | 6 | 360.0 |
| BLESSING | 3 | Wealth of Plutus | obol per 3 days | 10 | 216.0 |
| BEAUTY | 1 | Dolphins | leap in the sea | 3 | 0.0 |
| BEAUTY | 2 | Pegasus | flies by | 7 | 0.0 |
| BEAUTY | 3 | The Pleiades | seven stars at night | 11 | 0.0 |

- [ ] **Step 1: Write the failing tests** in `ShopTest.kt`:

```kotlin
class ShopTest {
    @Test
    fun theShopHasFiveLinesOfThree() {
        assertEquals(15, ITEMS.size)
        for (line in Line.entries) assertEquals(listOf(1, 2, 3), ITEMS.filter { it.line == line }.map { it.tier })
        assertEquals(87, ITEMS.sumOf { it.price })
    }

    @Test
    fun pricesAndEffectsFollowTheDesign() {
        fun of(line: Line) = ITEMS.filter { it.line == line }.map { it.price to it.effect }
        assertEquals(listOf(2 to 1.5, 5 to 2.0, 9 to 3.0), of(Line.ROCK))
        assertEquals(listOf(3 to 1.25, 6 to 1.5, 10 to 2.0), of(Line.BESIDE))
        assertEquals(listOf(1 to 6.0, 4 to 12.0, 8 to 24.0), of(Line.ALONE))
        assertEquals(listOf(2 to 504.0, 6 to 360.0, 10 to 216.0), of(Line.BLESSING))
        assertEquals(listOf(3, 7, 11), ITEMS.filter { it.line == Line.BEAUTY }.map { it.price })
    }

    @Test
    fun eachLineIsBoughtInOrder() {
        val shop = Shop(obols = 20.0)
        assertEquals(1, shop.next(Line.ROCK)?.tier)
        assertTrue(shop.buy(Line.ROCK))
        assertEquals(1, shop.tier(Line.ROCK))
        assertEquals(18.0, shop.obols, 1e-9)
        assertEquals(2, shop.next(Line.ROCK)?.tier)
    }

    @Test
    fun nothingIsBoughtWithoutEnoughObols() {
        val shop = Shop(obols = 1.99)
        assertFalse(shop.buy(Line.ROCK))
        assertEquals(0, shop.tier(Line.ROCK))
        assertEquals(1.99, shop.obols, 1e-9)
    }

    @Test
    fun aCompleteLineSellsNothingMore() {
        val shop = Shop(tiers = mapOf(Line.ROCK to 3), obols = 100.0)
        assertNull(shop.next(Line.ROCK))
        assertFalse(shop.buy(Line.ROCK))
    }

    @Test
    fun theRockAndTheHelperBesideHimMultiply() {
        assertEquals(1.0, Shop().bonus, 1e-9)
        assertEquals(1.5 * 1.25, Shop(tiers = mapOf(Line.ROCK to 1, Line.BESIDE to 1)).bonus, 1e-9)
        assertEquals(6.0, Shop(tiers = mapOf(Line.ROCK to 3, Line.BESIDE to 3)).bonus, 1e-9)
    }

    @Test
    fun workTurnsIntoObols() {
        val shop = Shop()
        shop.addWork(720.0)
        assertEquals(1.0, shop.obols, 1e-9)
        val blessed = Shop(tiers = mapOf(Line.BLESSING to 3))
        blessed.addWork(216.0)
        assertEquals(1.0, blessed.obols, 1e-9)
    }

    @Test
    fun helpersOnTheirOwnPushByTheHour() {
        assertEquals(0.0, Shop().helpersPerHour)
        assertNull(Shop().helper)
        val shop = Shop(tiers = mapOf(Line.ALONE to 3))
        assertEquals(24.0, shop.helpersPerHour)
        assertEquals("Thanatos", shop.helper?.name)
    }
}
```

- [ ] **Step 2: Run** `./gradlew :composeApp:jvmTest --tests 'dev.oblac.sisyphus.ShopTest'`. Expected: compile failure, `Shop` and `ITEMS` are unresolved.
- [ ] **Step 3: Implement** `Shop.kt` with the catalog table above and the `Shop` class. With nothing owned, `bonus` is 1.0, `helpersPerHour` is 0.0 and `workPerObol` is `WORK_PER_OBOL`. With something owned, each line's value is the effect of the owned tier. `addWork` adds `turns / workPerObol` to `obols`. `buy` subtracts the price.
- [ ] **Step 4: Run** the same command. Expected: 8 tests pass.

---

### Task 2: The game earns obols, and the sun keeps its pace

**Files:**
- Modify: `composeApp/src/commonMain/kotlin/dev/oblac/sisyphus/game/Game.kt`
- Test: `composeApp/src/commonTest/kotlin/dev/oblac/sisyphus/GameTest.kt`

**Interfaces:**
- Consumes: `Shop`, `Line`, `SUN_CAP` (Task 1).
- Produces:
  - `Game.shop: Shop`;
  - `fun Game.buy(line: Line): Boolean` (buys, then saves at once);
  - store keys: `"obols"`, and the tiers `"shop.rock"`, `"shop.beside"`, `"shop.alone"`, `"shop.blessing"` and `"shop.beauty"`, each saved as a `Double`.

- [ ] **Step 1: Write the failing tests** in `GameTest`. Use the existing `Game.turn(radians)` helper. It turns 62 steps of 0.1, which is 0.987 of a full turn.

```kotlin
@Test
fun oneTurnEarnsItsShareOfAnObol() {
    val game = Game(seed = 1)
    game.turn(2 * PI)
    assertEquals(1.0 / 720, game.shop.obols, 1e-4)
}

@Test
fun aStrongRockEarnsMoreButTheSunKeepsItsPace() {
    val game = Game(MemoryStore(mapOf("shop.rock" to 3.0, "shop.beside" to 3.0)), seed = 1)
    game.turn(2 * PI)
    assertEquals(PX_PER_TURN * 1.5, game.effort, 0.5)
    assertEquals(PX_PER_TURN * 1.5, game.position, 0.5)
    assertEquals(6.0 / 720, game.shop.obols, 1e-3)
}

@Test
fun theShopIsSavedAndComesBack() {
    val store = MemoryStore(mapOf("obols" to 5.0))
    val game = Game(store, seed = 1)
    assertTrue(game.buy(Line.ALONE))
    val again = Game(store, seed = 1)
    assertEquals(1, again.shop.tier(Line.ALONE))
    assertEquals(4.0, again.shop.obols, 1e-9)
}
```

- [ ] **Step 2: Run** `./gradlew :composeApp:jvmTest --tests 'dev.oblac.sisyphus.GameTest'`. Expected: compile failure, `shop` and `buy` are unresolved.
- [ ] **Step 3: Implement** in `Game`:
  - Build `shop` from the store keys; a missing key means tier 0 or 0 obols.
  - In `update`, `turns = abs(turn) / (2 * PI)`. Set `moved = turns * min(shop.bonus, SUN_CAP) * PX_PER_TURN`, then call `shop.addWork(turns * shop.bonus)`.
  - `save()` writes the new keys.
  - `buy(line)` calls `shop.buy(line)`, then `save()` when it bought something.
  - Update the class KDoc: effort still moves the sun, and a strong rock now earns obols faster instead.
- [ ] **Step 4: Run** the full suite: `./gradlew :composeApp:jvmTest :composeApp:testAndroidHostTest`. Expected: all pass, including the existing `turningBackAlsoPushesUp` and `aRestartedGameIsWhereItEnded`.

---

### Task 3: Helpers push on their own, also with the app closed

**Files:**
- Modify: `composeApp/src/commonMain/kotlin/dev/oblac/sisyphus/Platform.kt`, `composeApp/src/commonMain/kotlin/dev/oblac/sisyphus/game/Game.kt`, `composeApp/src/commonMain/kotlin/dev/oblac/sisyphus/game/Quotes.kt`
- Test: `GameTest.kt`, `QuotesTest.kt`

**Interfaces:**
- Consumes: `Game.shop`, `Shop.helpersPerHour`, `Shop.helper`, `Shop.addWork` (Tasks 1–2).
- Produces:
  - `fun wallSeconds(): Double` in `Platform.kt`: seconds since 1970 from `kotlin.time.Clock.System`. Add `@OptIn(ExperimentalTime::class)` only if the compiler asks for it.
  - `Game(store, seed, wall: () -> Double = ::wallSeconds)`.
  - `fun Game.pause()`: saves and remembers the time.
  - `fun Game.resume()`: adds the helpers' work since `pause()`; does nothing if there was no pause.
  - `fun QuoteBox.announce(quote: Quote)`: shows `quote` at once (typed from the start) without changing `drawn`.
  - Store key `"savedAt"`, written by every `save()`.

Rules:
- While the game runs, each `update(dt)` adds `helpersPerHour * dt / 3600` helper turns to `effort` (times `PX_PER_TURN`) and to `shop.addWork`.
- On start, if `"savedAt"` exists and is in the past, add the helpers' work for the time since then. Do the same in `resume()` for the time since `pause()`.
- When the helpers did at least one whole turn while the player was away, announce:
  - text: `"<helper name> pushed <whole turns> times while you were away."`;
  - author: `"Hermes"` when no whole obol was gained, otherwise `"Hermes · <n> obol"` or `"Hermes · <n> obols"`. Here n is how much the whole-obol count went up.

- [ ] **Step 1: Write the failing tests.** In `GameTest`:

```kotlin
@Test
fun thanatosPushesTwentyFourTimesAnHour() {
    val game = Game(MemoryStore(mapOf("shop.alone" to 3.0)), seed = 1, wall = { 0.0 })
    repeat(3600) { game.update(1.0) }
    assertEquals(24 * PX_PER_TURN, game.effort, 1e-6)
    assertEquals(0.0, game.position)
    assertEquals(24.0 / 720, game.shop.obols, 1e-9)
}

@Test
fun helpersWorkWhileTheAppIsClosed() {
    var now = 1000.0
    val store = MemoryStore(mapOf("shop.alone" to 3.0))
    Game(store, seed = 1, wall = { now }).save()
    now += 3 * 3600
    val again = Game(store, seed = 1, wall = { now })
    assertEquals(72 * PX_PER_TURN, again.effort, 1e-6)
    assertEquals(72.0 / 720, again.shop.obols, 1e-9)
    assertEquals("Thanatos pushed 72 times while you were away.", again.quotes.quote.text)
    assertEquals("Hermes", again.quotes.quote.author)
}

@Test
fun helpersWorkWhileTheAppIsInTheBackground() {
    var now = 0.0
    val game = Game(MemoryStore(mapOf("shop.alone" to 1.0)), seed = 1, wall = { now })
    game.pause()
    now += 3600.0
    game.resume()
    game.resume()
    assertEquals(6 * PX_PER_TURN, game.effort, 1e-6)
}

@Test
fun anOldSaveWithoutATimeGetsNoAwayTime() {
    val game = Game(MemoryStore(mapOf("effort" to 500.0, "shop.alone" to 3.0)), seed = 1, wall = { 1.0e9 })
    assertEquals(500.0, game.effort)
    assertEquals(0.0, game.shop.obols)
}

@Test
fun aClockMovedBackGivesNothing() {
    var now = 5000.0
    val store = MemoryStore(mapOf("shop.alone" to 3.0))
    Game(store, seed = 1, wall = { now }).save()
    now = 1000.0
    assertEquals(0.0, Game(store, seed = 1, wall = { now }).effort)
}

@Test
fun aYearAwayStaysSane() {
    var now = 0.0
    val store = MemoryStore(mapOf("shop.alone" to 3.0, "shop.blessing" to 3.0))
    Game(store, seed = 1, wall = { now }).save()
    now += 365 * 24 * 3600.0
    val game = Game(store, seed = 1, wall = { now })
    assertEquals(24 * 24 * 365.0 / 216, game.shop.obols, 1e-6)
    val q = game.quotes.quote
    assertEquals("Hermes · 973 obols", q.author)
    assertTrue(PixelFont.wrap(q.text, 119).size <= 3)
    assertTrue(PixelFont.width("— ${q.author}") <= 119)
}
```

In `QuotesTest`:

```kotlin
@Test
fun anAnnouncementComesFirstAndTheRoundGoesOn() {
    val box = QuoteBox(seed = 3, drawn = 5, firstTime = false)
    box.announce(Quote("A goat pushed 6 times while you were away.", "Hermes"))
    assertEquals("A goat pushed 6 times while you were away.", box.quote.text)
    assertEquals(QuoteBox.Phase.TYPING, box.phase)
    assertEquals(0.0, box.typed)
    assertEquals(5, box.drawn)
}
```

- [ ] **Step 2: Run** `./gradlew :composeApp:jvmTest --tests 'dev.oblac.sisyphus.GameTest' --tests 'dev.oblac.sisyphus.QuotesTest'`. Expected: compile failure, `wall`, `pause`, `resume` and `announce` are unresolved.
- [ ] **Step 3: Implement** the rules above. Apply time away at the end of `Game`'s init, after `quotes` exists, so an announcement replaces the tutorial or the resumed quote. Clamp negative time to zero.
- [ ] **Step 4: Run** the full suite. Expected: all pass.

---

### Task 4: The Agora, a full-screen shop with the sound setting

**Files:**
- Modify: `composeApp/src/commonMain/kotlin/dev/oblac/sisyphus/ui/Layout.kt`, `ui/UiRenderer.kt`, `Screen.kt`, `App.kt`
- Test: `ScreenTest.kt`, `composeApp/src/jvmTest/kotlin/dev/oblac/sisyphus/SnapshotTest.kt`

**Interfaces:**
- Consumes: `Game.shop`, `Game.buy`, `Game.soundOn`, `Game.toggleSound`, `ITEMS`, `Item.name`, `Item.note`, `Item.price` (Tasks 1–2).
- Produces:
  - `enum class Touch { NONE, KNOB, SOUND, SHOP, BUY }`;
  - `Screen.agoraOpen: Boolean` (private set);
  - `Layout.coinIcon: Rect`, `Layout.agoraSound: Rect`, `fun Layout.agoraRow(index: Int): Rect`;
  - `UiRenderer.coins(px: Pixels, r: Rect, obols: Int)`, `UiRenderer.agora(px: Pixels, layout: Layout, shop: Shop, soundOn: Boolean)`.
  - `Layout.soundIcon` goes away.

Layout and behavior:
- **Main screen.**
  - The speaker is gone. In its place, bottom-left, is the coin button: a 7×7 coin in the bronze colors and the obol count (`floor(obols)`, capped at 9999 for display).
  - It is wide enough for "9999", as high as the speaker was, and keeps `TOUCH_SLOP`.
- **The Agora.** It covers the whole screen inside the safe area, on the game background. The display, the quote and the knob aren't drawn while it is open, but the game keeps updating.
  - **Top:** the word "Agora" in cream, and the obol count with a coin on the right.
  - **Middle:** five equal rows, in `Line` order, each with three text lines:
    1. the line's title in terra: "Rock", "Beside him", "On their own", "Blessings", "Beauty";
    2. the next item's name, with its price and a small coin on the right;
    3. its note, with three 3×3 pips on the right (filled for owned tiers).

    Affordable rows are bright (cream and light terra); the others are dim. A complete line shows its last item's name and note, with three filled pips and no price.
  - **Bottom:** the coin button in the same place as on the main screen, which closes the Agora. Bottom-right, the speaker (`agoraSound`) turns the sound on and off, drawn as today.
- **Taps.**
  - Main screen: the coin button opens the Agora and returns `SHOP`; otherwise the knob, as today.
  - Agora, in this order:
    1. the coin button closes it and returns `SHOP`;
    2. the speaker toggles the sound and returns `SOUND`;
    3. a row buys one item and returns `BUY`, or returns `NONE` when it can't be bought;
    4. anything else returns `NONE`.

    The knob is never grabbed while the Agora is open.
- **App.kt.** Treat `SHOP` and `BUY` like `SOUND`: a haptic tick, and a click when the sound is on. `SOUND` keeps its current handling.

- [ ] **Step 1: Write the failing tests** in `ScreenTest`.
  - Replace `theSpeakerTurnsTheSoundOffAndOn` with `theSpeakerLivesInTheAgora` below.
  - Add a helper `smallest()` = `Screen(Game(MemoryStore(), seed = 1), seed = 2).apply { resize(1080, 2340, 144, 102) }`, which is 135 virtual pixels wide.

```kotlin
@Test
fun theCoinOpensAndClosesTheAgora() {
    val s = screen()
    val l = s.layout!!
    assertEquals(Touch.SHOP, s.tap(l.coinIcon))
    assertTrue(s.agoraOpen)
    assertEquals(Touch.SHOP, s.tap(l.coinIcon))
    assertFalse(s.agoraOpen)
}

@Test
fun theSpeakerLivesInTheAgora() {
    val store = MemoryStore()
    val s = screen(store)
    val l = s.layout!!
    s.tap(l.coinIcon)
    assertEquals(Touch.SOUND, s.tap(l.agoraSound))
    assertFalse(s.game.soundOn)
    assertFalse(Game(store, seed = 1).soundOn)
    assertEquals(Touch.SOUND, s.tap(l.agoraSound))
    assertTrue(s.game.soundOn)
}

@Test
fun tappingARowBuysTheNextItem() {
    val s = screen(MemoryStore(mapOf("obols" to 2.0)))
    val l = s.layout!!
    s.tap(l.coinIcon)
    assertEquals(Touch.BUY, s.tap(l.agoraRow(Line.ALONE.ordinal)))
    assertEquals(1, s.game.shop.tier(Line.ALONE))
    assertEquals(1.0, s.game.shop.obols, 1e-9)
}

@Test
fun oneTapBuysOneItem() {
    val s = screen(MemoryStore(mapOf("obols" to 100.0)))
    val l = s.layout!!
    s.tap(l.coinIcon)
    s.tap(l.agoraRow(Line.ROCK.ordinal))
    assertEquals(1, s.game.shop.tier(Line.ROCK))
}

@Test
fun theKnobRestsWhileTheAgoraIsOpen() {
    val s = screen()
    val l = s.layout!!
    s.tap(l.coinIcon)
    assertNotEquals(Touch.KNOB, s.tap(l.knobX + l.knobR * 0.6f, l.knobY + 0.5f))
    assertFalse(s.game.knob.held)
}

@Test
fun theCoinWinsOverTheKnobOnTheSmallestPhone() {
    val s = smallest()
    assertEquals(Touch.SHOP, s.tap(s.layout!!.coinIcon))
    assertFalse(s.game.knob.held)
}

@Test
fun everyItemFitsItsRowOnTheSmallestPhone() {
    val l = smallest().layout!!
    val row = l.agoraRow(0)
    for (item in ITEMS) {
        for (ch in item.name + item.note) assertTrue(PixelFont.hasGlyph(ch), "missing '$ch' in ${item.name}")
        assertTrue(PixelFont.width(item.name) + 24 <= row.w, item.name)
        assertTrue(PixelFont.width(item.note) + 16 <= row.w, item.note)
    }
    assertTrue(row.h >= 3 * PixelFont.LINE_HEIGHT)
    assertTrue(l.agoraRow(4).bottom <= l.agoraSound.y)
}
```

- [ ] **Step 2: Run** `./gradlew :composeApp:jvmTest --tests 'dev.oblac.sisyphus.ScreenTest'`. Expected: compile failure, `coinIcon`, `agoraRow`, `agoraSound` and `agoraOpen` are unresolved.
- [ ] **Step 3: Implement** the layout, the touch handling, the drawing, and the two new `Touch` branches in `App.kt`. In `SnapshotTest.firstLaunch`, drop the `0-sound-off` part: the speaker is no longer on the main screen.
- [ ] **Step 4: Run** the full suite. Expected: all pass.
- [ ] **Step 5: Add a snapshot.** In `SnapshotTest`, add `agora()`. It renders `screen(day = 3, time = 0.4)` with `"obols" to 6.0, "shop.rock" to 1.0, "shop.alone" to 1.0` in its store, opens the Agora, and writes `8-agora.png` at scale 3. Run the suite.
- [ ] **Step 6: Checkpoint.** Show the user `composeApp/build/snapshots/8-agora.png`, together with the main screen and its coin button (`2-noon-clear.png`). Wait for their OK or changes before Task 5.

---

### Task 5: Each rock looks different

**Files:**
- Modify: `composeApp/src/commonMain/kotlin/dev/oblac/sisyphus/scene/SceneRenderer.kt`
- Test: `composeApp/src/commonTest/kotlin/dev/oblac/sisyphus/RockLookTest.kt` (create), `SnapshotTest.kt`

**Interfaces:**
- Consumes: `Game.shop.tier(Line.ROCK)` (Task 2).
- Produces: `drawRock` takes the rock tier (0 to 3).

The looks:

| Tier | Look | Palette (dark to light, 6 tones) | Marks |
|---|---|---|---|
| 0 | Today's rock | unchanged `ROCK` | unchanged: spots, crack and moss |
| 1 | River stone | `0x353A3E, 0x4E555A, 0x6B7378, 0x8C9498, 0xAEB4B6, 0xD2D6D6` | two soft spots, no crack, no moss |
| 2 | Pumice | `0x6E665C, 0x8C8376, 0xA89E8F, 0xC2B8A8, 0xD8CFC0, 0xEDE6DA` | many 1-pixel pores (hash of rock coordinates, about 1 in 9), so they roll with the rock |
| 3 | Sphere of Daedalus | `0x5C5248, 0x80766A, 0xA69C8E, 0xC9C0B2, 0xE6DED2, 0xFFFFFF` | none: a smooth gradient and one crisp 2×2 white highlight |

- [ ] **Step 1: Write the failing test** in `RockLookTest`:

```kotlin
class RockLookTest {
    private fun rockPixels(tier: Int): List<Int> {
        val geo = SceneGeometry(64, 64)
        val game = Game(MemoryStore(mapOf("shop.rock" to tier.toDouble(), "effort" to 300.0)), seed = 3)
        val px = Pixels(64, 64)
        SceneRenderer(geo).render(px, game, World(9).apply { reset(geo, Weather.CLEAR) }, banner = 0.0, hud = false)
        val cx = (geo.heroX + geo.rockDx).toInt()
        val cy = (geo.baseY + geo.rockDy).toInt()
        val r = geo.rockR.toInt()
        return (cy - r..cy + r).flatMap { y -> (cx - r..cx + r).map { x -> px[x, y] } }
    }

    @Test
    fun eachRockTierLooksDifferent() {
        assertEquals(4, (0..3).map { rockPixels(it) }.toSet().size)
    }
}
```
- [ ] **Step 2: Run** it. Expected: FAIL, all four are the same.
- [ ] **Step 3: Implement** the looks from the table.
- [ ] **Step 4: Run** the suite. Expected: all pass.
- [ ] **Step 5: Add a snapshot.** Add `rockSheet()` to `SnapshotTest`: four 64×64 scenes side by side, written as `rocks.png` at scale 6. Check it yourself; it goes to the user with Task 6's pictures.

---

### Task 6: Beauty: dolphins, Pegasus and the Pleiades

**Files:**
- Modify: `scene/World.kt`, `scene/Props.kt`, `scene/SceneRenderer.kt`, `Screen.kt`
- Test: `composeApp/src/commonTest/kotlin/dev/oblac/sisyphus/BeautyTest.kt` (create), `SnapshotTest.kt`

**Interfaces:**
- Consumes: `Game.shop.tier(Line.BEAUTY)` (Task 2).
- Produces:
  - `World.update(..., beauty: Int)`: a new last parameter; `Screen.update` passes the tier.
  - `World.dolphins: List<Leap>`, `World.pegasus: Flyer?`.
  - Sprites in `Props.kt`: `DOLPHIN`, `PEGASUS_UP`, `PEGASUS_DOWN`.

Behavior:
- **Dolphins** (tier 1 and up): one leap every 15–40 seconds, at random.
  - A leap lasts 1.2 seconds and draws an arc that peaks 5 pixels above the horizon.
  - It starts at a random x from 3 to `heroX - 20`, so it stays in the visible sea; no leap if that range is under 6 pixels.
  - The 5×3 sprite is dark slate; flip it by direction.
  - A splash at the start and the end uses the existing particles.
- **Pegasus** (tier 2 and up): crosses the sky every 45–120 seconds, from a random side.
  - Height: y from 10 to `horizon * 0.4`; speed 14–20 pixels a second.
  - The 11×7 white sprite with outline flaps its wings 3 times a second (two frames).
  - It shows day and night.
- **The Pleiades** (tier 3): seven stars in a small fixed cluster, about 9×7 pixels, around `(width * 0.7, horizon * 0.2)`.
  - They draw only when `atmosphere.stars > 0.15`, and twinkle like the other stars.
  - The seventh star, Merope, always uses the dim star color.

- [ ] **Step 1: Write the failing tests** in `BeautyTest`. A fresh `World` that was never updated has no leaps and no Pegasus, so two frames differ only by the Pleiades.

```kotlin
class BeautyTest {
    private val geo = SceneGeometry(133, 133)

    private fun live(beauty: Int, seconds: Int, check: (World) -> Unit) {
        val world = World(4).apply { reset(geo, Weather.CLEAR) }
        repeat(seconds * 10) {
            world.update(0.1, 0.0, 0.0, 0, Weather.CLEAR, 0.5, 0f, beauty)
            check(world)
        }
    }

    private fun frame(beauty: Int, time: Double): IntArray {
        val effort = (time - START_TIME) * DAY_LENGTH
        val game = Game(MemoryStore(mapOf("shop.beauty" to beauty.toDouble(), "effort" to effort)), seed = 3)
        val px = Pixels(geo.width, geo.height)
        SceneRenderer(geo).render(px, game, World(4).apply { reset(geo, Weather.CLEAR) }, banner = 0.0, hud = false)
        return px.data.copyOf()
    }

    @Test
    fun dolphinsLeapOnlyOnceBought() {
        var leaps = 0
        live(0, 120) { leaps += it.dolphins.size }
        assertEquals(0, leaps)
        live(1, 120) { leaps += it.dolphins.size }
        assertTrue(leaps > 0)
    }

    @Test
    fun pegasusFliesByOnceBought() {
        var seen = false
        live(1, 240) { if (it.pegasus != null) seen = true }
        assertFalse(seen)
        live(2, 240) { if (it.pegasus != null) seen = true }
        assertTrue(seen)
    }

    @Test
    fun thePleiadesShineOnlyAtNight() {
        assertFalse(frame(3, 0.95).contentEquals(frame(2, 0.95)))
        assertTrue(frame(3, 0.5).contentEquals(frame(2, 0.5)))
    }
}
```
- [ ] **Step 2: Run** them. Expected: compile failure, then FAIL.
- [ ] **Step 3: Implement** the three.
- [ ] **Step 4: Run** the suite. Expected: all pass.
- [ ] **Step 5: Add snapshots.** Add `beauty()` to `SnapshotTest`: a morning with a dolphin mid-leap, a noon with Pegasus, and a night with the Pleiades, as `9-dolphins.png`, `9-pegasus.png` and `9-pleiades.png`.
- [ ] **Step 6: Checkpoint.** Show the user `rocks.png` and the three beauty pictures. Wait for their OK or changes.

---

### Task 7: Pause and resume on every platform

**Files:**
- Modify:
  - `composeApp/src/iosMain/kotlin/dev/oblac/sisyphus/MainViewController.kt`
  - `iosApp/iosApp/SisyphusApp.swift`
  - `composeApp/src/androidMain/kotlin/dev/oblac/sisyphus/SisyphusController.kt`
  - `androidApp/src/main/kotlin/dev/oblac/sisyphus/MainActivity.kt`
  - `README.md`

**Interfaces:**
- Consumes: `Game.pause()`, `Game.resume()` (Task 3).
- Produces: `SisyphusController.resume()` on iOS and Android.

- [ ] **Step 1: iOS.**
  - `SisyphusController.pause()` calls `game.pause()` instead of `game.save()`, and still pauses the sound.
  - Add `fun resume() = game.resume()`.
  - In `SisyphusApp.swift`, `onChange(of: scenePhase)` calls `controller.resume()` when the phase is `.active`, and `controller.pause()` otherwise.
- [ ] **Step 2: Android.** `SisyphusController.pause()` calls `game.pause()`. Add `fun resume() = game.resume()`, and call it from `MainActivity.onResume()`.
- [ ] **Step 3: Build both apps.** Expected: both succeed.
  - Android: `./gradlew :androidApp:assembleDebug`.
  - iOS: `PATH=/usr/bin:/bin:/usr/sbin:/sbin:$PATH xcodebuild -project iosApp/iosApp.xcodeproj -scheme Sisyphus -configuration Debug -sdk iphonesimulator -destination 'generic/platform=iOS Simulator' build`.
- [ ] **Step 4: Check time away on the Android emulator.**
  - Install the debug build.
  - With `adb exec-in "run-as dev.oblac.sisyphus sh -c 'cat > shared_prefs/sisyphus.xml'"`, write prefs with `shop.alone` 3.0, `obols` 0.0, some `effort`, and `savedAt` 3 hours in the past.
  - Launch the app. Expected: the quote panel types "Thanatos pushed 72 times while you were away." with "— Hermes".
- [ ] **Step 5: README.** In the description, add one line: "Earn obols as you push, and spend them in the shop: better rocks, helpers, blessings and beauty."
- [ ] **Step 6: Final run** of the full suite. Expected: all pass. Report the results and the changed files to the user; the user commits.
