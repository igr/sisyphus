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

/** The three gods of [place]: Hermes, and the two who ask its riddles. */
fun godsOf(place: Place) = God.entries.filter { it.place == null || it.place == place }

/** A question, with its [right] and [wrong] answer: [yes] is the reply to a right answer, [no] to a wrong one. */
class Question(val text: String, val right: String, val wrong: String, val yes: String, val no: String) {
    /** What the god says after the answer: first whether it was right. */
    fun reply(right: Boolean) = if (right) "Correct. $yes" else "Wrong. $no"
}

/** The riddles: one pool for all worlds, from which the two gods of each world ask in turn. */
val RIDDLES = listOf(
    Question(
        "What walks on four feet at dawn, on two at noon, and on three at dusk?", "A man", "A horse",
        "A child crawls, a man walks, and the old lean on a staff.",
        "A man. A child crawls, a man walks, and the old lean on a staff.",
    ),
    Question(
        "I have a mouth but never eat, and I run to the sea. What am I?", "A river", "A road",
        "Even water does not rest.", "A river. It runs, and it never rests.",
    ),
    Question(
        "The more you take, the more you leave behind. What are they?", "Steps", "Obols",
        "You leave many on this hill.", "Steps. You leave many on this hill.",
    ),
    Question(
        "I climb without legs or hands, and every evening I go back down. What am I?", "The sun", "The rock",
        "It climbs each day, and nobody pushes it.", "The sun. Your rock needs your hands.",
    ),
    Question(
        "What has a face but no eyes, and tells the hour only while the sun shines?", "A sundial", "A mirror",
        "On a cloudy day, it keeps its secrets.", "A sundial. On a cloudy day, it keeps its secrets.",
    ),
    Question(
        "The more of it there is, the less you see. What is it?", "Darkness", "Light",
        "And yet the stars need it.", "Darkness. And yet the stars need it.",
    ),
    Question(
        "What can you keep after you have given it to someone?", "Your word", "Your obols",
        "Keep it, and it is worth more than gold.", "Your word. Obols, once given, are gone.",
    ),
    Question(
        "What goes up the hill and down again, yet never moves?", "The path", "Your shadow",
        "It waits for you every morning.", "The path. Your shadow follows you everywhere.",
    ),
    Question(
        "What has many teeth, but never bites?", "A comb", "A wolf",
        "Even Medusa had no use for one.", "A comb. A wolf bites, as you well know.",
    ),
    Question(
        "What has a neck but no head, and two ears but no face?", "An amphora", "A goose",
        "Dionysus keeps a few full ones.", "An amphora. A goose has a head, and no wine.",
    ),
    Question(
        "Feed me and I live. Give me a drink and I die. What am I?", "Fire", "A horse",
        "Prometheus paid dearly to bring it.", "Fire. A horse likes a drink, as you do.",
    ),
    Question(
        "What has one eye, but cannot see?", "A needle", "A Cyclops",
        "A Cyclops has one eye too, and sees well enough.", "A needle. A Cyclops sees well enough, for a while.",
    ),
    Question(
        "What belongs to you, but others use it more than you do?", "Your name", "Your rock",
        "It travels where you cannot.", "Your name. Nobody else wants your rock.",
    ),
    Question(
        "What breaks as soon as you say its name?", "Silence", "A jar",
        "And now it is broken.", "Silence. You just broke it.",
    ),
    Question(
        "What runs around the whole field without moving?", "A fence", "A goat",
        "It runs all day and never tires.", "A fence. The goat runs, then it lies down.",
    ),
    Question(
        "What flies without wings, and weeps without eyes?", "A cloud", "An owl",
        "It weeps, and the grass is glad.", "A cloud. The owl has wings, and dry eyes.",
    ),
    Question(
        "What is lighter than a feather, yet even Heracles cannot hold it for long?", "Breath", "A feather",
        "Even the strongest must breathe out.", "Breath. A feather he could hold all day.",
    ),
    Question(
        "The poor have it, the rich need it, and if you eat it, you starve. What is it?", "Nothing", "Bread",
        "A riddle about nothing, and you solved it.", "Nothing. Bread, at least, keeps you going.",
    ),
    Question(
        "What is full of holes, yet still holds water?", "A sponge", "A sieve",
        "It drinks the sea and gives it back.", "A sponge. A sieve lets it all go.",
    ),
    Question(
        "What has cities but no houses, and hills that nobody climbs?", "A map", "A garden",
        "On a map, every hill is easy.", "A map. A garden has no cities, only bees.",
    ),
    Question(
        "Two roads: one short and easy, one long and steep. Which does a wise man take?", "The steep one", "The easy one",
        "The road to virtue is steep at first, then it grows easy.", "The easy road is short, but it leads nowhere worth going.",
    ),
    Question(
        "When does a wise man begin?", "Today", "Tomorrow",
        "Tomorrow is only a word.", "Today. Tomorrow never comes on its own.",
    ),
    Question(
        "What does a wise man want?", "Little", "Much",
        "He who wants little has much.", "Little. He who wants much is always poor.",
    ),
    Question(
        "A wise man stumbles on the road. What does he do?", "Gets up", "Turns back",
        "Every road has stones.", "He gets up. Every road has stones.",
    ),
    Question(
        "Two cups at noon: one of water, one of wine. Which does a wise man drink?", "The water", "The wine",
        "The wine can wait for the evening.", "The water. The wine can wait for the evening.",
    ),
    Question(
        "The road ahead is long. What does a wise man count?", "His steps", "The miles",
        "The miles take care of themselves.", "His steps. The miles take care of themselves.",
    ),
    Question(
        "A storm comes over the hill. What does a wise man do?", "Shelters", "Pushes on",
        "The storm passes, and the hill will wait.", "He shelters. The storm passes, and the hill will wait.",
    ),
    Question(
        "A stranger comes to the crossroads. What does a wise man offer?", "Bread", "A cold look",
        "Bread first; the road can wait.", "Bread. A cold look feeds nobody.",
    ),
    Question(
        "The road is dark. What does a wise man carry?", "A torch", "A sword",
        "A light shows the way for the next one too.", "A torch. A sword does not show the way.",
    ),
    Question(
        "At the top of the hill, what does a wise man do?", "Looks around", "Boasts",
        "The view does not last, so he looks well.", "He looks around. Boasting spoils the view.",
    ),
    Question(
        "What does a wise man say most often?", "Thank you", "I know",
        "Two small words that open many doors.", "He says thank you. Who says 'I know' stops learning.",
    ),
    Question(
        "The rock rolls back down. What does a wise man do?", "Walks down", "Weeps",
        "The walk down is a rest of its own.", "He walks down. The rock waits for him at the bottom.",
    ),
    Question(
        "Whom does a wise man try to beat?", "Himself", "Others",
        "The only rival who never leaves.", "Himself. Others have their own hills.",
    ),
    Question(
        "What does a wise man do with the past?", "Lets it go", "Carries it",
        "One rock is enough to carry.", "He lets it go. One rock is enough to carry.",
    ),
    Question(
        "When does a wise man rest?", "When tired", "Never",
        "Even the sun sets.", "When he is tired. Even the sun sets.",
    ),
    Question(
        "A friend is slower on the hill. What does a wise man do?", "Walks with him", "Walks ahead",
        "A hill is shorter for two.", "He walks with him. A hill is shorter for two.",
    ),
    Question(
        "Where does a wise man find quiet?", "In himself", "In the hills",
        "He carries it on every road.", "In himself. He carries it on every road.",
    ),
    Question(
        "What makes a long road short?", "Good company", "Grumbling",
        "Talk well, and the miles fly.", "Good company. Grumbling makes every mile longer.",
    ),
    Question(
        "A wise man finds a coin on the road. What does he ask?", "Whose is it?", "Who saw me?",
        "A light heart is worth more than a coin.", "Whose is it? A light heart is worth more than a coin.",
    ),
    Question(
        "Which is richer: a full purse or a full heart?", "A full heart", "A full purse",
        "A heart cannot be stolen.", "A full heart. A purse can be stolen.",
    ),
    Question(
        "Who answers every call, but has no voice of her own?", "Echo", "A lark",
        "She can only say the last words of others.", "Echo. A lark sings a song of her own.",
    ),
    Question(
        "What has a head and a tail, but no body?", "An obol", "A snake",
        "Every coin has two faces, and no body at all.", "An obol. A snake is nearly all body.",
    ),
    Question(
        "What must be broken before you can use it?", "An egg", "A shield",
        "And the hen will lay another one.", "An egg. A broken shield helps nobody.",
    ),
    Question(
        "What has four legs, a head and a foot, yet never walks?", "A bed", "A goat",
        "Rest in it well; the hill will wait.", "A bed. A goat walks all day long.",
    ),
    Question(
        "What flies all day and never rests, though it has no wings?", "The wind", "An eagle",
        "It pushes the clouds, as you push the rock.", "The wind. An eagle rests on the rocks.",
    ),
    Question(
        "What follows you in the sun, and leaves you in the dark?", "Your shadow", "Your dog",
        "It climbs the hill with you every day.", "Your shadow. A dog stays by you at night.",
    ),
    Question(
        "What can you break without touching it?", "A promise", "A loaf",
        "So keep it whole.", "A promise. Bread you break with your hands.",
    ),
    Question(
        "What is always ahead of you, yet you can never see it?", "Tomorrow", "The path",
        "Today is the only day you can push.", "Tomorrow. The path you can see, a little.",
    ),
    Question(
        "Which is heavier: a talent of feathers, or a talent of stone?", "They weigh the same", "The stone",
        "A talent is a talent. Your rock is heavier.", "They weigh the same. A talent is a talent.",
    ),
    Question(
        "Who has a hundred eyes and never sleeps?", "Argus", "A peacock",
        "Later, Hera set his eyes on the peacock's tail.", "Argus. The peacock only wears his eyes.",
    ),
    Question(
        "Who carries the sky on his shoulders?", "Atlas", "Hermes",
        "And you thought your rock was heavy.", "Atlas. Hermes carries only messages.",
    ),
    Question(
        "Who flew too close to the sun?", "Icarus", "Daedalus",
        "His wings were wax. Your rock is stone.", "Icarus. His father flew low, and lived.",
    ),
    Question(
        "What did Prometheus steal from the gods, for men?", "Fire", "Gold",
        "He was chained to a rock for it. You only push one.", "Fire. The gods kept their gold.",
    ),
    Question(
        "Who opened the jar that let all the troubles out?", "Pandora", "Penelope",
        "Only hope stayed inside.", "Pandora. Penelope wove, and waited.",
    ),
    Question(
        "Who waited twenty years for her husband to come home?", "Penelope", "Helen",
        "She wove by day, and undid it by night.", "Penelope. Helen was the one who left.",
    ),
    Question(
        "Who stands in water he can never drink?", "Tantalus", "Orpheus",
        "Your rock is kinder than his thirst.", "Tantalus. Orpheus had other troubles.",
    ),
    Question(
        "Who looked back, and lost his love at the gates of the dead?", "Orpheus", "Odysseus",
        "Some roads are walked without looking back.", "Orpheus. Odysseus found his way home.",
    ),
    Question(
        "Who cleaned the stables of King Augeas in one day?", "Heracles", "Theseus",
        "He turned two rivers through them.", "Heracles. He turned two rivers through them.",
    ),
    Question(
        "Who found the way out of the labyrinth with a thread?", "Theseus", "Minos",
        "Ariadne gave him the thread.", "Theseus. Minos kept the beast inside.",
    ),
    Question(
        "Who pushes a rock up a hill, forever?", "Sisyphus", "Atlas",
        "And one day, you may smile at it.", "Sisyphus. You of all people should know.",
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
    whoSaid(saying), saying.author, other,
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
     * Each round of three visions brings Hermes with the next saying, and the two gods of [place] with the next two riddles,
     * in an order made from the seed. So no saying and no riddle comes again before all have come, also across a move.
     */
    fun visionAt(k: Int, place: Place): Vision {
        val round = k / 3
        val order = Random(seed + round * 7919)
        val slot = listOf(0, 1, 2).shuffled(order)[k % 3]
        val gods = godsOf(place).filter { it != God.HERMES }.shuffled(order)
        if (slot > 0) return Vision(nth(RIDDLES, 2 * round + slot - 1, 0), gods[slot - 1], Random(seed + k * 31).nextBoolean())
        val saying = nth(SAYINGS, round, HERMES_ORDER)
        return Vision(hermes(saying, PEOPLE.filter { it != saying.author }.random(Random(seed xor k))), God.HERMES, Random(seed + k * 31).nextBoolean())
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
        /** Hermes keeps the order of his sayings from before the gods of each world. */
        private const val HERMES_ORDER = 104729
    }
}
