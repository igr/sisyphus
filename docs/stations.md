# Stations

Design for the stations: carved stones by the path that tell a tale, one part for each item bought. In Corinth, they tell the tale of Sisyphus himself.

## Why

The middle of a climb has few new things. At 5 minutes a day, a session is about 300 turns, and the only news is the next item. A tale gives each session something to wait for: what the next stone says. In Corinth, the stones tell why Sisyphus pushes the rock, so the top and the words of Camus mean more.

## Goals

- When the player buys an item, a stele, a carved stone, stands by the path ahead. When Sisyphus walks past it, the next part of the tale types out in the panel of the wise words.
- A tale has 15 parts, one for each item. The 15th part, the rock, comes at the start of the final climb. Thus every player hears the whole tale before the top, at any pace.
- At 5 minutes a day, a session brings about 2 parts. At 30 minutes a day, it brings about 7.
- Corinth tells the tale of Sisyphus. Some parts name what the player buys in the Agora: Merope, Death (Thanatos), Hermes and the dolphin.

## Not in this project

- The tales of the other worlds. Mount Etna, The Underworld and Cairo get no stones yet. See "Later".
- A new tale for the next climb. The next climb in Corinth tells the same tale again. See "Later".
- Rewards of any kind. A stone gives no obols, and it does not make a climb shorter or longer.
- A page that lists the parts told so far.
- A sound for a stone.

## Must not happen

- Two texts in the panel at the same time.
- A part comes twice, or a part is missed, in one climb.
- A stone moves Sisyphus, stops him, or stands in front of him or the rock.
- The gods bring a part. Only the player's own turns walk Sisyphus to a stone, as only they move him up the hill.

## How it works

- **A stone for each item.** When the player buys an item, a stone stands by the path 30 turns of walking ahead: 360 pixels, as one turn walks 12 pixels without a rock. With a rock, he walks faster, so he reaches it in 20 to 30 turns.
- **The part.** When Sisyphus walks past the stone, its text types out in the panel, as the wise words do. The place stands under it, as the author stands under the wise words, for example "— Peirene's spring". The wise words wait, and they go on after the reading time of the text. A part with two texts shows them one after the other.
- **One at a time.** When the player buys two or more items together, their stones stand one after another, 30 turns apart.
- **The visions.** A text does not come while a vision shows: Sisyphus walks past the stone, and its text comes after the vision. When a vision comes while a text shows, the text stops, and it comes again after the vision.
- **The final climb.** The 15th item begins the final climb, and Hermes says that the top is in sight. The 15th stone stands 30 turns ahead, so its part comes before the first herm at 100 turns.
- **The stones stay.** A stone that he walked past stays by the path behind him, like the props. A climb has at most 15 stones.
- **A new climb.** At the top, at a move to another world, and at the Reset of the dev screen, the tale starts again: no part is told, and no stone stands ahead. A world with no tale has no stones.
- **Old saved games.** A climb saved before the stations counts the items already bought as parts told. Its next item brings the next part.

## The tale of Sisyphus

Corinth. The place stands under the text in the panel. The relief is the small picture carved on the stone.

| # | Place | Relief | Text |
|---|---|---|---|
| 1 | the old walls | city walls | Long ago, this city was Ephyra. Sisyphus built it, and he became its king. |
| 2 | the two harbors | a ship | His ships sailed on two seas, and his city grew rich. Later, men called it Corinth. |
| 3 | the palace | a crown | Homer calls him the craftiest of men. Even the gods could not trust him. |
| 4 | the cattle pen | a bull | The thief Autolycus stole his cattle, and changed their colors. |
| 5 | the hoofprints | a hoof | But under every hoof, Sisyphus had cut the words: stolen by Autolycus. |
| 6 | the pine | a dolphin | A dolphin carried a drowned boy to shore. Sisyphus buried him by a pine. |
| 7 | the stadium | a crown of pine | For the boy, he began the Isthmian Games. Winners wore a crown of pine. |
| 8 | the cliff | an eagle | One day he saw Zeus carry off Aegina, the daughter of the river god Asopus. |
| 9 | Peirene's spring | water | "Give my city a spring," he told Asopus, "and I will tell you where she is." |
| 10 | the dark road | wings of Death | Zeus was angry. He sent Death with chains, to take the king below. |
| 11 | the chains | chains | "Show me how the chains work," said Sisyphus. Then he chained Death himself. |
| 12 | the battlefield | a helmet | With Death in chains, nobody died, not even in battle. So Ares set Death free. |
| 13 | the empty grave | a grave | Before he went below, he told Merope, his wife: "Do not bury me." |
| 14 | the gate below | a pomegranate | Below, he told Persephone: "My wife did not bury me. Let me go and punish her." Then: She let him go. He stayed in the sun, until Hermes took him down by force. |
| 15 | this hill | a rock | For his tricks, the gods gave him a rock. He must push it to the top, forever. |

At the top, Camus ends the tale: "One must imagine Sisyphus happy."

## Texts

- Plain words, warm and calm, like the wise words. The tale tells what the myths tell, and it does not judge him.
- Each text fits in 3 lines of the panel, and each place fits in the line under it, on the smallest phone. A test checks every text, as `QuotesTest` does for the wise words. Today all 16 texts fit.
- A text uses only the letters of `PixelFont`.

## Sources

- Homer, Iliad 6.153: the craftiest of men. Odyssey 11.593–600: the rock.
- Apollodorus, Library 1.9.3: Sisyphus founds Ephyra, tells Asopus about Aegina, and pushes a rock in Hades.
- Pausanias, Description of Greece 2.1.3: the dolphin, Melicertes and the Isthmian Games. 2.5.1: the spring on the hill of Corinth, for Asopus.
- Pherecydes, in the scholia on the Iliad 6.153: Death in chains, Ares, Merope, and the way back from below.
- Theognis 702–712: Sisyphus persuades Persephone.
- Hyginus, Fabulae 201: Autolycus and the marked hooves.
- Albert Camus, The Myth of Sisyphus (1942).

## The art

- **The stone:** a slab of pale limestone with a rounded top, about 9 pixels wide and 15 pixels high, standing on the slope like the props, a little in front of the path. Its relief, about 7 by 7 pixels, fills the top half. Short lines of carved letters fill the bottom half.
- One sprite for the stone, and 15 reliefs, listed in the table. The light of the time of day falls on it, as on the props.

## Saved data

- `stelesTold`: the parts told in this climb, 0 to 15. Back to 0 at the top and in another world.
- `nextStele`: where the next stone stands, in pixels walked, or -1 when no stone stands ahead. Back to -1 at the top and in another world.
- `docs/saved-data.md` lists the new keys, with their start values for old saved games.

## To test it

- Tests: a stone stands 30 turns ahead after an item is bought; walking past it tells the next part; the parts come in order, once each; two items bought together give two stones, 30 turns apart; the gods' pushes do not reach a stone; a text waits for a vision; the 15th part comes before the first herm; the stones and the parts told survive a restart; a new climb starts the tale again; a world with no tale has no stones; every text fits.
- Snapshots: a stone by the path, and its part in the panel, also on the smallest phone.

## Later

- The tales of the other worlds: Mount Etna (Empedocles, and Typhon under the mountain), The Underworld (Orpheus), Cairo (Isis and Osiris).
- A new tale for each climb in a world. In Corinth: Bellerophon, the grandson of Sisyphus, who caught Pegasus at Peirene's spring.
- A labor for the tales told.
- The tales told, on a tab of the Worlds screen.
