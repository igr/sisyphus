# Jokers

Design for the jokers: famous figures of old books and old times who stand by the path, about once a day or two, and tell a joke or a riddle in a comic bubble. Each joker has a card, like a playing card, on the third tab of the Worlds screen. A card turns face up when the player meets its joker.

## Why

The climb is long, and the same every day. A joker is a small surprise: who stands by the path today? Most days it is Diogenes with his lamp. Once in a long while, it is someone rare. The cards keep count, so a player has a reason to look at each new figure, and a hope for the rare ones.

## Goals

- On day 1 of each climb, and then about once a game day or two of walking, a joker stands by the path ahead. When the rock comes near, a bubble over the joker tells a joke or a riddle, with the name of the joker as its note.
- 10 jokers, all from the public domain: figures of old books, of folk tales and of history. Each has its own chance: from 1 in 2 for the most common, Diogenes, to 1 in 500 for the rarest, Cthulhu.
- A joker only talks: no obols, and no change to the climb.
- The bubble of a joker never shows with the popup of a stone, or during a vision.
- A third tab of the Worlds screen, Jokers, shows the 10 cards of the jokers. The card of a joker that the player has met is face up, and a tap on it shows who the joker is. The other cards are face down, and a tap does nothing.
- The jokers are the same in every world.

## Not in this project

- Answers by the player. A riddle shows its answer after the question; nothing is asked of the knob.
- Rewards: no obols, no labor for the jokers.
- Jokers who walk with him, or who come more than one at a time.

## Must not happen

- A bubble of a joker shows with the popup of a stone, or while a vision shows.
- A joker stands in front of him or the rock, or a bubble covers the day or the obols.
- A joker stops him, or moves him.
- The gods bring a joker. Only the player's own turns walk him to a joker, as only they walk him to a stone.
- The choice of a joker changes the visions, the sights or the tales. The jokers take no number from their random numbers.
- A joker talks at the top.

## How it works

- **Where a joker stands.** At the start of each climb, the first joker stands by the path 20 to 40 turns of walking ahead, at random, `Config.FIRST_JOKER`: day 2 begins after 53 turns, so he talks on day 1. After each joker, the next stands 72 to 144 turns of walking ahead, at random: one to two days at the pace of a new climb, `Config.JOKER_TURNS`. With a rock, he walks faster, and the days go faster too, so it stays one to two days.
- **Which joker.** The game takes the joker when it puts one by the path, at random, by the weights below. Each weight is out of 500, so the chance of Diogenes is 250 in 500: 1 in 2.
- **The bubble.** It works as the popup of a stone does. It opens when the rock is 3 turns of walking from the joker, `Config.NEAR_TURNS`. Each text shows for 6 turns of walking, `Config.READ_TURNS`. A riddle and most jokes have two texts: the question, then the answer. Its tail points down at the joker, from above his head or the rock, and its note is the name of the joker. It opens with a pop, `popup.wav` at half its volume, as the popup of a stone does, unless the sound is off. The pop plays once for each bubble: not again for its second text, after a vision, or after a restart.
- **Which line.** Each joker has a set of lines, and the most common jokers have the most. The game counts how many times the player met each joker, in all climbs and worlds. A joker tells the lines in turn: the first line the first time, the second line the next time, and after the last line the first again.
- **After the bubble.** The joker is met: the card turns face up, and stays face up. The joker turns to watch Sisyphus go, and stays by the path behind him, out of the picture. Then the next joker stands ahead.
- **Room for the stones.** A joker stands at least 15 turns of walking from the next stone, `Config.JOKER_ROOM`, so that his bubble and its popup do not meet. When a joker would stand nearer, or a new stone stands near a joker, the joker stands 15 turns past the stone. A new stone stands 30 turns ahead, so the joker that moves is never in the picture.
- **The visions.** A vision hides the bubble, as it hides the popup of a stone. A vision also stops him, so the bubble waits, and it shows again after the vision.
- **The top.** No bubble shows at the top. A new climb, a move to another world and the Reset of the climb put a new joker by the path, 20 to 40 turns into the new climb.
- **The light.** The light of the time of day falls on the jokers, as on the props. It does not fall on the bubble.
- **Old saved games.** A game saved before the jokers has met none. Its first joker stands 20 to 40 turns ahead.
- **The dev screen.** In dev builds, Joker in the row of the worlds, now "Worlds and jokers", brings the next joker in the order of the cards, a turn into the bubble. So a few taps show all 10. A stone too near that joker stands 15 turns past the joker.

## The jokers

The id is the order of the cards, from the most common to the rarest. It never changes.

