package dev.oblac.sisyphus.game

/**
 * The numbers that set the pace of the game, all in one place. Change one here, and the game follows:
 * the texts that show a number, such as "obol per day", are made from these. Every world has the same numbers.
 */
object Config {
    /** Knob turns that move the sun through one game day, before any rock or sidekick. */
    const val TURNS_PER_DAY = 72.0

    /** However strong the rock and the sidekick, the sun and the walk move at most this much faster. */
    const val SUN_CAP = 1.5

    /** Turns of work for one obol, before any blessing: 2 days of pushing. */
    const val WORK_PER_OBOL = 144.0

    /** How many obols at the start of a climb are fast, so that the first items come soon. */
    const val FAST_OBOLS = 4

    /** The part of the work per obol that each fast obol needs. A blessing makes it smaller too. */
    const val FAST_PART = 0.25

    /** Each obol after the fast ones needs its work, give or take up to this part of it, so that obols come at any time of the day. */
    const val OBOL_SPREAD = 0.25

    /**
     * The price in obols and the effect of each item, from tier I to tier III. The effect is how many times a turn counts
     * for the rock and the sidekicks, pushes an hour for the gods, and the part of the work per obol that a blessing leaves.
     * Beauty does nothing.
     */
    val ITEMS = mapOf(
        Line.ROCK to listOf(2 to 1.5, 5 to 2.0, 9 to 3.0),
        Line.SIDEKICKS to listOf(3 to 1.25, 6 to 1.5, 10 to 2.0),
        Line.GODS to listOf(1 to 6.0, 4 to 12.0, 8 to 24.0),
        Line.BLESSING to listOf(2 to 0.75, 6 to 0.5, 10 to 0.25),
        Line.BEAUTY to listOf(1 to 0.0, 2 to 0.0, 3 to 0.0),
    )

    /** Turns of pushing from the 15th item to the top, at the fastest walk. */
    const val CLIMB_TURNS = 300

    /** A herm, the stone marker of Greek roads, stands by the path after every this many turns of the final climb. */
    const val HERM_TURNS = 100

    /**
     * The player's own turns before the first vision of a climb: a random number between these two, so that it comes on day 2.
     * Day 2 begins after 53 turns. Day 3 begins after 107 turns at the soonest, when the rock is bought as soon as it can be.
     */
    val FIRST_VISION = 70.0..100.0

    /** The player's own turns from one vision to the next: a random number between these two. */
    val NEXT_VISION = 200.0..600.0

    /** Obols for a right answer to a vision. */
    const val VISION_OBOLS = 1

    /** A stone of the tale stands this many turns of walking ahead of him. */
    const val STELE_TURNS = 30

    /**
     * The price of each world after Corinth, in US dollars. The stores set the price that the game shows, in the money of
     * each player, so a change here must be made in RevenueCat, App Store Connect and Play Console too.
     */
    const val WORLD_PRICE = 1.99
}
