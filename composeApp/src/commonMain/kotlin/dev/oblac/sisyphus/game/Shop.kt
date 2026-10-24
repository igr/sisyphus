package dev.oblac.sisyphus.game

/** The five lines of the shop, in the order the Agora shows them. */
enum class Line { ROCK, BESIDE, ALONE, BLESSING, BEAUTY }

/** One thing to buy. Once owned, [effect] is its line's value: a bonus, turns an hour, or work per obol. */
class Item(val line: Line, val tier: Int, val name: String, val note: String, val price: Int, val effect: Double)

/** However strong the rock and the helpers, the sun and the walk move at most this much faster. */
const val SUN_CAP = 1.5

/** Turns of work for one obol, before any blessing: 10 days of pushing. */
const val WORK_PER_OBOL = 720.0

val ITEMS = listOf(
    Item(Line.ROCK, 1, "River stone", "turns count 1.5x", 2, 1.5),
    Item(Line.ROCK, 2, "Pumice", "turns count 2x", 5, 2.0),
    Item(Line.ROCK, 3, "Sphere of Daedalus", "turns count 3x", 9, 3.0),
    Item(Line.BESIDE, 1, "Merope", "helps push 1.25x", 3, 1.25),
    Item(Line.BESIDE, 2, "Bellerophon", "helps push 1.5x", 6, 1.5),
    Item(Line.BESIDE, 3, "Heracles", "helps push 2x", 10, 2.0),
    Item(Line.ALONE, 1, "A goat", "6 pushes an hour", 1, 6.0),
    Item(Line.ALONE, 2, "Pan", "12 pushes an hour", 4, 12.0),
    Item(Line.ALONE, 3, "Thanatos", "24 pushes an hour", 8, 24.0),
    Item(Line.BLESSING, 1, "Purse of Hermes", "obol per 7 days", 2, 504.0),
    Item(Line.BLESSING, 2, "Gift of Tyche", "obol per 5 days", 6, 360.0),
    Item(Line.BLESSING, 3, "Wealth of Plutus", "obol per 3 days", 10, 216.0),
    Item(Line.BEAUTY, 1, "Dolphins", "leap in the sea", 3, 0.0),
    Item(Line.BEAUTY, 2, "Pegasus", "flies by", 7, 0.0),
    Item(Line.BEAUTY, 3, "The Pleiades", "seven stars at night", 11, 0.0),
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

    private fun owned(line: Line): Item? = ITEMS.firstOrNull { it.line == line && it.tier == tier(line) }
}
