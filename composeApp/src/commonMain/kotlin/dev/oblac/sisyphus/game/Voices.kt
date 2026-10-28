package dev.oblac.sisyphus.game

import dev.oblac.sisyphus.gfx.PixelFont
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

/** The narrowest text of the panel of the wise words, on the smallest phone (135 virtual pixels wide). */
const val NARROWEST_TEXT = 119

/** The narrowest answer of a vision, in a bottom corner of the scene on the smallest phone. */
const val NARROWEST_ANSWER = 46

/** The gods who come as visions. Each says its [farewell] when nobody answers. */
enum class God(val title: String, val farewell: String) {
    SPHINX("the Sphinx", "You are silent. I will ask again another day."),
    HERMES("Hermes", "No answer? Then I fly on."),
    HECATE("Hecate", "The roads will wait for you."),
}

/** A question of a god, with its [right] and [wrong] answer: [yes] is the reply to a right answer, [no] to a wrong one. */
class Question(val god: God, val text: String, val right: String, val wrong: String, val yes: String, val no: String)

/** The riddles of the Sphinx. */
val RIDDLES = listOf(
    Question(
        God.SPHINX, "What walks on four feet at dawn, on two at noon, and on three at dusk?", "A man", "A horse",
        "Yes: a man. A child crawls, a man walks, and the old lean on a staff.",
        "No: a man. A child crawls, a man walks, and the old lean on a staff.",
    ),
    Question(
        God.SPHINX, "I have a mouth but never eat, and I run to the sea. What am I?", "A river", "A road",
        "Yes, a river. Even water does not rest.", "No, a river. It runs, and it never rests.",
    ),
    Question(
        God.SPHINX, "The more you take, the more you leave behind. What are they?", "Steps", "Obols",
        "Yes: steps. You leave many on this hill.", "No: steps. You leave many on this hill.",
    ),
    Question(
        God.SPHINX, "I climb without legs or hands, and every evening I go back down. What am I?", "The sun", "The rock",
        "Yes, the sun. It climbs each day, and nobody pushes it.", "No, the sun. Your rock needs your hands.",
    ),
    Question(
        God.SPHINX, "What has a face but no eyes, and tells the hour only while the sun shines?", "A sundial", "A mirror",
        "Yes. On a cloudy day, it keeps its secrets.", "A sundial. On a cloudy day, it keeps its secrets.",
    ),
    Question(
        God.SPHINX, "The more of it there is, the less you see. What is it?", "Darkness", "Light",
        "Yes. And yet the stars need it.", "Darkness. And yet the stars need it.",
    ),
    Question(
        God.SPHINX, "What can you keep after you have given it to someone?", "Your word", "Your obols",
        "Yes. Keep it, and it is worth more than gold.", "Your word. Obols, once given, are gone.",
    ),
    Question(
        God.SPHINX, "What goes up the hill and down again, yet never moves?", "The path", "Your shadow",
        "Yes, the path. It waits for you every morning.", "The path. Your shadow follows you everywhere.",
    ),
    Question(
        God.SPHINX, "What has many teeth, but never bites?", "A comb", "A wolf",
        "Yes. Even Medusa had no use for one.", "A comb. A wolf bites, as you well know.",
    ),
    Question(
        God.SPHINX, "What has a neck but no head, and two ears but no face?", "An amphora", "A goose",
        "Yes. Dionysus keeps a few full ones.", "An amphora. A goose has a head, and no wine.",
    ),
    Question(
        God.SPHINX, "Feed me and I live. Give me a drink and I die. What am I?", "Fire", "A horse",
        "Yes. Prometheus paid dearly to bring it.", "Fire. A horse likes a drink, as you do.",
    ),
    Question(
        God.SPHINX, "What has one eye, but cannot see?", "A needle", "A Cyclops",
        "Yes. A Cyclops has one eye too, and sees well enough.", "A needle. A Cyclops sees well enough, for a while.",
    ),
    Question(
        God.SPHINX, "What belongs to you, but others use it more than you do?", "Your name", "Your rock",
        "Yes. It travels where you cannot.", "Your name. Nobody else wants your rock.",
    ),
    Question(
        God.SPHINX, "What breaks as soon as you say its name?", "Silence", "A jar",
        "Yes. And now it is broken.", "Silence. You just broke it.",
    ),
    Question(
        God.SPHINX, "What runs around the whole field without moving?", "A fence", "A goat",
        "Yes. It runs all day and never tires.", "A fence. The goat runs, then it lies down.",
    ),
    Question(
        God.SPHINX, "What flies without wings, and weeps without eyes?", "A cloud", "An owl",
        "Yes. It weeps, and the grass is glad.", "A cloud. The owl has wings, and dry eyes.",
    ),
    Question(
        God.SPHINX, "What is lighter than a feather, yet even Heracles cannot hold it for long?", "Breath", "A feather",
        "Yes. Even the strongest must breathe out.", "Breath. A feather he could hold all day.",
    ),
    Question(
        God.SPHINX, "The poor have it, the rich need it, and if you eat it, you starve. What is it?", "Nothing", "Bread",
        "Yes. A riddle about nothing, and you solved it.", "Nothing. Bread, at least, keeps you going.",
    ),
    Question(
        God.SPHINX, "What is full of holes, yet still holds water?", "A sponge", "A sieve",
        "Yes. It drinks the sea and gives it back.", "A sponge. A sieve lets it all go.",
    ),
    Question(
        God.SPHINX, "What has cities but no houses, and hills that nobody climbs?", "A map", "A garden",
        "Yes. On a map, every hill is easy.", "A map. A garden has no cities, only bees.",
    ),
)

