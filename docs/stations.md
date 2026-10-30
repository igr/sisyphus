# Stations

Design for the stations: carved stones by the path that tell a tale, from day 3 of each climb, and one more part for each item bought. Each climb tells one tale from a pool of tales, in every world. The tale of Sisyphus comes first.

## Why

The middle of a climb has few new things. At 5 minutes a day, a session is about 300 turns, and the only news is the next item. A tale gives each session something to wait for: what the next stone says. The tale of Sisyphus tells why he pushes the rock, so the top and the words of Camus mean more. The other tales give each new climb, in each world, a new story.

## Goals

- From day 3 of each climb, steles, carved stones, stand by the path ahead, one at a time: the first with no purchase, and one more for each item bought. When he comes near a stone, a comic popup over it tells the next part of the tale, and how far the tale has come, such as "9/15".
- A tale has 15 parts: the first on day 3, then one for each item bought, up to the 14th. The 15th part comes at the start of the final climb. Thus every player hears the whole tale before the top, at any pace.
- At 5 minutes a day, a session brings about 2 parts. At 30 minutes a day, it brings about 7.
- A pool of fourteen tales: Sisyphus, Typhon, Orpheus, Isis and Osiris, Bellerophon, Empedocles, Demeter and Persephone, Hermes, Phaethon, Arion, Daedalus and Icarus, Io, Kaguya, and Izanagi and Izanami. Each climb tells one of them. The tale of Sisyphus comes first, then each other tale once, at random. After that, any tale can come again, but not the tale of the climb before.
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
- **The pool.** Each tale has 15 parts, and an id that never changes: Sisyphus 0, Typhon 1, Orpheus 2, Isis and Osiris 3, Bellerophon 4, Empedocles 5, Demeter and Persephone 6, Hermes 7, Phaethon 8, Arion 9, Daedalus and Icarus 10, Io 11, Kaguya 12, Izanagi and Izanami 13. Any tale can be told in any world, so no text names the place where it is told.
- **The tale of a climb.** A climb takes its tale when it begins: in a new game, at the top, at a move to another world, and at the Reset of the climb in the dev screen. A move before he has walked in the climb, as right after the top, keeps the tale that the climb took. While the tale of Sisyphus is not told, it is the tale. After it, the tale is a random one that is not told yet. A tale is told when its 15th part is told. When all are told, the tale is a random one of them, but not the tale of the climb before. A tale stays told. A climb that ends before its tale is told leaves that tale not told, so it can come again. The Reset of the shop in the dev screen starts the same tale again from its first part.
- **The stones of a world.** All stones have the same shape. Each world has its own stone: pale limestone in Corinth, dark lava rock on Mount Etna, black stone in The Underworld, sandstone in Cairo, and gray granite on Mount Fuji. The relief is carved in the colors of that stone.
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

### The tale of Bellerophon

Id 4. The grandson of Sisyphus, who caught Pegasus at Peirene, the spring of Corinth. He pushes beside Sisyphus in Corinth, and Pegasus flies by there. Relief: a wing.

| # | Text |
|---|---|
| 1 | Bellerophon was the grandson of Sisyphus. The gods gave him beauty and courage. |
| 2 | Pegasus, the winged horse, came to drink at the spring Peirene, in Corinth. |
| 3 | Bellerophon tried in vain to catch him. A seer told him to sleep on Athena's altar. |
| 4 | "Are you sleeping?" Athena asked in his dream. "Take this charm for the horse." |
| 5 | He woke, and a bridle of gold lay beside him. With it, he caught Pegasus. |
| 6 | Later, the wife of King Proetus loved him. He said no, and she lied about him. |
| 7 | Proetus sent him to the queen's father in Lycia, with a folded tablet of deadly signs. |
| 8 | The king of Lycia feasted him for nine days. On the tenth, he read the signs. |
| 9 | So he sent Bellerophon to kill the Chimera, part lion, part goat, part snake. |
| 10 | It breathed fire. But Bellerophon flew high on Pegasus, and shot it from the sky. |
| 11 | Then he beat the Solymi, the Amazons, and the king's men who lay in wait for him. |
| 12 | The king gave him his daughter as his wife, and shared his kingdom with him. |
| 13 | He wanted to fly up to Zeus, to the home of the gods. But Pegasus threw him. |
| 14 | The gods turned from him, and he wandered alone, far from the paths of men. |
| 15 | Pegasus found a home on Olympus. Now he carries the thunder and lightning of Zeus. |

