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

- Questions of each world's own. All worlds share the same wise words; only the gods who ask about them change.
- The gods change their minds over the climbs. See "Later".
- The legendary tasks. This project only counts the right answers for them.

## Must not happen

- A vision in the final climb, at the top, or in the ending.
- A vision while the player does not push. The work of the gods, also while the app is closed, does not bring a vision.
- The player loses something: obols, items, or the place on the hill.
- A climb gets much shorter. For a player who answers every question right, a climb takes about 12% less time: 191 minutes of turning instead of 219, at a turn a second.

## When a vision comes

- Only the player's own turns of the knob count, in turns, as the knob measures them. The bonus of the rock and the sidekicks does not count, so that the visions come at the same pace all through a climb.
- After each vision, the game sets the turns until the next one at random: from 200 to 600 turns, 400 on average. That is about 7 minutes of turning at a turn a second. At 5 minutes a day, a vision comes in most sessions, and about 5 come in a climb. The table in `docs/shop.md` has the count for each daily play.
- The first vision of each climb comes sooner, after 70 to 100 turns: on day 2, also when the rock moves the sun as fast as it can. So a new player meets the voices in the first session.
- These numbers are in `game/Config.kt`.
- In the final climb, the count stops. A new climb, after the top or in another world, starts it again, for its first vision on day 2.
- The count is saved, so a restart does not change it. The visions that came and the right answers stay at the top and in another world.

## What the player sees

1. **The vision comes.** The scene goes dark, as at dusk, and the god fades into the sky over about a second: a large figure that glows, with the soft sound of a magic spell.
2. **The question** types out in the panel of the wise words, as the wise words do. A thin terra line frames the panel, and the name of the god stands under the question in light orange, for example "— the Sphinx". The wise words wait.
3. **The answers** stand in the dark scene, under the vision: the left answer in the bottom-left corner, and the right answer in the bottom-right corner. Each is cream with a dark outline, with a small terra triangle that points outward, and it may take 2 lines. By the knob, two curved arrows show which way to turn: down the left side of the knob for the left answer, and down the right side for the right one.
4. **While the vision shows, the knob does not move Sisyphus.** A vision always comes while the player turns, and the knob coasts after a flick. So when a vision comes, the knob stops at once and does not coast. A turn counts as an answer only after the answers show, and only from a new touch: the finger lifts and touches the knob again. Then an eighth of a turn to one side picks the answer on that side.
5. **The reply** types out in the panel. It starts with "Correct." or "Wrong.", so the player knows at once. After a right answer, one obol comes as any new obol does: the coin sounds and glints, and "+1" rises under the obol count. After a wrong answer, the god gives the right answer, and nothing else happens.
6. **The vision fades,** the light comes back, and the wise words go on. The knob pushes again as soon as the answer is picked.

With no answer in 20 seconds, the god says a farewell line, and the vision fades. A vision also ends without an answer when the app goes to the background, when the Agora, the Worlds screen or the help opens, when the final climb begins, and when the player moves to another world. In all these cases, nothing is lost, and the count to the next vision starts again.

## The voices

In each world, one of three gods comes, chosen at random. Hermes comes in every world. The two other gods belong to the world:

| World | The two gods of the world |
|---|---|
| Corinth | The Sphinx, and Hecate of the crossroads |
| Mount Etna | Hephaestus, the smith of the gods, whose forge is under the volcano, and Empedocles, the philosopher who leapt into its crater |
| The Underworld | Charon, the ferryman of the dead, and Persephone, their queen |
| Cairo | Thoth, the scribe of the gods, with the head of an ibis, and Anubis, the jackal who weighs the hearts of the dead |
| Mount Fuji | Sakuya-hime, the goddess of the mountain and of the cherry blossom, and Iwanaga-hime, her elder sister, the princess of the rock and of long life |

There are three kinds of questions. Each round of three visions asks one of each, in an order that the game shuffles. The two answers are always of the same kind, so that only knowing helps. The first voices also asked riddles, but their answers gave them away, so they are gone.

