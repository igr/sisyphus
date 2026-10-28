# Voices

Design for the voices: now and then, a god appears as a vision in the sky and asks the player a question.

## Why

A climb needs moments that the player does not expect, not only a new item now and then. In the myths, gods and monsters test travelers with questions: the Sphinx stopped everyone on the road to Thebes with her riddle. A vision is a short surprise in the calm, and a right answer earns a small reward.

## Goals

- Now and then, a god appears as a vision and asks a question with two answers.
- The player answers with the knob: a turn to the left or to the right.
- A right answer gives one obol.
- The game stays calm. A wrong answer, or no answer, costs nothing.

## Not in this project

- Voices of their own in each world, such as Charon and Persephone in The Underworld. All the worlds have the same three voices first. See "Later".
- The gods change their minds over the climbs. See "Later".
- The legendary tasks. This project only counts the right answers for them.

## Must not happen

- A vision in the final climb, at the top, or in the ending.
- A vision while the player does not push. The work of the gods, also while the app is closed, does not bring a vision.
- The player loses something: obols, items, or the place on the hill.
- A climb gets much shorter. For a player who answers every question right, a climb takes about 12% less time: 191 minutes of turning instead of 219, at a turn a second.

## When a vision comes

- Only the player's own turns of the knob count, in turns, as the knob measures them. The bonus of the rock and the sidekicks does not count, so that the visions come at the same pace all through a climb.
- After each vision, the game sets the turns until the next one at random: from 600 to 1800 turns, 1200 on average. That is about 20 minutes of turning at a turn a second. At 5 minutes a day, a vision comes about every 4 days, and about 8 come in a climb. The table in `docs/shop.md` has the count for each daily play.
- The first vision of a new game comes sooner, after 300 to 600 turns, so that a new player meets the voices in the first session.
- In the final climb, the count stops. It goes on in the next climb.
- The count is saved, so a restart does not change it. It stays at the top and in another world.

## What the player sees

1. **The vision comes.** The scene goes dark, as at dusk, and the god fades into the sky over about a second: a large figure that glows, with the soft sound of a magic spell.
2. **The question** types out in the panel of the wise words, as the wise words do. A thin terra line frames the panel, and the name of the god stands under the question in light orange, for example "— the Sphinx". The wise words wait.
3. **The answers** stand in the dark scene, under the vision: the left answer in the bottom-left corner, and the right answer in the bottom-right corner. Each is cream with a dark outline, with a small terra triangle that points outward, and it may take 2 lines. By the knob, two curved arrows show which way to turn: down the left side of the knob for the left answer, and down the right side for the right one.
4. **While the vision shows, the knob does not move Sisyphus.** A vision always comes while the player turns, and the knob coasts after a flick. So when a vision comes, the knob stops at once and does not coast. A turn counts as an answer only after the answers show, and only from a new touch: the finger lifts and touches the knob again. Then an eighth of a turn to one side picks the answer on that side.
5. **The reply** types out in the panel. It starts with "Correct." or "Wrong.", so the player knows at once. After a right answer, one obol comes as any new obol does: the coin sounds and glints, and "+1" rises under the obol count. After a wrong answer, the god gives the right answer, and nothing else happens.
6. **The vision fades,** the light comes back, and the wise words go on. The knob pushes again as soon as the answer is picked.

With no answer in 20 seconds, the god says a farewell line, and the vision fades. A vision also ends without an answer when the app goes to the background, when the Agora, the Worlds screen or the help opens, when the final climb begins, and when the player moves to another world. In all these cases, nothing is lost, and the count to the next vision starts again.

## The voices

Three gods come in turn, in an order that the game shuffles:

| God | Asks | Example |
|---|---|---|
| The Sphinx | A riddle | "What walks on four feet at dawn, on two at noon, and on three at dusk?" A man, or a horse. |
| Hermes | "Who said this?" | One of the wise words, its author, and the author of another one. |
| Hecate, at a crossroads | Which road a wise man takes | Two roads, such as the steep road and the easy road. |

- Each riddle and each crossroads has a question, a right answer, a wrong answer, a reply for a right answer, and a reply for a wrong answer. The game puts "Correct." or "Wrong." before the reply, so a reply does not start with "Yes" or "No". The first version has 20 riddles and 20 crossroads.
- The riddles, the crossroads and the replies may be written for the game. They do not need an old source, but they keep the calm and warm mood of the wise words.
- Hermes makes his questions from the wise words, so they never run out. He uses only sayings of real people, not the lines of Sisyphus and his world, and the other author is never the same person under another name.
- The right answer stands on the left or on the right at random.
- A question does not come again until all the questions of its god have come.
- Every text uses only the letters of `PixelFont`, and fits on the smallest phone: a question or a reply in 3 lines of the panel, and an answer in 2 lines of its half of the scene, 46 pixels wide. Today, 5 names of authors do not fit: Antisthenes, Empedocles, La Rochefoucauld, Schopenhauer and Xenophanes. Hermes leaves them out. A test checks every text, as `QuotesTest` does for the wise words.

## The reward

- A right answer gives one whole obol. It is an obol of the climb like any other: it counts among the first four fast obols, and the part of the next obol that is earned stays.
- The simulation behind the numbers above answers every question right. Most players will be wrong now and then, so their climbs get less than 12% shorter.

## The art

- Three large sprites, one for each god, about 50 by 30 pixels, drawn in code like the rest of the game: the Sphinx (a woman's head, a lion's body and wings), Hermes (the winged hat and the caduceus), and Hecate (a torch in each hand).
- The vision: the scene darkens toward deep blue by about 60%. A soft, dithered light surrounds the god, a cream line traces its edge, and the figure shows at about 80% over the dark scene.
- In every world, the vision appears in the same place, high in the scene. In The Underworld, it appears under the rock roof.

## Saved data

- `nextVision`: the turns of the player that are left until the next vision. Kept at the top and in another world.
- The order of the gods and of their questions, so that a restart does not repeat a question: a seed and a count, as `quoteSeed` and `quotesDrawn` do for the wise words.
- `rightAnswers`: how many questions the player answered right, for the legendary tasks later.
- `docs/saved-data.md` lists the new keys, with their start values for old saved games.

## To test it

- The dev screen gets a row for the voices. Now brings a vision at once, and Next turn brings one at the next click of the knob, the way a vision really comes.
- Tests: a vision comes when the count of the player's turns runs out, and the work of the gods does not move the count; no vision in the final climb; a right answer adds one obol, and a wrong answer or no answer adds nothing; every text fits.
- Snapshots: a vision in each world, and the answers under the vision on the smallest phone.

## Later

- Voices of their own in each world: Charon and Persephone in The Underworld, Hephaestus and Empedocles on Mount Etna.
- The gods change their minds over the climbs: Zeus is angry, then puzzled, then silent.
- More gods: Zeus and Heracles.
