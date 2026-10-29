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

/** The pool of tales, in the order of their ids. Each climb tells one of them. */
val TALES = listOf(SISYPHUS, TYPHON, ORPHEUS, ISIS_AND_OSIRIS)