| Kind | Example | The two answers |
|---|---|---|
| "Who said this?" | "Who said this? "The sun is new every day."" Heraclitus, or Pindar. | The author of the wise words, and the author of other ones. |
| "Who lived first?" | Thales, or Livy. The reply: "Thales was born about 624 BC, Livy about 59 BC." | Two people of the wise words, born a century apart or more, so that the order is sure. Both lived in antiquity, or both in modern times, so that the era does not give the answer away. |
| "What do the Romans call ...?" | "What do the Romans call Hermes?" Mercury, or Pluto. | The Roman name of the god, and the Roman name of another god, or of another goddess for a goddess. |

- Each god has a farewell of its own, which it says when nobody answers.
- The gods make the questions from the wise words, so they do not run out: 410 sayings and 761 pairs of people. They use only sayings of real people, not the lines of Sisyphus and his world, and the other author is never the same person under another name.
- "Who lived first?" uses only the 58 people whose year of birth is known well enough. Before the year 1000, the reply says "about".
- "What do the Romans call ...?" uses 23 gods whose Roman name does not sound like the Greek one, so Heracles and Persephone are left out.
- No god names itself as an answer: Empedocles of Mount Etna wrote wise words too.
- The right answer stands on the left or on the right at random.
- No question comes again until all the questions of its kind have come, in all worlds together.
- Every text uses only the letters of `PixelFont`, and fits on the smallest phone: a question or a reply in 3 lines of the panel, and an answer in 2 lines of its half of the scene, 46 pixels wide. Today, 5 names of authors do not fit: Antisthenes, Empedocles, La Rochefoucauld, Schopenhauer and Xenophanes. The gods leave them out. A test checks every text, as `QuotesTest` does for the wise words.

## A new world

Each new world names its two gods, draws their sprites, and writes their farewells. The `new-world` skill lists them among the parts of a world.

## The reward

- A right answer gives one whole obol. It is an obol of the climb like any other: it counts among the first four fast obols, and the part of the next obol that is earned stays.
- The simulation behind the numbers above answers every question right. Most players will be wrong now and then, so their climbs get less than 12% shorter.

## The art

- Eleven large sprites, one for each god, about 50 by 30 pixels, drawn in code like the rest of the game: the Sphinx (a woman's head, a lion's body and wings), Hermes (the winged hat and the caduceus), Hecate (a torch in each hand), Hephaestus (a bearded smith with his hammer), Empedocles (a philosopher with the bronze sandal that the volcano threw back), Charon (a hooded ferryman with his pole), Persephone (a crown, and a pomegranate in her hand), Thoth (the head of an ibis, the moon over it, and a scribe's palette), Anubis (the head of a jackal, and the scales), Sakuya-hime (a pink robe, and a branch of cherry in bloom), and Iwanaga-hime (white hair, a gray robe, and a stone in her hand).
- The vision: the scene darkens toward deep blue by about 60%. A soft, dithered light surrounds the god, a cream line traces its edge, and the figure shows at about 80% over the dark scene.
- In every world, the vision appears in the same place, high in the scene. In The Underworld, it appears under the rock roof.

## Saved data

- `nextVision`: the turns of the player that are left until the next vision. Each new climb sets it again, for its first vision on day 2.
- The order of the gods and of their questions, so that a restart does not repeat a question: a seed and a count, as `quoteSeed` and `quotesDrawn` do for the wise words.
- `rightAnswers`: how many questions the player answered right, for the legendary tasks later.
- `docs/saved-data.md` lists the new keys, with their start values for old saved games.

## To test it

- The dev screen gets a row for the voices. Now brings a vision at once, and Next turn brings one at the next click of the knob, the way a vision really comes.
- Tests: a vision comes when the count of the player's turns runs out, and the work of the gods does not move the count; no vision in the final climb; a right answer adds one obol, and a wrong answer or no answer adds nothing; every text fits.
- Snapshots: a vision of each god, and the answers under the vision on the smallest phone.

## Later

- The gods change their minds over the climbs: Zeus is angry, then puzzled, then silent.
- More gods: Zeus and Heracles.
