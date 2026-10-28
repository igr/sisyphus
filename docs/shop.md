# Obols and the shop

Design for the obols, the shop, and the final climb to the top.

## Why

Players need a reason to come back day after day. They push the rock, earn coins, and spend them on help and beauty. Everything uses the names and characters of the Greek myth. The game must stay calm: the coins add a slow, steady goal, not a race.

## Goals

- Players earn coins by pushing. There is no real money in the game.
- The shop has 15 items: five lines of three.
- A player who turns the knob about 5 minutes a day buys everything in about a month.
- The day and night animation keeps its calm pace.
- Some helpers push on their own, also when the app is closed.

## Not in this project

- In-app purchases, ads, accounts.
- Drawing the helpers on the hill, and the Lyre of Orpheus with its music. See "Later".
- The art of the new worlds. See "Later".

## Must not happen

- The sun races. A full day must never take fewer than 48 turns of the knob (today it takes 72). The limit is per turn, not in real seconds: a fast spin may still make a day quicker, as it does today.
- Helpers take over the game. Pushing stays the main way to progress. Target: for a player who pushes 5 minutes a day, helpers that push on their own do about 15–40% of the work. Lighter players get a bigger share from helpers; that is fine.

## The coin: the obol

The obol is the coin the Greeks gave Charon to ferry the dead into the underworld, where Sisyphus pushes his rock.

Obols come from **work**:

- One turn of the knob is 1 unit of work, multiplied by the bonus from the rock and the helper beside him (1× to 6×).
- Helpers that push on their own add their own work every hour.
- The first four obols of a climb need a quarter of the work per obol each: 180 units of work, two and a half days of pushing for each. Thus the first items come soon: three items in the first 20 minutes of turning.
- Each obol after them needs 720 units of work, plus or minus up to 36 (half a day). On average, that is 10 days of pushing (a day is 72 turns). Because each obol is a little shorter or longer, obols come at all times of the day, not always at the same hour.
- The work for the next obol is set when the obol before it comes, and it is saved.
- Blessings lower the work per obol (see the shop). A blessing also makes the next obol, and the fast obols after it, smaller in the same proportion. The part of the next obol that is earned is kept.

To test the obols, the Climb row of the dev screen has two buttons. Reset starts the climb again from day 1, so the fast obols come again. Next obol pushes Sisyphus on until three clicks of the knob are left before the next obol. The sun moves with him.

## The shop

Each line is bought from left to right (I, then II, then III). A newer item replaces the effect of the one before it in the same line.

| Line | I | II | III |
|---|---|---|---|
| **Rock**: your turns count more | River stone of Asopus · 2 obols · 1.5× | Pumice from Hephaestus' forge · 5 · 2× | Sphere of Daedalus · 9 · 3× |
| **Sidekicks**: help only while you push | Merope, his wife · 3 · 1.25× | Bellerophon, his grandson · 6 · 1.5× | Heracles · 10 · 2× |
| **Gods**: push all the time, also with the app closed | Amalthea, the goat who nursed Zeus · 1 · 6 turns an hour | Pan · 4 · 12 turns an hour | Thanatos, who never sleeps · 8 · 24 turns an hour |
| **Blessings**: more obols | Purse of Hermes · 2 · an obol per 7 days of work | Gift of Tyche · 6 · per 5 days | Wealth of Plutus · 10 · per 3 days |
| **Beauty**: in the background, no effect on progress | Dolphins of Poseidon · 1 | Pegasus · 2 | The Pleiades · 3 |

All 15 items cost 72 obols together. A blessing leaves a part of the work per obol of 720: 0.7 with Hermes (504), 0.5 with Tyche (360), and 0.3 with Plutus (216). So a new work per obol changes all of them.

## What the player sees

