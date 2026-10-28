# Labors

Design for the labors: a list of tasks across all climbs, like the labors of Heracles. They are on a second tab of the Worlds screen.

## Why

The rock always rolls back down, and nothing makes the next climb shorter. So the player needs a record of what they did over all their climbs. A labor is an honor, a small story with a funny name. It gives nothing else.

## Goals

- The Worlds screen gets two tabs: Worlds and Labors.
- There are 20 labors, in 4 categories of 5 tiers each.
- The player sees every labor from the start. A labor that is not done shows in gray, with its progress.
- When a labor is done, Hermes brings the news in the panel of the wise words, and a bronze dot shows by the day until the player opens the Labors tab.

## Not in this project

- Rewards of any kind: no obols, no items, nothing that makes a climb faster.
- Records, such as the fastest climb. They reward rushing.
- Other categories, such as the top in every world, or the sights (Pegasus). See "Later".
- A sound for a labor.

## Must not happen

- A labor changes the game.
- A count goes down. The top, a move to another world, and the Reset button of the dev screen keep all the counts.
- A count goes up without pushing. A move to another world, for example, adds no day.
- The news of a labor cuts a question of a vision, a reply, or other news short.
- A labor counts time. All counts are in turns, tops, game days and answers. Only the gods' pushes move the game day by the clock, as they already do.

## The labors

| Category | Color | Counts | Tiers and names |
|---|---|---|---|
| Hills climbed | terra, the rock | Tops reached (`climbs`) | 1 Top of the Morning · 10 Rolling Stone · 25 King of the Hill · 50 Over the Hill · 100 Imagine Him Happy |
| Knob turns | bronze, the knob | The player's own turns | 100 Turn On · 1,000 Spin Doctor · 10,000 Tina Turner · 100,000 Revolutionary · 1,000,000 Perpetual Motion |
| Days | dawn gold, the sun | The game days pushed through in all climbs | 10 Carpe Diem · 100 The Hundred Days · 1,000 Days of Our Lives · 10,000 Groundhog Day · 100,000 Eternal Sunshine |
| Right answers | night blue, the visions | Right answers to the visions (`rightAnswers`) | 1 Lucky Guess · 10 Teacher's Pet · 50 The Oracle · 100 Know-It-All · 500 Know Thyself |

Each labor has a plain line under its name:

- Hills climbed: "Reach the top once." and "Reach the top 10 times.", up to 100.
- Knob turns: "Turn the knob 100 times.", up to 1,000,000.
- Days: "Push through 10 days.", up to 100,000.
- Right answers: "Answer a vision right." and "Answer 10 visions right.", up to 500.

At 5 minutes a day, a climb takes about a week of play: about 2,300 turns of the player, about 80 game days, and about 4 right answers when 3 answers in 4 are right. So the first tier of each category comes in the first days or with the first top. The last tier of each category takes years. "Imagine Him Happy" needs 100 tops: about 2 years at 5 minutes a day, or about 7 months at 30 minutes a day.

## What counts

- **Hills climbed**: the `climbs` that the game already saves. It goes up by 1 at each top.
- **Knob turns**: the player's own turns of the knob, as the knob measures them, the same turns that bring a vision. The gods' pushes do not count. The turns of the final climb count. The turns while a vision holds the knob do not count, because they do not push.
- **Days**: the days pushed through in all climbs. In a climb, they are its DAY number minus 1, so a climb on DAY 1 has none. At the top, at a move to another world, and at the Reset of the dev screen, the days of the climb add to the days before.
- **Right answers**: the `rightAnswers` that the game already saves.

## What the player sees

1. **The tabs.** The title row of the Worlds screen holds "Worlds" and "Labors". The shown tab is cream, the other one is gray. A tap on the other tab shows it. A tap on the shown tab closes the screen, as a tap on the title does today. The terra line stays under the title row.
2. **Opening.** A tap on the day opens the tab that was shown last since the app started. When the dot shows, it opens the Labors tab.
3. **The list.** Each category has a header, with its name in its color and how many of its labors are done, such as "2/5". Under it stands one card. The list scrolls as the list of worlds does: a drag, and a coast after a quick swipe.
4. **The card** shows, from the top:
   - the next labor of the category, with its name in cream and its progress at the right, in the color of its category: "5/10". The progress is short, so that it fits: "850/1k", "12k/100k", "123k/1M";
   - a bar of how far the next labor has come, in the color of its category;
   - a dot for each of the five tiers, filled when done, with the goal of the tier under it: "1 · 10 · 50 · 100 · 1k";
   - a check and the name of the last labor done, such as "Top of the Morning", in the color of its category.
5. **When all five are done,** the card shows the last labor, and a full bar.
6. **The news.** When a labor is done, the panel of the wise words shows "A labor is done: Rolling Stone." from "Hermes". The news comes in place of the next wise word. So it never cuts a question, a reply or other news short. While the Agora, the Worlds screen or the help hides the panel, the news waits. When more labors are done at once, each one gets its own news, one after the other.
7. **The dot.** A small bronze dot stands after "DAY n" while a labor is done that the player has not seen. It also stands after "Labors" on the tab. It goes away when the Labors tab opens.
8. **At the start.** Labors that are already done when the game starts, for example from the counts of an older saved game, get no news. They show the dot.
9. **No climbs under the day.** The main screen no longer shows the peak and the number of climbs under the day. The Labors tab shows them, in Hills climbed. The ending still says how many times Sisyphus reached the top.

The bottom row of the Worlds screen, "Restore" and the support ID, stays on both tabs.

The help says: "The sun moves only while you push. Tap the day for worlds and labors." The text is this short so that the notes of the help fit the scene on the smallest phone.

## Saved data

- `turnsPushed`: the player's own turns, in all climbs and worlds. It starts at 0, also in an older saved game. Kept at the top and in another world.
- `daysBefore`: the days pushed through in all the climbs before the current one. It starts at 0. Plus the days of the climb at the top, at a move, and at the Reset of the dev screen. Kept in another world.
- `laborsSeen`: how many labors were done when the Labors tab was last open. It starts at 0, so that an older saved game with a labor done shows the dot. Kept.
- `climbs` and `rightAnswers` stay as they are.
- `docs/saved-data.md` lists the new keys, with their start values for older saved games.

## To test it

- The dev screen gets a Labor button, in the row of the voices, that takes the knob turns to one click before the next turns tier. So the next click of the knob does a labor, the way it really happens.
- Tests: each count reaches each tier and does its labor; each labor gets its news once; the news waits for a vision to end; the dot shows until the Labors tab opens; the counts stay at the top, at a move, and at the Reset of the dev screen; a move on DAY 1 adds no day; a tap on the shown tab closes the screen.
- Every name, plain line, header, progress text and news uses only the letters of `PixelFont`, and fits the smallest phone.
- Snapshots: the Labors tab on the smallest phone with no labor done, and with some done; the day with the dot.

## Left to the builder

- The exact colors of the categories, from the colors that the game already uses.
- The exact sizes of the headers, the rows and the bands, and the scrollbar.
- Where the dev button goes on the dev screen.

## Later

- More categories: the top in every world, the sights (see Pegasus 10 times), the gods (meet the Sphinx, Hermes and Hecate).
- The ending shows the labors done in the climb.
