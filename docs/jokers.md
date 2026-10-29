# Jokers

Design for the jokers: famous figures of old books and old times who stand by the path, about once a day or two, and tell a joke or a riddle in a comic bubble. Each joker has a card, like a playing card, on the third tab of the Worlds screen. A card turns face up when the player meets its joker.

## Why

The climb is long, and the same every day. A joker is a small surprise: who stands by the path today? Most days it is Diogenes with his lamp. Once in a long while, it is someone rare. The cards keep count, so a player has a reason to look at each new figure, and a hope for the rare ones.

## Goals

- On day 1 of each climb, and then about once a game day or two of walking, a joker stands by the path ahead. When the rock comes near, a bubble over the joker tells a joke or a riddle, with the name of the joker as its note.
- 12 jokers, all from the public domain: figures of old books, of folk tales and of history. Each has a chance: from 1 in 2 for the most common, Diogenes, to 1 in 500 for the rarest, Cthulhu.
- A joker only talks: no obols, and no change to the climb.
- The bubble of a joker never shows with the popup of a stone, or during a vision.
- A third tab of the Worlds screen, Jokers, shows the 12 cards of the jokers. The card of a joker that the player has met is face up, and a tap on it shows who the joker is. The other cards are face down, and a tap does nothing.
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
- **Which joker.** The game takes the joker when it puts one by the path, at random, by the weights below. Each weight is out of 500, so the chance of Diogenes is 227 in 500: about 1 in 2.
- **The bubble.** It works as the popup of a stone does. It opens when the rock is 3 turns of walking from the joker, `Config.NEAR_TURNS`. Each text shows for 6 turns of walking, `Config.READ_TURNS`. A riddle and most jokes have two texts: the question, then the answer. Its tail points down at the joker, from above his head or the rock, and its note is the name of the joker. It opens with a pop, `popup.wav` at half its volume, as the popup of a stone does, unless the sound is off. The pop plays once for each bubble: not again for its second text, after a vision, or after a restart.
- **Which line.** Each joker has at least as many lines as his weight, and at least 2: Diogenes 227, Cthulhu 2. A joker of weight w comes once in about 750 / w game days, so each joker comes back to a line after some 750 game days, and none sooner. The game counts how many times the player met each joker, in all climbs and worlds. A joker tells the lines in turn: the first line the first time, the second line the next time, and after the last line the first again.
- **After the bubble.** The joker is met: the card turns face up, and stays face up. The joker turns to watch Sisyphus go, and stays by the path behind him, out of the picture. Then the next joker stands ahead.
- **Room for the stones.** A joker stands at least 15 turns of walking from the next stone, `Config.JOKER_ROOM`, so that his bubble and its popup do not meet. When a joker would stand nearer, or a new stone stands near a joker, the joker stands 15 turns past the stone. A new stone stands 30 turns ahead, so the joker that moves is never in the picture.
- **The visions.** A vision hides the bubble, as it hides the popup of a stone. A vision also stops him, so the bubble waits, and it shows again after the vision.
- **The top.** No bubble shows at the top. A new climb, a move to another world and the Reset of the climb put a new joker by the path, 20 to 40 turns into the new climb.
- **The light.** The light of the time of day falls on the jokers, as on the props. It does not fall on the bubble.
- **Old saved games.** A game saved before the jokers has met none. Its first joker stands 20 to 40 turns ahead.
- **The dev screen.** In dev builds, Joker in the row of the worlds, now "Worlds and jokers", brings the next joker in the order of the cards, a turn into the bubble. So a few taps show all 12. A stone too near that joker stands 15 turns past the joker.

## The jokers

The id is the order of the cards, and it never changes. The first ten go from the most common to the rarest; a new joker takes the next id, so its card comes last.

| Id | Joker | Weight | Chance | Look | Who (on the card) |
|---|---|---|---|---|---|
| 0 | Diogenes | 227 | 1 in 2 | An old man with a white beard, a brown cloak and bare feet. He holds a lit lamp. | The Cynic of Corinth. He lived in a jar, and with a lamp by day he looked for an honest man. |
| 1 | Alice | 124 | 1 in 4 | A girl with long pale hair, a blue band, a yellow dress with a white pinafore, and white stockings. | A girl who fell down a rabbit hole into Wonderland, and stepped through a mirror. Lewis Carroll, 1865. |
| 2 | Red Riding Hood | 62 | 1 in 8 | A girl in a red cape with a hood, with a basket. | A girl who met a wolf on the way to her grandmother, in the tale of Charles Perrault, 1697. |
| 3 | Don Quixote | 31 | 1 in 16 | A tall, thin knight with a beard, a basin for a helmet, and a long lance. | A knight who fought windmills as giants, in the book of Miguel de Cervantes, 1605. |
| 4 | Pinocchio | 15 | 1 in 33 | A wooden boy with a long nose and a pointed paper cap. | A wooden puppet whose nose grows when he lies, in the book of Carlo Collodi, 1883. |
| 5 | Cleopatra | 8 | 1 in 63 | A queen with a black bob, a gold band, a gold collar and a white dress. | The last queen of Egypt, the friend of Caesar and of Mark Antony. |
| 6 | Sherlock Holmes | 4 | 1 in 125 | A thin man in a deerstalker cap and a cape, with a pipe. | A detective of Baker Street, London, who sees what others miss. Arthur Conan Doyle, 1887. |
| 7 | Hamlet | 3 | 1 in 167 | A pale prince in black, who holds up a skull. | The prince of Denmark, with the skull of the jester Yorick. William Shakespeare, about 1600. |
| 8 | Count Dracula | 2 | 1 in 250 | A pale man with black hair and a black cape with a red lining and a high collar. | A count of Transylvania who sleeps by day and drinks blood by night. Bram Stoker, 1897. |
| 9 | Cthulhu | 1 | 1 in 500 | A small green figure with a head like an octopus, small wings and claws. | A great old one who sleeps, dreaming, in his sunken city of R'lyeh. H. P. Lovecraft, 1928. |
| 10 | Isaac Newton | 15 | 1 in 33 | An old scholar in a grey curly wig, a dark coat and a white cravat, with a red apple in his hand. | An English scholar who found the law of gravity: why an apple falls, and why the moon goes around the Earth. 1687. |
| 11 | Scheherazade | 8 | 1 in 63 | A woman with a teal veil, black hair and a purple dress with gold, with a book in her hands. | Each night she told the king a tale, and stopped at dawn, so that he let her live one more day. The Arabian Nights. |

