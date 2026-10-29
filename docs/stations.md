# Stations

Design for the stations: carved stones by the path that tell a tale, from day 3 of each climb, and one more part for each item bought. Each climb tells one tale from a pool of tales, in every world. The tale of Sisyphus comes first.

## Why

The middle of a climb has few new things. At 5 minutes a day, a session is about 300 turns, and the only news is the next item. A tale gives each session something to wait for: what the next stone says. The tale of Sisyphus tells why he pushes the rock, so the top and the words of Camus mean more. The other tales give each new climb, in each world, a new story.

## Goals

- From day 3 of each climb, steles, carved stones, stand by the path ahead, one at a time: the first with no purchase, and one more for each item bought. When he comes near a stone, a comic popup over it tells the next part of the tale, and how far the tale has come, such as "9/15".
- A tale has 15 parts: the first on day 3, then one for each item bought, up to the 14th. The 15th part comes at the start of the final climb. Thus every player hears the whole tale before the top, at any pace.
- At 5 minutes a day, a session brings about 2 parts. At 30 minutes a day, it brings about 7.
- A pool of tales: Sisyphus, Typhon, Orpheus, and Isis and Osiris. Each climb tells one of them. The tale of Sisyphus comes first, then each other tale once, at random. After that, any tale can come again, but not the tale of the climb before.
- Every world has stones. The stone is of the world; the relief is of the tale.

## Not in this project

- Rewards of any kind. A stone gives no obols, and it does not make a climb shorter or longer.
- A page that lists the tales and the parts told so far.
- A choice of the tale by the player.

## Must not happen

- A popup covers Sisyphus, the rock, the day or the obols.
- A part comes twice, or a part is missed, in one climb.
- A climb changes its tale before it ends.
- A stone moves Sisyphus, stops him, or stands in front of him or the rock.
- The gods bring a part. Only the player's own turns walk Sisyphus to a stone, as only they move him up the hill.
- The choice of a tale changes the visions or the sights. It takes no number from the random numbers of the game.

## How it works

- **The stones.** On day 3 of a climb, `Config.TALE_DAY`, the first stone stands by the path 30 turns of walking ahead: 360 pixels, as one turn walks 12 pixels without a rock. Each item bought brings one more part, up to the 14th, and its stone stands 30 turns ahead once the part before it is told. Before day 3, no stone stands: the parts of the items bought then come from day 3, one after the other. With a rock, he walks faster, so he reaches a stone in 20 to 30 turns.
- **The popup.** When the rock is 3 turns of walking from the stone, `Config.NEAR_TURNS`, a comic popup opens over it, with a pop, `popup.wav`, unless the sound is off. It shows the text of the part, and at the right of its last line how far the tale has come, such as "9/15". It stays for 6 turns of walking, `Config.READ_TURNS`: the rock reaches the stone in the middle of them. Then the part is told. A part with two texts shows them one after the other, 6 turns each. The wise words go on in the panel as before.
- **One at a time.** When the player buys two or more items together, their stones stand one after another, 30 turns apart.
- **The visions.** A vision hides the popup. A vision also stops him, so the popup waits, and it shows again after the vision. A part is not told while a vision shows.
- **The final climb.** The 15th item begins the final climb, and Hermes says that the top is in sight. The 15th stone stands 30 turns ahead, so its part comes before the first herm at 100 turns.
- **The stones stay.** A stone whose part was told stays by the path, and then behind him, like the props. A climb has at most 15 stones.
- **A restart.** The next stone stays where it stands until its part is told. When the app starts again in the middle of a popup, the popup goes on where it was.
- **The pool.** Each tale has 15 parts, and an id that never changes: Sisyphus 0, Typhon 1, Orpheus 2, Isis and Osiris 3. Any tale can be told in any world, so no text names the place where it is told.
- **The tale of a climb.** A climb takes its tale when it begins: in a new game, at the top, at a move to another world, and at the Reset of the climb in the dev screen. A move before he has walked in the climb, as right after the top, keeps the tale that the climb took. While the tale of Sisyphus is not told, it is the tale. After it, the tale is a random one that is not told yet. A tale is told when its 15th part is told. When all are told, the tale is a random one of them, but not the tale of the climb before. A tale stays told. A climb that ends before its tale is told leaves that tale not told, so it can come again. The Reset of the shop in the dev screen starts the same tale again from its first part.
- **The stones of a world.** All stones have the same shape. Each world has its own stone: pale limestone in Corinth, dark lava rock on Mount Etna, black stone in The Underworld, and sandstone in Cairo. The relief is carved in the colors of that stone.
- **Old saved games.** A climb saved before the stations counts the items already bought as parts told. Its next item brings the next part. A climb saved before the pool tells the tale of Sisyphus, and no tale counts as told.
- **The dev screen.** In dev builds, Stone in the row of the climb sets the next stone a turn into its popup, also before its part is due, and closes the Agora, so that its part shows at once. To make room, Next obol is now Obol.
- **A known limit.** The next stone stands only after the part before it is told, so each part takes 33 turns of walking: 27 to its popup, and 6 in it. The final climb has room for 13 parts. When 14 or more parts still wait as it begins, the last of them are not told before the top, and no popup shows at the top. This needs 13 items bought with no walk between them, so it does not happen in a normal climb.

