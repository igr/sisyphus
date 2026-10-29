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
}

/** The three gods of [place]: Hermes, and two of its own. */
fun godsOf(place: Place) = God.entries.filter { it.place == null || it.place == place }

/** A question, with its [right] and [wrong] answer: [yes] is the reply to a right answer, [no] to a wrong one. */
class Question(val text: String, val right: String, val wrong: String, val yes: String, val no: String) {
    /** What the god says after the answer: first whether it was right. */
    fun reply(right: Boolean) = if (right) "Correct. $yes" else "Wrong. $no"
}

/** The voices of Sisyphus's world and the proverbs: the gods ask only about the words of people. */
private val NOT_PEOPLE = setOf(
    "Sisyphus", "the Rock", "the Hill", "the Sun", "the Moon", "the Column", "Zeus", "Hades", "Hermes", "a Goat", "a Bird",
    "Gravity", "the Olive Tree", "the Wind", "the Cypress", "the Rain", "the Sea", "Proverb", "Delphic maxim",
    "Japanese proverb", "Latin proverb", "French proverb", "Chinese proverb", "Persian proverb", "Zen saying",
)

private fun fits(text: String, width: Int, lines: Int) =
    PixelFont.wrap(text, width).let { wrapped -> wrapped.size <= lines && wrapped.all { PixelFont.width(it) <= width } }

private fun whoSaidText(saying: Quote) = "Who said this? \"${saying.text}\""

/** The people a god can name: each name fits a bottom corner of the scene. */
val PEOPLE: List<String> = QUOTES.map { it.author }.distinct().filter { it !in NOT_PEOPLE && fits(it, NARROWEST_ANSWER, 2) }

/** The wise words the gods ask about: words of [PEOPLE] with no quotes of their own, whose question fits the panel. */
val SAYINGS: List<Quote> = QUOTES.filter { it.author in PEOPLE && '"' !in it.text && fits(whoSaidText(it), NARROWEST_TEXT, 3) }

/** A god asks who said [saying], and names [other] too. */
fun whoSaid(saying: Quote, other: String) = Question(
    whoSaidText(saying), saying.author, other,
    "These are the words of ${saying.author}.", "These are the words of ${saying.author}.",
)

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
     * Each vision asks who said the next saying, in an order made from the seed, so no saying comes again before all have come,
     * also across a move. One of the three gods of [place] asks it, chosen at random, but never a god whose name is an answer.
     */
    fun visionAt(k: Int, place: Place): Vision {
        val saying = nth(SAYINGS, k, SAYINGS_ORDER)
        val other = PEOPLE.filter { it != saying.author }.random(Random(seed xor k))
        val god = godsOf(place).filter { it.title != saying.author && it.title != other }.random(Random(seed + k * 6007))
        return Vision(whoSaid(saying, other), god, Random(seed + k * 31).nextBoolean())
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
        /** The sayings keep their order from the time when only Hermes asked about them. */
        private const val SAYINGS_ORDER = 104729
    }
}
