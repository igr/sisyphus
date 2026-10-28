package dev.oblac.sisyphus.game

import kotlin.math.floor

/** The four kinds of labors, each with its own color on the Labors tab. */
enum class Category(val title: String) {
    HILLS("Hills climbed"),
    TURNS("Knob turns"),
    DAYS("Days"),
    ANSWERS("Right answers"),
}

/** A task across all climbs: done when the count of its [category] reaches [goal]. It gives nothing but the honor. */
class Labor(val category: Category, val goal: Long, val name: String, val line: String)

/** How far the player has come, in all climbs and worlds. */
class Counts(val hills: Int, val turns: Double, val days: Int, val answers: Int) {
    fun of(category: Category): Double = when (category) {
        Category.HILLS -> hills.toDouble()
        Category.TURNS -> turns
        Category.DAYS -> days.toDouble()
        Category.ANSWERS -> answers.toDouble()
    }
}

fun Labor.done(counts: Counts) = counts.of(category) >= goal

/** The 20 labors, 5 in each category, from the first tier to the last. */
val LABORS: List<Labor> = listOf(
    Category.HILLS to listOf(
        1L to "Top of the Morning", 10L to "Rolling Stone", 50L to "King of the Hill", 100L to "Over the Hill", 1000L to "Imagine Him Happy",
    ),
    Category.TURNS to listOf(
        100L to "Turn On", 1_000L to "Spin Doctor", 10_000L to "Tina Turner", 100_000L to "Revolutionary", 1_000_000L to "Perpetual Motion",
    ),
    Category.DAYS to listOf(
        10L to "Carpe Diem", 100L to "The Hundred Days", 1_000L to "Days of Our Lives", 10_000L to "Groundhog Day", 100_000L to "Eternal Sunshine",
    ),
    Category.ANSWERS to listOf(
        1L to "Lucky Guess", 10L to "Teacher's Pet", 50L to "The Oracle", 100L to "Know-It-All", 500L to "Know Thyself",
    ),
).flatMap { (category, tiers) -> tiers.map { (goal, name) -> Labor(category, goal, name, line(category, goal)) } }

private fun line(category: Category, goal: Long) = when (category) {
    Category.HILLS -> if (goal == 1L) "Reach the top once." else "Reach the top ${grouped(goal)} times."
    Category.TURNS -> "Turn the knob ${grouped(goal)} times."
    Category.DAYS -> "Push through ${grouped(goal)} days."
    Category.ANSWERS -> if (goal == 1L) "Answer a vision right." else "Answer ${grouped(goal)} visions right."
}

/** A number with commas between its thousands, such as "1,000,000". */
private fun grouped(n: Long) = n.toString().reversed().chunked(3).joinToString(",").reversed()

/** How far a labor has come, short enough for the smallest phone: "7/10", "850/1k", "12k/100k", "123k/1M". */
fun progress(count: Double, goal: Long) = "${short(floor(count).toLong())}/${short(goal)}"

private fun short(n: Long) = when {
    n >= 1_000_000 -> "${n / 1_000_000}M"
    n >= 1_000 -> "${n / 1_000}k"
    else -> "$n"
}

/** Hermes brings the news of a labor that is done. */
fun laborNews(labor: Labor) = Quote("A labor is done: ${labor.name}.", "Hermes")
