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
- 720 units of work earn 1 obol. That is 10 days of pushing (a day is 72 turns).
- Blessings lower the work per obol (see the shop). The fraction of the next obol is kept and saved.

## The shop

Each line is bought from left to right (I, then II, then III). A newer item replaces the effect of the one before it in the same line.

| Line | I | II | III |
|---|---|---|---|
| **Rock**: your turns count more | River stone of Asopus · 2 obols · 1.5× | Pumice from Hephaestus' forge · 5 · 2× | Sphere of Daedalus · 9 · 3× |
| **Sidekicks**: help only while you push | Merope, his wife · 3 · 1.25× | Bellerophon, his grandson · 6 · 1.5× | Heracles · 10 · 2× |
| **Gods**: push all the time, also with the app closed | Amalthea, the goat who nursed Zeus · 1 · 6 turns an hour | Pan · 4 · 12 turns an hour | Thanatos, who never sleeps · 8 · 24 turns an hour |
| **Blessings**: more obols | Purse of Hermes · 2 · an obol per 7 days of work | Gift of Tyche · 6 · per 5 days | Wealth of Plutus · 10 · per 3 days |
| **Beauty**: in the background, no effect on progress | Dolphins of Poseidon · 3 | Pegasus · 7 | The Pleiades · 11 |

All 15 items cost 87 obols together. In work per obol, the blessings are 504 (Hermes), 360 (Tyche) and 216 (Plutus).

## What the player sees

- **The Agora Market.** The shop is a full screen, named after the Greek marketplace. The obol count sits in the top-right corner of the display, across from the day. Tapping it opens the Agora Market: the five lines, and the sound setting at the bottom. The count stays in the same place there, and tapping it again closes the market. Items you cannot afford yet show a gray price and do nothing. Tapping an item you can afford opens an offer: its picture, what it does, and Yes or No. Yes buys it, with a short fanfare. The speaker leaves the main screen.
- **Rocks.** Each rock looks different: the river stone smooth and grey, the pumice light and full of small holes, the sphere of Daedalus perfectly round and polished.
- **Helpers** are not drawn in this project. They work, but you see them only in the shop.
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

Simulation, assuming one knob turn per second while the player plays. "Top reached" adds the final climb.

| Daily play | First item | All 15 | Longest wait between items | Top reached |
|---|---|---|---|---|
| 2 minutes | Day 6 | Day 66 | 6 days | Day 84 |
| 5 minutes | Day 3 | Day 37 | 3 days | Day 44 |
| 10 minutes | Day 2 | Day 22 | 2 days | Day 26 |
| 30 minutes | Day 1 | Day 9 | 1 day | Day 11 |

Non-stop play is allowed: at one turn a second, about 4 hours of turning buys everything.

Turning speed matters too: at 5 minutes a day, a slow turner (30 turns a minute) buys everything on day 58, a fast one (90 a minute) on day 28.

Order of purchases at 5 minutes a day, buying the cheapest affordable item: Goat, River stone, Hermes, Merope, Dolphins, Pan, Pumice, Tyche, Bellerophon, Pegasus, Thanatos, Sphere, Heracles, Plutus, Pleiades.

## Left to the builder

- The exact layout of the Agora, in the game's pixel style. Show the player a picture of it early.
- The exact pixel art of the rocks, the dolphins, Pegasus and the Pleiades, drawn in code like the rest of the game.
- How the player learns what the helpers did while they were away.
- How the new state is saved.

## Later

- More in the Agora: a small picture of each item, the map of new places, the quotes you have seen, and stats.
- Draw the helpers on the hill: Merope, Bellerophon and Heracles pushing beside Sisyphus; Amalthea, Pan and Thanatos somewhere on the hill.
- The Lyre of Orpheus: music from a sound file, like the click. It can become a beauty item when it is built.

## The final climb

- **The climb.** After the 15th item, Hermes says the top is in sight. Reaching it takes as much pushing as 7 days at 5 minutes a day: 2,100 turns at the fastest walk. Only the player's own turns move him up; the gods do not climb for him, so he always reaches the top while someone plays.
- **The top** is a flat hill. It comes into view just before he gets there, and the knob stops moving him on it.
- **The rock rolls down.** He stands a moment, then the rock rolls back past him and down the hill, faster and faster, until it is out of sight.
- **The black screen.** The screen goes black for Camus: "The struggle itself toward the heights is enough to fill a man's heart. One must imagine Sisyphus happy." It also says how many times he reached the top. After a few seconds, a tap begins again.
- **Everything starts from the beginning:** day 1, the shop, the obols. Only the number of climbs is kept, with the sound setting. After the first climb, a small peak and the count sit under the day.
- **To test it,** the dev screen has a Top button: it buys everything and takes him to two turns below the top.

## Worlds

- **The Worlds screen** opens when you tap the day. It shows a picture of each world: Corinth (the hill by the sea), Mount Etna (the hill on the volcano) and The Underworld (the hill in Tartarus). The current world has a bright frame and a check. The help points to it.
- **The list scrolls** when the worlds do not fit, with a thin scrollbar on the right. A drag scrolls it, and after a quick swipe it coasts and slows down. A tap chooses a world when the finger lifts. A tap that stops the coasting list chooses nothing.
- **The pictures** are cut from the real scene of each world at noon.
- **Moving** to another world is possible at any time, after a question, because the climb starts over there: day 1, no items, no obols. The climbs and the sound setting stay.
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

The items of The Underworld:

| Line | I | II | III |
|---|---|---|---|
| Rock | Stone of Lethe | Rock of Tantalus | Adamant sphere |
| Sidekicks | Orpheus | Theseus | Achilles |
| Gods | Cerberus | Persephone | Hades |
| Blessings | Obol for the ferry | Golden bough | Riches of Plouton |
| Beauty | Boat of Charon | Psyche | Lights of Elysium |

The beauty of The Underworld: now and then Charon rows his boat across the Styx; Psyche flies by on butterfly wings; at night, the souls of Elysium drift over the meadow as small lights.

## Later

- More worlds. The checklist for a new world is in `.claude/skills/new-world/SKILL.md`.