| Id | Joker | Weight | Chance | Look | Who (on the card) |
|---|---|---|---|---|---|
| 0 | Diogenes | 250 | 1 in 2 | An old man with a white beard, a brown cloak and bare feet. He holds a lit lamp. | The Cynic of Corinth. He lived in a jar, and with a lamp by day he looked for an honest man. |
| 1 | Humpty Dumpty | 124 | 1 in 4 | A big egg with a face, a cravat and thin legs. | An egg from an old riddle, who talks to Alice in Through the Looking-Glass by Lewis Carroll, 1871. |
| 2 | Red Riding Hood | 62 | 1 in 8 | A girl in a red cape with a hood, with a basket. | A girl who met a wolf on the way to her grandmother, in the tale of Charles Perrault, 1697. |
| 3 | Don Quixote | 31 | 1 in 16 | A tall, thin knight with a beard, a basin for a helmet, and a long lance. | A knight who fought windmills as giants, in the book of Miguel de Cervantes, 1605. |
| 4 | Pinocchio | 15 | 1 in 33 | A wooden boy with a long nose and a pointed paper cap. | A wooden puppet whose nose grows when he lies, in the book of Carlo Collodi, 1883. |
| 5 | Cleopatra | 8 | 1 in 63 | A queen with a black bob, a gold band, a gold collar and a white dress. | The last queen of Egypt, the friend of Caesar and of Mark Antony. |
| 6 | Sherlock Holmes | 4 | 1 in 125 | A thin man in a deerstalker cap and a cape, with a pipe. | A detective of Baker Street, London, who sees what others miss. Arthur Conan Doyle, 1887. |
| 7 | Hamlet | 3 | 1 in 167 | A pale prince in black, who holds up a skull. | The prince of Denmark, with the skull of the jester Yorick. William Shakespeare, about 1600. |
| 8 | Count Dracula | 2 | 1 in 250 | A pale man with black hair and a black cape with a red lining and a high collar. | A count of Transylvania who sleeps by day and drinks blood by night. Bram Stoker, 1897. |
| 9 | Cthulhu | 1 | 1 in 500 | A small green figure with a head like an octopus, small wings and claws. | A great old one who sleeps, dreaming, in his sunken city of R'lyeh. H. P. Lovecraft, 1928. |

The jokers come from works that are in the public domain everywhere. Their looks follow the old books and pictures, not later films: Humpty Dumpty as John Tenniel drew him, Pinocchio with the paper cap of the book, Dracula with no look of a film.

At about 1.5 days of walking for each joker, a player meets Diogenes about every 3 days, and Cthulhu about once in 750 days: about 54,000 turns.

## The lines

A `|` is the change from the first text to the second. On the smallest phone, every text fits the bubble with its note in at most 4 rows, and the first text of a line in at most 3. The first text shows while the joker stands ahead, higher on the slope, so a bubble of 3 rows stays above his head. A test in `LookTest` checks this.

### Diogenes

| # | Line |
|---|---|
| 1 | I carry a lamp at noon. I am looking for an honest man. \| You push a rock, and you say that it is a rock. You may be the one. |
| 2 | Alexander the Great asked me what he could do for me. \| I said: Stand out of my sun. |
| 3 | Plato said: a man is an animal on two legs, with no feathers. \| So I brought him a plucked chicken: Behold, Plato's man! |
| 4 | I ask the statues for money. \| Why? To get used to no. |
| 5 | I threw away my cup when I saw a boy drink from his hands. \| You cannot throw away your rock. It always comes back. |
| 6 | Which wine do I like best? \| Someone else's. |
| 7 | When should a man eat? \| If he is rich, when he wants. If he is poor, when he can. |
| 8 | I live in a jar. It is round, like your rock. \| Mine does not roll. I made sure of that. |
| 9 | Where am I from? I am a citizen of the world. \| Most of the world is flat. Your part of it is not. |
| 10 | They call me the Dog, because I bark at fools. \| Do not worry. At you, I wag. |

### Humpty Dumpty

| # | Line |
|---|---|
| 1 | My words mean just what I choose them to mean. \| Today, rock means feather. There. Lighter already? |
| 2 | Riddle: what must be broken before you can use it? \| An egg. Please do not look at me like that. |
| 3 | Riddle: what can you break without touching it? \| A promise. Eggs need touching. Do not touch me. |
| 4 | Riddle: the more you take, the more you leave behind. \| Footsteps. You leave a great many. |
| 5 | Riddle: what goes up and never comes down? \| Your age. Not your rock. That one always comes down. |
| 6 | Today is my unbirthday. You have one most days too. \| Happy unbirthday. My present: I will not sit on your rock. |
| 7 | I sit on walls. You push rocks. \| We must both take care not to fall. Shake hands? Gently. |
| 8 | Riddle: what has a head and a tail, but no body? \| A coin. Not an egg. An egg is all body. Ahem. |