The jokers come from works that are in the public domain everywhere. Their looks follow the old books and pictures, not later films: Alice in the colors that John Tenniel gave her in The Nursery Alice (1890), Pinocchio with the paper cap of the book, Dracula with no look of a film.

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
| 11 | The oracle told me to change the coin. I thought it meant money. \| It meant the ways of men. Gods speak in riddles. Yours just said: Push. |
| 12 | Antisthenes took no pupils. He raised his stick at me. \| I said: Strike. No stick is hard enough to keep me away. He gave up first. |
| 13 | I learned how to live from a mouse. \| It needs no bed, fears no dark and wants no cakes. And it pushes no rock. |
| 14 | The Athenians built fine halls and porches for me to live in. \| They think they built them for Zeus. I let them think so. |
| 15 | I asked a friend to find me a little house. He took a long time. \| So I moved into a jar. I am still waiting for his answer. |
| 16 | In summer I roll in hot sand. In winter I hug cold statues. \| It makes a man tough. You push a rock uphill in all weathers. Show-off. |
| 17 | Plato sailed to Sicily for fine dinners, and ate olives there. \| I asked him: Do olives not grow in Athens? |
| 18 | I offered Plato a share of my dried figs. He ate them all. \| I said: I asked you to share them, not to finish them. |
| 19 | I walked on Plato's fine carpets and said: I tread on his pride. \| He said: Yes, with a pride of your own. Plato always gets the last word. |
| 20 | I asked Plato for a few figs. He sent me a whole jar. \| Ask him what two and two make, and he will say twenty. |
| 21 | When I spoke of wise things, no one came. So I whistled. \| A crowd came running. Men run to nonsense, and stroll to wisdom. |
| 22 | Scholars study the troubles of Odysseus, not their own. \| Yours are easy to study. There is just one, and you push it every day. |
| 23 | Men tune the lyre, and leave their souls out of tune. \| You have one string and one song: push. It is never out of tune. |
| 24 | Some men study the moon, and miss what lies at their feet. \| You never miss what is at your feet. It is big, grey and round. |
| 25 | Hold out your hand to a friend with the fingers open. \| A fist gives nothing. And an open hand pushes a rock better. You know. |
| 26 | Pirates sold me in Crete. The crier asked what I could do. \| I said: Rule men. Sell me to that man in purple. He needs a master. |
| 27 | At the slave market, they told me not to sit down. \| I said: Fish lie any way they like, and still they find buyers. |
| 28 | When men buy a pot, they tap it to hear if it rings true. \| When they choose a man, they only look. I have listened to you. You ring true. |
| 29 | I taught my master's sons to serve themselves. \| To eat plain food, drink water and go barefoot. You would be my best pupil. |
| 30 | My master asked how I wished to be buried. I said: Face down. \| Soon, down will turn into up. You know how that goes. |
| 31 | Alexander said: If I were not Alexander, I would be Diogenes. \| And if I were not Diogenes? I would be Diogenes. I like my jar. |
| 32 | Men praise me as a fine hunting dog. \| But none will come hunting with me. Too far, too hard. You would come. |
| 33 | They told me: You are old, Diogenes. Take a rest. \| Does a runner slow down near the finish? No, he runs faster. So do you. |
| 34 | I was asked to dinner, and I said no. \| The last time I went, my host did not thank me enough. |
| 35 | A man dropped his bread, and was too ashamed to pick it up. \| So I dragged a jar on a rope across the market. A load is no shame. |
| 36 | When I gave my master orders, he cried: Rivers run uphill! \| Rivers never run uphill, I said. Rocks do, with a little help. |
| 37 | A man asked to be my pupil. I gave him a cheese to carry. \| He would not carry it. A cheese worth half an obol broke our friendship. |
| 38 | The gods own all things. The wise are friends of the gods. \| And friends share all. So your rock is mine. Do keep pushing it for me. |
| 39 | All the curses of the old plays fell on me. \| No home, no city, I beg for my bread. But no god gave me a rock. Lucky me. |
| 40 | A man read for hours. At last, we saw the end of his scroll. \| I cried: Cheer up, men! Land in sight! I say the same to you, near the top. |
| 41 | A man proved to me, with logic, that I have horns. \| I touched my forehead and said: Well, I do not see any. |
| 42 | A wise man told me that there is no such thing as motion. \| So I got up and walked about. You and your rock prove him wrong all day. |
| 43 | A man told me all about the sky, the sun and the stars. \| I asked: How many days did it take you to come down from there? |
| 44 | A bad man wrote on his door: Let nothing evil come in. \| I asked: Then how will the master of the house get in? |
| 45 | Mice climbed onto my table to share my dinner. \| I said: Look, even Diogenes keeps hangers-on. I am a rich man now. |
| 46 | As I left the baths, a man asked: Are there many men inside? \| I said: No. Then another asked: Is it crowded? I said: Yes, very. |
| 47 | Men pray for what seems good to them, not for what is good. \| You never pray for a lighter rock. A wise man, or a tired one. |
| 48 | Men care so much about what they do in their dreams. \| And so little about what they do awake. You, at least, push wide awake. |
| 49 | A boy broke my jar. The people of Athens gave me a new one. \| That is how I knew they loved me. Nobody gives a jar to a man he hates. |
| 50 | After the battle, King Philip asked me who I was. \| I said: A spy on your greed. He liked that, and he let me go. |
| 51 | The gods gave men an easy life. \| We buried it under honey cakes and sweet oils. Yours is under a rock. |
| 52 | Some boys cried: Watch out, the Dog will bite us! \| I said: Do not fear, boys. A dog does not eat beetroot. |
| 53 | A man wore a lion skin, as Heracles did. \| I said: Do not shame the coat of courage. Earn it first, with a labor or two. |
| 54 | Men call Callisthenes lucky: he eats with Alexander. \| Unlucky, I said. He may eat only when Alexander says so. |
| 55 | When I need money, I do not beg my friends for gifts. \| I ask them to pay back what they owe me. I gave them a lot of wisdom. |
| 56 | Riddle: a sheep with a golden fleece, on two legs. What is it? \| A rich man who knows nothing. Fine to look at. Do not ask him the way. |
| 57 | I once went to a very dirty bathhouse. \| I asked: After people wash in it, where do they go to get clean? |
| 58 | One singer always lost his crowd. I called him: Rooster! \| Why? Because when he sings, everyone gets up. |
| 59 | A young man gave a speech. I stood in front, eating beans. \| Everyone watched me instead. How odd, I said, to leave a speech for beans. |
| 60 | They mock me for being sent away from my city. \| But that is how I became a wise man. And a rock is how you became strong. |
| 61 | The people of Sinope sent me away forever. \| So I sentenced them to stay at home forever. Fair is fair. |
| 62 | When I ask for money, I always say the same thing. \| If you gave to someone else, give to me too. If you did not, start with me. |
| 63 | How does Dionysius the tyrant treat his friends? \| Like purses. Full, he hangs them up. Empty, he throws them away. |
| 64 | Riddle: what is the mother of all evils? \| The love of money. So I keep my money in other men's purses. |
| 65 | Riddle: which animal has the worst bite? \| Of the wild ones, the slanderer. Of the tame ones, the flatterer. |
| 66 | Sweet words are honey that sticks in your throat. \| So no sweet words from me. Your rock is heavy, and you are stubborn. There. |
| 67 | Riddle: why is gold so pale? \| Fear. So many thieves are after it. Your rock has a healthy grey. |
| 68 | Good men are the images of the gods. \| Hold still, Sisyphus. Let me look at you. No, push on. It suits you better. |
| 69 | I have no servant. They asked me: Who will bury you, then? \| I said: Whoever wants my house. It is a jar, so it may take a while. |
| 70 | Plato talks of tablehood and cuphood. \| I said: Table and cup, I can see. Tablehood and cuphood, I cannot. |
| 71 | Someone asked Plato: What sort of man is Diogenes? \| He said: A Socrates gone mad. I took it as praise. Half of it, anyway. |
| 72 | A man asked what I would take to let him box my ears. \| I said: A helmet. |
| 73 | I saw a young man blush, and I said: Courage! \| That is the color of goodness. Your face is red too. Is it the climb? |
| 74 | Two men went to law, and I judged against them both. \| One had surely stolen. The other had surely lost nothing. |
| 75 | They told me: Many people laugh at you. \| I said: But I am not laughed down. Your rock rolls down. You never do. |
| 76 | A man told me that life is a bad thing. \| I said: Not life. Living badly. You live uphill, but you live well. |
| 77 | My servant Manes ran away. My friends said: Go after him! \| I said: If Manes can live without me, I can live without Manes. |
| 78 | Someone hid a cake among my olives. I threw it out. \| I said: Stranger, make way for the princes! The olives, I mean. |
| 79 | Do wise men eat cakes? \| Yes. All kinds of cakes, just like other men. Do you have one? No? Pity. |
| 80 | Why do men give to beggars, and not to wise men? \| They fear they may be lame or blind one day. Never that they will be wise. |
| 81 | A man mocked me: You made false coins once! \| I said: Then I was what you are now. What I am now, you will never be. |
| 82 | I came to a tiny town with very big gates. \| I said: Shut your gates, or your town will run away through them. |
| 83 | A teacher spoke on and on. So I held up a salted fish. \| His pupils came to look. One obol of fish broke up his whole class. |
| 84 | They scold me for eating in the market. \| But the market is where I got hungry. Where do you eat? Halfway up, I bet. |
| 85 | Plato said: Serve the king, and you need not wash lettuce. \| I said: Wash lettuce, and you need not serve the king. |
| 86 | Alexander said to me: I am Alexander, the great king. \| I said: And I am Diogenes, the Dog. He had more men. I had more sun. |
| 87 | I ate breakfast in the market, and a crowd gathered. \| They cried: Dog! I said: You are the dogs. You stand around watching me eat. |
| 88 | Men praised a man who gave me a coin. \| I said: And you have no praise for me, who was worthy of it? |
| 89 | A man asked me to give him back his cloak. \| I said: If it was a gift, it is mine. If it was a loan, I am still using it. |
| 90 | What did I gain from wisdom? \| To be ready for any luck. Even for a rock that rolls down. You have that too. |
| 91 | I saw parents pray to the gods for a son. \| I asked: Will you not also pray for the kind of man he turns out to be? |
| 92 | They scold me for going into dirty places. \| The sun shines into dirty places too, and it stays clean. |
| 93 | The Athenians voted to call Alexander a god. \| I said: Then vote me a god too. I will not need a temple. I have a jar. |
| 94 | A man said: You know nothing, and you call yourself wise. \| I said: Even to reach for wisdom is wisdom. You reach for the top every day. |
| 95 | A man brought me his son: so clever, so good, he said. \| I asked: Then what does he need me for? |
| 96 | Men who speak well and never do well are like a harp. \| It makes fine music, and cannot hear a note of it. |
| 97 | I walk into the theater just as everyone else walks out. \| Why? I have practiced it all my life. You go up while your rock goes down. |
| 98 | A man asked: Why do you drink in a tavern, Diogenes? \| I asked him: Why do you get your hair cut at a barber's? |
| 99 | I asked a big spender for a fortune. Others, for an obol. \| Why? The others will give again. He will soon have nothing left. |
| 100 | They say: Plato does not beg, as you do. \| Oh, he begs. He just keeps his head down low, so that no one hears. |
| 101 | I saw a very bad archer at practice. \| So I sat down right next to the target. It was the safest spot. |
| 102 | Alexander asked me: Are you not afraid of me? \| I asked: Are you good or bad? He said: Good. Then who fears the good? |
| 103 | Learning keeps the young in line, and comforts the old. \| It is wealth to the poor, and an ornament to the rich. And it weighs nothing. |
| 104 | Riddle: what is the finest thing in the world? \| Free speech. So I will say it: your rock could do with a wash. |
| 105 | I saw a school with many statues of the Muses, and few pupils. \| I said: With the gods' help, teacher, you have a full class. |
| 106 | If breakfast is fine, then breakfast in the market is fine. \| That is logic. And if it is fine to push a rock, it is fine to push it uphill. |
| 107 | Nothing in life comes right without hard practice. \| And practice can beat anything. You have practiced for a very long time. |
| 108 | When you are used to it, giving up pleasure is a joy. \| You gave up rest long ago. You must be a very happy man by now. |
| 109 | I live as Heracles lived: freedom above all. \| He had twelve labors, and then he was free. You have one. Take your time. |
| 110 | My friends wanted to buy my freedom. I called them fools. \| A lion is no slave to the man who feeds him. The man fears the lion. |
| 111 | A man sent his son to Athens to hear me. The boy stayed. \| He sent his other son to fetch him. He stayed. Then he came, and stayed too. |
| 112 | When I died, my pupils fought over who would bury me. \| In the end, their fathers settled it. I was in no hurry. |
| 113 | On my grave they put a pillar, and a marble dog on top. \| It sits in the sun all day, and asks for nothing. A very good likeness. |
| 114 | My city put up a bronze statue of me. \| It says I showed men the easiest path in life. Clearly, they never met you. |
| 115 | They say Alexander and I died on the very same day. \| He left the world, and I left a jar. We each left something round. |
| 116 | Sotion says that one of my books was called Sisyphus. \| No one can find it now. I think it rolled down a hill. |
| 117 | There were five men named Diogenes. \| I am the one with the jar. The other four had roofs, and much less fun. |
| 118 | All the great men came to greet Alexander. I did not go. \| So Alexander came to me. Do not go to a king. Let the king come to you. |
| 119 | When Alexander came with all his men, I sat up a little. \| Only a little. A king is worth a little sitting up, not a whole one. |
| 120 | Once I stood soaked to the skin, and people felt sorry for me. \| Plato told them: Walk away, and he will stop. He was right. I like a crowd. |
| 121 | People used to say I was mad, with my lamp at noon. \| Then they saw you push a rock up a hill forever. Thank you, friend. |
| 122 | My lamp needs a little oil. I need a little bread. \| Your rock needs a whole man, all day. It is a very spoiled rock. |
| 123 | Forty years with my lamp, and still no honest man. \| But your rock never once promised to stay at the top. I will take that. |
| 124 | Now and then I carry my lamp at night, like other people. \| Just to see how it feels to be normal. I did not care for it. |
| 125 | A merchant asked the price of my lamp. \| One honest man, I said. He had none in stock. |
| 126 | My jar has no door and no lock. \| Thieves look in, feel sorry for me, and leave a little bread. |
| 127 | My jar was made to hold oil, or wine, or grain. \| Now it holds a philosopher. It has never been so full of nonsense. |
| 128 | My jar is a fine house: one room, and no stairs. \| Yours is the other kind: all stairs, and no room. |
| 129 | Someone offered me a bigger jar. \| I said no. In a bigger jar, I would need a second lamp just to find myself. |
| 130 | My jar echoes when I talk. \| So I always agree with myself. It is very restful. |
| 131 | My jar has a better view than any palace. \| The whole sky. A palace keeps putting a roof in the way. |
| 132 | If you ever want to stop, I know a good jar. \| Round, cheap, and flat inside. Not a hill in it. |
| 133 | A dog is honest, sleeps anywhere, and loves a bone. \| That is why they call me the Dog. Two out of three so far. |
| 134 | People call me Dog to make me cross. \| So I sit up and give them my paw. Nothing makes them more cross. |
| 135 | Cerberus guards a gate with three heads. \| I guard a jar with one. Nobody has ever got into my jar. I must be good. |
| 136 | I sleep like a dog: anywhere, at once, with one ear open. \| You must sleep like a rock. It would be rude not to. |
| 137 | However often your rock runs off, you go and get it. \| A dog could not be more loyal. Trust me. I am the Dog. |
| 138 | A dog does not care if you are rich, only if you are kind. \| So every dog would follow you up the hill. Ask me. I have been one for years. |
| 139 | In the dog days of summer, even your rock gets hot. \| Push early, and nap at noon. That is the law of dogs. I wrote it. |
| 140 | Cynic comes from our word for dog. \| People mean it as an insult. I have never met a dog who agreed. |
| 141 | I own a cloak, a staff, a bag and a lamp. \| Four things. You own one. Next to you, I live in luxury. |
| 142 | I gave away all I owned in one afternoon. \| Best afternoon of my life. Second best: the nap after it. |
| 143 | The simple life is simple. That is the whole secret. \| People pay good money to hear it said at greater length. |
| 144 | I have no plate, no spoon and no table. \| So I never wash the dishes. You see? Philosophy works. |
| 145 | I have not worn shoes in years. \| My feet are as hard as your rock now. Not as round. I checked. |
| 146 | I owned a goat once. \| By the end of the week, the goat owned me. Things are like that. |
| 147 | People ask how I got so old. \| No things, no fuss, and a nap after lunch. And one before lunch. |
| 148 | A rich man asked me the way to be happy. \| I said: Put down the gold, and walk. He put down a little, and walked a little. |
| 149 | A king has a palace and a thousand guards. \| I have a jar, and no guards at all. Guess which of us sleeps better. |
| 150 | Rich men spend half their lives counting their gold. \| You have one rock. You never lose count. That is a kind of freedom. |
| 151 | Gold can buy anything, people say. \| Then let it buy my friend a flat hill. We will wait. |
| 152 | Fame is like your rock. \| Hard to push up, and it comes down by itself. I leave mine at the bottom. |
| 153 | You were a king once, with gold and a palace. \| Now you have a rock and a hill. A good trade, I think. Fewer things to dust. |
| 154 | You and I both live outside, and both work for nothing. \| We should start a school. No. Then people would come. |
| 155 | Be careful what you say in front of writers. \| I said a few odd things. Now they are in books, and much funnier. |
| 156 | They say Plutus, the god of wealth, is blind. \| So he gives gold to anyone he bumps into. I keep well out of his way. |
| 157 | A traveler asked me for the quickest way to the top. \| I said: Follow the man with the rock. It is not quick, but he knows it best. |
| 158 | Do I care what people think? Of course I do. \| I think about them all the time. That is why I live in a jar. |
| 159 | Zeus has thunder. Hades rules the dead. \| And the best they could think of for you was a rock? I expected more of gods. |
| 160 | The gods have everything, and they are always cross. \| I have nothing, and I am in a fine mood. Think about it on the way up. |
| 161 | The hill is steep, the rock is heavy, the top is far. \| Still, nobody makes you dress for dinner. Count your blessings. |
| 162 | Some say the rock is your fate. \| I say it is your rock. Fate is a big word for a stone. |
| 163 | Protagoras says man is the measure of all things. \| So your rock weighs exactly one Sisyphus. I always thought so. |
| 164 | Plato says men sit in a cave and look at shadows. \| And they call me odd. At least I come out of my jar in the morning. |
| 165 | Socrates said: All I know is that I know nothing. \| I know that too. I just say it in fewer words. |
| 166 | Aristotle teaches as he walks, and his pupils follow. \| You walk too. Your pupil is a rock. It runs off at the end of every lesson. |
| 167 | Aristotle says the best is always in the middle. \| So you are at your best halfway up. Rest there. Tell him I said so. |
| 168 | Anaxagoras says the sun is only a hot stone. \| So someone rolls a hot stone across the sky each day. Yours, at least, is cool. |
| 169 | Heraclitus says you never step in the same river twice. \| But the same rock, you push every day. Rocks are more loyal than rivers. |
| 170 | Heraclitus says the way up and the way down are one. \| He never pushed a rock. The way down is much faster. |
| 171 | Parmenides says that nothing ever really changes. \| I think he watched you for a day, and then wrote it down. |
| 172 | Democritus says all things are made of tiny bits. \| Your rock too. Ten thousand tiny bits, and every one wants to go down. |
| 173 | Pythagoras says souls come back in new bodies. \| Next time, I want to be a dog. They tell me I am halfway there. |
| 174 | Pythagoras says the stars make music as they turn. \| Your rock makes music too, on the way down. Mostly drums. |
| 175 | Delphi says: Know yourself. \| I did. It took one morning. There is not much to me. |
| 176 | Delphi says: Nothing in excess. \| I agree. I have nothing, and I am careful never to have too much of it. |
| 177 | The philosophers talk all day about the good life. \| You and I just live it. Well, I live it. You push it. |
| 178 | You are the most patient man I know. \| And I once sat through a whole lecture by Plato. |
| 179 | I would help you push, but I am a philosopher. \| We are very good at explaining why a rock is heavy. |
| 180 | Your days are all the same, they say. \| So are mine, and I chose it. You got yours for free. Lucky man. |
| 181 | If you ever reach the top and the rock stays, call me. \| I will bring my lamp. That, I want to see. |
| 182 | They say you were the cleverest man alive. You tricked Death. \| That is how it goes with clever men. They always end up pushing something. |
| 183 | I have watched you for a while now. \| You never complain. I complain for a living. I am worried for my job. |
| 184 | You push all day, and never say a word. \| I talk all day, and push nothing. Together we would make one wise man. |
| 185 | Every day you push, and every day I watch. \| I think I have the better job. But you have the better legs. |
| 186 | You know what I like about your rock? \| It has never once tried to be a bigger rock. Most men cannot say that. |
| 187 | Some men push gold. Some men push their luck. \| Yours is a rock. I like a man who knows what he is pushing. |
| 188 | Do you ever wish your rock were smaller? \| Careful. Then a smaller hill, then a smaller life. Then one day, a jar. |
| 189 | People ask why you push the same rock every day. \| I ask why they carry the same worries every day. You at least get a view. |
| 190 | The gods meant your rock to make you suffer. \| No master, no debts, no taxes, and nobody to argue with. Gods are bad at this. |
| 191 | The top must be a fine place. \| You never stay long enough to tell me about it. |
| 192 | What is at the top, I wonder? \| If it is nothing, then I live there already. Come and visit. |
| 193 | When your rock rolls down, thank it. \| It saved you carrying it down. Very kind, for a rock. |
| 194 | At the top, when it gets away from you, do you shout? \| I would. Something short and wise, like: Oh well. Then I would go and get it. |
| 195 | I do not envy you the rock. I envy you the hill. \| The wind, the view, and no philosophers. Well, one. |
| 196 | You will push forever, I hear. Forever is a long time. \| Push slowly, then. It makes no difference, and it looks wise. |
| 197 | I tried to push a rock once. \| It wanted to go down. I let it. We parted as friends. |
| 198 | Poor Tantalus wants fruit he can never reach. \| I want nothing, so I always get it. Someone should tell him the trick. |
| 199 | Your rock is your oldest friend, and the hill your oldest enemy. \| I am your newest friend. Hello. I promise not to roll away. |
| 200 | You are the only man I know who knows where he is going. \| Up. Always up. And then, for a short while, down. |
| 201 | Riddle: what goes all around the tree, but never goes in? \| The bark. I am the Dog, so bark is my trade. |
| 202 | Riddle: when is a door not a door? \| When it is ajar. A jar is my door, my house and my bed. |
| 203 | Riddle: the poor have it, and the rich need it. \| Nothing. I have more of it than anyone. I am the richest man alive. |
| 204 | Riddle: what has many teeth, but never bites? \| A comb. I have never owned one. Look at my beard. You can tell. |
| 205 | Riddle: what goes into a jar at night, and comes out wiser? \| Me. Well, older. Wiser takes longer. |
| 206 | Riddle: what gets wetter, the more it dries? \| A towel. I have none. When I get wet, I shake, like a dog. Much cheaper. |
| 207 | Riddle: who has truer friends, a king or a beggar? \| The beggar. Nobody makes friends with him for his gold. |
| 208 | Riddle: it fills a room, but takes up no room at all. \| Light. My jar is small, so my lamp fills it very fast. |
| 209 | Riddle: the same weight all day, and heavier by evening. \| Your rock. The rock never changes. The evening does. |
| 210 | Riddle: no house, no money, no shoes, and no worries. \| Me. Or the Dog. Call either name, and I come running. |
| 211 | Riddle: thrown out when it is needed, taken in when it is not. \| An anchor. Throw your rock into the sea one day. Then it is the sea's problem. |
| 212 | Riddle: which is older, your rock or your hill? \| The hill, by far. But the rock has seen much more of it. |
| 213 | Riddle: who has heard all my jokes, and never laughed once? \| Your rock. It may be the wisest of us all. |
| 214 | Riddle: four legs at dawn, two at noon, three at dusk. \| Man, says the Sphinx. Some days I go on four. They call me the Dog for a reason. |
| 215 | Riddle: the more you have, the more it weighs on you. \| Things. I have none, so I am light as a bird. You have one, and look at it. |
| 216 | Riddle: the higher you push it, the harder it falls. \| Your rock. Also pride. Push the rock, and leave the pride at the bottom. |
| 217 | Riddle: men chase it all their lives, and it never stays. \| A coin. You chase a rock. It never stays either, but it is much easier to find. |
| 218 | Riddle: what carries its house on its back, everywhere? \| A snail. I thought of carrying my jar like that. Then I saw you. No, thank you. |
| 219 | Riddle: who works all day, earns nothing, and spends nothing? \| You. I earn nothing and spend nothing too, but I skip the work. |
| 220 | Riddle: what is the shortest book of philosophy? \| Mine. One page: Want less. You could read it on the way up. |
| 221 | Riddle: what do Plato and your rock have in common? \| Both are heavy, and with both, you end up back where you began. |
| 222 | Riddle: who is the one man who never needs a map? \| You. Up, then down, then up again. Even I could not get lost. |
| 223 | Riddle: it has no legs, but it wins every race down the hill. \| Your rock. You come second, every time. Do not feel bad. |
| 224 | Riddle: what does a dog chase all day, and never catch? \| His tail. You chase the top. You catch it, and your rock runs off. |
| 225 | Riddle: one goes up slowly, the other comes down fast. \| You and your rock. You are the slow one. Do not tell it I said so. |
| 226 | Riddle: what do I need to light a lamp at noon? \| A lamp, some oil, and no shame at all. The last is the easy part. |
| 227 | Riddle: you take it for a walk every day, but it never walks. \| Your rock. Like a lazy dog. And it will not fetch itself. You have to. |

