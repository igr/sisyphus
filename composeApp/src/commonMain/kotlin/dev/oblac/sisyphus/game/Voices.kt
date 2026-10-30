package dev.oblac.sisyphus.game

import dev.oblac.sisyphus.gfx.PixelFont
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

/** The narrowest text of the panel of the wise words, on the smallest phone (135 virtual pixels wide). */
const val NARROWEST_TEXT = 119

/** The narrowest answer of a vision, in a bottom corner of the scene on the smallest phone. */
const val NARROWEST_ANSWER = 46

/** The gods who come as visions: Hermes in every world, the others only in their [place]. Each says its [farewell] when nobody answers. */
enum class God(val title: String, val farewell: String, val place: Place?) {
    SPHINX("the Sphinx", "You are silent. I will ask again another day.", Place.CORINTH),
    HERMES("Hermes", "No answer? Then I fly on.", null),
    HECATE("Hecate", "The roads will wait for you.", Place.CORINTH),
    HEPHAESTUS("Hephaestus", "The forge is cooling. I go back down to it.", Place.ETNA),
    EMPEDOCLES("Empedocles", "Silence is an answer too. Farewell.", Place.ETNA),
    CHARON("Charon", "No answer. The river waits for everyone.", Place.UNDERWORLD),
    PERSEPHONE("Persephone", "Spring is calling me. I will ask again.", Place.UNDERWORLD),
    THOTH("Thoth", "No answer? I will write that down.", Place.CAIRO),
    ANUBIS("Anubis", "The scales can wait. So can I.", Place.CAIRO),
    SAKUYA("Sakuya-hime", "No answer? The blossoms will not wait.", Place.FUJI),
    IWANAGA("Iwanaga-hime", "No answer. A rock can wait forever.", Place.FUJI),
}

/** The three gods of [place]: Hermes, and two of its own. */
fun godsOf(place: Place) = God.entries.filter { it.place == null || it.place == place }

/** What stands under the wise words when a god asks who said them, in place of the god's name. */
const val WHO_SAID = "Who said this?"

/**
 * A question, with its [right] and [wrong] answer: [yes] is the reply to a right answer, [no] to a wrong one.
 * A [prompt] stands under its text in the panel, in place of the god's name.
 */
class Question(val text: String, val right: String, val wrong: String, val yes: String, val no: String, val prompt: String? = null) {
    /** What the god says after the answer: first whether it was right. */
    fun reply(right: Boolean) = if (right) "Correct. $yes" else "Wrong. $no"
}

/** The voices of Sisyphus's world, who speak in the wise words too. */
val VOICES = setOf(
    "Sisyphus", "the Rock", "the Hill", "the Sun", "the Moon", "the Column", "Zeus", "Hades", "Hermes", "a Goat", "a Bird",
    "Gravity", "the Olive Tree", "the Wind", "the Cypress", "the Rain", "the Sea",
)

/** The wise words of no one person. */
val PROVERBS = setOf(
    "Proverb", "Delphic maxim", "Japanese proverb", "Latin proverb", "French proverb", "Chinese proverb", "Persian proverb", "Zen saying",
)

/** Who said the wise words: people, the voices of Sisyphus's world, and the proverbs. */
val AUTHORS: List<String> = QUOTES.map { it.author }.distinct()

val PEOPLE: List<String> = AUTHORS.filter { it !in VOICES && it !in PROVERBS }

/** The authors of the kind of [author], [author] too: another person for a person, another voice for a voice, another proverb for a proverb. */
fun kin(author: String): List<String> = when (author) {
    in VOICES -> AUTHORS.filter { it in VOICES }
    in PROVERBS -> AUTHORS.filter { it in PROVERBS }
    else -> PEOPLE
}

/** The wise words the gods ask about: all of them. */
val SAYINGS: List<Quote> = QUOTES

/** A god asks who said [saying], and names [other] too. The words show as the wise words do, with "Who said this?" under them. */
fun whoSaid(saying: Quote, other: String): Question {
    val said = when (saying.author) {
        "Proverb" -> "This is a proverb."
        in PROVERBS -> "This is a ${saying.author}."
        else -> "These are the words of ${saying.author}."
    }
    return Question(saying.text, saying.author, other, said, said, WHO_SAID)
}

/** Where a name too wide for the corner of an answer breaks, with a hyphen: after this many letters. */
private val BREAKS = mapOf("Rochefoucauld" to 5, "Schopenhauer" to 7, "Empedocles" to 4, "Xenophanes" to 4, "Antisthenes" to 5)

/** The lines of an [answer] in a corner [width] pixels wide: a word too wide for it breaks where [BREAKS] says, with a hyphen. */
fun answerLines(answer: String, width: Int): List<String> {
    val words = answer.split(' ').flatMap { word ->
        val at = BREAKS[word]
        if (at != null && PixelFont.width(word) > width) listOf(word.take(at) + "-", word.drop(at)) else listOf(word)
    }
    return PixelFont.wrap(words.joinToString(" "), width)
}