### The tale of Empedocles

Id 5. The wise man of Sicily, who leapt into Etna and left only a sandal. He pushes beside Sisyphus on Mount Etna, and asks riddles there. Relief: a sandal.

| # | Text |
|---|---|
| 1 | Empedocles was born in Acragas, in Sicily. His grandfather won a horse race at Olympia. |
| 2 | "My people feast as if to die tomorrow," he said, "and build as if to live forever." |
| 3 | In his poems, he taught that all things are made of four: fire, water, earth and air. |
| 4 | Love joins them into one, he said, and Strife pulls them apart, again and again. |
| 5 | "I was once a boy and a girl," he wrote, "a bush, a bird and a fish." |
| 6 | He caught the winds that hurt the crops in bags of donkey skin, set on the hills. |
| 7 | A woman did not breathe for thirty days. Empedocles sent her home alive. |
| 8 | At Selinus, a foul river made the people sick. He led two other rivers into it. |
| 9 | The water turned sweet, and the sickness ended. The people prayed to him as to a god. |
| 10 | He was asked to be king, but he refused, for he loved a simple life. |
| 11 | He wore a purple robe, a golden belt, a wreath of laurel, and sandals of bronze. |
| 12 | "I walk among you as an immortal god," he told his city, "no longer a mortal." |
| 13 | After a feast, his friends slept in a field. At dawn, Empedocles was gone. |
| 14 | A voice had called him in the night. "He is a god now," said his friend Pausanias. |
| 15 | Others say he leapt into the crater of Etna. It threw back one of his bronze sandals. |

### The tale of Demeter and Persephone

Id 6. Persephone, the queen below, let Sisyphus go, and she pushes in The Underworld. Demeter lit her torches in the fire of Etna, as the Torches of Demeter show on Mount Etna. Relief: an ear of wheat.

| # | Text |
|---|---|
| 1 | Demeter was the first to plow, and to give men grain. Her daughter was Persephone. |
| 2 | In Sicily, by a lake near Henna, spring never ended. Persephone picked flowers there. |
| 3 | The Earth grew a narcissus with a hundred flowers. As she picked it, the ground opened. |
| 4 | Hades rose in his golden chariot, and carried her off below. She called for her mother. |
| 5 | Demeter lit two torches in the fire of Etna, and searched for her night and day. |
| 6 | For nine days she wandered the earth. In her grief, she did not eat or drink. |
| 7 | Then she asked the Sun, who sees all. "Zeus gave her to Hades," he said. |
| 8 | Angry with Zeus, she left the gods, and came to Eleusis as an old woman. |
| 9 | The queen of Eleusis took her in, to nurse her baby son, Demophon. |
| 10 | At night she laid him in the fire, so that he would never grow old or die. |
| 11 | His mother saw it, and cried out. "I am Demeter," said the goddess. "Build me a temple." |
| 12 | She sat in her temple, longing for her daughter. All that year, she let no seed grow. |
| 13 | So Zeus sent Hermes below. Hades let her go, but first he gave her a pomegranate seed. |
| 14 | She ran into her mother's arms. "Did you eat anything below?" asked Demeter. She had. |
| 15 | So for a third of each year, she is queen below. Each spring, she comes back up. |

### The tale of Hermes

Id 7. Hermes guides the player, and he took Sisyphus below by force. On the day he was born he stole cattle, and his son Autolycus stole the cattle of Sisyphus. Relief: the staff of Hermes.