### Alice

| # | Line |
|---|---|
| 1 | Curiouser and curiouser! A man who pushes a rock up a hill. \| In Wonderland, that would be quite normal. |
| 2 | The Hatter asked me: why is a raven like a writing-desk? \| He had no answer. Do you? No? Then push on. |
| 3 | The White Queen believed six impossible things before breakfast. \| A rock that you push forever is only one. You are doing well. |
| 4 | I fell down a rabbit hole for a very long time. \| Then I had to climb out. I know how you feel. |
| 5 | Riddle: what gets bigger the more you take away from it? \| A hole. Mine was a rabbit hole. |
| 6 | The Cheshire Cat told me that we are all mad. \| He meant you too, I think. In the nicest way. |
| 7 | In the race of the Dodo, everybody won, and all had prizes. \| Your prize is the top. Then the race starts again. |
| 8 | Riddle: what has a mouth but never eats, and runs but never walks? \| A river. It runs downhill, which is the easy way. |
| 9 | Riddle: a face, two hands, and it lives in a waistcoat pocket. \| The White Rabbit's watch. It only ever told him one thing: late, late, late! |
| 10 | I shut up like a telescope, till I was only ten inches high. \| Then I could not reach the key on the table. There is always a catch. Yours is round. |
| 11 | I give myself very good advice, though I very seldom follow it. \| My advice to you: rest at the top, and look at the view. You may follow it or not. |
| 12 | Four times five is twelve, and four times six is thirteen, and so on. \| I would never get to twenty at that rate. Please do not count your climbs my way. |
| 13 | Riddle: take a bone from a dog. What remains? \| The dog's temper, said the Red Queen. I think I would not remain either. |
| 14 | The Mouse said: mine is a long and a sad tale! \| It is a long tail, certainly, I said. But why do you call it sad? He did not laugh. |
| 15 | The Lory said: I am older than you, and must know better. \| And it would not tell its age, so there was no more to be said. Rocks are like that. |
| 16 | A little Crab said: Ma, you would try the patience of an oyster! \| I think you have the patience of an oyster. Two oysters. A whole bed of them. |
| 17 | Riddle: what is round, never late, and waits at the bottom? \| Your rock. The White Rabbit could learn a thing or two from it. |
| 18 | When I grew very tall, I said goodbye to my feet. So far away! \| I meant to send them new boots every Christmas, by post. With my love. |
| 19 | I pretend to be two people. Once I cheated myself at croquet. \| So I boxed my own ears. If you were two people, one could push while one rests. |
| 20 | London is the capital of Paris, and Paris is the capital of Rome. \| My geography went wrong in Wonderland. Is the top the capital of the hill? |
| 21 | Riddle: what is left of a cat, when the cat goes away tail first? \| The grin. The Cheshire Cat's grin stayed for some time after the rest of him had gone. |
| 22 | In the looking-glass, your rock would roll uphill by itself. \| And you would run after it, backwards. What a sight! Shall I find you a big mirror? |
| 23 | The Mouse told us the driest thing he knew, to dry us off. \| It was all about William the Conqueror. We stayed just as wet. History is not a towel. |
| 24 | The White Rabbit took me for his maid, and sent me home for his gloves. \| How queer, to run errands for a rabbit! Next, Dinah will send me. Who sends you? |
| 25 | Riddle: divide a loaf by a knife. What is the answer? \| Bread-and-butter, of course. The Red Queen said it before I could. |
| 26 | A very big puppy came to play with me. Or else I was very small. \| I threw it a stick, and it ran after it. You would like a dog that fetches rocks. |
| 27 | Do cats eat bats? Do bats eat cats? I could not answer either one. \| So it did not matter which way I put it. Up the hill, or down: it is the same hill. |
| 28 | In the Rabbit's house I grew so big that one foot went up the chimney. \| Out flew Bill the Lizard, like a sky-rocket. Your rock goes the other way, I see. |
| 29 | Riddle: who may look at a king, and need not bow? \| A cat. I read that in some book. The Cheshire Cat looked, and grinned. |
| 30 | The Caterpillar asked me: who are you? \| I hardly knew. I had changed so often since morning. You know who you are. Lucky. |
| 31 | The Pigeon took me for a serpent, because my neck was so very long. \| Little girls eat eggs too, I said. Then they are a kind of serpent, said the Pigeon. |
| 32 | The Duchess's Footman said he would sit by the door till tomorrow. \| On and off, for days and days, he said. You go up and down for days and days. Better. |
| 33 | Riddle: what is one and one and one and one and one and one? \| I lost count when the White Queen asked me. Like your pushes. Nobody could count those. |
| 34 | I used to think fairy tales never happened. Then I was in one! \| There ought to be a book written about me. And one about you, with a rock in it. |
| 35 | Pebbles came flying at me, and turned into little cakes. \| If your rock ever turns into a cake, only eat a bit. Cakes change you. |
| 36 | Which way? I asked the Cheshire Cat. Where to? he asked back. \| You want the top. So it is up. You are sure to get there, if you only walk long enough. |
| 37 | Riddle: what climbs all day, and never grows any taller? \| You. I climbed nothing, and grew nine feet tall. Wonderland is not fair at all. |
| 38 | The Hare buttered the Hatter's watch. It was the best butter. \| But crumbs got in, and the watch went two days wrong. Never butter your rock. |
| 39 | The Mock Turtle sat sad and lonely on a little ledge of rock. \| He sobbed. It is all his fancy, said the Gryphon. You have a real rock, and you smile. |
| 40 | Riddle: what is the cause of lightning? \| The thunder, I said. No, the other way round! Too late, said the Red Queen. |
| 41 | It is no use going back to yesterday. I was a different girl then. \| Not you, I think. Yesterday you were just what you are today: brave, and pushing. |
| 42 | Mind the sense, said the Duchess, and sounds will mind themselves. \| So mind the push, and the top will mind itself. That is my moral for you. |
| 43 | Three inches high is a very good height, said the Caterpillar. \| It was exactly three inches high, so I did not argue. Your rock is a good size too. |
| 44 | Riddle: what is the oldest rule in the King of Hearts' book? \| Rule Forty-two, he said. Then it ought to be Number One, I said. He shut the book. |
| 45 | The Queen played croquet with flamingoes and hedgehogs. \| My hedgehog unrolled and walked off. Your rock only rolls downhill. Much more polite. |
| 46 | Dormouse logic: I breathe when I sleep, so I sleep when I breathe. \| True, for him. And for you: I push when I climb, and I climb when I push. |
| 47 | The Mock Turtle studied Reeling, Writhing and Uglification. \| And Drawling, and Fainting in Coils. I only learned French and music. |
| 48 | Riddle: when is a hill a valley? \| When the Red Queen is near. She has seen hills that would make yours a valley. |
| 49 | If I had your rock, I would give it a name. Dinah, perhaps. \| No, Dinah is my cat. She would not like that. Let us call it Mabel. Hello, Mabel! |
| 50 | The White Knight fell off his horse every few steps. \| Plenty of practice! he said. You have plenty too. And you never fall off your rock. |
| 51 | The Gryphon said: explanations take such a dreadful time. \| So I never asked why you push the rock. Push first. Explain some other day. |
| 52 | Riddle: what are tarts made of? \| Pepper, mostly, said the Cook. Treacle, said a sleepy voice: the Dormouse. |
| 53 | The Queen of Hearts shouts: off with his head! all day long. \| It is all her fancy, said the Gryphon. They never executes nobody. Not even once. |
| 54 | The Hatter sang, and the Queen cried: he is murdering the time! \| Since then, Time will not do a thing he asks. Be kind to Time. You spend a lot together. |
| 55 | The Mock Turtle had ten hours of lessons, then nine, then eight. \| So the eleventh day was a holiday. And the twelfth? Enough lessons, said the Gryphon. |
| 56 | Riddle: what can you see on an empty road, with very good eyes? \| Nobody. The White King wished he had my eyes, to see Nobody so far off. |
| 57 | Let us pretend the glass is soft, like gauze, I told my kitten. \| And it was! I went right through. Pretending works. Pretend your rock is a bun. |
| 58 | A Looking-glass book had all its words the wrong way round. \| I held it to a mirror: Jabberwocky. Somebody killed something: that is clear. |
| 59 | The jury wrote their names down, so as not to forget them. \| Before the end of the trial! Perhaps write yours on your rock. Just in case. |
| 60 | Riddle: which side of the mushroom makes you grow taller? \| The left. The right made my chin bump my foot. I had to try both to find out. |
| 61 | The Queen of Hearts should make a rule: rocks stay at the top. \| Rule Forty-three. Your rock would not listen. Rocks never read the rules. |
| 62 | At the Mad Tea-Party, they move round as the cups get used up. \| What happens when you come to the beginning again? I asked. They changed the subject. |
| 63 | The White King said: the horror! I shall never, never forget it! \| You will, though, said the Queen, if you do not make a memorandum of it. |
| 64 | Riddle: why is a whiting called a whiting? \| It does the boots and shoes under the sea, said the Gryphon. Soles and eels, of course. |
| 65 | I walked to a hill, but the path kept taking me back to the house. \| A flower told me to walk the other way. It worked! Do not try it with a rock. |
| 66 | Behind the glass, the clock had an old man's face, and grinned. \| Clocks should not grin. That is a job for cats. Cheshire ones. |
| 67 | Riddle: jaws that bite, claws that catch, and eyes of flame. \| The Jabberwock. Do not worry. It lives in a poem, and poems stay where they are. |
| 68 | Off with her head! cried the Queen at me. Nobody moved. \| Who cares for you? I said. You are nothing but a pack of cards! And then I woke up. |
| 69 | As I left, they were putting the Dormouse into the teapot. \| He did not even wake. Some can sleep through anything. You can push through anything. |
| 70 | The Hatter could not stand down. He was on the floor already. \| Then you may sit down, said the King. Some days, that is the best advice of all. |
| 71 | Riddle: why did the Mock Turtle call his old teacher Tortoise? \| Because he taught us, said the Mock Turtle. He thought me very dull for asking. |
| 72 | If I nibbled the left side of the mushroom, I would be as tall as a tree. \| Then your rock would be a pebble to me. I could pop it on the top for you. Just once. |
| 73 | In Looking-glass chess, a pawn who reaches the end is a queen. \| You walk to the top every day. You ought to be a king by now. A bishop, at least. |
| 74 | The Tiger-lily talks, when anybody is worth talking to. \| Then all the flowers talked at once. So I was worth talking to. So are you. |
| 75 | Riddle: what lives on weak tea with cream in it? \| The Bread-and-butter-fly. Its head is a lump of sugar. |
| 76 | On the Looking-glass train, a word costs a thousand pounds. \| So I said nothing at all. Your rock never says a word. It must be very rich. |
| 77 | I ran with the Red Queen, and we stayed under the same tree. \| All the running you can do keeps you in the same place, she said. You two would get on. |
| 78 | In the Lobster Quadrille, you throw the lobsters far out to sea. \| Then you swim after them. Like you and your rock, after it rolls. Only wetter. |
| 79 | Riddle: what time is it at the Hatter's table, all day long? \| Six o'clock. Always tea-time, so there is never time to wash the cups. |
| 80 | The White King has two Messengers: one to come, and one to go. \| I beg your pardon? I said. It is not respectable to beg, said the King. |
| 81 | I grew in court. The Dormouse said: you have no right to grow! \| I cannot help it, I said, I am growing. You cannot help pushing. It is the same. |
| 82 | In Jabberwocky, the father cries: O frabjous day! Callooh! Callay! \| He chortled in his joy, the poem says. Try it at the top. Chortling is good for you. |
| 83 | Riddle: it came yesterday, and it comes tomorrow, but never today. \| Jam, at the White Queen's. Jam every other day, and today is never any other day. |
| 84 | In the wood where things have no names, I forgot my own. \| I knew it began with L. It does not, of course. Yours begins with S, in case you forget. |
| 85 | The White Knight trains his hair up a stick, like a fruit-tree. \| Things never fall upwards, you know, he said. Your rock agrees with him. |
| 86 | The Hatter told me my hair wanted cutting. \| No personal remarks, I said. It is very rude. Your rock looks very nice today. |
| 87 | Riddle: what does no wise fish go anywhere without? \| A porpoise. Off on a journey? With what porpoise? the Mock Turtle would ask. |
| 88 | Tweedledee said: you won't make yourself a bit realler by crying. \| So I stopped. You never cry over your rock, I see. You must be very real indeed. |
| 89 | When I came to the Eighth Square, I found a crown on my head. \| Queen Alice! Then two Queens fell asleep on my shoulders. Being a queen is hard work. |
| 90 | A Rocking-horse-fly is wooden, and swings from tree to tree. \| It lives on sap and sawdust. If your rock had wings, would it be a Rock-fly? |
| 91 | Riddle: when may you wear a hat that is not yours? \| When you are a hatter. The Hatter keeps his to sell. He has none of his own. |
| 92 | A voice on the train said I should be labelled: Lass, with care. \| Like glass, with care. I would label your rock: Rock, with care. This way up. |
| 93 | Running with the Red Queen, I asked her: are we nearly there? \| Nearly there! she cried. We passed it ten minutes ago! Faster! I never even saw it. |
| 94 | The White King eats hay when faint. Nothing like it, he says. \| Not that nothing is better, mind. Just nothing like it. Hay at the top, perhaps? |
| 95 | Riddle: what can you take more of, when you have had none? \| Tea. It is very easy to take more than nothing, said the Hatter. |
| 96 | In the wood with no names, a Fawn walked by my side. \| At the edge it knew I was a girl, and ran away. Names spoil things, a little. |
| 97 | Tweedledum and Tweedledee meant to fight over a rattle. \| They put on bolsters and a saucepan helmet. Then a crow came, and both ran off. |
| 98 | Riddle: why carry a box upside down? \| So the rain cannot get in, said the White Knight. The lid was open. Out fell everything. |
| 99 | The White Queen screams first, and pricks her finger after. \| She has done all the screaming already, she says. Why do it twice? Very sensible. |
| 100 | The Red Queen said: curtsey while you think. It saves time. \| So I curtsey to your rock while I think what to say. It looks very pleased. |
| 101 | The King said: begin at the beginning, go to the end, then stop. \| You do the first two very well. The stopping, never. I think that is braver. |
| 102 | Riddle: what did three little sisters draw, down in the well? \| All things that begin with M: mouse-traps, the moon, memory and muchness. |
| 103 | To gyre, said Humpty Dumpty, is to go round like a gyroscope. \| Your rock gyres all the way down. It does not gimble, I hope. That is making holes. |
| 104 | Tweedledee said: if it were so, it would be. \| But as it isn't, it ain't. That's logic. Your rock rolls down. That is logic too. |
| 105 | The White Knight carries a beehive. No bee has come near it yet. \| He is still very hopeful. I like hopeful people. You are one too. |
| 106 | Riddle: the more I walked toward it, the further off it got. \| The egg in the Sheep's shop. When at last I got to it, it was Humpty Dumpty. |
| 107 | The White Queen lives backwards. Her memory works both ways. \| She remembers best what happens the week after next. She says your rock goes up. |
| 108 | The Red Queen said: even a joke should have some meaning. \| So mine all have one. This one means: I think you are very brave. |
| 109 | The Queen of Hearts said: sentence first, verdict afterwards. \| Stuff and nonsense! I said. It is a good thing to say. Try it when the rock rolls down. |
| 110 | Riddle: like a badger, a lizard and a corkscrew. What is it? \| A tove. Humpty Dumpty says they nest under sundials, and live on cheese. |
| 111 | The Unicorn thought I was a fabulous monster. I thought he was. \| So we made a bargain: if you believe in me, I will believe in you. I believe in you! |
| 112 | The White Knight would climb a gate by standing on his head on it. \| Then the feet are high enough, he said. He had not tried it yet. It might be hard. |
| 113 | The prettiest rushes were always just out of my reach. \| Like the top, some days, I should think. But the ones I picked were lovely too. |
| 114 | The White Queen said: consider how far you have come today. \| Consider anything, only do not cry! So I consider your rock. It is very round. |
| 115 | The Red Queen told me: speak only when you are spoken to. \| Then nobody would ever say anything, I said. So I spoke first. Hello! |
| 116 | Was it my dream or the Red King's? I was in his, and he in mine. \| Which do you think? If someone dreams you and your rock, I hope it is a kind dream. |
| 117 | A wabe, said Humpty Dumpty, goes a long way before and behind. \| So your path is a wabe: a long way before you, and a long way behind. |
| 118 | Riddle: the Lion and the Unicorn fight for a crown. Whose crown is it? \| The White King's. The best of the joke, he said, is that it is mine all the while. |
| 119 | I asked the White Knight if his song was very long. \| It is long, he said, but very, very beautiful. Your climb is like that, I think. |
| 120 | Riddle: fivepence farthing for one, but twopence for two. What is it? \| An egg, in the Sheep's shop. But if you buy two, you must eat them both. |
| 121 | The White Queen said: nobody can do two things at once. \| She cannot, and I cannot. You push a rock and smile at me at once. Very clever. |
| 122 | The Red Queen said: Alice, Mutton. Mutton, Alice. And the mutton bowed. \| It is not polite to cut anyone you have been introduced to, she said. So, no dinner. |
| 123 | Riddle: which cake do you hand round first, and cut afterwards? \| A Looking-glass cake. It came apart by itself as I carried it round. |
| 124 | The White Knight said: over that brook, and you will be a queen. \| I waved my handkerchief till he was out of sight. Now I wave it at you. |

