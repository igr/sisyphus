package dev.oblac.sisyphus.game

/** A part of a tale: its [texts] show one after the other, in a popup over its stone. */
class Part(vararg val texts: String)

/** A stone of the tale by the path, carved with the [part] of the tale that it tells. The rock reaches it when he has walked [at] pixels. */
class Stele(val at: Double, val part: Int)

/** A tale of the pool, told by the stones of a climb in any world. Its [id] never changes: saved games keep it. See `docs/stations.md`. */
class Tale(val id: Int, val parts: List<Part>)

/** The tale of Sisyphus himself: why he pushes the rock. It comes first. */
val SISYPHUS = Tale(
    0,
    listOf(
        Part("Long ago, Sisyphus built the city of Ephyra, and he became its king."),
        Part("His ships sailed on two seas, and his city grew rich. Later, men called it Corinth."),
        Part("Homer calls him the craftiest of men. Even the gods could not trust him."),
        Part("The thief Autolycus stole his cattle, and changed their colors."),
        Part("But under every hoof, Sisyphus had cut the words: stolen by Autolycus."),
        Part("A dolphin carried a drowned boy to shore. Sisyphus buried him by a pine."),
        Part("For the boy, he began the Isthmian Games. Winners wore a crown of pine."),
        Part("One day he saw Zeus carry off Aegina, the daughter of the river god Asopus."),
        Part("\"Give my city a spring,\" he told Asopus, \"and I will tell you where she is.\""),
        Part("Zeus was angry. He sent Death with chains, to take the king below."),
        Part("\"Show me how the chains work,\" said Sisyphus. Then he chained Death himself."),
        Part("With Death in chains, nobody died, not even in battle. So Ares set Death free."),
        Part("Before he went below, he told Merope, his wife: \"Do not bury me.\""),
        Part(
            "Below, he told Persephone: \"My wife did not bury me. Let me go and punish her.\"",
            "She let him go. He stayed in the sun, until Hermes took him down by force.",
        ),
        Part("For his tricks, the gods gave him a rock. He must push it to the top, forever."),
    ),
)

/** Typhon, who fought Zeus, and lies under Mount Etna. */
val TYPHON = Tale(
    1,
    listOf(
        Part("The gods won their war with the Giants. The Earth, mother of the Giants, was angry."),
        Part("She gave birth to Typhon, taller than the mountains. His head hit the stars."),
        Part("A hundred snake heads grew from his hands, and fire flashed from his eyes."),
        Part("He threw burning rocks at the sky. The gods were afraid of him."),
        Part("They ran away to Egypt, and they hid there as animals."),
        Part("Only Zeus stayed to fight him, with his lightning and a sickle."),
        Part("Typhon caught Zeus in his coils, and cut the sinews of his hands and feet."),
        Part("He hid the sinews in a cave, in a bearskin, and a dragon guarded them."),
        Part("Hermes and Pan stole the sinews back, and fitted them into Zeus again."),
        Part("Zeus rose in a chariot of winged horses, and his lightning fell like rain."),
        Part("The Fates gave Typhon fruit. \"It will make you strong,\" they said. It made him weak."),
        Part("Typhon threw whole mountains at Zeus. The lightning threw them back at him."),
        Part("His blood ran down a mountain in Thrace. Men named it Haemus, the mountain of blood."),
        Part("He ran over the sea to Sicily. There Zeus threw Mount Etna on top of him."),
        Part("He lies there still. When he turns, the earth shakes, and fire comes out of Etna."),
    ),
)

/** Orpheus, who sang below for Eurydice, and Sisyphus sat on his rock. */
val ORPHEUS = Tale(
    2,
    listOf(
        Part("Orpheus was the son of a Muse. When he sang, trees and stones came near to hear."),
        Part("He sailed with Jason on the Argo. His song was louder than the song of the Sirens."),
        Part("He married Eurydice, but at the wedding the torch only smoked. It was a bad sign."),
        Part("Soon after, a snake bit Eurydice as she walked in the grass, and she died."),
        Part("Orpheus sang his grief on earth. Then he went down to the dead to find her."),
        Part("The dead came near to hear him. Even Cerberus, the dog with three heads, stood still."),
        Part("He sang to Hades and Persephone: \"Love brought me here. Give her back to me.\""),
        Part("\"When her years are over, she is yours again. I ask for a loan, not a gift.\""),
        Part("The dead wept. The wheel of Ixion stood still, and Sisyphus sat down on his rock."),
        Part("Persephone called Eurydice. She came slowly, for her wound still hurt."),
        Part("\"Do not look back at her,\" said Hades, \"until you come out into the light.\""),
        Part("They climbed a steep, dark path, in silence, through thick mist."),
        Part("Near the top, afraid for her, and longing to see her, he looked back."),
        Part("She died a second time, but she did not blame him. He had only loved her."),
        Part("After his death, he found her below. Now they walk together, and he can look back."),
    ),
)