| # | Text |
|---|---|
| 1 | Hermes, the son of Zeus and Maia, was born at dawn, in a cave on Mount Cyllene. |
| 2 | At the door he found a tortoise. By noon, he had made its shell into a lyre. |
| 3 | By evening he longed for meat. He ran to Pieria, and took fifty cows of Apollo. |
| 4 | He made the cows walk backwards, and he wore sandals of twigs, to hide the tracks. |
| 5 | An old man in a vineyard saw them. "You saw nothing," said Hermes, "and heard nothing." |
| 6 | By the river Alpheus, he made the first fire with sticks, and roasted two of the cows. |
| 7 | At dawn he went home. Like a mist, he slipped through the keyhole, into his cradle. |
| 8 | Apollo looked for his cows. "A child drove them backwards," said the old man. |
| 9 | A bird showed Apollo the thief. In the cave, the baby lay curled up, as if asleep. |
| 10 | "I was born yesterday," said Hermes. "My feet are soft. What are cows?" |
| 11 | Apollo took him to Zeus. Zeus laughed out loud to hear his son lie so well. |
| 12 | Hermes played the lyre. Apollo laughed for joy: "This song is worth fifty cows!" |
| 13 | Hermes gave Apollo the lyre, and got the cows for it. They were friends forever. |
| 14 | Apollo gave him a staff of gold. Zeus made him the only messenger to Hades. |
| 15 | Later, Hermes gave his skill to his son Autolycus, who stole the cattle of Sisyphus. |

### The tale of Phaethon

Id 8. The sun crosses the sky only while Sisyphus pushes, and Phaethon drove it for a day. Epaphus, who pushes in Cairo, begins the quarrel; Etna burns, and the Nile hides; his sisters become poplars, as by the path in Corinth; and the Sun goes back to a work that never ends. Relief: the sun.

| # | Text |
|---|---|
| 1 | Phaethon was proud that the Sun was his father. Epaphus, son of Zeus, laughed at him. |
| 2 | His mother, Clymene, swore by the Sun that it was true. "Go and ask him," she said. |
| 3 | He went east, and climbed a steep path to the palace of the Sun, bright with gold. |
| 4 | "You are my son," said the Sun. "Ask for any gift. I swear it by the Styx." |
| 5 | Phaethon asked for the chariot of the Sun, for one day. His father was sorry he swore. |
| 6 | "The road starts steep," said the Sun. "My horses can hardly climb it, even at dawn." |
| 7 | "Too high, and the sky burns. Too low, the earth. Keep to the middle," he said. |
| 8 | At dawn Phaethon took the reins. But the horses ran wild, and came too near the earth. |
| 9 | The earth caught fire. Etna burned with double flames, and the Nile hid its head. |
| 10 | The Earth begged Zeus for help. He threw his lightning, and Phaethon fell like a star. |
| 11 | Far from home, the river Eridanus took him in. The nymphs of the west buried him. |
| 12 | "Here lies Phaethon," they wrote on his stone. "He fell, but he dared great things." |
| 13 | The Sun hid his face in grief. For one day there was no sun, only firelight. |
| 14 | Phaethon's sisters wept until they became poplars. Their tears turned into amber. |
| 15 | "My work never ends," said the Sun. But the gods begged him, so he drove again. |

### The tale of Arion

Id 9. A singer at the court of Corinth, saved by a dolphin, as a dolphin brought the boy whom Sisyphus buried. Dolphins leap in the sea of Corinth. Relief: a dolphin.