### Red Riding Hood

| # | Line |
|---|---|
| 1 | Is this the way to my grandmother's house? \| Past the man with the rock? Oh, that is you. Thank you! |
| 2 | Grandmother, what big ears you have! \| That was the wolf. Now I look at the ears first. |
| 3 | What do wolves say when they meet? \| Howl do you do? |
| 4 | I have cake and wine for my grandmother. \| Here, have a crumb. Pushing is hungry work. |
| 5 | My mother said: do not leave the path. \| You never leave the path. She would like you. |
| 6 | Riddle: what is red, has a basket, and is never late? \| Me. Well, once. There was a wolf. |
| 7 | My red cap is of velvet. I never wear anything else. \| And you never push anything else. We both know what suits us. |
| 8 | A red hood in France, a red cap in Germany. \| Either way, the wolves see me coming. Red is not a color for hiding. |
| 9 | Mother said: walk nicely, and mind the bottle. \| You walk very nicely. And lucky you: a rock does not break. It only rolls. |
| 10 | Mother said: say good morning before you peep. \| Good morning! There. Now may I peep at your rock? Oh, what a fine one. |
| 11 | Grandmother lives under three big oak trees. \| Come for tea one day. Knock twice and say your name. We have had wolves. |
| 12 | A wolf said: You walk as if you were going to school! \| You walk like that too. But you have a good reason. It is a very big rock. |
| 13 | I saw a flower, then a prettier one, farther on. \| Like your top: always a little farther on. But you get there. I got lost. |
| 14 | On the long way, I ran after butterflies. \| You never chase butterflies. Your hands are full. I will chase one for you. |
| 15 | The wolf said: Let us race to Grandmother's! \| He took the short way. You have no short way. But no wolf races you, either. |
| 16 | Grandmother says: Pull the bobbin, and the latch goes up. \| If only rocks were like that. Pull the bobbin, and up goes the rock! |
| 17 | Grandmother's door had a latch. Then the wolf came. \| Now it has a latch, a bolt, two locks, and a very loud goose. |
| 18 | A wolf knocked and said: Open! I am Red Riding Hood! \| But I was inside, with Grandmother. We just kept eating our cake. |
| 19 | The wolf sat on the roof and smelled sausages. \| He leaned and leaned, and slid into the trough. Splash! Silly grey-beard. |
| 20 | I sing all the way through the wood. \| Mother says it keeps the wolves away. The birds too. I am not a good singer. |
| 21 | The huntsman heard loud snores at Grandmother's. \| It was the wolf, in her cap. Grandmother never snores. Well, only a little. |
| 22 | Grandmother ate the cake and drank the wine. \| She was well at once. Cake and wine fix most things. Not rocks, though. |
| 23 | A wolf said good morning, with a wicked look. \| I said good morning back, and walked on. Manners are free. So are legs. |
| 24 | The wolf went round and round Grandmother's house. \| He never gave up. Like you! But you have much better manners. |
| 25 | Mother sent Grandmother a cake and a little pot of butter. \| Butter on your rock? It might slide up easier. Or down. Better not. |
| 26 | Wolves try no tricks when woodcutters are near. \| So I like strong folk about. You will do. You and your big rock. |
| 27 | Is that a rock, or a wolf curled up very small? \| A rock. Good. A wolf would never let you push him like that. |
| 28 | Why do wolves never push rocks? \| Too much work. A wolf would rather lie in bed, in a nightcap, and wait. |
| 29 | How do you tell a wolf from a grandmother? \| Ask her to knit. Wolves drop every stitch. |
| 30 | A wolf once told me how sweetly the birds sing in the wood. \| He was right about that. Wrong about everything else. |
| 31 | The wolf said: Big hands are the better to hug you with. \| Your hands are big too. Give your rock a hug from me. |
| 32 | The wolf had big eyes, the better to see me with. \| Yet he never saw the huntsman come in. So much for big eyes. |
| 33 | Grandmother, what a deep voice you have! \| I thought she had a cold. It was worse. It was a wolf. |
| 34 | A wolf asked me the way to Grandmother's house. \| I sent him the long way: up a hill, behind a man with a rock. Sorry! |
| 35 | Oh, what a big rock you have! \| And you did not say: The better to eat you with! Good. You are not a wolf. |
| 36 | Grandmother says: the sweetest wolves are the worst. \| So if someone is too sweet, I look for a tail. No tail on you. Good. |
| 37 | Let me put a flower on your rock. \| There! A happy rock. It may roll down, but it will roll down pretty. |
| 38 | I wonder what your rock would say, if it could talk. \| Wheee! That is my guess. Rocks must love the way down. |
| 39 | Grandmother's house is the first one past the mill. \| If your rock rolls that far, she will send it back. With a cake for you. |
| 40 | Monsieur Perrault ended my tale with the wolf. \| The Brothers Grimm sent a huntsman. I like their ending much better. |
| 41 | Never call a wolf in a nightcap Grandmother. \| Say: Good morning, wolf! Then run. Downhill is fastest. Ask your rock. |
| 42 | I counted the wolves in the wood. None! \| That is the best number of wolves. Next best: one, far away, fast asleep. |
| 43 | Wolves walk on four legs, and you push with two arms. \| Who is faster? The wolf. Who is stronger? You. Who is nicer? Not the wolf. |
| 44 | I am not afraid of the wood. Of wolves, a little. \| And of rocks that roll down hills. Please hold on tight. |
| 45 | What do you call a wolf who stays on the path? \| Lost. Wolves never stay on the path. That is how you can tell. |
| 46 | My basket goes to Grandmother. Your rock goes up. \| We both do our rounds. But mine ends with cake. |
| 47 | A rock by the door keeps wolves away, they say. \| May I borrow yours? Oh. You are using it. Forever? Never mind, then. |
| 48 | The Brothers Grimm say I went home joyously. \| Joyously! What a word. Try it on your rock. Push joyously! |
| 49 | Riddle: thirty white horses on a red hill. \| Teeth! The wolf had a whole herd. Grandmother has six, and she loves each one. |
| 50 | Riddle: a head, a foot, four legs, and never a step. \| A bed. Before you sit on Grandmother's, look who is in it. |
| 51 | Riddle: what has a neck, but no head? \| The wine bottle in my basket. Grandmother says it is for her health. |
| 52 | Riddle: what flies, but has no wings? \| Time. It flew while I picked flowers. The wolf did not stop for any. |
| 53 | Riddle: the more you take, the more you leave behind. \| Footsteps! You must have left a million. Most of them uphill. |
| 54 | Riddle: what runs round the house, but never moves? \| The fence. Grandmother wants a taller one now. I wonder why. |
| 55 | Riddle: what goes up when the rain comes down? \| An umbrella. I have a hood. You have a rock. That does not help at all. |
| 56 | Riddle: the longer she stands, the shorter she grows. \| A candle. If only hills did that. Yours stays as tall as ever. |
| 57 | Riddle: what breaks when you say its name? \| Silence. We kept it when the wolf knocked. He had to try the roof. |
| 58 | Riddle: what room has no door and no window? \| A mushroom. The wood is full of them. Grandmother likes the good ones. |
| 59 | Riddle: higher than a house, higher than a tree. \| A star. When I am late in the wood, it always shows me the path home. |
| 60 | Riddle: what has leaves, but is not a tree? \| A book. My tale is in one. The wolf does not come off well. |
| 61 | Riddle: what has hands, but cannot clap? \| A clock. When it strikes nine, I go home. Before dark, and before wolves. |
| 62 | Riddle: a thousand needles, and it cannot sew. \| A hedgehog. I met one on the path. Much nicer than a wolf, and much smaller. |