/** Isis, who found the pieces of Osiris one by one. */
val ISIS_AND_OSIRIS = Tale(
    3,
    listOf(
        Part("Osiris was the king of Egypt. He taught men to sow wheat, and he gave them laws."),
        Part("His brother Set wanted the throne. He measured Osiris in secret, and made a fine chest."),
        Part("At a feast, Set said: \"The chest is for the one who fits in it.\" Everyone tried."),
        Part("When Osiris lay down in it, Set shut the lid, and threw the chest into the Nile."),
        Part("The river carried it to the sea, and the sea carried it to Byblos."),
        Part("A tree grew around the chest. The king of Byblos made a pillar of the tree."),
        Part("Isis, the wife of Osiris, cut her hair, and walked the world to find him."),
        Part("In Byblos, she turned into a swallow, and flew around the pillar, crying."),
        Part("She took the chest home to Egypt, and hid it in the marshes."),
        Part("Set found it while he hunted by moonlight. He cut Osiris into fourteen pieces."),
        Part("He threw the pieces all over Egypt, so that nobody could find them."),
        Part("Isis sailed the marshes in a boat of papyrus. Even the crocodiles let her pass."),
        Part("She found the pieces one by one, and she built a tomb for each."),
        Part("Their son Horus grew up, and he fought Set, and he won."),
        Part("Osiris became the king of the dead. Before him, each heart is weighed against a feather."),
    ),
)

/** Bellerophon, the grandson of Sisyphus, who caught Pegasus. */
val BELLEROPHON = Tale(
    4,
    listOf(
        Part("Bellerophon was the grandson of Sisyphus. The gods gave him beauty and courage."),
        Part("Pegasus, the winged horse, came to drink at the spring Peirene, in Corinth."),
        Part("Bellerophon tried in vain to catch him. A seer told him to sleep on Athena's altar."),
        Part("\"Are you sleeping?\" Athena asked in his dream. \"Take this charm for the horse.\""),
        Part("He woke, and a bridle of gold lay beside him. With it, he caught Pegasus."),
        Part("Later, the wife of King Proetus loved him. He said no, and she lied about him."),
        Part("Proetus sent him to the queen's father in Lycia, with a folded tablet of deadly signs."),
        Part("The king of Lycia feasted him for nine days. On the tenth, he read the signs."),
        Part("So he sent Bellerophon to kill the Chimera, part lion, part goat, part snake."),
        Part("It breathed fire. But Bellerophon flew high on Pegasus, and shot it from the sky."),
        Part("Then he beat the Solymi, the Amazons, and the king's men who lay in wait for him."),
        Part("The king gave him his daughter as his wife, and shared his kingdom with him."),
        Part("He wanted to fly up to Zeus, to the home of the gods. But Pegasus threw him."),
        Part("The gods turned from him, and he wandered alone, far from the paths of men."),
        Part("Pegasus found a home on Olympus. Now he carries the thunder and lightning of Zeus."),
    ),
)

/** Empedocles, the wise man of Sicily, who leapt into Etna. */
val EMPEDOCLES = Tale(
    5,
    listOf(
        Part("Empedocles was born in Acragas, in Sicily. His grandfather won a horse race at Olympia."),
        Part("\"My people feast as if to die tomorrow,\" he said, \"and build as if to live forever.\""),
        Part("In his poems, he taught that all things are made of four: fire, water, earth and air."),
        Part("Love joins them into one, he said, and Strife pulls them apart, again and again."),
        Part("\"I was once a boy and a girl,\" he wrote, \"a bush, a bird and a fish.\""),
        Part("He caught the winds that hurt the crops in bags of donkey skin, set on the hills."),
        Part("A woman did not breathe for thirty days. Empedocles sent her home alive."),
        Part("At Selinus, a foul river made the people sick. He led two other rivers into it."),
        Part("The water turned sweet, and the sickness ended. The people prayed to him as to a god."),
        Part("He was asked to be king, but he refused, for he loved a simple life."),
        Part("He wore a purple robe, a golden belt, a wreath of laurel, and sandals of bronze."),
        Part("\"I walk among you as an immortal god,\" he told his city, \"no longer a mortal.\""),
        Part("After a feast, his friends slept in a field. At dawn, Empedocles was gone."),
        Part("A voice had called him in the night. \"He is a god now,\" said his friend Pausanias."),
        Part("Others say he leapt into the crater of Etna. It threw back one of his bronze sandals."),
    ),
)