| # | Text |
|---|---|
| 1 | Arion was the finest lyre player of his day. He lived with Periander, ruler of Corinth. |
| 2 | When he sang, the streams stood still, and the wolf stopped chasing the lamb. |
| 3 | He sailed to Italy and Sicily, and his songs made him famous there, and rich. |
| 4 | To go home, he took a ship of Corinth, for he trusted the Corinthians most. |
| 5 | But out at sea, the sailors planned to take his money and throw him overboard. |
| 6 | He offered them the money for his life. But they told him to jump into the sea. |
| 7 | "Let me sing first," he asked. The sailors let him, glad to hear the best singer alive. |
| 8 | He put on his purple robe and his crown. He took his lyre, and he sang. |
| 9 | When the song was over, he leapt into the sea in all his finery. |
| 10 | The ship sailed on to Corinth. But a dolphin took Arion on its back. |
| 11 | Seated on the dolphin, he played his lyre, and paid for the ride with a song. |
| 12 | The dolphin carried him to Taenarum. From there he went to Corinth, and told his tale. |
| 13 | Periander did not believe him. When the ship came, he asked the sailors about Arion. |
| 14 | "He is safe in Italy," they said. Then Arion stood before them, in his purple robe. |
| 15 | For its kindness, Zeus set the dolphin among the stars. It shines there still. |

### The tale of Daedalus and Icarus

Id 10. The great inventor made the Sphere of Daedalus, the third rock of Corinth, and he flew to Sicily. Relief: the labyrinth.

| # | Text |
|---|---|
| 1 | Daedalus of Athens was the great inventor. Men said his statues could see and walk. |
| 2 | In Crete, he built the Labyrinth for King Minos. Even he could hardly find the way out. |
| 3 | Daedalus longed for Athens, his home. But Minos would not let him leave Crete. |
| 4 | "Minos may close the land and the sea," said Daedalus, "but the sky is open." |
| 5 | He laid feathers in a row, from small to large, and bound them with thread and wax. |
| 6 | His son Icarus played. He chased the feathers, and softened the wax with his thumb. |
| 7 | "Fly between the sea and the sun," said Daedalus, "not too low, and not too high." |
| 8 | They flew. Fishermen, shepherds and farmers looked up, and thought the two were gods. |
| 9 | But Icarus loved the flight, and flew higher and higher. The sun melted the wax. |
| 10 | He fell into the sea, calling his father. That sea is still called the Icarian Sea. |
| 11 | Daedalus buried him on an island, now called Icaria, and flew on to Sicily. |
| 12 | King Cocalus took him in. Daedalus built the king a city on a rock, safe from attack. |
| 13 | Minos searched for him with a spiral shell. "A prize to the one who threads it!" |
| 14 | Cocalus gave it to Daedalus, who tied a thread to an ant. The ant walked through. |
| 15 | So Minos knew Daedalus was there. But Minos died in his bath, and Daedalus was safe. |

### The tale of Io

Id 11. Io and her son Epaphus push in Cairo. Like Sisyphus, she could not rest: the gadfly drove her over the world, until the Nile. Relief: a cow.

| # | Text |
|---|---|
| 1 | Io was the daughter of the river god Inachus. Zeus saw her, and he loved her. |
| 2 | She ran away, but he hid the land under a dark cloud, and caught her. |
| 3 | Hera saw the cloud, and came down. But Zeus had turned Io into a white cow. |
| 4 | Hera asked for the lovely cow as a gift, and Zeus could not say no. |
| 5 | Hera gave her to Argus, who had a hundred eyes. Only two slept at a time. |
| 6 | When Io tried to speak, she mooed, and her own voice frightened her. |
| 7 | Her father did not know her. So with her hoof, she wrote her name in the dust. |
| 8 | Zeus sent Hermes. He came as a shepherd, and he played to Argus on reed pipes. |
| 9 | Hermes told of Syrinx, a nymph who became reeds. Before he finished, all the eyes slept. |
| 10 | Then Hermes killed Argus. Hera set his hundred eyes in the tail of her peacock. |
| 11 | Hera sent a gadfly. Its sting drove Io over the world, across lands and seas. |
| 12 | She crossed a strait, and men named it after her: the Bosporus, the cow's crossing. |
| 13 | At last, by the Nile, a touch of Zeus calmed her. She was a woman again. |
| 14 | She bore Epaphus, named for that touch. He became a king, and he founded Memphis. |
| 15 | In Egypt, men called her Isis: a woman with the horns of a cow. |

### The tale of Kaguya

Id 12. The moon princess pushes on Mount Fuji. Her tale ends on that mountain: the elixir of no death burned on its top. Relief: the moon.