### Don Quixote

| # | Line |
|---|---|
| 1 | Halt! Is that a giant that you push? \| Ah, a rock. Carry on, good knight. |
| 2 | I once fought a giant with four long arms. \| Sancho says it was a windmill. Sancho sees too little. |
| 3 | A knight needs a quest. Yours is a fine one. \| Up, and up again. I will sing of it to Dulcinea. |
| 4 | My helmet is the golden helmet of Mambrino. \| A barber says it is his basin. Barbers know nothing. |
| 5 | Riddle: four arms, no hands, and it turns all day. \| A windmill. Or a giant. It depends on who you ask. |
| 6 | I took four days to name my horse, and eight to name myself. \| What is your rock called? Take your time. You have all the time there is. |
| 7 | My visor was pasteboard. I struck it with my sword. \| It broke at once. So I made another, and very wisely, I never tested it. |
| 8 | A knight with no lady is a tree with no leaves and no fruit. \| Who is your lady? Ah, your rock. You do hug it a great deal. |
| 9 | The sage Friston turned my giants into windmills, out of spite. \| And when your rock rolls down from the top? Friston again, I wager. The rogue! |
| 10 | A knight-errant may not complain of any wound. \| Sancho may. He complains for both of us, and for Rocinante too. |
| 11 | Once I charged a great army, hidden in a cloud of dust. \| A strange army. Its war cry was baa, and it ran off to graze. |
| 12 | Sancho named me the Knight of the Rueful Countenance. \| I name you the Knight of the Rock. Rueful? No. Mighty, and a bit dusty. |
| 13 | One night, Rocinante would not move at all. \| An enchantment, I thought. It was Sancho. He had tied his legs. |
| 14 | I was born in an age of iron, to bring back the age of gold. \| You were born to bring a rock to the top. Both are heavy work. |
| 15 | Where one door shuts, another opens. \| Your rock rolls down? A new climb opens! Every loss is an adventure. |
| 16 | I have seen Dulcinea not four times in twelve years. \| She has never once noticed me. True love needs no looking. |
| 17 | In my sleep I fought a giant, and won! \| He was a skin of red wine. The innkeeper's wife has not forgiven me. |
| 18 | I once called a lion out of his cage, to fight me. \| He yawned, turned round, showed me his back, and lay down. I won, I think. |
| 19 | Sancho kept fresh curds in my helmet, and I put it on. \| I thought my brains were melting. Always look in your helmet before battle. |
| 20 | Sancho's grandmother said: the world has two families. \| The Haves and the Haven'ts. You have a rock. So you are a Have! |
| 21 | In the cave, the maid of Dulcinea asked to borrow six reals. \| I had only four. A knight is rich in honor. Less so in reals. |
| 22 | A knight in the cave told me: Patience, and shuffle. \| Good words for you too. Patience, and push. |
| 23 | We flew on Clavileno, a wooden horse, eyes covered. \| Through wind and fire! And we came down in the very garden we left. Like your climb. |
| 24 | Most knights must ride for days to find a giant. \| You bring yours along, and fight him every day. Very wise. |
| 25 | Riddle: what is always coming, but never comes? \| Tomorrow. Sancho said the same of his island. Then it came! |
| 26 | Riddle: what can a knight give, and still keep? \| His word. A knight keeps his. So do you: up you go, every day. |
| 27 | Riddle: which knight does Sancho like best? \| A good night. He sleeps through every one, while I keep watch. |
| 28 | Riddle: what has a head and a tail, but no body? \| A coin. A knight never carries one, I said. Now Sancho carries them for me. |
| 29 | Riddle: what is as big as a giant, but weighs nothing? \| His shadow. I fought one once. It fought back very well. |
| 30 | Riddle: what shines all night, but has no fire? \| The moon. I kept watch over my armor by its light, the night I became a knight. |
| 31 | Riddle: what answers you, but never speaks first? \| An echo. In the mountains I called for Dulcinea. Only the echo answered. |