/** A [god] on the screen: it comes in, asks its [question], and leaves. The right answer stands on the left when [rightOnLeft]. */
class Vision(val question: Question, val god: God, val rightOnLeft: Boolean) {
    enum class Phase { COMING, ASKING, LEAVING }

    var phase = Phase.COMING
        private set
    /** How much of the vision shows: 0 is nothing, 1 is all of it. */
    var light = 0.0
        private set
    /** Seconds that the vision has asked. */
    var asked = 0.0
        private set

    val left get() = if (rightOnLeft) question.right else question.wrong
    /** What stands under the question in the panel, as the author under the wise words: its prompt, or the god's name. */
    val byline get() = question.prompt ?: god.title
    val right get() = if (rightOnLeft) question.wrong else question.right
    val over get() = phase == Phase.LEAVING && light <= 0.0

    fun ask() {
        phase = Phase.ASKING
    }

    fun leave() {
        phase = Phase.LEAVING
    }

    fun update(dt: Double) {
        when (phase) {
            Phase.COMING -> light = min(1.0, light + dt / COME_TIME)
            Phase.ASKING -> asked += dt
            Phase.LEAVING -> light = max(0.0, light - dt / LEAVE_TIME)
        }
    }

    private companion object {
        const val COME_TIME = 1.0
        const val LEAVE_TIME = 1.5
    }
}

/**
 * The visions: which god comes, with which question, and when. Only the player's own turns bring the next one.
 * The order is a shuffle made from [seed], so [drawn] alone tells which vision comes next.
 */
class Voices(private val seed: Int, drawn: Int, count: Double?, rightAnswers: Int, private val random: Random) {
    /** How many visions have come so far. */
    var drawn = drawn
        private set
    /** The player's turns until the next vision. */
    var count = count?.coerceAtMost(Config.NEXT_VISION.endInclusive) ?: random.nextDouble(Config.FIRST_VISION.start, Config.FIRST_VISION.endInclusive)
        private set
    var rightAnswers = rightAnswers
        private set
    var vision: Vision? = null
        private set

    /**
     * Each round of two visions asks, in an order made from the seed, who said the next wise words, and the next question of
     * [TRIVIA]. So no question comes again before all of its kind have come, also across a move. One of the three gods of
     * [place] asks, chosen at random, but never a god whose name is an answer.
     */
    fun visionAt(k: Int, place: Place): Vision {
        val round = k / 2
        val question = if (listOf(0, 1).shuffled(Random(seed + round * 7919))[k % 2] == 0) {
            nth(SAYINGS, round, SAYINGS_ORDER).let { saying ->
                whoSaid(saying, kin(saying.author).filter { it != saying.author }.random(Random(seed xor k)))
            }
        } else {
            nth(TRIVIA, round, TRIVIA_ORDER)
        }
        val god = godsOf(place).filter { it.title != question.right && it.title != question.wrong }.random(Random(seed + k * 6007))
        return Vision(question, god, Random(seed + k * 31).nextBoolean())
    }

    /** The [n]th of [list], which comes in a new shuffle each time through it, made from the seed and [salt]. */
    private fun <T> nth(list: List<T>, n: Int, salt: Int): T {
        val order = list.indices.shuffled(Random(seed + salt + n / list.size * 7919))
        return list[order[n % list.size]]
    }

    /** The player turned the knob [turns] times. When the count runs out, the next vision comes, and it is returned. */
    fun pushed(turns: Double, place: Place): Vision? {
        if (vision != null) return null
        count -= turns
        return if (count > 0) null else summon(place)
    }

    /** The next vision comes at once, and the count starts again. */
    fun summon(place: Place): Vision {
        val v = visionAt(drawn++, place)
        vision = v
        count = random.nextDouble(Config.NEXT_VISION.start, Config.NEXT_VISION.endInclusive)
        return v
    }

    /** A new climb begins: its first vision comes on day 2. */
    fun newClimb() {
        count = random.nextDouble(Config.FIRST_VISION.start, Config.FIRST_VISION.endInclusive)
    }

    /** For dev builds: the next vision comes after only [turns] of the player. */
    fun soon(turns: Double) {
        count = turns
    }

    fun answer(right: Boolean) {
        if (right) rightAnswers++
        vision?.leave()
    }

    fun end() {
        vision = null
    }

    companion object {
        const val ASK_TIME = 20.0
        /** The turn, after a new touch, that picks an answer: an eighth of a turn. */
        const val ANSWER_TURN = 1.0 / 8
        private const val SAYINGS_ORDER = 104729
        private const val TRIVIA_ORDER = 1299709
    }
}
