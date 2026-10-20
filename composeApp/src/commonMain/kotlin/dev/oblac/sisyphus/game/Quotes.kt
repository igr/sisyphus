package dev.oblac.sisyphus.game

import kotlin.random.Random

class Quote(val text: String, val author: String)

val TUTORIAL = Quote("Turn the knob below to push the rock up the hill.", "the Gods")

val QUOTES = listOf(
    Quote("One must imagine Sisyphus happy.", "Albert Camus"),
    Quote("The way up and the way down are one and the same.", "Heraclitus"),
    Quote("No man ever steps in the same river twice.", "Heraclitus"),
    Quote("Character is destiny.", "Heraclitus"),
    Quote("What stands in the way becomes the way.", "Marcus Aurelius"),
    Quote("Very little is needed to make a happy life.", "Marcus Aurelius"),
    Quote("Waste no more time arguing what a good man should be. Be one.", "Marcus Aurelius"),
    Quote("We suffer more often in imagination than in reality.", "Seneca"),
    Quote("Difficulties strengthen the mind, as labor does the body.", "Seneca"),
    Quote("As long as you live, keep learning how to live.", "Seneca"),
    Quote("No great thing is created suddenly.", "Epictetus"),
    Quote("First say what you would be; then do what you have to do.", "Epictetus"),
    Quote("Well begun is half done.", "Aristotle"),
    Quote("Happiness depends upon ourselves.", "Aristotle"),
    Quote("The beginning is the most important part of the work.", "Plato"),
    Quote("Even the gods do not fight against necessity.", "Simonides"),
    Quote("Know thyself.", "Delphic maxim"),
    Quote("Nothing in excess.", "Delphic maxim"),
    Quote("Dripping water hollows out stone.", "Ovid"),
    Quote("Slow and steady wins the race.", "Aesop"),
    Quote("A journey of a thousand miles begins with a single step.", "Lao Tzu"),
    Quote("It does not matter how slowly you go, as long as you do not stop.", "Confucius"),
    Quote("He who has a why to live can bear almost any how.", "Nietzsche"),
    Quote("Fall seven times, stand up eight.", "Japanese proverb"),
    Quote("A smooth sea never made a skilled sailor.", "Proverb"),
    Quote("Little by little, one travels far.", "Proverb"),
    Quote("The hill is endless. So is my patience.", "Sisyphus"),
    Quote("Some days you push the rock. Some days it pushes you.", "Sisyphus"),
    Quote("The top is a rumor. The slope is real.", "Sisyphus"),
    Quote("Every turn of the knob is a small yes.", "Sisyphus"),
    Quote("Rest is part of the climb.", "Sisyphus"),
    Quote("Gravity is patient. Be more patient.", "Sisyphus"),
    Quote("The rock forgets. The legs remember.", "Sisyphus"),
    Quote("Push. Breathe. Push again.", "Sisyphus"),
    Quote("The view gets better, one step at a time.", "Sisyphus"),
    Quote("Today I climb. Tomorrow I climb. That is the job.", "Sisyphus"),
    Quote("The sun has seen it all. It still rises.", "Sisyphus"),
    Quote("Strong legs, light heart, heavy rock.", "Sisyphus"),
    Quote("The struggle is the summit.", "Sisyphus"),
    Quote("Every morning, the same rock. Every morning, a new me.", "Sisyphus"),
    Quote("A heavy load makes for a steady step.", "Sisyphus"),
    Quote("I am not heavy. You are just early.", "the Rock"),
    Quote("See you tomorrow, same place.", "the Rock"),
    Quote("We are in this together.", "the Rock"),
    Quote("Roll with it.", "the Rock"),
    Quote("I will be here tomorrow.", "the Hill"),
    Quote("Nobody said it was downhill.", "the Hill"),
)

/**
 * The wise words under the display. A quote is typed out, stays for a while,
 * and dissolves once Sisyphus has pushed far enough. Then a random new one comes.
 */
class QuoteBox(private val random: Random, firstTime: Boolean) {
    enum class Phase { TYPING, SHOWING, FADING }

    private val bag = ArrayDeque<Quote>()

    var quote: Quote = if (firstTime) TUTORIAL else next()
        private set
    var phase = Phase.TYPING
        private set
    /** Revealed characters: the text first, then the author. */
    var typed = 0.0
        private set
    /** 0 is fully visible, 1 is gone. */
    var fade = 0f
        private set

    private var delay = 0.8
    private var shown = 0.0
    private var budget = 0.0

    val length get() = quote.text.length + quote.author.length

    fun update(dt: Double, pushed: Double, stillFor: Double) {
        when (phase) {
            Phase.TYPING -> {
                if (delay > 0) {
                    delay -= dt
                    return
                }
                typed += dt * TYPE_SPEED
                if (typed >= length) {
                    typed = length.toDouble()
                    phase = Phase.SHOWING
                    shown = 0.0
                    budget = if (quote === TUTORIAL) PX_PER_TURN else random.nextDouble(3.0, 7.0) * PX_PER_TURN
                }
            }
            Phase.SHOWING -> {
                shown += dt
                budget -= pushed
                val minShow = if (quote === TUTORIAL) 1.0 else 7.0
                val bored = shown > 45 && stillFor > 20
                if ((shown > minShow && budget <= 0) || bored) phase = Phase.FADING
            }
            Phase.FADING -> {
                fade += (dt / FADE_TIME).toFloat()
                if (fade >= 1f) {
                    quote = next()
                    phase = Phase.TYPING
                    typed = 0.0
                    fade = 0f
                    delay = 0.4
                }
            }
        }
    }

    private fun next(): Quote {
        if (bag.isEmpty()) bag.addAll(QUOTES.shuffled(random))
        return bag.removeFirst()
    }

    private companion object {
        const val TYPE_SPEED = 26.0
        const val FADE_TIME = 0.7
    }
}
