---
name: new-joker
description: Use when the user asks for a new joker in the Sisyphus game, or asks to add, write, draw or change a joker, a figure who stands by the path and tells a joke or a riddle in a bubble, such as Diogenes, Alice or Sherlock Holmes.
---

# New joker

A joker is a figure from the public domain who stands by the path and tells a joke or a riddle in a bubble. Each joker has a chance, a sprite, a card on the Jokers tab, and a few lines. The jokers are `JOKERS` in `game/Jokers.kt`. The game, the bubble, the cards, the saved keys and most tests already work for each joker in `JOKERS`, so a new joker changes only the parts below. `docs/jokers.md` is the spec: it has the rules, the table of the jokers, and the lines of each.

## What changes

| Part | Where |
|---|---|
| The joker | `Joker(<id>, "<name>", <weight>, "<who>", listOf(Joke(...), ...))`, last in `JOKERS` in `game/Jokers.kt`. "Who" is the text of the open card: who the joker is, the work and its year. |
| Id | The size of `JOKERS` before the new joker. The id is the position in `JOKERS` and in `JOKER_SPRITES`, and the place of the card on the tab. The saved keys `joker` and `met.<id>` hold it. So a joker is never removed, moved or given a new id, and a new joker is always the last card. `jokersSeen` keeps one bit for each id in an `Int`, so the last id is 30. |
| Weight | Out of `JOKER_WEIGHTS`, 500: the weights always add up to 500. Choose a chance between those of the other jokers; two jokers can have the same chance. Take the weight from Diogenes, the most common, and keep him at 201 or more, so that his card still says "1 in 2". When he is at 201, take it from the next most common joker, and keep the chance that its card shows. The open card shows the chance, `500 / weight` rounded: weight 15 is "1 in 33". A chance of 1 in 100 or rarer, a weight of 5 or less, makes the joker rare: his card gets a gold banner, and `JokersTest.theRarestJokersAreRare` lists him. |
| Sprite | A new last entry of `JOKER_SPRITES` in `scene/Jokers.kt`, with a one-line KDoc, as the others have. Use `SKIN`, or give `s` and `S` of its own for another skin, as Cleopatra does. |
| Test | `JokersTest`: the new id and name in `theJokersAreInTheOrderOfTheirIds`, and the weights in `theWeightsAddUpToFiveHundred` with the chances. `everyJokerHasAsManyLinesAsHisWeight` checks the number of lines. |
| Spec | `docs/jokers.md` and `docs/saved-data.md`. |

These tests already check each joker in `JOKERS`, so they need no change:

- `JokersTest.everyTextHasOnlyLettersOfTheFont` and `eachJokerComesAsOftenAsHisWeightSays`.
- `LookTest.everyJokerHasASprite`: the size of the sprite, and that it differs from the others.
- `LookTest.theTextsOfTheJokersFitTheirRows` and `theBubbleOfAJokerFitsTheSmallestPhone`.
- `ScreenTest.theCardsFitTheSmallestPhone`: the card, and the name and "who" on the open card.
- `JokersOnThePathTest`: the bubble, the meetings, the sound and the dev button.
- `ScreenTest.theCardsShowNoRank` and `theCardsShowTheJokersMet`: they meet and see every joker.
- `SnapshotTest.jokers`: `jokers.png` gets the new joker.

The Jokers tab does not scroll. When the cards no longer fit the smallest phone, `ScreenTest.theCardsFitTheSmallestPhone` fails: then ask the user.

## Steps

1. Run `git status`. If other work is not committed, ask the user before you mix it into this change. Check that the joker is in the public domain everywhere (see "Rules for the joker"). If you are not sure, ask the user.
2. Write the sheet, and show it to the user in chat:
   - the name, the id, the weight and the chance, and the new weight of Diogenes
   - the look in words, and the sprite as text art
   - who the joker is, for the open card
   - the lines in a table, each with its source

   Before you show it, check each text: the letters of the font, and the rows of the bubble, with a throwaway copy of `PixelFont.wrap` (`gfx/PixelFont.kt`) and `popupRows` (`gfx/Popup.kt`). So a text that the user agreed to does not change later. Write no file before the user agrees.