| # | Text |
|---|---|
| 1 | Long ago, an old bamboo cutter saw a stalk that shone. Inside it was a tiny girl. |
| 2 | He took her home to his wife. From then on, he often found gold in the bamboo. |
| 3 | In three months she grew up. She was named Kaguya, and her light filled the house. |
| 4 | Five lords wanted to marry her. She asked each for a wonder from far away. |
| 5 | One prince brought a jewelled branch. Then the men who made it came for their pay. |
| 6 | Another brought a robe of fire-rat fur, which no fire could burn. It burned in a flash. |
| 7 | The Emperor came to take her away. She vanished like a shadow, and he let her be. |
| 8 | For three years, they wrote letters. Then she began to weep when she looked at the moon. |
| 9 | "I came from the moon," she said. "At the full moon, its people will take me back." |
| 10 | The Emperor sent two thousand soldiers to guard her on the night of the full moon. |
| 11 | In a light brighter than noon, the people of the moon came down. No soldier could fight. |
| 12 | She sent the Emperor a letter, and the elixir of no death from the moon. |
| 13 | In the robe of feathers, she forgot all sorrow. Then she rose into the sky. |
| 14 | The Emperor had the letter and the elixir burned on the mountain nearest to heaven. |
| 15 | The mountain was named Fuji, and its smoke still rises into the clouds, they say. |

### The tale of Izanagi and Izanami

Id 13. Izanagi pushes on Mount Fuji. Like Sisyphus he came back from the dead, like Orpheus he looked back, and his rock stays where he put it. Relief: a peach.

