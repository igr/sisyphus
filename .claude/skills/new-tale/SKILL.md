---
name: new-tale
description: Use when the user asks for a new tale in the Sisyphus game, or asks to write, change or draw a tale that the stones by the path tell, such as the tale of Sisyphus, Typhon, Orpheus, or Isis and Osiris.
---

# New tale

A tale is 15 texts and one relief. The stones by the path tell it, one part at a time. The pool of tales is `TALES` in `game/Tales.kt`. The game, the scene, the worlds and the saved keys already work for each tale in `TALES`, so a new tale changes only the parts below. `docs/stations.md` is the spec: it has the rules, and the texts of each tale.

## What changes

| Part | Where |
|---|---|
| Texts | `val <NAME> = Tale(<id>, "<name>", listOf(Part("..."), ...))` after the last tale in `game/Tales.kt`, with a one-line KDoc as the others have. Add it at the end of `TALES`. The name stands on the open tale, on the Cards tab. |
| Id | The size of `TALES` before the new tale. The id is the position of the tale in `TALES` and in `RELIEF_ART`, and the saved key `tale` holds it. So a tale is never removed, moved or given a new id. `talesTold` keeps one bit for each id in an `Int`, so the last id is 30. |
| Relief | A new last entry of `RELIEF_ART` in `scene/Steles.kt`, with a comment such as `// Orpheus: a lyre`. It shows one thing that stands for the tale, in 7 rows of 7 characters: `k` is carved deep, `c` less deep, and `.` is not carved. The corners of its top row lie on the rim of the slab. Each world carves it in the colors of its stone. |
| Test | `TalesTest.thePoolHasTalesOfFifteenParts`: the new id, the new tale in the list, and its first and last texts. |
| Spec | `docs/stations.md`: the section of the tale, and the tale in each list of the tales. |

These tests already check each tale in `TALES`, so they need no change:

- `TalesTest.everyTextHasOnlyLettersOfTheFont`.
- `LookTest.theTextsOfTheTalesFitTheirRows`: on the narrow phones, each text with its note takes at most 4 rows of the popup.
- `LookTest.thePopupOfAStoneFitsTheNarrowPhones`: on the narrow phones, the popup of each text stays under the day and the obols.
- `LookTest.everyStoneHasTheReliefOfEveryTale`: each stone has a relief of 7 by 7 for each tale.
- `StationsTest`: the choice of the tale of a climb.
- `SnapshotTest.steles`: the sheet `steles.png` shows each relief on the stone of each world. It gets a column for the new tale.
- `ScreenTest.theCardsFitTheSmallestPhone`: the card of each tale on the Cards tab, and its name and each text on the open tale.
- `SnapshotTest.jokerCards`: the Cards tab, with a card for each tale.

## Steps

1. Write the sheet, and show it to the user in chat:
   - The name of the tale, and one line on why it is in the pool. Link it to Sisyphus, to a world or to the game, as the other tales do in `docs/stations.md`.
   - The 15 texts in a table, each with its source.
   - The relief as a grid of 7 by 7.

   Write no file before the user agrees to the sheet.
2. Before any change, run `./gradlew :composeApp:jvmTest` and copy `composeApp/build/snapshots` to the scratchpad.
3. Add the section `### The tale of <name>` after the last tale in `docs/stations.md`: the id, the line on why, "Relief: <thing>.", and the table `| # | Text |`. Copy the texts from this table to the code word for word.
4. Change `TalesTest`. Run it, and see it fail.
5. Add the tale to `game/Tales.kt`. Run the tests: `LookTest.everyStoneHasTheReliefOfEveryTale`, `LookTest.thePopupOfAStoneFitsTheNarrowPhones` and `SnapshotTest.steles` fail. A stone of the new tale has no relief yet, so it cannot be drawn.
6. Add the relief. Run `./gradlew :composeApp:jvmTest :composeApp:testAndroidHostTest`, then `./gradlew :androidApp:assembleDebug :composeApp:compileKotlinIosSimulatorArm64`. If a text does not fit the popup, make it shorter in the code and in the doc, and tell the user.
7. Compare the snapshots with `cmp`. `steles.png` changes, and so do the shots of the Cards tab that show the tales, such as `14-tales.png`: their count and the new card. Look at its new column, and show it to the user: the relief must show on each stone, also on the dark stone of The Underworld.
8. Update the docs:
   - `docs/stations.md`: the names in "Goals", the ids in "The pool", the reliefs in "The art", the bits of `talesTold` in "Saved data", and the sources of the tale in "Sources". If "Later" names the tale, remove it there.
   - `docs/saved-data.md`: the bits of `talesTold`, and the ids of the tales in "Ids that never change".
   - The Economy page, `analysis/economy.html`: the tales of the stones in "What the game holds", with their names and texts, and the date of the count. Then publish the page again to the same artifact. Its link is not in the repo: if you do not have it, ask the user.
9. Tell the user when players meet the tale. Ask before you commit.

## Rules for the texts

- 15 texts, one for each part. As in the other tales, each text has 10 to 17 words and at most 88 characters, in short sentences, because a part shows for only 6 turns of walking. Most texts fill the 4 lines of the popup, so only `LookTest.theTextsOfTheTalesFitTheirRows` can tell if a text fits.
- Plain words, warm and calm, like the wise words. Third person, past tense. Tell what the myths tell, and do not judge. Tell a death or a crime in few words, with no detail.
- Part 1 starts the tale. Part 15 ends it: it comes at the start of the final climb, before the top.
- A tale shows in every world. So no text points to the place where it shows, such as "here" or "this hill", and the teller never speaks to the player with "you" or "we". Only a spoken line in quotes can do these.
- The names of places in the myths are good: Corinth, Egypt, Lycia.
- Use the names that the game already uses. For example, the item Bellerophon in `game/Shop.kt` is "His grandson, the rider of Pegasus".
- Only the letters of `PixelFont`. Write numbers in words.
- Take each fact from an ancient source. List the sources in "Sources" of `docs/stations.md`, with the book and the lines, as the other tales do. Give the numbers of Perseus, as the doc does. Read each passage itself, not a summary of it, and not from memory.

## When players meet a new tale

`nextTale()` in `game/Game.kt` gives the tale of Sisyphus first, then a tale not told yet, at random. So a player who told all the other tales hears the new tale in the next climb. Other players meet it among the tales that they did not tell yet. The update does not change the tale of a climb that has started. On the Cards tab, the card of the new tale is a smooth stone until the stones tell its first part.

## To see it in the game

Only when the user asks, and ask first, because it changes their save: on the simulator, quit the app, set the saved key `tale` to the new id, start the app, and tap Stone in the dev screen. The app saves its keys every second, so a change made while it runs is lost. `docs/saved-data.md` tells where each platform keeps the keys. Do not use `just run`: the desktop game has the user's real save.