## The tales

Each tale has one relief: the small picture carved on all of its stones. So a new tale needs only its texts and one relief. The popup shows only the text. At the top, Camus ends every tale: "One must imagine Sisyphus happy."

### The tale of Sisyphus

Id 0. The first text changed from "Long ago, this city was Ephyra." to name the city, as the tale is told in every world. Relief: a rock.

| # | Text |
|---|---|
| 1 | Long ago, Sisyphus built the city of Ephyra, and he became its king. |
| 2 | His ships sailed on two seas, and his city grew rich. Later, men called it Corinth. |
| 3 | Homer calls him the craftiest of men. Even the gods could not trust him. |
| 4 | The thief Autolycus stole his cattle, and changed their colors. |
| 5 | But under every hoof, Sisyphus had cut the words: stolen by Autolycus. |
| 6 | A dolphin carried a drowned boy to shore. Sisyphus buried him by a pine. |
| 7 | For the boy, he began the Isthmian Games. Winners wore a crown of pine. |
| 8 | One day he saw Zeus carry off Aegina, the daughter of the river god Asopus. |
| 9 | "Give my city a spring," he told Asopus, "and I will tell you where she is." |
| 10 | Zeus was angry. He sent Death with chains, to take the king below. |
| 11 | "Show me how the chains work," said Sisyphus. Then he chained Death himself. |
| 12 | With Death in chains, nobody died, not even in battle. So Ares set Death free. |
| 13 | Before he went below, he told Merope, his wife: "Do not bury me." |
| 14 | Below, he told Persephone: "My wife did not bury me. Let me go and punish her." Then: She let him go. He stayed in the sun, until Hermes took him down by force. |
| 15 | For his tricks, the gods gave him a rock. He must push it to the top, forever. |

### The tale of Typhon

Id 1. Like Sisyphus, Typhon carries his punishment forever: a mountain on top of him. Relief: a volcano.

| # | Text |
|---|---|
| 1 | The gods won their war with the Giants. The Earth, mother of the Giants, was angry. |
| 2 | She gave birth to Typhon, taller than the mountains. His head hit the stars. |
| 3 | A hundred snake heads grew from his hands, and fire flashed from his eyes. |
| 4 | He threw burning rocks at the sky. The gods were afraid of him. |
| 5 | They ran away to Egypt, and they hid there as animals. |
| 6 | Only Zeus stayed to fight him, with his lightning and a sickle. |
| 7 | Typhon caught Zeus in his coils, and cut the sinews of his hands and feet. |
| 8 | He hid the sinews in a cave, in a bearskin, and a dragon guarded them. |
| 9 | Hermes and Pan stole the sinews back, and fitted them into Zeus again. |
| 10 | Zeus rose in a chariot of winged horses, and his lightning fell like rain. |
| 11 | The Fates gave Typhon fruit. "It will make you strong," they said. It made him weak. |
| 12 | Typhon threw whole mountains at Zeus. The lightning threw them back at him. |
| 13 | His blood ran down a mountain in Thrace. Men named it Haemus, the mountain of blood. |
| 14 | He ran over the sea to Sicily. There Zeus threw Mount Etna on top of him. |
| 15 | He lies there still. When he turns, the earth shakes, and fire comes out of Etna. |