/** Hecate at a crossroads: which road a wise man takes. */
val ROADS = listOf(
    Question(
        God.HECATE, "Two roads: one short and easy, one long and steep. Which does a wise man take?", "The steep one", "The easy one",
        "Yes. The road to virtue is steep at first, then it grows easy.", "The easy road is short, but it leads nowhere worth going.",
    ),
    Question(
        God.HECATE, "When does a wise man begin?", "Today", "Tomorrow",
        "Yes. Tomorrow is only a word.", "Today. Tomorrow never comes on its own.",
    ),
    Question(
        God.HECATE, "What does a wise man want?", "Little", "Much",
        "Yes. He who wants little has much.", "Little. He who wants much is always poor.",
    ),
    Question(
        God.HECATE, "A wise man stumbles on the road. What does he do?", "Gets up", "Turns back",
        "Yes. Every road has stones.", "He gets up. Every road has stones.",
    ),
    Question(
        God.HECATE, "Two cups at noon: one of water, one of wine. Which does a wise man drink?", "The water", "The wine",
        "Yes. The wine can wait for the evening.", "The water. The wine can wait for the evening.",
    ),
    Question(
        God.HECATE, "The road ahead is long. What does a wise man count?", "His steps", "The miles",
        "Yes. The miles take care of themselves.", "His steps. The miles take care of themselves.",
    ),
    Question(
        God.HECATE, "A storm comes over the hill. What does a wise man do?", "Shelters", "Pushes on",
        "Yes. The storm passes, and the hill will wait.", "He shelters. The storm passes, and the hill will wait.",
    ),
    Question(
        God.HECATE, "A stranger comes to the crossroads. What does a wise man offer?", "Bread", "A cold look",
        "Yes. Bread first; the road can wait.", "Bread. A cold look feeds nobody.",
    ),
    Question(
        God.HECATE, "The road is dark. What does a wise man carry?", "A torch", "A sword",
        "Yes. I have two, if you need one.", "A torch. I have two, if you need one.",
    ),
    Question(
        God.HECATE, "At the top of the hill, what does a wise man do?", "Looks around", "Boasts",
        "Yes. The view does not last, so he looks well.", "He looks around. Boasting spoils the view.",
    ),
    Question(
        God.HECATE, "What does a wise man say most often?", "Thank you", "I know",
        "Yes. Two small words that open many doors.", "Thank you. Who says 'I know' stops learning.",
    ),
    Question(
        God.HECATE, "The rock rolls back down. What does a wise man do?", "Walks down", "Weeps",
        "Yes. The walk down is a rest of its own.", "He walks down. The rock waits for him at the bottom.",
    ),
    Question(
        God.HECATE, "Whom does a wise man try to beat?", "Himself", "Others",
        "Yes. The only rival who never leaves.", "Himself. Others have their own hills.",
    ),
    Question(
        God.HECATE, "What does a wise man do with the past?", "Lets it go", "Carries it",
        "Yes. One rock is enough to carry.", "He lets it go. One rock is enough to carry.",
    ),
    Question(
        God.HECATE, "When does a wise man rest?", "When tired", "Never",
        "Yes. Even the sun sets.", "When he is tired. Even the sun sets.",
    ),
    Question(
        God.HECATE, "A friend is slower on the hill. What does a wise man do?", "Walks with him", "Walks ahead",
        "Yes. A hill is shorter for two.", "He walks with him. A hill is shorter for two.",
    ),
    Question(
        God.HECATE, "Where does a wise man find quiet?", "In himself", "In the hills",
        "Yes. He carries it on every road.", "In himself. He carries it on every road.",
    ),
    Question(
        God.HECATE, "What makes a long road short?", "Good company", "Grumbling",
        "Yes. Talk well, and the miles fly.", "Good company. Grumbling makes every mile longer.",
    ),
    Question(
        God.HECATE, "A wise man finds a coin on the road. What does he ask?", "Whose is it?", "Who saw me?",
        "Yes. A light heart is worth more than a coin.", "Whose is it? A light heart is worth more than a coin.",
    ),
    Question(
        God.HECATE, "Which is richer: a full purse or a full heart?", "A full heart", "A full purse",
        "Yes. A heart cannot be stolen.", "A full heart. A purse can be stolen.",
    ),
)