| # | Text |
|---|---|
| 1 | Long ago, the gods gave Izanagi and Izanami a jewelled spear, to make the land. |
| 2 | From the Floating Bridge of Heaven, they stirred the sea. The drops became an island. |
| 3 | On the island they walked around a pillar, and married. Islands and gods were born. |
| 4 | Then came the god of fire. His birth burned Izanami, and she fell sick and died. |
| 5 | Izanagi wept for her. Then he followed her down to Yomi, the land of the dead. |
| 6 | "Come back," he said. "The lands that we made are not finished yet." |
| 7 | "Too late," she said. "I have eaten the food of Yomi. But I will ask its gods." |
| 8 | "Do not look at me," she said. She went inside, and she was gone too long. |
| 9 | He lit a tooth of his comb, and looked. He saw what death had done to her. |
| 10 | He ran. "You have shamed me!" she cried, and she sent a hag of Yomi after him. |
| 11 | His headdress became grapes, and his comb bamboo shoots. The hag stopped to eat. |
| 12 | Then warriors of Yomi chased him. At the pass, he threw three peaches, and they fled. |
| 13 | Last came Izanami. He shut the pass with a rock that only a thousand men could move. |
| 14 | She swore to kill a thousand people a day. He swore fifteen hundred would be born. |
| 15 | He washed in a river, and the Sun goddess, the Moon god and Susanoo were born. |

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
- Homer, Iliad 6.152–202: the grandson of Sisyphus, the wife of Proetus, the folded tablet, the nine days in Lycia, the Chimera, the Solymi, the Amazons, the ambush, the king's daughter, and the lonely wandering. Pindar, Olympian 13.60–92: Pegasus by the spring, the seer, Athena and the golden bridle in a dream, and Pegasus in the stables of Zeus. Pindar, Isthmian 7.43–48: Pegasus throws him. Strabo, Geography 8.6.21: Pegasus caught while drinking at Peirene. Apollodorus, Library 2.3.1–2: the queen's lie, and the Chimera shot from the sky. Hesiod, Theogony 284–286 and 319–325: the thunder and lightning of Zeus, and the Chimera.
- Diogenes Laertius, Lives of Eminent Philosophers 8.51–77: Empedocles of Acragas, his grandfather at Olympia, his saying on the people of Acragas, the four elements, Love and Strife, the boy, the girl, the bush, the bird and the fish, the winds caught in bags of donkey skin, the woman who did not breathe for thirty days, the rivers of Selinus, the kingship he refused, the purple robe and the bronze sandals, the immortal god, the night after the feast, Pausanias, and Etna and the sandal. Strabo, Geography 6.2.8: the sandal by the rim of the crater. Horace, Ars Poetica 463–466: the leap into Etna. Lucretius, On the Nature of Things 1.716–717: Empedocles of Acragas, in Sicily.
- Homeric Hymn 2, to Demeter, 1–447: the narcissus, Hades and his golden chariot, the nine days with torches and no food, the Sun, Eleusis and the old woman, Demophon in the fire, the temple, the year with no seed, Hermes, the pomegranate seed, and a third of each year below until spring. Ovid, Metamorphoses 5.341–343, 5.385–445 and 5.505–508: the first plow, the lake by Henna in Sicily, the torches lit at Etna, and the queen below. Ovid, Fasti 4.491–494: the torches lit at Etna. Apollodorus, Library 1.5.1–3: torches by night and day, and a third of each year.
- Homeric Hymn 4, to Hermes, 1–580: the birth on Cyllene, the lyre, the fifty cows, the sandals, the old man, the fire, the keyhole, "born yesterday", Zeus laughs, the lyre for the cows, the staff of gold, and the messenger to Hades. Apollodorus, Library 3.10.2: the cave of Cyllene, and the cows for the lyre. Homer, Odyssey 19.394–398, Ovid, Metamorphoses 11.312–315, and Hyginus, Fabulae 201: his son Autolycus, and the cattle of Sisyphus.
- Ovid, Metamorphoses 1.748–779 and 2.1–400: Epaphus and Clymene, the palace of the Sun, the oath by the Styx, the steep road and the middle way, the wild horses, Etna and the Nile, the Earth's prayer, the lightning, the river Eridanus and the grave, the day with no sun, the tears of amber, and the work of the Sun that never ends. Hyginus, Fabulae 152A and 154: the sisters become poplars.
- Herodotus 1.23–24: Arion and Periander, Italy and Sicily, the Corinthian ship, the last song, the dolphin to Taenarum, and the sailors at Corinth. Ovid, Fasti 2.79–118: the streams and the wolf, the purple robe and the crown, the song on the dolphin, and the dolphin among the stars.
- Ovid, Metamorphoses 8.152–235 and 8.260–262: the Labyrinth, the sky that Minos does not rule, the wings of feathers, thread and wax, Icarus at play, the middle way, the fishermen, shepherds and farmers, the fall, the Icarian Sea, Icaria, and Sicily. Apollodorus, Library 3.15.8, and Epitome 1.12–15: the first inventor of images, the advice, Sicily, Cocalus, the spiral shell and the ant, and the end of Minos. Diodorus Siculus, Library of History 4.76–79: the statues that see and walk, Athens, the city on a rock for Cocalus, and Minos in the bath.
- Ovid, Metamorphoses 1.582–747: Io and Inachus, the cloud, the white cow, Argus, the name in the dust, Hermes and Syrinx, the eyes on the peacock, the Nile, and Io a woman again. Aeschylus, Prometheus Bound 566–682 and 729–852, and Suppliants 299–315 and 574–579: the gadfly, the Bosporus, the touch of Zeus, and Epaphus. Apollodorus, Library 2.1.3–4: the gadfly, Egypt, Epaphus by the Nile, Memphis, and Io called Isis. Herodotus 2.41: Isis has the horns of a cow, as the Greeks show Io.
- The Tale of the Bamboo Cutter (Taketori Monogatari, about 900), tr. F. V. Dickins, The Old Bamboo-Hewer's Story (1888), pp. 1–36: the shining bamboo, the gold, Kaguya, the five lords, the jewelled branch and its makers, the robe of fire-rat fur, the Emperor and the letters, the moon, the two thousand soldiers, the people of the moon, the elixir and the letter, the robe of feathers, and the burning on Fuji. The Japanese text, 8: she becomes a shadow; 9: the elixir of no death, and the mountain nearest to heaven. Perseus has no Japanese texts, so the pages are those of the translation.
- Kojiki (712), Book I, sections III–VII and IX–X, tr. B. H. Chamberlain (1882): the jewelled spear and the first island, the pillar, the islands and the gods, the god of fire and the death of Izanami, Yomi, the food of Yomi, the comb, the hag, the grapes and the bamboo shoots, the three peaches, the rock of a thousand men, the thousand deaths and fifteen hundred births, and the washing that brought the Sun goddess, the Moon god and Susanoo. Nihongi (720), Book I, tr. W. G. Aston (1896), pp. 24–30: the peaches flung, and the rock of a thousand men.
- Albert Camus, The Myth of Sisyphus (1942).