3. Before any change, run `./gradlew :composeApp:jvmTest` and copy `composeApp/build/snapshots` to the scratchpad.
4. Add the joker to `docs/jokers.md`: a row in the table of the jokers, a section `### <name>` after the last joker with the table `| # | Line |`, and the sources. Copy the texts from the doc to the code word for word.
5. Change `JokersTest`. Run it, and see it fail.
6. Add the joker to `game/Jokers.kt`, and change the weight of Diogenes. Run the tests: the tests that draw the jokers fail, because the sprite is missing.
7. Add the sprite. Run `./gradlew :composeApp:jvmTest :composeApp:testAndroidHostTest`, then `./gradlew :androidApp:assembleDebug :composeApp:compileKotlinIosSimulatorArm64`. If a text does not fit its rows, make it shorter in the code and in the doc, and tell the user.
8. Add `met.<id>` to the store of `SnapshotTest.jokerCards`, so that `14-jokers.png` shows the new card face up. Compare the snapshots with `cmp`: `jokers.png`, `14-jokers.png`, `14-jokers-small.png` and `14-card.png` change. Another image can change only where a new game meets its first joker, because the new weights change which joker comes: look at each one. Send `jokers.png` and `14-jokers.png` from `composeApp/build/snapshots` to the user.
9. Update the counts and the lists in the docs: search `docs/jokers.md` and `docs/saved-data.md` for the number of jokers, the last id (such as "met.9" and "0 to 9"), its bit (such as "512"), the old weight of Diogenes, and the name of the last joker.
   - The Economy page, `analysis/economy.html`: the jokers in "What the game holds", with their lines, jokes, riddles and texts, and the date of the count; in "The jokers", a row for the new joker, the new weight of Diogenes, and the row of all the jokers. Then publish the page again to the same artifact. Its link is not in the repo: if you do not have it, ask the user.
10. Tell the user how often players meet the joker: about once in 750 / weight game days, which is about 54,000 / weight turns. Ask before you commit.

## Rules for the joker

- A figure from a work that is in the public domain everywhere: published before 1930, by an author who died more than 70 years ago, or from folk tales, myth or history. The look follows the old books and pictures, not a film. No figure that a company owns, such as Tarzan or Zorro; a name that firms also use, such as Robin Hood, is fine.
- Not a joker that is already there, and not a figure of the game: the items in `game/Shop.kt`, the gods in `game/Voices.kt`.
- The sprite is at most 15 by 22, so that it fits its card with its outline. It faces left, down the hill, with the feet on the last row. Every letter of the art must be in its colors: `sprite()` makes any other letter a hole. It must show on the grass and at night: look at the two rows of `jokers.png`. Not all in the green of the grass.
- The name fits the open card, and "who" fits its 5 rows.

## Rules for the lines

- A joker has at least as many lines as his weight, and at least 2: weight 15 needs 15 lines. So each joker comes back to a line after some 750 game days. `JokersTest.everyJokerHasAsManyLinesAsHisWeight` checks. Weight that a new joker takes from Diogenes leaves Diogenes with more lines than he needs, which is fine.
- A line is a joke or a riddle in the voice of the joker, with one or two texts. A riddle gives its answer as the second text. Most answers turn to Sisyphus, his rock or the climb. A joker only talks: no obols, no tricks.
- On the smallest phone, with the name as the note, the first text takes at most 3 rows of the bubble and the second at most 4. The first text shows while the joker stands ahead, higher on the slope, and a shorter bubble stays above his head.
- A joker stands in any world, so no line names the place where he stands. Warm and short, with no cruelty, and no joke that another joker tells.
- Only the letters of `PixelFont`. The jokes are new, or words from a public-domain work; old folk riddles are free. List the sources in "Sources" of `docs/jokers.md`. Read each passage itself, not a summary of it, and not from memory.

## When players meet a new joker

A joker of weight w comes w times in 500 jokers, and a joker comes about once a game day and a half: so about once in 750 / w game days. A save from before the new joker reads `met.<id>` as 0: his card is face down until the player meets him. The dev screen's Joker, in the row "Worlds and jokers", brings the jokers in the order of the cards, so the new joker comes at the last tap of a round.

## To see it in the game

Only when the user asks, and ask first, because it changes their save: on the simulator, tap Joker in the row "Worlds and jokers" of the dev screen until the new joker comes. Each tap brings the next joker in the order of the cards, from the first after the app starts, so the new joker comes at tap id + 1. Do not use `just run`: the desktop game has the user's real save.