/** Demeter, who looked for her daughter Persephone with torches lit at Etna. */
val DEMETER_AND_PERSEPHONE = Tale(
    6,
    listOf(
        Part("Demeter was the first to plow, and to give men grain. Her daughter was Persephone."),
        Part("In Sicily, by a lake near Henna, spring never ended. Persephone picked flowers there."),
        Part("The Earth grew a narcissus with a hundred flowers. As she picked it, the ground opened."),
        Part("Hades rose in his golden chariot, and carried her off below. She called for her mother."),
        Part("Demeter lit two torches in the fire of Etna, and searched for her night and day."),
        Part("For nine days she wandered the earth. In her grief, she did not eat or drink."),
        Part("Then she asked the Sun, who sees all. \"Zeus gave her to Hades,\" he said."),
        Part("Angry with Zeus, she left the gods, and came to Eleusis as an old woman."),
        Part("The queen of Eleusis took her in, to nurse her baby son, Demophon."),
        Part("At night she laid him in the fire, so that he would never grow old or die."),
        Part("His mother saw it, and cried out. \"I am Demeter,\" said the goddess. \"Build me a temple.\""),
        Part("She sat in her temple, longing for her daughter. All that year, she let no seed grow."),
        Part("So Zeus sent Hermes below. Hades let her go, but first he gave her a pomegranate seed."),
        Part("She ran into her mother's arms. \"Did you eat anything below?\" asked Demeter. She had."),
        Part("So for a third of each year, she is queen below. Each spring, she comes back up."),
    ),
)

/** Hermes, who stole the cows of Apollo on the day he was born. */
val HERMES = Tale(
    7,
    listOf(
        Part("Hermes, the son of Zeus and Maia, was born at dawn, in a cave on Mount Cyllene."),
        Part("At the door he found a tortoise. By noon, he had made its shell into a lyre."),
        Part("By evening he longed for meat. He ran to Pieria, and took fifty cows of Apollo."),
        Part("He made the cows walk backwards, and he wore sandals of twigs, to hide the tracks."),
        Part("An old man in a vineyard saw them. \"You saw nothing,\" said Hermes, \"and heard nothing.\""),
        Part("By the river Alpheus, he made the first fire with sticks, and roasted two of the cows."),
        Part("At dawn he went home. Like a mist, he slipped through the keyhole, into his cradle."),
        Part("Apollo looked for his cows. \"A child drove them backwards,\" said the old man."),
        Part("A bird showed Apollo the thief. In the cave, the baby lay curled up, as if asleep."),
        Part("\"I was born yesterday,\" said Hermes. \"My feet are soft. What are cows?\""),
        Part("Apollo took him to Zeus. Zeus laughed out loud to hear his son lie so well."),
        Part("Hermes played the lyre. Apollo laughed for joy: \"This song is worth fifty cows!\""),
        Part("Hermes gave Apollo the lyre, and got the cows for it. They were friends forever."),
        Part("Apollo gave him a staff of gold. Zeus made him the only messenger to Hades."),
        Part("Later, Hermes gave his skill to his son Autolycus, who stole the cattle of Sisyphus."),
    ),
)