## The art

- **The stone:** a slab with a rounded top, about 9 pixels wide and 15 pixels high, standing on the slope like the props, a little in front of the path. Its relief, about 7 by 7 pixels, fills the top half. Short lines of carved letters fill the bottom half.
- One sprite for the stone, in the colors of each world: pale limestone, dark lava rock, black stone, sandstone, gray granite. One relief for each tale: a rock, a volcano, a lyre, the eye of Horus, a wing, a sandal, an ear of wheat, the staff of Hermes, the sun, a dolphin, the labyrinth, a cow, the moon, and a peach, carved in the colors of the stone. The light of the time of day falls on it, as on the props.
- **The popup:** a bubble of paper (F1E4C8) with a dark outline (2A1A0C) and round corners, 2 pixels from the edges of the scene. The text is dark, from the left; the note is terra (C8643C). Its tail points down at the stone and ends 3 pixels above the top of the rock, so that it covers neither him nor the rock. The light of the time of day does not fall on it. `drawPopup` in `gfx/Popup.kt` draws a popup for any text, so other speakers can use it.

## Saved data

- `stelesTold`: the parts told in this climb, 0 to 15. Back to 0 when a climb begins.
- `nextStele`: where the next stone stands, in pixels walked, or -1 when no stone stands ahead. It stays until the part of the stone is told. Back to -1 when a climb begins.
- `tale`: the id of the tale of this climb. Old saved games: 0, the tale of Sisyphus.
- `talesTold`: the tales told, one bit for each id: 1 for Sisyphus, 2 for Typhon, 4 for Orpheus, 8 for Isis and Osiris, 16 for Bellerophon, 32 for Empedocles, 64 for Demeter and Persephone, 128 for Hermes, 256 for Phaethon, 512 for Arion, 1024 for Daedalus and Icarus, 2048 for Io, 4096 for Kaguya, 8192 for Izanagi and Izanami. Old saved games: 0.
- `docs/saved-data.md` lists the new keys, with their start values for old saved games.

## To test it

- Tests: the first stone stands 30 turns ahead on day 3, with no item bought; no stone stands before day 3; a stone stands 30 turns ahead after an item is bought; its popup opens 3 turns before the rock reaches it, and the part is told 6 turns after it opens; a part with two texts shows them one after the other; the panel shows no part; the parts come in order, once each; two items bought together give two stones, 30 turns apart; the gods' pushes do not reach a stone; a popup waits for a vision; the final climb does not cut a part; the 15th part comes before the first herm; no popup shows at the top; the stones, the parts told and an open popup survive a restart; a new climb starts its tale from the first part; every text fits the popup on the smallest phone, and a vision hides it; the dev screen's Stone shows the next part at once, and its row fits the smallest phone.
- Tests of the pool: the first climb tells the tale of Sisyphus; it comes again until it is told; the next climbs tell each other tale once, before any tale comes again; after that, the tale of the climb before does not come at once; a climb keeps its tale across a restart; the choice takes no number from the random numbers of the game; an old save tells the tale of Sisyphus; every world has stones, in its own colors; the stones of a tale share its relief.
- Snapshots: a stone ahead of the rock with its popup, also on the smallest phone, and in each world; the stone of each world with the relief of each tale.

## Later

- A labor for the tales told.
- The tales told, on a tab of the Worlds screen.