### The tale of Orpheus

Id 2. When Orpheus sang below, Sisyphus sat on his rock: the only rest that the myths give him. Relief: a lyre.

| # | Text |
|---|---|
| 1 | Orpheus was the son of a Muse. When he sang, trees and stones came near to hear. |
| 2 | He sailed with Jason on the Argo. His song was louder than the song of the Sirens. |
| 3 | He married Eurydice, but at the wedding the torch only smoked. It was a bad sign. |
| 4 | Soon after, a snake bit Eurydice as she walked in the grass, and she died. |
| 5 | Orpheus sang his grief on earth. Then he went down to the dead to find her. |
| 6 | The dead came near to hear him. Even Cerberus, the dog with three heads, stood still. |
| 7 | He sang to Hades and Persephone: "Love brought me here. Give her back to me." |
| 8 | "When her years are over, she is yours again. I ask for a loan, not a gift." |
| 9 | The dead wept. The wheel of Ixion stood still, and Sisyphus sat down on his rock. |
| 10 | Persephone called Eurydice. She came slowly, for her wound still hurt. |
| 11 | "Do not look back at her," said Hades, "until you come out into the light." |
| 12 | They climbed a steep, dark path, in silence, through thick mist. |
| 13 | Near the top, afraid for her, and longing to see her, he looked back. |
| 14 | She died a second time, but she did not blame him. He had only loved her. |
| 15 | After his death, he found her below. Now they walk together, and he can look back. |

### The tale of Isis and Osiris

Id 3. Isis finds the pieces of Osiris one by one, as the player finds the parts of the tale. Relief: the eye of Horus.

| # | Text |
|---|---|
| 1 | Osiris was the king of Egypt. He taught men to sow wheat, and he gave them laws. |
| 2 | His brother Set wanted the throne. He measured Osiris in secret, and made a fine chest. |
| 3 | At a feast, Set said: "The chest is for the one who fits in it." Everyone tried. |
| 4 | When Osiris lay down in it, Set shut the lid, and threw the chest into the Nile. |
| 5 | The river carried it to the sea, and the sea carried it to Byblos. |
| 6 | A tree grew around the chest. The king of Byblos made a pillar of the tree. |
| 7 | Isis, the wife of Osiris, cut her hair, and walked the world to find him. |
| 8 | In Byblos, she turned into a swallow, and flew around the pillar, crying. |
| 9 | She took the chest home to Egypt, and hid it in the marshes. |
| 10 | Set found it while he hunted by moonlight. He cut Osiris into fourteen pieces. |
| 11 | He threw the pieces all over Egypt, so that nobody could find them. |
| 12 | Isis sailed the marshes in a boat of papyrus. Even the crocodiles let her pass. |
| 13 | She found the pieces one by one, and she built a tomb for each. |
| 14 | Their son Horus grew up, and he fought Set, and he won. |
| 15 | Osiris became the king of the dead. Before him, each heart is weighed against a feather. |

## Texts

- Plain words, warm and calm, like the wise words. A tale tells what the myths tell, and it does not judge.
- On the smallest phone, each text fits in the popup with its note, under the day and the obols: at most 4 lines. A test in `LookTest` checks every text.
- A text uses only the letters of `PixelFont`.

## Sources

