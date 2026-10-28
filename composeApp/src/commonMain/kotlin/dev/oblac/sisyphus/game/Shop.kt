package dev.oblac.sisyphus.game

import kotlin.math.floor
import kotlin.math.roundToLong
import kotlin.random.Random

/**
 * The five lines of the shop, in the order the Agora shows them. Saved games know a line by its [id], which never changes:
 * the Sidekicks and the Gods keep the ids they had before they got these names.
 */
enum class Line(val id: String) {
    ROCK("rock"),
    SIDEKICKS("beside"),
    GODS("alone"),
    BLESSING("blessing"),
    BEAUTY("beauty"),
}

/**
 * One thing to buy in a [place]. Once owned, [effect] is its line's value: a bonus, turns an hour, or the part of the work per obol.
 * The Agora shows [note] in its row, and [details] when it asks to buy.
 */
class Item(
    val line: Line, val tier: Int, val name: String, val note: String, val price: Int, val effect: Double, val details: String,
    val place: Place = Place.CORINTH,
)

/** Turns of work for each of the fast obols at the start of a climb, before any blessing. */
const val FIRST_OBOL = Config.WORK_PER_OBOL * Config.FAST_PART

/** The items of Corinth. */
private val CORINTH_ITEMS = with(Place.CORINTH) {
    listOf(
        item(Line.ROCK, 1, "River stone",
            "Worn smooth by the river. Every turn of the knob counts {x} times, so obols come faster."),
        item(Line.ROCK, 2, "Pumice",
            "Light, and full of holes. Every turn counts {x} times. It takes the place of the river stone."),
        item(Line.ROCK, 3, "Sphere of Daedalus",
            "Made perfectly round by the great inventor. Every turn counts {x} times. It takes the place of the pumice."),
        item(Line.SIDEKICKS, 1, "Merope",
            "His wife pushes beside him while you turn the knob. Each turn counts {x} times more, on top of the rock."),
        item(Line.SIDEKICKS, 2, "Bellerophon",
            "His grandson, the rider of Pegasus, pushes beside him. Each turn counts {x} times more. He takes Merope's place."),
        item(Line.SIDEKICKS, 3, "Heracles",
            "The strongest of the heroes pushes beside him. Each turn counts {x} times more. He takes Bellerophon's place."),
        item(Line.GODS, 1, "Amalthea",
            "The goat who nursed Zeus pushes the rock {x} times an hour, even when the app is closed."),
        item(Line.GODS, 2, "Pan",
            "The god of the wild pushes {x} times an hour, even when the app is closed. He takes Amalthea's place."),
        item(Line.GODS, 3, "Thanatos",
            "Death, whom Sisyphus once chained, pushes {x} times an hour, day and night. He takes Pan's place."),
        item(Line.BLESSING, 1, "Purse of Hermes",
            "Hermes fills your purse faster: one obol for every {days} of pushing, not {base}."),
        item(Line.BLESSING, 2, "Gift of Tyche",
            "Fortune smiles on you: one obol for every {days} of pushing. It takes the place of the purse."),
        item(Line.BLESSING, 3, "Wealth of Plutus",
            "The god of wealth is on your side: one obol for every {days} of pushing. It takes the place of Tyche's gift."),
        item(Line.BEAUTY, 1, "Dolphins",
            "Dolphins leap from the sea now and then. Beauty stays, whatever else you buy.", "leap in the sea"),
        item(Line.BEAUTY, 2, "Pegasus",
            "The winged horse flies across the sky now and then.", "flies by"),
        item(Line.BEAUTY, 3, "The Pleiades",
            "The seven sisters shine at night. The faintest one is Merope.", "seven stars at night"),
    )
}

private val ETNA_ITEMS = with(Place.ETNA) {
    listOf(
        item(Line.ROCK, 1, "Lava stone",
            "Black and rough, from an old flow of lava. Every turn of the knob counts {x} times, so obols come faster."),
        item(Line.ROCK, 2, "Volcanic bomb",
            "Lava that the mountain threw into the air. It cooled round as it flew. Every turn counts {x} times. It takes the place of the lava stone."),
        item(Line.ROCK, 3, "Obsidian sphere",
            "Black glass of the volcano, polished perfectly round. Every turn counts {x} times. It takes the place of the volcanic bomb."),
        item(Line.SIDEKICKS, 1, "Empedocles",
            "The wise man who leapt into the crater, and left only a sandal, pushes beside him. Each turn counts {x} times more, on top of the rock."),
        item(Line.SIDEKICKS, 2, "Odysseus",
            "His son, some say, who tricked the Cyclops, pushes beside him. Each turn counts {x} times more. He takes Empedocles' place."),
        item(Line.SIDEKICKS, 3, "Polyphemus",
            "The Cyclops who threw rocks at the ship of Odysseus pushes beside him. Each turn counts {x} times more. He takes Odysseus' place."),
        item(Line.GODS, 1, "Aetna",
            "The nymph who gave the mountain her name pushes the rock {x} times an hour, even when the app is closed."),
        item(Line.GODS, 2, "Hephaestus",
            "The smith of the gods, whose forge is under the mountain, pushes {x} times an hour, even when the app is closed. He takes Aetna's place."),
        item(Line.GODS, 3, "Typhon",
            "The giant whom Zeus buried under the mountain pushes {x} times an hour, day and night. He takes Hephaestus' place."),
        item(Line.BLESSING, 1, "Sheaf of Demeter",
            "Sicily is the island of Demeter. Her grain fills your purse faster: one obol for every {days} of pushing, not {base}."),
        item(Line.BLESSING, 2, "Wine of Dionysus",
            "Vines love the black soil of the mountain: one obol for every {days} of pushing. It takes the place of the sheaf."),
        item(Line.BLESSING, 3, "Silver of Arethusa",
            "The silver of Syracuse, with the face of the nymph Arethusa: one obol for every {days} of pushing. It takes the place of the wine."),
        item(Line.BEAUTY, 1, "Lava fountain",
            "Now and then the crater throws fire into the sky. Beauty stays, whatever else you buy.", "fire from the crater"),
        item(Line.BEAUTY, 2, "Eagle of Zeus",
            "The eagle of Zeus flies by now and then, with a thunderbolt from the forge under the mountain.", "carries a thunderbolt"),
        item(Line.BEAUTY, 3, "Torches of Demeter",
            "At night Demeter walks the black fields with two torches, lit in the crater, and looks for her daughter.", "torches in the night"),
    )
}