### Red Riding Hood

| # | Line |
|---|---|
| 1 | Is this the way to my grandmother's house? \| Past the man with the rock? Oh, that is you. Thank you! |
| 2 | Grandmother, what big ears you have! \| That was the wolf. Now I look at the ears first. |
| 3 | What do wolves say when they meet? \| Howl do you do? |
| 4 | I have cake and wine for my grandmother. \| Here, have a crumb. Pushing is hungry work. |
| 5 | My mother said: do not leave the path. \| You never leave the path. She would like you. |
| 6 | Riddle: what is red, has a basket, and is never late? \| Me. Well, once. There was a wolf. |

### Don Quixote

| # | Line |
|---|---|
| 1 | Halt! Is that a giant that you push? \| Ah, a rock. Carry on, good knight. |
| 2 | I once fought a giant with four long arms. \| Sancho says it was a windmill. Sancho sees too little. |
| 3 | A knight needs a quest. Yours is a fine one. \| Up, and up again. I will sing of it to Dulcinea. |
| 4 | My helmet is the golden helmet of Mambrino. \| A barber says it is his basin. Barbers know nothing. |
| 5 | Riddle: four arms, no hands, and it turns all day. \| A windmill. Or a giant. It depends on who you ask. |

### Pinocchio

| # | Line |
|---|---|
| 1 | I am not made of wood. \| Oops. My nose. Please look away. |
| 2 | Your rock looks very light today. \| There it goes again. Sorry. |
| 3 | Why do I always lose at hide and seek? \| My nose is always the first to be found. |
| 4 | Riddle: what grows when it tells a lie? \| My nose. Too easy? That is the truth. Look, no change. |

### Cleopatra

| # | Line |
|---|---|
| 1 | I came to Caesar rolled up in a carpet. \| Try it with your rock. Make an entrance. |
| 2 | I once melted a pearl in vinegar, and drank it, to win a bet. \| Never bet against a queen. |
| 3 | Why could I never see my mistakes? \| I was the queen of the Nile. And of denial. |

### Sherlock Holmes

| # | Line |
|---|---|
| 1 | I perceive that you push a rock uphill. \| How? You are pushing a rock uphill. Some cases are simple. |
| 2 | Rule out the impossible, and what is left is the truth. \| So: you will push it again tomorrow. |
| 3 | Riddle: the more of it there is, the less you see. \| The dark. Elementary. |

### Hamlet

| # | Line |
|---|---|
| 1 | Alas, poor Yorick! I knew him well. \| He told the best jokes. Now his head is the joke. |
| 2 | To push, or not to push: that is the question. \| You always give the same answer. I envy you. |
| 3 | Who builds stronger than a mason, a shipwright or a carpenter? \| The gravedigger. His houses last till doomsday. |

### Count Dracula

| # | Line |
|---|---|
| 1 | Good evening. I came out in the sun to meet you. \| Very brave of me. Also, I am on fire a little. Goodbye! |
| 2 | What is the favorite fruit of a vampire? \| A neck-tarine. |

### Cthulhu

| # | Line |
|---|---|
| 1 | Ph'nglui mglw'nafh Cthulhu R'lyeh wgah'nagl fhtagn. \| It means: nice rock. Roughly. |
| 2 | I sleep for ages. Then I wake. Then I sleep again. \| You push, it rolls down, you push again. We should talk. |

## Sources

- Diogenes Laertius, Lives of the Eminent Philosophers 6.20–81: Diogenes, his lamp, his jar, the plucked chicken, the statues, the cup, the wine, when to eat, and the citizen of the world. Plutarch, Life of Alexander 14: "Stand out of my sun."
- Lewis Carroll, Through the Looking-Glass (1871), chapter 6: Humpty Dumpty, his words and his unbirthday.
- Charles Perrault, Le Petit Chaperon rouge (1697), and the Brothers Grimm (1812): the cake and the wine, the path, and the big ears.
- Miguel de Cervantes, Don Quixote (1605), part 1, chapters 8 and 21: the windmills, and the helmet of Mambrino.
- Carlo Collodi, The Adventures of Pinocchio (1883): the nose.
- Plutarch, Life of Antony 25–29 and Life of Caesar 49: Cleopatra comes to Caesar rolled up. Pliny, Natural History 9.119–121: the pearl in vinegar.
- Arthur Conan Doyle, The Sign of Four (1890): the impossible and the truth.
- William Shakespeare, Hamlet (about 1600), act 5, scene 1: Yorick, and the riddle of the gravedigger.
- Bram Stoker, Dracula (1897).
- H. P. Lovecraft, The Call of Cthulhu (1928): the words of the cult.
- The other riddles are old folk riddles.