### Pinocchio

| # | Line |
|---|---|
| 1 | I am not made of wood. \| Oops. My nose. Please look away. |
| 2 | Your rock looks very light today. \| There it goes again. Sorry. |
| 3 | Why do I always lose at hide and seek? \| My nose is always the first to be found. |
| 4 | Riddle: what grows when it tells a lie? \| My nose. Too easy? That is the truth. Look, no change. |
| 5 | I told the Cricket: my trade is to eat, sleep and play. \| You chose pushing a rock. Mine is more fun, but yours has a better view. |
| 6 | The Fox said: plant a gold coin, water it, and a tree of gold grows. \| Do not plant your rock. A tree of rocks would be a lot of work. |
| 7 | Riddle: a mile long, three rows of teeth, and my father inside. \| The great shark. It had asthma and slept with its mouth open. We tiptoed out. |
| 8 | The Snail came down from the fourth floor to let me in. \| It took her nine hours. She would think you are very fast. So do I. |
| 9 | I drew a hundred buckets of water a day, for a glass of milk. \| You push all day, and get no milk at all. Ask the gods. It never hurts to ask. |
| 10 | In the Land of Toys, every day is a Saturday, except Sunday. \| And no school at all. Your week, I think, is seven Mondays. |
| 11 | The more Geppetto cut my nose, the longer it grew. \| I had not told a single lie yet. My nose was just practicing. |
| 12 | Fire Eater sneezes when he feels sorry for someone. \| If he saw you push that rock, he would sneeze all day. Bless him. |
| 13 | Riddle: what can you catch, but not throw? \| A cold. Or donkey fever. I caught that once, and my ears grew ten inches. |
| 14 | I was a log once. Master Cherry hit me. I cried: Not so hard! \| Your rock never says a word, however hard you push. What a polite rock. |
| 15 | Riddle: how can a wooden boy get to the top before you? \| One big lie, and his nose is there before him. But I will not. The top is yours. |