private val UNDERWORLD_ITEMS = with(Place.UNDERWORLD) {
    listOf(
        item(Line.ROCK, 1, "Stone of Lethe",
            "From the river of forgetting: it has forgotten how heavy it is. Every turn of the knob counts {x} times, so obols come faster."),
        item(Line.ROCK, 2, "Rock of Tantalus",
            "The rock that hangs over Tantalus, always about to fall. Every turn counts {x} times. It takes the place of the stone of Lethe."),
        item(Line.ROCK, 3, "Adamant sphere",
            "Adamant, the stone of the gates of Tartarus, made perfectly round. Every turn counts {x} times. It takes the place of the rock of Tantalus."),
        item(Line.SIDEKICKS, 1, "Orpheus",
            "When he sang here, Sisyphus sat on his rock to listen. Now he pushes beside him. Each turn counts {x} times more, on top of the rock."),
        item(Line.SIDEKICKS, 2, "Theseus",
            "The hero who sat stuck on the chair of forgetting pushes beside him. Each turn counts {x} times more. He takes Orpheus' place."),
        item(Line.SIDEKICKS, 3, "Achilles",
            "The greatest of the heroes, a shade now, pushes beside him. Each turn counts {x} times more. He takes Theseus' place."),
        item(Line.GODS, 1, "Cerberus",
            "The three-headed dog of the gate pushes the rock {x} times an hour, even when the app is closed."),
        item(Line.GODS, 2, "Persephone",
            "The queen of the dead, whom Sisyphus once tricked, pushes {x} times an hour, even when the app is closed. She takes Cerberus' place."),
        item(Line.GODS, 3, "Hades",
            "The king of the dead pushes {x} times an hour, day and night. He takes Persephone's place."),
        item(Line.BLESSING, 1, "Obol for the ferry",
            "The dead pay the ferryman an obol, and now obols come your way: one obol for every {days} of pushing, not {base}."),
        item(Line.BLESSING, 2, "Golden bough",
            "The gift that opened the way for Aeneas: one obol for every {days} of pushing. It takes the place of the obol."),
        item(Line.BLESSING, 3, "Riches of Plouton",
            "Hades is also Plouton, the rich one, and the gold of the earth is his: one obol for every {days} of pushing. It takes the place of the bough."),
        item(Line.BEAUTY, 1, "Boat of Charon",
            "Now and then Charon rows the dead across the Styx. Beauty stays, whatever else you buy.", "crosses the Styx"),
        item(Line.BEAUTY, 2, "Psyche",
            "Psyche, the soul, flies by now and then on butterfly wings. She came down here once for Aphrodite.", "on butterfly wings"),
        item(Line.BEAUTY, 3, "Lights of Elysium",
            "At night the blessed souls of Elysium drift over the meadow as small lights.", "glow at night"),
    )
}

/**
 * An item of this world: a name and details of its own, with the price and the effect from [Config.ITEMS].
 * In the details, {x} stands for the effect, {days} for the days of pushing for an obol with this blessing, and {base} for those without one.
 * A beauty item has a [note] of its own; the other lines have the same notes in every world.
 */
private fun Place.item(line: Line, tier: Int, name: String, details: String, note: String? = null): Item {
    val (price, effect) = Config.ITEMS.getValue(line)[tier - 1]
    val text = details.replace("{x}", plain(effect))
        .replace("{days}", workDays(Config.WORK_PER_OBOL * effect))
        .replace("{base}", plain(Config.WORK_PER_OBOL / Config.TURNS_PER_DAY))
    return Item(line, tier, name, note ?: note(line, effect), price, effect, text, this)
}

