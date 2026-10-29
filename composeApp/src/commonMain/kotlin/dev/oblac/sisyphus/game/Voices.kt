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

/** When the people of the wise words were born, a year before Christ as a negative number. People of unsure years are left out. */
val BIRTH_YEARS = mapOf(
    "Thales" to -624, "Pittacus" to -640, "Solon" to -630, "Aesop" to -620, "Xenophanes" to -570, "Simonides" to -556,
    "Confucius" to -551, "Heraclitus" to -535, "Aeschylus" to -525, "Pindar" to -518, "Sophocles" to -497, "Empedocles" to -494,
    "Euripides" to -480, "Socrates" to -470, "Democritus" to -460, "Antisthenes" to -446, "Aristippus" to -435, "Plato" to -428,
    "Diogenes" to -412, "Aristotle" to -384, "Chuang Tzu" to -369, "Menander" to -342, "Epicurus" to -341, "Zeno of Citium" to -334,
    "Cleanthes" to -330, "Bion" to -325, "Aristo" to -320, "Theocritus" to -300, "Archimedes" to -287, "Chrysippus" to -279,
    "Cicero" to -106, "Sallust" to -86, "Publilius Syrus" to -85, "Virgil" to -70, "Horace" to -65, "Augustus" to -63,
    "Livy" to -59, "Ovid" to -43, "Phaedrus" to -15, "Seneca" to -4, "Quintilian" to 35, "Plutarch" to 46, "Epictetus" to 50,
    "Juvenal" to 55, "Pliny the Younger" to 61, "Demonax" to 70, "Marcus Aurelius" to 121, "Montaigne" to 1533,
    "La Rochefoucauld" to 1613, "Pascal" to 1623, "Benjamin Franklin" to 1706, "Samuel Johnson" to 1709,
    "Oliver Goldsmith" to 1728, "Goethe" to 1749, "William Blake" to 1757, "Schopenhauer" to 1788, "Emerson" to 1803,
    "Thoreau" to 1817, "Nietzsche" to 1844, "Robert Frost" to 1874, "Will Durant" to 1885, "George Mallory" to 1886,
    "Albert Camus" to 1913,
)

/** A year of birth, with "about" before the years that nobody knows for sure. */
private fun born(year: Int) = when {
    year < 0 -> "about ${-year} BC"
    year < 1000 -> "about $year AD"
    else -> "in $year"
}

/** A god asks who lived first, [first] or [second]. */
private fun livedFirst(first: String, second: String): Question {
    val years = "$first was born ${born(BIRTH_YEARS.getValue(first))}, $second ${born(BIRTH_YEARS.getValue(second))}."
    return Question("Who lived first?", first, second, years, years)
}

/**
 * Every pair of [PEOPLE] born a century apart or more, so that the dates are surely in that order, whose reply fits the panel.
 * Both lived in antiquity, or both in modern times, so that the era does not give the answer away.
 */
val LIVED_FIRST: List<Question> = PEOPLE.filter { it in BIRTH_YEARS }.sortedBy { BIRTH_YEARS.getValue(it) }.let { people ->
    people.flatMapIndexed { i, first ->
        val year = BIRTH_YEARS.getValue(first)
        people.drop(i + 1).filter { BIRTH_YEARS.getValue(it).let { other -> other - year >= 100 && (other < 1000) == (year < 1000) } }
            .map { livedFirst(first, it) }
    }
}.filter { q -> listOf(true, false).all { fits(q.reply(it), NARROWEST_TEXT, 3) } }

/** A god of Greece, [greek], whom the Romans call [roman]. */
class Roman(val greek: String, val roman: String, val goddess: Boolean)

/** The gods whose Roman name does not sound like the Greek one, so that only knowing helps. */
val ROMANS = listOf(
    Roman("Zeus", "Jupiter", false), Roman("Hera", "Juno", true), Roman("Poseidon", "Neptune", false),
    Roman("Hades", "Pluto", false), Roman("Athena", "Minerva", true), Roman("Ares", "Mars", false),
    Roman("Aphrodite", "Venus", true), Roman("Artemis", "Diana", true), Roman("Hermes", "Mercury", false),
    Roman("Hephaestus", "Vulcan", false), Roman("Demeter", "Ceres", true), Roman("Dionysus", "Bacchus", false),
    Roman("Hestia", "Vesta", true), Roman("Eros", "Cupid", false), Roman("Cronus", "Saturn", false),
    Roman("Gaia", "Terra", true), Roman("Helios", "Sol", false), Roman("Selene", "Luna", true),
    Roman("Eos", "Aurora", true), Roman("Nike", "Victoria", true), Roman("Tyche", "Fortuna", true),
    Roman("Hypnos", "Somnus", false), Roman("Pan", "Faunus", false),
).filter { fits(it.roman, NARROWEST_ANSWER, 2) && fits(romanName(it, it.roman).reply(right = true), NARROWEST_TEXT, 3) }

/** A god asks what the Romans call [god], and names [other] too. */
fun romanName(god: Roman, other: String): Question {
    val answer = "${god.greek} is ${god.roman} in Rome."
    return Question("What do the Romans call ${god.greek}?", god.roman, other, answer, answer)
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
     * Each round of three visions asks, in an order made from the seed, who said the next saying, who of the next two people
     * lived first, and what the Romans call the next god. So no question comes again before all of its kind have come, also
     * across a move. One of the three gods of [place] asks, chosen at random, but never a god whose name is an answer.
     */
    fun visionAt(k: Int, place: Place): Vision {
        val round = k / 3
        val question = when (listOf(0, 1, 2).shuffled(Random(seed + round * 7919))[k % 3]) {
            0 -> nth(SAYINGS, round, SAYINGS_ORDER).let { saying ->
                whoSaid(saying, PEOPLE.filter { it != saying.author }.random(Random(seed xor k)))
            }
            1 -> nth(LIVED_FIRST, round, LIVED_FIRST_ORDER)
            else -> nth(ROMANS, round, ROMANS_ORDER).let { god ->
                romanName(god, ROMANS.filter { it != god && it.goddess == god.goddess }.random(Random(seed xor k)).roman)
            }
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
        /** The sayings keep their order from the time when only Hermes asked about them. */
        private const val SAYINGS_ORDER = 104729
        private const val LIVED_FIRST_ORDER = 1299709
        private const val ROMANS_ORDER = 15485863
    }
}