- Homer, Iliad 6.153: the craftiest of men. Odyssey 11.593–600: the rock.
- Apollodorus, Library 1.9.3: Sisyphus founds Ephyra, tells Asopus about Aegina, and pushes a rock in Hades.
- Pausanias, Description of Greece 2.1.3: the dolphin, Melicertes and the Isthmian Games. 2.5.1: the spring on the hill of Corinth, for Asopus.
- Pherecydes, in the scholia on the Iliad 6.153: Death in chains, Ares, Merope, and the way back from below.
- Theognis 702–712: Sisyphus persuades Persephone.
- Hyginus, Fabulae 201: Autolycus and the marked hooves.
- Apollodorus, Library 1.6.3: Typhon, the sinews, Hermes and Aegipan (here Pan), the fruits of the Fates, Haemus, and Etna. Ovid, Metamorphoses 5.321–358: the gods hide in Egypt as animals, and Etna lies on Typhon. Pindar, Pythian 1.15–28: the fire of Etna.
- Ovid, Metamorphoses 10.1–77 and 11.61–66: Orpheus and Eurydice, Sisyphus sits on his rock, the loan, the look back, and the walk together below. Virgil, Georgics 4.453–527: Cerberus. Apollonius of Rhodes, Argonautica 4.891–911: the Sirens.
- Plutarch, On Isis and Osiris 13–19: the chest, Byblos, the swallow, the fourteen pieces, the papyrus boat, the tombs, and Horus. The Book of the Dead, spell 125: the heart weighed against a feather.
- Albert Camus, The Myth of Sisyphus (1942).

## The art

- **The stone:** a slab with a rounded top, about 9 pixels wide and 15 pixels high, standing on the slope like the props, a little in front of the path. Its relief, about 7 by 7 pixels, fills the top half. Short lines of carved letters fill the bottom half.
- One sprite for the stone, in the colors of each world: pale limestone, dark lava rock, black stone, sandstone. One relief for each tale: a rock, a volcano, a lyre, and the eye of Horus, carved in the colors of the stone. The light of the time of day falls on it, as on the props.
- **The popup:** a bubble of paper (F1E4C8) with a dark outline (2A1A0C) and round corners, 2 pixels from the edges of the scene. The text is dark, from the left; the note is terra (C8643C). Its tail points down at the stone and ends 3 pixels above the top of the rock, so that it covers neither him nor the rock. The light of the time of day does not fall on it. `drawPopup` in `gfx/Popup.kt` draws a popup for any text, so other speakers can use it.

## Saved data

- `stelesTold`: the parts told in this climb, 0 to 15. Back to 0 when a climb begins.
- `nextStele`: where the next stone stands, in pixels walked, or -1 when no stone stands ahead. It stays until the part of the stone is told. Back to -1 when a climb begins.
- `tale`: the id of the tale of this climb. Old saved games: 0, the tale of Sisyphus.
- `talesTold`: the tales told, one bit for each id: 1 for Sisyphus, 2 for Typhon, 4 for Orpheus, 8 for Isis and Osiris. Old saved games: 0.
- `docs/saved-data.md` lists the new keys, with their start values for old saved games.

## To test it

- Tests: the first stone stands 30 turns ahead on day 3, with no item bought; no stone stands before day 3; a stone stands 30 turns ahead after an item is bought; its popup opens 3 turns before the rock reaches it, and the part is told 6 turns after it opens; a part with two texts shows them one after the other; the panel shows no part; the parts come in order, once each; two items bought together give two stones, 30 turns apart; the gods' pushes do not reach a stone; a popup waits for a vision; the final climb does not cut a part; the 15th part comes before the first herm; no popup shows at the top; the stones, the parts told and an open popup survive a restart; a new climb starts its tale from the first part; every text fits the popup on the smallest phone, and a vision hides it; the dev screen's Stone shows the next part at once, and its row fits the smallest phone.
- Tests of the pool: the first climb tells the tale of Sisyphus; it comes again until it is told; the next climbs tell each other tale once, before any tale comes again; after that, the tale of the climb before does not come at once; a climb keeps its tale across a restart; the choice takes no number from the random numbers of the game; an old save tells the tale of Sisyphus; every world has stones, in its own colors; the stones of a tale share its relief.
- Snapshots: a stone ahead of the rock with its popup, also on the smallest phone, and in each world; the stone of each world with the relief of each tale.

## Later

- More tales in the pool: Bellerophon, the grandson of Sisyphus, who caught Pegasus at Peirene's spring; Empedocles, who leapt into Etna.
- A labor for the tales told.
- The tales told, on a tab of the Worlds screen.