- **The Agora Market.** The shop is a full screen, named after the Greek marketplace. The obol count sits in the top-right corner of the display, across from the day. Tapping it opens the Agora Market: the five lines, and the sound setting at the bottom. The title counts the items bought, for example "3/15". After the name of each line, three dots stand for its items, as for the tiers of the labors: a filled dot for each item bought, a bright ring for the item on offer, and dim rings for the items after it. The count stays in the same place there, and tapping it again closes the market. Items you cannot afford yet show a gray price and do nothing. Tapping an item you can afford opens an offer: its picture, what it does, and Yes or No. Yes buys it, with a short fanfare. The speaker leaves the main screen. Across from the speaker, a small Sisyphus with his rock and the note "Buy all 15 for the top." stand until everything is bought.
- **The obol bar.** Between the day and the obols, a short strip of the Greek key, like the band under the display, turns bronze as the next obol comes. It goes dark again when the obol is there. During the final climb, it shows the way to the top instead: it turns cream as Sisyphus climbs. The help points at it.
- **The coin.** When an obol comes, the count goes up at once. Its coin glints, a small "+1" rises under the count and fades, and a coin rings, unless the sound is off. When the game comes back from the background with obols that the gods earned, the number tells how many came, for example "+3".
- **Rocks.** Each rock looks different: the river stone smooth and grey, the pumice light and full of small holes, the sphere of Daedalus perfectly round and polished.
- **Helpers and blessings** are not drawn on the hill. Their pictures stand side by side in the bottom-right corner of the display: the sidekick, the god, and the blessing in the corner itself.
- **Dolphins** jump in the sea now and then.
- **Pegasus** flies across the sky now and then.
- **The Pleiades** shine in the night sky: seven stars, and Merope, his wife, is one of them. She is the faintest, because she married a mortal.

## Rules

- **Bonus.** The rock and the helper beside him multiply each other: up to 3 × 2 = 6×.
- **The sun and the walk.** The sun, and Sisyphus's walk up the hill, move at most 1.5× faster than today, whatever the bonus. Every turn moves them by `min(bonus, 1.5)` turns. The rest of the bonus goes only into obols.
- **Helpers on their own** add their turns to both the sun and the obols, all the time. The rock and the helper beside him do not multiply these turns. Time away is worked out on return from the clock, with no limit. These turns are too slow to see while you watch (Thanatos makes a day every 3 hours).
- **Beauty items add up.** Each one stays when the next is bought.
- **The day counter** ("DAY N") follows the sun.
- **Left-over obols** stay. They will carry over into the second project.

## How long it takes

Simulation, assuming one knob turn per second while the player plays, and the average work for each obol. "Top reached" adds the final climb. "Visions" tells how many times a god comes as a vision in the climb: the average, and in brackets the range of most climbs, because the count is random. The days count no obols from the visions.

| Daily play | First item | All 15 | Longest wait between items | Top reached | Visions |
|---|---|---|---|---|---|
| 2 minutes | Day 2 | Day 55 | 5 days | Day 63 | 6 (5 to 7) |
| 5 minutes | Day 1 | Day 31 | 3 days | Day 34 | 8 (7 to 9) |
| 10 minutes | Day 1 | Day 18 | 2 days | Day 19 | 9 (8 to 10) |
| 30 minutes | Day 1 | Day 7 | 1 day | Day 7 | 10 (9 to 11) |

Only the player's own turns bring a vision. So a player who plays less, and lets the gods push while the app is closed, sees fewer visions. Each right answer gives one obol: a player who answers every vision right buys everything about 4 days sooner at 5 minutes a day, on day 27, and reaches the top on day 30.

Non-stop play is allowed: at one turn a second, about three and a half hours of turning buys everything, and about 10 visions come.

Turning speed matters too: at 5 minutes a day, a slow turner (30 turns a minute) buys everything on day 49, a fast one (90 a minute) on day 23.

Order of purchases at 5 minutes a day, buying the cheapest affordable item: Goat, Dolphins, River stone, Hermes, Pegasus, Merope, Pleiades, Pan, Pumice, Bellerophon, Tyche, Thanatos, Sphere, Heracles, Plutus.

The order matters too. At 5 minutes a day, the fastest order reaches the top on day 27, and the slowest on day 68. The fastest buys the blessings early and Beauty last. The slowest buys Beauty first and the blessings last. A random order reaches the top on day 34, the middle of 300 random orders. A player who saves every obol and buys everything at the end reaches the top on day 169. Beauty is cheap, so that a player who buys it first loses only a week: the top on day 41.

## Left to the builder

- The exact layout of the Agora, in the game's pixel style. Show the player a picture of it early.
- The exact pixel art of the rocks, the dolphins, Pegasus and the Pleiades, drawn in code like the rest of the game.
- How the player learns what the helpers did while they were away.
- How the new state is saved.

## Later