/** Phaethon, who drove the chariot of the Sun for a day. */
val PHAETHON = Tale(
    8,
    listOf(
        Part("Phaethon was proud that the Sun was his father. Epaphus, son of Zeus, laughed at him."),
        Part("His mother, Clymene, swore by the Sun that it was true. \"Go and ask him,\" she said."),
        Part("He went east, and climbed a steep path to the palace of the Sun, bright with gold."),
        Part("\"You are my son,\" said the Sun. \"Ask for any gift. I swear it by the Styx.\""),
        Part("Phaethon asked for the chariot of the Sun, for one day. His father was sorry he swore."),
        Part("\"The road starts steep,\" said the Sun. \"My horses can hardly climb it, even at dawn.\""),
        Part("\"Too high, and the sky burns. Too low, the earth. Keep to the middle,\" he said."),
        Part("At dawn Phaethon took the reins. But the horses ran wild, and came too near the earth."),
        Part("The earth caught fire. Etna burned with double flames, and the Nile hid its head."),
        Part("The Earth begged Zeus for help. He threw his lightning, and Phaethon fell like a star."),
        Part("Far from home, the river Eridanus took him in. The nymphs of the west buried him."),
        Part("\"Here lies Phaethon,\" they wrote on his stone. \"He fell, but he dared great things.\""),
        Part("The Sun hid his face in grief. For one day there was no sun, only firelight."),
        Part("Phaethon's sisters wept until they became poplars. Their tears turned into amber."),
        Part("\"My work never ends,\" said the Sun. But the gods begged him, so he drove again."),
    ),
)

/** Arion, the singer of Corinth whom a dolphin saved. */
val ARION = Tale(
    9,
    listOf(
        Part("Arion was the finest lyre player of his day. He lived with Periander, ruler of Corinth."),
        Part("When he sang, the streams stood still, and the wolf stopped chasing the lamb."),
        Part("He sailed to Italy and Sicily, and his songs made him famous there, and rich."),
        Part("To go home, he took a ship of Corinth, for he trusted the Corinthians most."),
        Part("But out at sea, the sailors planned to take his money and throw him overboard."),
        Part("He offered them the money for his life. But they told him to jump into the sea."),
        Part("\"Let me sing first,\" he asked. The sailors let him, glad to hear the best singer alive."),
        Part("He put on his purple robe and his crown. He took his lyre, and he sang."),
        Part("When the song was over, he leapt into the sea in all his finery."),
        Part("The ship sailed on to Corinth. But a dolphin took Arion on its back."),
        Part("Seated on the dolphin, he played his lyre, and paid for the ride with a song."),
        Part("The dolphin carried him to Taenarum. From there he went to Corinth, and told his tale."),
        Part("Periander did not believe him. When the ship came, he asked the sailors about Arion."),
        Part("\"He is safe in Italy,\" they said. Then Arion stood before them, in his purple robe."),
        Part("For its kindness, Zeus set the dolphin among the stars. It shines there still."),
    ),
)

/** Daedalus, who flew from Crete on wings of wax, and Icarus, who flew too high. */
val DAEDALUS_AND_ICARUS = Tale(
    10,
    listOf(
        Part("Daedalus of Athens was the great inventor. Men said his statues could see and walk."),
        Part("In Crete, he built the Labyrinth for King Minos. Even he could hardly find the way out."),
        Part("Daedalus longed for Athens, his home. But Minos would not let him leave Crete."),
        Part("\"Minos may close the land and the sea,\" said Daedalus, \"but the sky is open.\""),
        Part("He laid feathers in a row, from small to large, and bound them with thread and wax."),
        Part("His son Icarus played. He chased the feathers, and softened the wax with his thumb."),
        Part("\"Fly between the sea and the sun,\" said Daedalus, \"not too low, and not too high.\""),
        Part("They flew. Fishermen, shepherds and farmers looked up, and thought the two were gods."),
        Part("But Icarus loved the flight, and flew higher and higher. The sun melted the wax."),
        Part("He fell into the sea, calling his father. That sea is still called the Icarian Sea."),
        Part("Daedalus buried him on an island, now called Icaria, and flew on to Sicily."),
        Part("King Cocalus took him in. Daedalus built the king a city on a rock, safe from attack."),
        Part("Minos searched for him with a spiral shell. \"A prize to the one who threads it!\""),
        Part("Cocalus gave it to Daedalus, who tied a thread to an ant. The ant walked through."),
        Part("So Minos knew Daedalus was there. But Minos died in his bath, and Daedalus was safe."),
    ),
)

