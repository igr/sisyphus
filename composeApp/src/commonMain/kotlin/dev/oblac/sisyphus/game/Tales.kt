package dev.oblac.sisyphus.game

/** A part of a tale, carved on a stone at [place]: its texts show one after the other, with the place under each. */
class Part(val place: String, vararg texts: String) {
    val quotes: List<Quote> = texts.map { Quote(it, place) }
}

/** A stone of the tale by the path, [at] pixels walked, carved with the [part] of the tale that it tells. */
class Stele(val at: Double, val part: Int)

/** The tale of Sisyphus, told by the stones of Corinth: one part for each item bought. See `docs/stations.md`. */
val CORINTH_TALE = listOf(
    Part("the old walls", "Long ago, this city was Ephyra. Sisyphus built it, and he became its king."),
    Part("the two harbors", "His ships sailed on two seas, and his city grew rich. Later, men called it Corinth."),
    Part("the palace", "Homer calls him the craftiest of men. Even the gods could not trust him."),
    Part("the cattle pen", "The thief Autolycus stole his cattle, and changed their colors."),
    Part("the hoofprints", "But under every hoof, Sisyphus had cut the words: stolen by Autolycus."),
    Part("the pine", "A dolphin carried a drowned boy to shore. Sisyphus buried him by a pine."),
    Part("the stadium", "For the boy, he began the Isthmian Games. Winners wore a crown of pine."),
    Part("the cliff", "One day he saw Zeus carry off Aegina, the daughter of the river god Asopus."),
    Part("Peirene's spring", "\"Give my city a spring,\" he told Asopus, \"and I will tell you where she is.\""),
    Part("the dark road", "Zeus was angry. He sent Death with chains, to take the king below."),
    Part("the chains", "\"Show me how the chains work,\" said Sisyphus. Then he chained Death himself."),
    Part("the battlefield", "With Death in chains, nobody died, not even in battle. So Ares set Death free."),
    Part("the empty grave", "Before he went below, he told Merope, his wife: \"Do not bury me.\""),
    Part(
        "the gate below",
        "Below, he told Persephone: \"My wife did not bury me. Let me go and punish her.\"",
        "She let him go. He stayed in the sun, until Hermes took him down by force.",
    ),
    Part("this hill", "For his tricks, the gods gave him a rock. He must push it to the top, forever."),
)

/** The tale that the stones of [place] tell; null for a world with no tale yet. */
fun taleOf(place: Place): List<Part>? = if (place == Place.CORINTH) CORINTH_TALE else null