### Cleopatra

| # | Line |
|---|---|
| 1 | I came to Caesar rolled up in a carpet. \| Try it with your rock. Make an entrance. |
| 2 | I once melted a pearl in vinegar, and drank it, to win a bet. \| Never bet against a queen. |
| 3 | Why could I never see my mistakes? \| I was the queen of the Nile. And of denial. |
| 4 | Riddle: what fish comes out of the sea already salted? \| The one my diver put on Antony's hook. I said: Leave fish to us. Catch kingdoms. |
| 5 | For a dinner of twelve, my cooks roasted eight boars. \| Antony might eat now, or in an hour. So one boar was always just right. |
| 6 | Some nights, Antony and I dressed as servants and played tricks. \| We knocked on doors and ran. Please do not tell Rome. |
| 7 | I spoke to every envoy in his own tongue, with no one to help me. \| In every tongue, I asked about your rock. All of them said: heavy. |
| 8 | Caesar wrote it in three words: I came, I saw, I conquered. \| You come, you see the hill, you push. Every day. Caesar did it only once. |

### Sherlock Holmes

| # | Line |
|---|---|
| 1 | I perceive that you push a rock uphill. \| How? You are pushing a rock uphill. Some cases are simple. |
| 2 | Rule out the impossible, and what is left is the truth. \| So: you will push it again tomorrow. |
| 3 | Riddle: the more of it there is, the less you see. \| The dark. Elementary. |
| 4 | How many steps up to my rooms? Watson cannot say. \| He has climbed them hundreds of times. Seventeen. Do not count yours. |

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

### Isaac Newton

| # | Line |
|---|---|
| 1 | What goes up must come down. \| You knew that long before I wrote it down. |
| 2 | A body at rest stays at rest, unless a force moves it. \| You are the force. Your rock is the body. Push on. |
| 3 | I stood on the shoulders of giants. \| You stand on a hill. That works too. |
| 4 | Riddle: what weighs more, a pound of rock or of feathers? \| Neither. But the feathers are kinder to push uphill. |
| 5 | If you press a stone, the stone presses you back. \| So your rock works as hard as you do. You are a fine team, you two. |
| 6 | Throw a stone from a mountain. Faster. Faster still. \| It goes round the Earth, back to the mountain. Your rock takes the short way. |
| 7 | An apple pulls the Earth, as the Earth pulls the apple. \| So when your rock rolls down, the Earth comes up to meet it. A little. |
| 8 | I was like a boy on the shore, finding smoother pebbles. \| You found a much bigger one. And you never put it down. I admire that. |
| 9 | Sunlight takes about eight minutes to reach you. \| It is much faster than you. But it carries nothing, and you carry a rock. |
| 10 | As a boy, I made a windmill run by a mouse on a wheel. \| We called him the miller. I would hire you too, but you already have a job. |
| 11 | A double force makes a double motion. That is my second law. \| So push twice as hard, and up it goes twice as fast. Down needs no help. |
| 12 | Riddle: when is your rock at its lightest? \| At the top. The Earth pulls less from far away. About one apple less. |
| 13 | As a boy, I flew kites at night with lanterns on their tails. \| People took them for comets. Tie one to your rock: a very slow comet. |
| 14 | The pull that brings your rock down also holds the moon. \| The moon has the easier life, I think. Do not tell your rock. |
| 15 | Riddle: who rolls your rock back down every time? \| Not the gods. The Earth does. I did the sums. It is nothing personal. |