- More in the Agora: a small picture of each item, the map of new places, the quotes you have seen, and stats.
- Draw the helpers on the hill: Merope, Bellerophon and Heracles pushing beside Sisyphus; Amalthea, Pan and Thanatos somewhere on the hill.
- The Lyre of Orpheus: music from a sound file, like the click. It can become a beauty item when it is built.

### The next three things

Sisyphus is an experience, not a clicker game. The rock always rolls back down, and Sisyphus never escapes. Nothing makes the next climb shorter. Each climb brings new moments instead, so that the player leaves with something profound and satisfying.

1. **A better ending, which offers the next world.** The ending is the best moment to sell a world, because a new climb begins anyway. Ideas for it:
   - The walk down. Camus calls the walk back to the rock "the hour of consciousness". After the rock rolls away, slow turns of the knob walk Sisyphus down the hill. No obols come. A few lines of Camus come one by one.
   - A memory of the climb on the black screen: its days, its storms, the sights, and the voices answered.
   - A different line of Camus at the end of each climb.
   - The choice of the next world: push again here, or go to another world. A world that is not bought shows its price.
   - After a few climbs, Sisyphus smiles at the top.
2. **Voices.** Now and then, a god appears as a vision in the sky and asks a question with two answers. The knob answers, to the left or to the right, and a right answer gives one obol. See `docs/voices.md`.
3. **Legendary tasks.** A list of tasks across climbs, like the labors of Heracles, in the Agora. For example: reach the top in each world, answer 50 voices, see Pegasus 10 times, climb without sidekicks. With records, such as the fastest climb.

Other ideas from the same talk, not planned: trials of the gods (a harder climb that the player chooses, for a laurel), and traces of each climb in the world (a stone on a cairn, a star in the sky).

## The final climb

- **The goal.** At the first purchase of each climb, Hermes says: "One of fifteen. Buy them all, and the top will come in sight."
- **The climb.** After the 15th item, Hermes says the top is in sight. Reaching it takes as much pushing as 3 days at 5 minutes a day: 900 turns at the fastest walk. Only the player's own turns move him up; the gods do not climb for him, so he always reaches the top while someone plays. The Greek key in the top row shows how far he has come. Obols stop coming, because there is nothing more to buy. The corner shows only the coin, which still opens the Agora and its sound setting.
- **Herms.** After every 100 turns of the climb, a herm stands by the path: the stone marker of Greek roads, a pillar with the head of Hermes. As Sisyphus passes it, Hermes says how far the top is, for example "600 turns to the top."
- **The top** is a flat hill. It comes into view just before he gets there, and the knob stops moving him on it. A victory fanfare plays as he reaches it.
- **The rock rolls down.** He holds it a moment. Then the rock slips back, and he stands up straight. When it rolls past him, he turns around and watches it go down the hill, faster and faster, until it is out of sight.
- **The black screen.** The screen goes black for Camus: "The struggle itself toward the heights is enough to fill a man's heart. One must imagine Sisyphus happy." It also says how many times he reached the top. After a few seconds, a tap opens the Worlds screen, so that the player can begin again in the same world or in a new one. When the Worlds screen closes, the first morning comes back in the world the player is in.
- **Everything starts from the beginning:** day 1, the shop, the obols. The number of climbs, the counts of the labors and the sound setting are kept. The Labors tab of the Worlds screen shows the climbs.
- **To test it,** the dev screen has a Top button: it buys everything and takes him to two turns below the top.

## Worlds