/** The voices of Sisyphus's world and the proverbs: Hermes asks only about the words of people. */
private val NOT_PEOPLE = setOf(
    "Sisyphus", "the Rock", "the Hill", "the Sun", "the Moon", "the Column", "Zeus", "Hades", "Hermes", "a Goat", "a Bird",
    "Gravity", "the Olive Tree", "the Wind", "the Cypress", "the Rain", "the Sea", "Proverb", "Delphic maxim",
    "Japanese proverb", "Latin proverb", "French proverb", "Chinese proverb", "Persian proverb", "Zen saying",
)

private fun fits(text: String, width: Int, lines: Int) =
    PixelFont.wrap(text, width).let { wrapped -> wrapped.size <= lines && wrapped.all { PixelFont.width(it) <= width } }

private fun whoSaid(saying: Quote) = "Who said this? \"${saying.text}\""

/** The people Hermes can name: each name fits a bottom corner of the scene. */
val PEOPLE: List<String> = QUOTES.map { it.author }.distinct().filter { it !in NOT_PEOPLE && fits(it, NARROWEST_ANSWER, 2) }

/** The wise words Hermes asks about: words of [PEOPLE] with no quotes of their own, whose question fits the panel. */
val SAYINGS: List<Quote> = QUOTES.filter { it.author in PEOPLE && '"' !in it.text && fits(whoSaid(it), NARROWEST_TEXT, 3) }

/** Hermes asks who said [saying], and names [other] too. */
fun hermes(saying: Quote, other: String) = Question(
    God.HERMES, whoSaid(saying), saying.author, other,
    "Yes. These are the words of ${saying.author}.", "No. These are the words of ${saying.author}.",
)

/** A god on the screen: it comes in, asks, and leaves. The right answer stands on the left when [rightOnLeft]. */
class Vision(val question: Question, val rightOnLeft: Boolean) {
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
    var count = count ?: random.nextDouble(FIRST.start, FIRST.endInclusive)
        private set
    var rightAnswers = rightAnswers
        private set
    var vision: Vision? = null
        private set

    /** The gods come in turn, each round in a new order. Each god asks all its questions before one comes again. */
    fun visionAt(k: Int): Vision {
        val round = k / God.entries.size
        val god = God.entries.shuffled(Random(seed + round * 7919))[k % God.entries.size]
        val question = when (god) {
            God.SPHINX -> pick(RIDDLES, god, round)
            God.HECATE -> pick(ROADS, god, round)
            God.HERMES -> pick(SAYINGS, god, round).let { saying ->
                hermes(saying, PEOPLE.filter { it != saying.author }.random(Random(seed xor k)))
            }
        }
        return Vision(question, Random(seed + k * 31).nextBoolean())
    }

    private fun <T> pick(list: List<T>, god: God, round: Int): T {
        val order = list.indices.shuffled(Random(seed + god.ordinal * 104729 + round / list.size * 7919))
        return list[order[round % list.size]]
    }

    /** The player turned the knob [turns] times. When the count runs out, the next vision comes, and it is returned. */
    fun pushed(turns: Double): Vision? {
        if (vision != null) return null
        count -= turns
        return if (count > 0) null else summon()
    }

    /** The next vision comes at once, and the count starts again. */
    fun summon(): Vision {
        val v = visionAt(drawn++)
        vision = v
        count = random.nextDouble(NEXT.start, NEXT.endInclusive)
        return v
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
        val FIRST = 300.0..600.0
        val NEXT = 600.0..1800.0
        const val ASK_TIME = 20.0
        /** The turn, after a new touch, that picks an answer: an eighth of a turn. */
        const val ANSWER_TURN = 1.0 / 8
    }
}