### Scheherazade

| # | Line |
|---|---|
| 1 | Each night I tell a tale, and I stop at the best part. \| You do the same with your rock. Tomorrow we go on. |
| 2 | Shall I tell you the tale of a man who pushes a rock? \| Ah, you know it. It is the one that never ends. |
| 3 | Riddle: the better you tell it, the shorter the night. \| A story. Yours is very long. |
| 4 | After each voyage, Sinbad said: Never again! \| He went to sea seven times. You two would get on very well. |
| 5 | Sinbad's crew lit a fire on an island. It dived into the sea. \| It was a sleeping whale. Before you rest, check that your hill is not a whale. |
| 6 | Riddle: white, round, fifty paces around, and no door. \| The egg of a roc, a giant bird. Your rock will never hatch. I think. |
| 7 | A man in the street cried: New lamps for old! \| The princess gave him the magic lamp. If he offers a new rock, keep yours. |
| 8 | Ali Baba's brother forgot the word. He said: Open, Barley! \| He tried every grain but sesame. Rocks are very strict about words. |

## Sources

- Diogenes Laertius, Lives of the Eminent Philosophers 6.20–81: Diogenes, his lamp, his jar, the plucked chicken, the statues, the cup, the wine, when to eat, and the citizen of the world. Plutarch, Life of Alexander 14: "Stand out of my sun." Most of his other lines retell the anecdotes and sayings of Diogenes Laertius 6.20–81, in the translation of R. D. Hicks (1925), with the section of each in the writers' notes.
- Lewis Carroll, Alice's Adventures in Wonderland (1865): the rabbit hole (chapter 1), "Curiouser and curiouser!" (2), the race of the Dodo (3), the Cheshire Cat (6) and the riddle of the Hatter (7). Through the Looking-Glass (1871), chapter 5: the six impossible things of the White Queen. Her other lines retell scenes of both books, chapter by chapter: the Caterpillar, the Duchess, the Hatter, the March Hare and the Dormouse, the Queen of Hearts, the Mock Turtle and the Gryphon, Tweedledum and Tweedledee, the White Knight, the two Queens, the Jabberwock and the game of chess.
- Charles Perrault, Le Petit Chaperon rouge (1697), and the Brothers Grimm (1812): the cake and the wine, the path, and the big ears. Her other lines also use the bobbin and the latch and the race of Perrault, and the second wolf of the Grimms.
- Miguel de Cervantes, Don Quixote (1605), part 1, chapters 8 and 21: the windmills, and the helmet of Mambrino. His other lines come from both parts (1605, 1615), in the translation of John Ormsby (1885): the visor of pasteboard, the flock of sheep, the fulling mills, the lion, Clavileno and the island of Sancho.
- Carlo Collodi, The Adventures of Pinocchio (1883): the nose. His other lines: Geppetto, Fire Eater, the Fox and the Cat, the field of miracles, the Fairy, and the great shark.
- Plutarch, Life of Antony 25–29 and Life of Caesar 49: Cleopatra comes to Caesar rolled up. Pliny, Natural History 9.119–121: the pearl in vinegar. Her other lines: Plutarch, Life of Antony 27–29 (her wit, her languages, the salted fish) and Life of Caesar 50 (I came, I saw, I conquered).
- Arthur Conan Doyle, The Sign of Four (1890): the impossible and the truth. A Scandal in Bohemia (1891): the seventeen steps.
- William Shakespeare, Hamlet (about 1600), act 5, scene 1: Yorick, and the riddle of the gravedigger.
- Bram Stoker, Dracula (1897).
- H. P. Lovecraft, The Call of Cthulhu (1928): the words of the cult.
- Isaac Newton, Principia (1687): the first law of motion. His letter to Robert Hooke (1675): "If I have seen further, it is by standing on the shoulders of giants." William Stukeley, Memoirs of Sir Isaac Newton's Life (1752): the apple. "What goes up must come down" is an old saying. His other lines: the Principia (the laws of motion, Book III), the Opticks (1704), and The Life of Sir Isaac Newton (1846).
- The Thousand and One Nights, the frame story: Scheherazade tells the king a tale each night, and stops at dawn. Her other lines: Sinbad the Sailor, Aladdin, and Ali Baba and the forty thieves, in the versions of Andrew Lang (1889, 1898).
- The other riddles are old folk riddles. The lines with no source are new jokes, in the spirit of their joker.

## The art

- **A joker:** a sprite of at most 15 by 22 pixels, so that it fits the card with its outline, a little shorter than Sisyphus, standing on the slope like the props, with the feet on the ground under the middle of the sprite. A joker faces down the hill, to Sisyphus, and turns to watch Sisyphus go once the rock has passed. A joker stands where the front of the rock reaches the middle of the sprite, as a stone does, and is drawn behind the rock and Sisyphus.
- **The bubble:** `drawPopup`, as for a stone. Its tail points at the middle of the joker; when the joker has left the picture, it points at the edge of the picture. The tip of the tail stands 3 pixels above the head of the joker, or above the rock when the rock is higher. The bubble stays under the day and the obols: when it would rise into them, it moves down.
- **A card:** like a small playing card, about as tall for its width, with round corners, 4 to a row. It has no rank: the chance shows on the open card. Face down, it shows the back of the deck: dark terra, with a pattern of crossed lines. Face up, it is paper, with the joker, outlined in dark, standing on its bottom. The outline lets the white and the cream of a joker show on the paper. A rare card, of a joker who comes 1 in 100 or less often (`RARE_CHANCE`), has a gold banner across its top-right corner, face up and face down: today Sherlock Holmes, Hamlet, Count Dracula and Cthulhu.
- **The open card:** a big card over the cards: the joker at twice the size, the name, the chance, such as "1 in 8", who the joker is, and how many times the player met the joker, such as "Met 3 times". The open card of a rare joker has the banner too, and says "rare" after the chance, such as "1 in 125 · rare".

## The Jokers tab

- The tabs of the Worlds screen become Worlds, Labors and Jokers. The Worlds screen shows no obols, so the three words fit the smallest phone. The obols open the Agora from the main screen.
- Under the title row, "Jokers met" and how many of the 12 the player has met, such as "3/12". Then the 12 cards in three rows of 4, in the order of the ids.
- A tap on a face-up card opens it. A tap anywhere closes it. The back button closes it too. The banners show which rare cards the player still has to find.
- While a card is face up that the player has not seen on the tab, a dot stands after "Jokers", and the dot by the day shows, as for a new labor. The Worlds screen then opens on the tab with the news: Labors first, then Jokers.

## Saved data

- `nextJoker`: where the next joker stands, in pixels walked, or -1 when none stands ahead yet. Old saved games: -1, so a joker is put by the path at once.
- `joker`: the id of the next joker. Old saved games: 0. An id that the game does not have is Diogenes.
- `met.0` to `met.11`: how many times the player met each joker, by id, in all climbs and worlds. A joker met once or more has the card face up, and the count tells which line comes next. Kept at the top and in another world.
- `jokersSeen`: the cards seen face up on the Jokers tab, one bit for each id: 1 for Diogenes, 2 for Alice, up to 2048 for Scheherazade. Kept.
- `docs/saved-data.md` lists the new keys.

## To test it

- The jokers: 12, with the ids 0 to 11 in order; the weights add up to 500; each has a line, each line one or two texts; every text has only the letters of the font; every text fits the bubble with its note on the smallest phone.
- The choice: over many choices, each joker comes about as often as its weight says; the choice takes no number from the random numbers of the game, so the visions and the tales stay the same.
- On the path: the first joker of a climb stands 20 to 40 turns ahead and talks on day 1, and after each joker the next stands 72 to 144 turns ahead; the bubble opens 3 turns before the rock reaches the joker, and each text shows for 6 turns; after it, the joker is met once more, and the next joker stands ahead; the lines of a joker come in turn; a joker stands at least 15 turns from the next stone, also when the stone comes after him; a vision hides the bubble, and it waits; the gods' pushes do not reach a joker; no bubble at the top; a new climb puts a new joker ahead; the joker ahead, his id and the jokers met survive a restart.
- The tab: the tabs fit the smallest phone; a face-down card does not open; a face-up card opens, and a tap closes it; the dot shows for a new card, and goes when the tab opens; the Worlds screen opens on the tab with the news; the cards of the rare jokers, and only they, have a banner, face up and face down, and so has the open card.
- The dev screen: Joker brings the next joker at once, and its row fits the smallest phone.
- Snapshots: Diogenes with his bubble, ahead of the rock and behind Sisyphus, also on the smallest phone; all the jokers in a row, as they are and in the light of the night; the Jokers tab with some cards face up, and the dot by the day; an open card.

## Later

- A labor for the jokers met.
- More jokers, and seasons for some of them.