- **The Worlds screen** opens when you tap the day, and after the black screen at the top. It shows a picture of each world: Corinth (the hill by the sea), Mount Etna (the hill on the volcano) and The Underworld (the hill in Tartarus). The current world has a bright frame and a check. The help points to it.
- **Two tabs** stand in the title row: Worlds and Labors. A tap on the other tab shows it, and a tap on the shown tab closes the screen. The Labors tab lists the labors of all climbs: see `docs/labors.md`.
- **The list scrolls** when the worlds do not fit, with a thin scrollbar on the right. A drag scrolls it, and after a quick swipe it coasts and slows down. A tap chooses a world when the finger lifts. A tap that stops the coasting list chooses nothing.
- **The pictures** are cut from the real scene of each world at noon.
- **Moving** to another world is possible at any time, after a question, because the climb starts over there: day 1, no items, no obols. The climbs, the counts of the labors and the sound setting stay. A fanfare of trumpets plays as the new world begins.
- **Buying a world.** Corinth is free. Each other world costs $0.99, once, and then it is the player's forever. A world that is not bought shows a lock and its price, in the money of the player's store. A tap on it asks first. Yes opens the store's payment sheet, and after the purchase the question about moving comes. A player who is in a world that is not bought stays there: the lock only stops a move into a world.
- **Restore**, at the bottom of the Worlds screen, asks the store for the worlds bought before: on another phone, or after the game was installed again. It shows while a world is not bought. RevenueCat sells the worlds: see "Purchases" in `README.md`.
- **The support ID**, across from Restore, is "ID" and the last characters of the player's anonymous id at RevenueCat, then the build of the game in brackets, such as "ID 3f9a [41]". A tap copies the whole id, for the player to send to support, and the screen says "Copied". The desktop game has no store, no id and no build.
- **For testers**, the dev screen of debug and beta builds opens every world without buying, until it is tapped again or the game restarts.
- **Each world has its own look and its own items.** The items have the prices and the effects of Corinth, so the economy is the same everywhere. Only the names, the details, the pictures and the beauty change.
- **Mount Etna** is a side cone of the volcano. The summit smokes on the horizon, with lava in its crater and down its side. Black lava fields lie below, with a flow of lava that glows at night. The ground is black ash with red stones and thin grass. Birch, broom, chestnut and black pine grow there, among lava boulders, steaming vents and burnt trees. Ravens fly by day. On the days it rains in Corinth, ash falls on Etna.
- **The Underworld** is Tartarus, where Homer saw Sisyphus push his rock. A rock roof with stalactites hangs over a misty cave, and the pale sun of Elysium crosses it, as Virgil tells. The dark Styx runs below, and on its far shore stand the gates of Hades, with a fire on each side. The ground is gray dust with pale stones and asphodels. Black poplars, willows, white cypresses and pomegranates grow there, among grave stones, amphorae and boulders. Bats fly by day, and the rain drips from the roof.

The items of Mount Etna:

| Line | I | II | III |
|---|---|---|---|
| Rock | Lava stone | Volcanic bomb | Obsidian sphere |
| Sidekicks | Empedocles | Odysseus | Polyphemus |
| Gods | Aetna | Hephaestus | Typhon |
| Blessings | Sheaf of Demeter | Wine of Dionysus | Silver of Arethusa |
| Beauty | Lava fountain | Eagle of Zeus | Torches of Demeter |

The beauty of Etna: now and then the crater throws fire into the sky; the eagle of Zeus flies by with a thunderbolt; at night, Demeter's two torches cross the lava fields.

The voices of Etna: Hephaestus and Empedocles ask the riddles, and Hermes asks "Who said this?". See `docs/voices.md`.

The items of The Underworld:

| Line | I | II | III |
|---|---|---|---|
| Rock | Stone of Lethe | Rock of Tantalus | Adamant sphere |
| Sidekicks | Orpheus | Theseus | Achilles |
| Gods | Cerberus | Persephone | Hades |
| Blessings | Obol for the ferry | Golden bough | Riches of Plouton |
| Beauty | Boat of Charon | Psyche | Lights of Elysium |

The beauty of The Underworld: now and then Charon rows his boat across the Styx; Psyche flies by on butterfly wings; at night, the souls of Elysium drift over the meadow as small lights.

The voices of The Underworld: Charon and Persephone ask the riddles, and Hermes asks "Who said this?".

## Later

- More worlds. The checklist for a new world is in `.claude/skills/new-world/SKILL.md`.
- **A move keeps the climb.** The player can move to another world at any time and lose nothing: the day, the obols, the items and the final climb go with him. Each item becomes the item of the same line and tier in the new world, for example the River stone becomes the Lava stone. Thus no warning comes before a move. The black screen at the top can also offer the worlds, because a new climb begins there anyway.
- **The same math in every world.** A world is chosen for its mood, so it never makes a climb faster or harder. Prices and speeds that differ would make a move cost the climb again, would let a player hop to the fastest world, and would make a bought world an advantage. The worlds differ in what happens instead: their own voices, such as Charon and Persephone in The Underworld; their own trials and Labors; and their own sights and lines.
