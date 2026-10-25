package dev.oblac.sisyphus.game

/** The five lines of the shop, in the order the Agora shows them. */
enum class Line { ROCK, BESIDE, ALONE, BLESSING, BEAUTY }

/**
 * One thing to buy. Once owned, [effect] is its line's value: a bonus, turns an hour, or work per obol.
 * The Agora shows [note] in its row, and [details] when it asks to buy.
 */
class Item(val line: Line, val tier: Int, val name: String, val note: String, val price: Int, val effect: Double, val details: String)

/** However strong the rock and the helpers, the sun and the walk move at most this much faster. */
const val SUN_CAP = 1.5

/** Turns of work for one obol, before any blessing: 10 days of pushing. */
const val WORK_PER_OBOL = 720.0

val ITEMS = listOf(
    Item(Line.ROCK, 1, "River stone", "turns count 1.5x", 2, 1.5,
        "Worn smooth by the river. Every turn of the knob counts 1.5 times, so obols come faster."),
    Item(Line.ROCK, 2, "Pumice", "turns count 2x", 5, 2.0,
        "Light, and full of holes. Every turn counts 2 times. It takes the place of the river stone."),
    Item(Line.ROCK, 3, "Sphere of Daedalus", "turns count 3x", 9, 3.0,
        "Made perfectly round by the great inventor. Every turn counts 3 times. It takes the place of the pumice."),
    Item(Line.BESIDE, 1, "Merope", "helps push 1.25x", 3, 1.25,
        "His wife pushes beside him while you turn the knob. Each turn counts 1.25 times more, on top of the rock."),
    Item(Line.BESIDE, 2, "Bellerophon", "helps push 1.5x", 6, 1.5,
        "His grandson, the rider of Pegasus, pushes beside him. Each turn counts 1.5 times more. He takes Merope's place."),
    Item(Line.BESIDE, 3, "Heracles", "helps push 2x", 10, 2.0,
        "The strongest of the heroes pushes beside him. Each turn counts 2 times more. He takes Bellerophon's place."),
    Item(Line.ALONE, 1, "Amalthea", "6 pushes an hour", 1, 6.0,
        "The goat who nursed Zeus pushes the rock 6 times an hour, even when the app is closed."),
    Item(Line.ALONE, 2, "Pan", "12 pushes an hour", 4, 12.0,
        "The god of the wild pushes 12 times an hour, even when the app is closed. He takes Amalthea's place."),
    Item(Line.ALONE, 3, "Thanatos", "24 pushes an hour", 8, 24.0,
        "Death, whom Sisyphus once chained, pushes 24 times an hour, day and night. He takes Pan's place."),
    Item(Line.BLESSING, 1, "Purse of Hermes", "obol per 7 days", 2, 504.0,
        "Hermes fills your purse faster: one obol for every 7 days of pushing, not 10."),
    Item(Line.BLESSING, 2, "Gift of Tyche", "obol per 5 days", 6, 360.0,
        "Fortune smiles on you: one obol for every 5 days of pushing. It takes the place of the purse."),
    Item(Line.BLESSING, 3, "Wealth of Plutus", "obol per 3 days", 10, 216.0,
        "The god of wealth is on your side: one obol for every 3 days of pushing. It takes the place of Tyche's gift."),
    Item(Line.BEAUTY, 1, "Dolphins", "leap in the sea", 3, 0.0,
        "Dolphins leap from the sea now and then. Beauty stays, whatever else you buy."),
    Item(Line.BEAUTY, 2, "Pegasus", "flies by", 7, 0.0,
        "The winged horse flies across the sky now and then."),
    Item(Line.BEAUTY, 3, "The Pleiades", "seven stars at night", 11, 0.0,
        "The seven sisters shine at night. The faintest one is Merope."),
)

/** What the player owns, and their obols. Each line is bought in order; the newest item of a line sets its effect. */
class Shop(tiers: Map<Line, Int> = emptyMap(), obols: Double = 0.0) {
    private val tiers = Line.entries.associateWith { (tiers[it] ?: 0).coerceIn(0, 3) }.toMutableMap()

    var obols = obols
        private set

    fun tier(line: Line): Int = tiers.getValue(line)

    fun next(line: Line): Item? = ITEMS.firstOrNull { it.line == line && it.tier == tier(line) + 1 }

    fun buy(line: Line): Boolean {
        val item = next(line) ?: return false
        if (obols < item.price) return false
        obols -= item.price
        tiers[line] = item.tier
        return true
    }

    val bonus: Double get() = (owned(Line.ROCK)?.effect ?: 1.0) * (owned(Line.BESIDE)?.effect ?: 1.0)
    val helpersPerHour: Double get() = owned(Line.ALONE)?.effect ?: 0.0
    val workPerObol: Double get() = owned(Line.BLESSING)?.effect ?: WORK_PER_OBOL
    val helper: Item? get() = owned(Line.ALONE)

    fun addWork(turns: Double) {
        obols += turns / workPerObol
    }

    /** For dev builds: adds [obols], or takes them away, but never below zero. */
    fun add(obols: Double) {
        this.obols = (this.obols + obols).coerceAtLeast(0.0)
    }

    /** For dev builds: everything bought goes back to the market. */
    fun reset() {
        for (line in Line.entries) tiers[line] = 0
    }

    /** The newest item bought in [line], the one that sets its effect; null before the first. */
    fun owned(line: Line): Item? = ITEMS.firstOrNull { it.line == line && it.tier == tier(line) }
}