/** The note of an item, from its effect: "turns count 1.5x", "helps push 2x", "6 pushes an hour", "obol per 1.5 days". */
private fun note(line: Line, effect: Double): String = when (line) {
    Line.ROCK -> "turns count ${plain(effect)}x"
    Line.SIDEKICKS -> "helps push ${plain(effect)}x"
    Line.GODS -> "${plain(effect)} pushes an hour"
    Line.BLESSING -> "obol per ${workDays(Config.WORK_PER_OBOL * effect)}"
    Line.BEAUTY -> error("A beauty item has a note of its own.")
}

/** The days of pushing that [work] takes, as the texts say them: "1.5 days", "day", "half day". */
internal fun workDays(work: Double): String = when (val days = plain(work / Config.TURNS_PER_DAY)) {
    "1" -> "day"
    "0.5" -> "half day"
    else -> "$days days"
}

/** A number as the texts show it, with two decimals at most: "1.5", "2", "1.25". */
internal fun plain(x: Double): String {
    val hundredths = (x * 100).roundToLong()
    return if (hundredths % 100 == 0L) "${hundredths / 100}" else "${hundredths / 100.0}"
}

/** The 15 items of [place], in the order the Agora shows them. */
fun items(place: Place): List<Item> = when (place) {
    Place.CORINTH -> CORINTH_ITEMS
    Place.ETNA -> ETNA_ITEMS
    Place.UNDERWORLD -> UNDERWORLD_ITEMS
}

/**
 * What the player owns in [place], and their obols. Each line is bought in order; the newest item of a line sets its effect.
 * [random] gives each obol after the fast ones its work.
 */
class Shop(
    tiers: Map<Line, Int> = emptyMap(),
    obols: Double = 0.0,
    val place: Place = Place.CORINTH,
    nextObol: Double? = null,
    private val random: Random = Random,
) {
    private val tiers = Line.entries.associateWith { (tiers[it] ?: 0).coerceIn(0, 3) }.toMutableMap()
    private val stock = items(place)

    /** The obols, and the part of the next obol that is earned. */
    var obols = obols
        private set

    /**
     * The work that the next obol needs. It is set when the obol before it comes, and a blessing makes it smaller in step.
     * Without it, the shop counts the obols of the climb: the first [Config.FAST_OBOLS] are fast.
     * An obol saved with other numbers needs no more work than an obol can need now.
     */
    var nextObol = (nextObol ?: if (earned < Config.FAST_OBOLS) fastObol else workPerObol).coerceAtMost(workPerObol * (1 + Config.OBOL_SPREAD))
        private set

    /** The obols that came in this climb: those in hand and those spent on items. */
    private val earned: Int get() = floor(obols).toInt() + stock.filter { it.tier <= tier(it.line) }.sumOf { it.price }

    private val fastObol: Double get() = FIRST_OBOL * blessing

    fun tier(line: Line): Int = tiers.getValue(line)

    /** How many items are bought, in all of the lines. */
    val bought: Int get() = Line.entries.sumOf { tier(it) }

    /** All 15 items are bought: the final climb can begin. */
    val allBought get() = Line.entries.all { tier(it) == 3 }

    fun next(line: Line): Item? = stock.firstOrNull { it.line == line && it.tier == tier(line) + 1 }

    fun buy(line: Line): Boolean {
        val item = next(line) ?: return false
        if (obols < item.price) return false
        val perObol = workPerObol
        obols -= item.price
        tiers[line] = item.tier
        nextObol *= workPerObol / perObol
        return true
    }

    val bonus: Double get() = (owned(Line.ROCK)?.effect ?: 1.0) * (owned(Line.SIDEKICKS)?.effect ?: 1.0)
    val helpersPerHour: Double get() = owned(Line.GODS)?.effect ?: 0.0
    val workPerObol: Double get() = Config.WORK_PER_OBOL * blessing

    /** The part of the work per obol that the newest blessing leaves: all of it before the first one. */
    private val blessing: Double get() = owned(Line.BLESSING)?.effect ?: 1.0
    val helper: Item? get() = owned(Line.GODS)

    /** The work fills the next obol. When it comes, the rest of the work goes to the obol after it. */
    fun addWork(turns: Double) {
        var work = turns
        while (work > 0) {
            val whole = floor(obols) + 1
            val left = (whole - obols) * nextObol
            if (work < left) {
                obols += work / nextObol
                return
            }
            work -= left
            obols = whole
            nextObol = if (earned < Config.FAST_OBOLS) fastObol else workPerObol * (1 + random.nextDouble(-Config.OBOL_SPREAD, Config.OBOL_SPREAD))
        }
    }

    /** For dev builds: adds [obols], or takes them away, but never below zero. */
    fun add(obols: Double) {
        this.obols = (this.obols + obols).coerceAtLeast(0.0)
    }

    /** For dev builds: everything bought goes back to the market. */
    fun reset() {
        for (line in Line.entries) tiers[line] = 0
    }

    /** For dev builds: everything is bought. */
    fun complete() {
        for (line in Line.entries) tiers[line] = 3
    }

    /** The newest item bought in [line], the one that sets its effect; null before the first. */
    fun owned(line: Line): Item? = stock.firstOrNull { it.line == line && it.tier == tier(line) }
}
