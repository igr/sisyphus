package dev.oblac.sisyphus.game

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
 * One thing to buy in a [place]. Once owned, [effect] is its line's value: a bonus, turns an hour, or work per obol.
 * The Agora shows [note] in its row, and [details] when it asks to buy.
 */
class Item(
    val line: Line, val tier: Int, val name: String, val note: String, val price: Int, val effect: Double, val details: String,
    val place: Place = Place.CORINTH,
)

/** However strong the rock and the helpers, the sun and the walk move at most this much faster. */
const val SUN_CAP = 1.5

/** Turns of work for one obol, before any blessing: 10 days of pushing. */
const val WORK_PER_OBOL = 720.0

/** The items of Corinth. Their prices and effects hold in every world. */
private val CORINTH_ITEMS = listOf(
    Item(Line.ROCK, 1, "River stone", "turns count 1.5x", 2, 1.5,
        "Worn smooth by the river. Every turn of the knob counts 1.5 times, so obols come faster."),
    Item(Line.ROCK, 2, "Pumice", "turns count 2x", 5, 2.0,
        "Light, and full of holes. Every turn counts 2 times. It takes the place of the river stone."),
    Item(Line.ROCK, 3, "Sphere of Daedalus", "turns count 3x", 9, 3.0,
        "Made perfectly round by the great inventor. Every turn counts 3 times. It takes the place of the pumice."),
    Item(Line.SIDEKICKS, 1, "Merope", "helps push 1.25x", 3, 1.25,
        "His wife pushes beside him while you turn the knob. Each turn counts 1.25 times more, on top of the rock."),
    Item(Line.SIDEKICKS, 2, "Bellerophon", "helps push 1.5x", 6, 1.5,
        "His grandson, the rider of Pegasus, pushes beside him. Each turn counts 1.5 times more. He takes Merope's place."),
    Item(Line.SIDEKICKS, 3, "Heracles", "helps push 2x", 10, 2.0,
        "The strongest of the heroes pushes beside him. Each turn counts 2 times more. He takes Bellerophon's place."),
    Item(Line.GODS, 1, "Amalthea", "6 pushes an hour", 1, 6.0,
        "The goat who nursed Zeus pushes the rock 6 times an hour, even when the app is closed."),
    Item(Line.GODS, 2, "Pan", "12 pushes an hour", 4, 12.0,
        "The god of the wild pushes 12 times an hour, even when the app is closed. He takes Amalthea's place."),
    Item(Line.GODS, 3, "Thanatos", "24 pushes an hour", 8, 24.0,
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

private val ETNA_ITEMS = with(Place.ETNA) {
    listOf(
        item(Line.ROCK, 1, "Lava stone",
            "Black and rough, from an old flow of lava. Every turn of the knob counts 1.5 times, so obols come faster."),
        item(Line.ROCK, 2, "Volcanic bomb",
            "Lava that the mountain threw into the air. It cooled round as it flew. Every turn counts 2 times. It takes the place of the lava stone."),
        item(Line.ROCK, 3, "Obsidian sphere",
            "Black glass of the volcano, polished perfectly round. Every turn counts 3 times. It takes the place of the volcanic bomb."),
        item(Line.SIDEKICKS, 1, "Empedocles",
            "The wise man who leapt into the crater, and left only a sandal, pushes beside him. Each turn counts 1.25 times more, on top of the rock."),
        item(Line.SIDEKICKS, 2, "Odysseus",
            "His son, some say, who tricked the Cyclops, pushes beside him. Each turn counts 1.5 times more. He takes Empedocles' place."),
        item(Line.SIDEKICKS, 3, "Polyphemus",
            "The Cyclops who threw rocks at the ship of Odysseus pushes beside him. Each turn counts 2 times more. He takes Odysseus' place."),
        item(Line.GODS, 1, "Aetna",
            "The nymph who gave the mountain her name pushes the rock 6 times an hour, even when the app is closed."),
        item(Line.GODS, 2, "Hephaestus",
            "The smith of the gods, whose forge is under the mountain, pushes 12 times an hour, even when the app is closed. He takes Aetna's place."),
        item(Line.GODS, 3, "Typhon",
            "The giant whom Zeus buried under the mountain pushes 24 times an hour, day and night. He takes Hephaestus' place."),
        item(Line.BLESSING, 1, "Sheaf of Demeter",
            "Sicily is the island of Demeter. Her grain fills your purse faster: one obol for every 7 days of pushing, not 10."),
        item(Line.BLESSING, 2, "Wine of Dionysus",
            "Vines love the black soil of the mountain: one obol for every 5 days of pushing. It takes the place of the sheaf."),
        item(Line.BLESSING, 3, "Silver of Arethusa",
            "The silver of Syracuse, with the face of the nymph Arethusa: one obol for every 3 days of pushing. It takes the place of the wine."),
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
            "From the river of forgetting: it has forgotten how heavy it is. Every turn of the knob counts 1.5 times, so obols come faster."),
        item(Line.ROCK, 2, "Rock of Tantalus",
            "The rock that hangs over Tantalus, always about to fall. Every turn counts 2 times. It takes the place of the stone of Lethe."),
        item(Line.ROCK, 3, "Adamant sphere",
            "Adamant, the stone of the gates of Tartarus, made perfectly round. Every turn counts 3 times. It takes the place of the rock of Tantalus."),
        item(Line.SIDEKICKS, 1, "Orpheus",
            "When he sang here, Sisyphus sat on his rock to listen. Now he pushes beside him. Each turn counts 1.25 times more, on top of the rock."),
        item(Line.SIDEKICKS, 2, "Theseus",
            "The hero who sat stuck on the chair of forgetting pushes beside him. Each turn counts 1.5 times more. He takes Orpheus' place."),
        item(Line.SIDEKICKS, 3, "Achilles",
            "The greatest of the heroes, a shade now, pushes beside him. Each turn counts 2 times more. He takes Theseus' place."),
        item(Line.GODS, 1, "Cerberus",
            "The three-headed dog of the gate pushes the rock 6 times an hour, even when the app is closed."),
        item(Line.GODS, 2, "Persephone",
            "The queen of the dead, whom Sisyphus once tricked, pushes 12 times an hour, even when the app is closed. She takes Cerberus' place."),
        item(Line.GODS, 3, "Hades",
            "The king of the dead pushes 24 times an hour, day and night. He takes Persephone's place."),
        item(Line.BLESSING, 1, "Obol for the ferry",
            "The dead pay the ferryman an obol, and now obols come your way: one obol for every 7 days of pushing, not 10."),
        item(Line.BLESSING, 2, "Golden bough",
            "The gift that opened the way for Aeneas: one obol for every 5 days of pushing. It takes the place of the obol."),
        item(Line.BLESSING, 3, "Riches of Plouton",
            "Hades is also Plouton, the rich one, and the gold under the earth is his: one obol for every 3 days of pushing. It takes the place of the bough."),
        item(Line.BEAUTY, 1, "Boat of Charon",
            "Now and then Charon rows the dead across the Styx. Beauty stays, whatever else you buy.", "crosses the Styx"),
        item(Line.BEAUTY, 2, "Psyche",
            "Psyche, the soul, flies by now and then on butterfly wings. She came down here once for Aphrodite.", "on butterfly wings"),
        item(Line.BEAUTY, 3, "Lights of Elysium",
            "At night the blessed souls of Elysium drift over the meadow as small lights.", "glow at night"),
    )
}

/** An item of this world: a name and details of its own, with the price, the effect and the note of the item of Corinth in its place. */
private fun Place.item(line: Line, tier: Int, name: String, details: String, note: String? = null): Item {
    val corinth = CORINTH_ITEMS.first { it.line == line && it.tier == tier }
    return Item(line, tier, name, note ?: corinth.note, corinth.price, corinth.effect, details, this)
}

/** The 15 items of [place], in the order the Agora shows them. */
fun items(place: Place): List<Item> = when (place) {
    Place.CORINTH -> CORINTH_ITEMS
    Place.ETNA -> ETNA_ITEMS
    Place.UNDERWORLD -> UNDERWORLD_ITEMS
}

/** What the player owns in [place], and their obols. Each line is bought in order; the newest item of a line sets its effect. */
class Shop(tiers: Map<Line, Int> = emptyMap(), obols: Double = 0.0, val place: Place = Place.CORINTH) {
    private val tiers = Line.entries.associateWith { (tiers[it] ?: 0).coerceIn(0, 3) }.toMutableMap()
    private val stock = items(place)

    var obols = obols
        private set

    fun tier(line: Line): Int = tiers.getValue(line)

    /** All 15 items are bought: the final climb can begin. */
    val allBought get() = Line.entries.all { tier(it) == 3 }

    fun next(line: Line): Item? = stock.firstOrNull { it.line == line && it.tier == tier(line) + 1 }

    fun buy(line: Line): Boolean {
        val item = next(line) ?: return false
        if (obols < item.price) return false
        obols -= item.price
        tiers[line] = item.tier
        return true
    }

    val bonus: Double get() = (owned(Line.ROCK)?.effect ?: 1.0) * (owned(Line.SIDEKICKS)?.effect ?: 1.0)
    val helpersPerHour: Double get() = owned(Line.GODS)?.effect ?: 0.0
    val workPerObol: Double get() = owned(Line.BLESSING)?.effect ?: WORK_PER_OBOL
    val helper: Item? get() = owned(Line.GODS)

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

    /** For dev builds: everything is bought. */
    fun complete() {
        for (line in Line.entries) tiers[line] = 3
    }

    /** The newest item bought in [line], the one that sets its effect; null before the first. */
    fun owned(line: Line): Item? = stock.firstOrNull { it.line == line && it.tier == tier(line) }
}