## The art

- **A joker:** a sprite of at most 15 by 22 pixels, so that it fits the card with its outline, a little shorter than Sisyphus, standing on the slope like the props, with the feet on the ground under the middle of the sprite. A joker faces down the hill, to Sisyphus, and turns to watch Sisyphus go once the rock has passed. A joker stands where the front of the rock reaches the middle of the sprite, as a stone does, and is drawn behind the rock and Sisyphus.
- **The bubble:** `drawPopup`, as for a stone. Its tail points at the middle of the joker; when the joker has left the picture, it points at the edge of the picture. The tip of the tail stands 3 pixels above the head of the joker, or above the rock when the rock is higher. The bubble stays under the day and the obols: when it would rise into them, it moves down.
- **A card:** like a small playing card, about as tall for its width, with round corners, 5 to a row. It has no rank: the chance shows on the open card. Face down, it shows the back of the deck: dark terra, with a pattern of crossed lines. Face up, it is paper, with the joker, outlined in dark, standing on its bottom. The outline lets the white and the cream of a joker show on the paper.
- **The open card:** a big card over the cards: the joker at twice the size, the name, the chance, such as "1 in 8", who the joker is, and how many times the player met the joker, such as "Met 3 times".

## The Jokers tab

- The tabs of the Worlds screen become Worlds, Labors and Jokers. The Worlds screen shows no obols, so the three words fit the smallest phone. The obols open the Agora from the main screen.
- Under the title row, "Jokers met" and how many of the 10 the player has met, such as "3/10". Then the 10 cards in two rows of 5, in the order of the ids.
- A tap on a face-up card opens it. A tap anywhere closes it. The back button closes it too.
- While a card is face up that the player has not seen on the tab, a dot stands after "Jokers", and the dot by the day shows, as for a new labor. The Worlds screen then opens on the tab with the news: Labors first, then Jokers.

## Saved data

- `nextJoker`: where the next joker stands, in pixels walked, or -1 when none stands ahead yet. Old saved games: -1, so a joker is put by the path at once.
- `joker`: the id of the next joker. Old saved games: 0. An id that the game does not have is Diogenes.
- `met.0` to `met.9`: how many times the player met each joker, by id, in all climbs and worlds. A joker met once or more has the card face up, and the count tells which line comes next. Kept at the top and in another world.
- `jokersSeen`: the cards seen face up on the Jokers tab, one bit for each id: 1 for Diogenes, 2 for Humpty Dumpty, up to 512 for Cthulhu. Kept.
- `docs/saved-data.md` lists the new keys.

## To test it

- The jokers: 10, with the ids 0 to 9 in order; the weights add up to 500, from 250 down to 1; each has a line, each line one or two texts; every text has only the letters of the font; every text fits the bubble with its note on the smallest phone.
- The choice: over many choices, each joker comes about as often as its weight says; the choice takes no number from the random numbers of the game, so the visions and the tales stay the same.
- On the path: the first joker of a climb stands 20 to 40 turns ahead and talks on day 1, and after each joker the next stands 72 to 144 turns ahead; the bubble opens 3 turns before the rock reaches the joker, and each text shows for 6 turns; after it, the joker is met once more, and the next joker stands ahead; the lines of a joker come in turn; a joker stands at least 15 turns from the next stone, also when the stone comes after him; a vision hides the bubble, and it waits; the gods' pushes do not reach a joker; no bubble at the top; a new climb puts a new joker ahead; the joker ahead, his id and the jokers met survive a restart.
- The tab: the tabs fit the smallest phone; a face-down card does not open; a face-up card opens, and a tap closes it; the dot shows for a new card, and goes when the tab opens; the Worlds screen opens on the tab with the news.
- The dev screen: Joker brings the next joker at once, and its row fits the smallest phone.
- Snapshots: Diogenes with his bubble, ahead of the rock and behind Sisyphus, also on the smallest phone; all 10 jokers in a row, as they are and in the light of the night; the Jokers tab with some cards face up, and the dot by the day; an open card.

## Later

- A labor for the jokers met.
- More jokers, and seasons for some of them.