/** Io, whom the gadfly drove over the world, until the Nile. */
val IO = Tale(
    11,
    listOf(
        Part("Io was the daughter of the river god Inachus. Zeus saw her, and he loved her."),
        Part("She ran away, but he hid the land under a dark cloud, and caught her."),
        Part("Hera saw the cloud, and came down. But Zeus had turned Io into a white cow."),
        Part("Hera asked for the lovely cow as a gift, and Zeus could not say no."),
        Part("Hera gave her to Argus, who had a hundred eyes. Only two slept at a time."),
        Part("When Io tried to speak, she mooed, and her own voice frightened her."),
        Part("Her father did not know her. So with her hoof, she wrote her name in the dust."),
        Part("Zeus sent Hermes. He came as a shepherd, and he played to Argus on reed pipes."),
        Part("Hermes told of Syrinx, a nymph who became reeds. Before he finished, all the eyes slept."),
        Part("Then Hermes killed Argus. Hera set his hundred eyes in the tail of her peacock."),
        Part("Hera sent a gadfly. Its sting drove Io over the world, across lands and seas."),
        Part("She crossed a strait, and men named it after her: the Bosporus, the cow's crossing."),
        Part("At last, by the Nile, a touch of Zeus calmed her. She was a woman again."),
        Part("She bore Epaphus, named for that touch. He became a king, and he founded Memphis."),
        Part("In Egypt, men called her Isis: a woman with the horns of a cow."),
    ),
)

/** Kaguya, the moon princess, whose elixir burned on Fuji. */
val KAGUYA = Tale(
    12,
    listOf(
        Part("Long ago, an old bamboo cutter saw a stalk that shone. Inside it was a tiny girl."),
        Part("He took her home to his wife. From then on, he often found gold in the bamboo."),
        Part("In three months she grew up. She was named Kaguya, and her light filled the house."),
        Part("Five lords wanted to marry her. She asked each for a wonder from far away."),
        Part("One prince brought a jewelled branch. Then the men who made it came for their pay."),
        Part("Another brought a robe of fire-rat fur, which no fire could burn. It burned in a flash."),
        Part("The Emperor came to take her away. She vanished like a shadow, and he let her be."),
        Part("For three years, they wrote letters. Then she began to weep when she looked at the moon."),
        Part("\"I came from the moon,\" she said. \"At the full moon, its people will take me back.\""),
        Part("The Emperor sent two thousand soldiers to guard her on the night of the full moon."),
        Part("In a light brighter than noon, the people of the moon came down. No soldier could fight."),
        Part("She sent the Emperor a letter, and the elixir of no death from the moon."),
        Part("In the robe of feathers, she forgot all sorrow. Then she rose into the sky."),
        Part("The Emperor had the letter and the elixir burned on the mountain nearest to heaven."),
        Part("The mountain was named Fuji, and its smoke still rises into the clouds, they say."),
    ),
)

/** Izanagi, who came back from the land of the dead, and shut its way with a rock. */
val IZANAGI_AND_IZANAMI = Tale(
    13,
    listOf(
        Part("Long ago, the gods gave Izanagi and Izanami a jewelled spear, to make the land."),
        Part("From the Floating Bridge of Heaven, they stirred the sea. The drops became an island."),
        Part("On the island they walked around a pillar, and married. Islands and gods were born."),
        Part("Then came the god of fire. His birth burned Izanami, and she fell sick and died."),
        Part("Izanagi wept for her. Then he followed her down to Yomi, the land of the dead."),
        Part("\"Come back,\" he said. \"The lands that we made are not finished yet.\""),
        Part("\"Too late,\" she said. \"I have eaten the food of Yomi. But I will ask its gods.\""),
        Part("\"Do not look at me,\" she said. She went inside, and she was gone too long."),
        Part("He lit a tooth of his comb, and looked. He saw what death had done to her."),
        Part("He ran. \"You have shamed me!\" she cried, and she sent a hag of Yomi after him."),
        Part("His headdress became grapes, and his comb bamboo shoots. The hag stopped to eat."),
        Part("Then warriors of Yomi chased him. At the pass, he threw three peaches, and they fled."),
        Part("Last came Izanami. He shut the pass with a rock that only a thousand men could move."),
        Part("She swore to kill a thousand people a day. He swore fifteen hundred would be born."),
        Part("He washed in a river, and the Sun goddess, the Moon god and Susanoo were born."),
    ),
)

/** The pool of tales, in the order of their ids. Each climb tells one of them. */
val TALES = listOf(
    SISYPHUS, TYPHON, ORPHEUS, ISIS_AND_OSIRIS, BELLEROPHON, EMPEDOCLES, DEMETER_AND_PERSEPHONE, HERMES, PHAETHON, ARION,
    DAEDALUS_AND_ICARUS, IO, KAGUYA, IZANAGI_AND_IZANAMI,
)
