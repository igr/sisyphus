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
    Quote("Accept the things to which fate binds you.", "Marcus Aurelius"),
    Quote("Our life is what our thoughts make it.", "Marcus Aurelius"),
    Quote("While we are postponing, life speeds by.", "Seneca"),
    Quote("Life is long, if you know how to use it.", "Seneca"),
    Quote("Fire tests gold, suffering tests brave men.", "Seneca"),
    Quote("People are not disturbed by things, but by their view of them.", "Epictetus"),
    Quote("The sun is new each day.", "Heraclitus"),
    Quote("Nothing endures but change.", "Heraclitus"),
    Quote("The unexamined life is not worth living.", "Socrates"),
    Quote("Before virtue, the gods have placed sweat.", "Hesiod"),
    Quote("Put a little upon a little, and soon it will become big.", "Hesiod"),
    Quote("Without labor nothing prospers.", "Sophocles"),
    Quote("Endure, my heart. You have endured worse.", "Homer"),
    Quote("Be patient and tough; someday this pain will be useful to you.", "Ovid"),
    Quote("Perhaps one day it will be pleasant to remember even this.", "Virgil"),
    Quote("Toil conquers all.", "Virgil"),
    Quote("Make haste slowly.", "Augustus"),
    Quote("Through hardships to the stars.", "Latin proverb"),
    Quote("Do not spoil what you have by desiring what you have not.", "Epicurus"),
    Quote("Nature does not hurry, yet everything is accomplished.", "Lao Tzu"),
    Quote("Drop by drop is the water pot filled.", "Buddha"),
    Quote("A rolling stone gathers no moss.", "Publilius Syrus"),
    Quote("Little by little, the bird builds its nest.", "French proverb"),
    Quote("Well done is better than well said.", "Benjamin Franklin"),
    Quote("Adopt the pace of nature: her secret is patience.", "Emerson"),
    Quote("The struggle itself toward the heights is enough to fill a man's heart.", "Albert Camus"),
    Quote("His fate belongs to him. His rock is his thing.", "Albert Camus"),
    Quote("Happiness and the absurd are two sons of the same earth.", "Albert Camus"),
    Quote("In the midst of winter, I found there was, within me, an invincible summer.", "Albert Camus"),
    Quote("Give me a place to stand, and I will move the earth.", "Archimedes"),
    Quote("The gods help them that help themselves.", "Aesop"),
    Quote("Put your shoulder to the wheel.", "Aesop"),
    Quote("Confine yourself to the present.", "Marcus Aurelius"),
    Quote("If it's endurable, then endure it. Stop complaining.", "Marcus Aurelius"),
    Quote("Begin at once to live, and count each separate day as a separate life.", "Seneca"),
    Quote("No man is free who is not master of himself.", "Epictetus"),
    Quote("We are what we repeatedly do.", "Will Durant"),
    Quote("Fortune favors the bold.", "Virgil"),
    Quote("Dare to be wise. Begin!", "Horace"),
    Quote("Life grants nothing to us mortals without hard work.", "Horace"),
    Quote("Practice is the best of all instructors.", "Publilius Syrus"),
    Quote("Anyone can hold the helm when the sea is calm.", "Publilius Syrus"),
    Quote("The higher we are placed, the more humbly we should walk.", "Cicero"),
    Quote("A sound mind in a sound body.", "Juvenal"),
    Quote("What does not kill me makes me stronger.", "Nietzsche"),
    Quote("Life can only be understood backwards; but it must be lived forwards.", "Kierkegaard"),
    Quote("The man who moves a mountain begins by carrying away small stones.", "Chinese proverb"),
    Quote("Chop wood, carry water.", "Zen saying"),
    Quote("He who conquers others is strong; he who conquers himself is mighty.", "Lao Tzu"),
    Quote("Our greatest glory is not in never falling, but in rising every time we fall.", "Oliver Goldsmith"),
    Quote("Little strokes fell great oaks.", "Benjamin Franklin"),
    Quote("The best way out is always through.", "Robert Frost"),
    Quote("Great things are done when men and mountains meet.", "William Blake"),
    Quote("Because it's there.", "George Mallory"),
    Quote("It is always darkest just before the dawn.", "Thomas Fuller"),
    Quote("Patience is a tree whose root is bitter, but its fruit is very sweet.", "Persian proverb"),
    Quote("Even monkeys fall from trees.", "Japanese proverb"),
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
    Quote("Uphill is just a direction.", "Sisyphus"),
    Quote("I have pushed this rock so long, we are on first-name terms.", "Sisyphus"),
    Quote("Every step forward is one the hill cannot take back.", "Sisyphus"),
    Quote("My legs are tired. My spirit is not.", "Sisyphus"),
    Quote("Some call it punishment. I call it cardio.", "Sisyphus"),
    Quote("The rock is the same. The morning is new.", "Sisyphus"),
    Quote("I stopped counting steps. Now I count sunsets.", "Sisyphus"),
    Quote("Push like nobody is watching. Nobody is.", "Sisyphus"),
    Quote("Gravity and I have an understanding.", "Sisyphus"),
    Quote("The top would only make me lazy.", "Sisyphus"),
    Quote("Today the rock felt lighter. Or I got stronger.", "Sisyphus"),
    Quote("Breathe in the hill. Breathe out the doubt.", "Sisyphus"),
    Quote("Turn the knob any way you like. I only know up.", "Sisyphus"),
    Quote("Uphill both ways. Every single day.", "Sisyphus"),
    Quote("I chained Death once. A hill does not scare me.", "Sisyphus"),
    Quote("Merope, I will be home late. Again.", "Sisyphus"),
    Quote("I thought I was the clever one. Then I met this hill.", "Sisyphus"),
    Quote("My calves have calves.", "Sisyphus"),
    Quote("I am not heavy. You are just early.", "the Rock"),
    Quote("See you tomorrow, same place.", "the Rock"),
    Quote("We are in this together.", "the Rock"),
    Quote("Roll with it.", "the Rock"),
    Quote("I don't roll back to spite you. It's just physics.", "the Rock"),
    Quote("You push, I roll. Teamwork.", "the Rock"),
    Quote("I was a mountain once. Then I let myself go.", "the Rock"),
    Quote("We have been through a lot together. Mostly grass.", "the Rock"),
    Quote("Mind the moss.", "the Rock"),
    Quote("I will be here tomorrow.", "the Hill"),
    Quote("Nobody said it was downhill.", "the Hill"),
    Quote("I am not against you. I am just steep.", "the Hill"),
    Quote("Take your time. I'm not going anywhere.", "the Hill"),
    Quote("Watch your step. Actually, watch all of them.", "the Hill"),
    Quote("I rise every morning. Nobody has to push me.", "the Sun"),
    Quote("I see you every day. You are getting stronger.", "the Sun"),
    Quote("I come back every night, and nobody calls it punishment.", "the Moon"),
    Quote("Even I have phases.", "the Moon"),
    Quote("I meant it as a punishment. You made it look like a dance.", "Zeus"),
    Quote("I gave you a rock. You made it a hobby.", "Hades"),
    Quote("I guided many souls. Yours was the stubborn one.", "Hermes"),
    Quote("Up here, the view is the reward.", "a Goat"),
    Quote("From up here, you are doing great.", "a Bird"),
    Quote("What goes up must come down. Nothing personal.", "Gravity"),
    Quote("Bend, don't break.", "the Olive Tree"),
    Quote("I push the clouds all day. Nobody writes songs about me.", "the Wind"),
    Quote("Rest a moment. I have been resting for centuries.", "the Column"),
    Quote("Standing still is harder than it looks.", "the Column"),
    Quote("Slow growth, deep roots.", "the Cypress"),
    Quote("I fall too. Nobody calls it failure.", "the Rain"),
    Quote("I go back and forth all day. Ask me about progress.", "the Sea"),
)

/**
 * The wise words under the display. A quote is typed out, stays for a while,
 * and dissolves once Sisyphus has pushed far enough. Then the next one comes.
 * The order is a shuffle made from [seed], so [drawn] alone tells where we are:
 * a restarted game shows the same quote again.
 */
class QuoteBox(private val seed: Int, drawn: Int, firstTime: Boolean) {
    enum class Phase { TYPING, SHOWING, FADING }

    /** How many quotes have come so far. */
    var drawn = drawn
        private set
    private val random = Random(seed xor drawn)

    var quote: Quote
        private set
    var phase: Phase
        private set
    /** Revealed characters: the text first, then the author. */
    var typed: Double
        private set
    /** 0 is fully visible, 1 is gone. */
    var fade = 0f
        private set

    private var delay = 0.8
    private var shown = 0.0
    private var budget = 0.0

    init {
        val resume = !firstTime && drawn > 0
        quote = when {
            firstTime -> TUTORIAL
            resume -> quoteAt(drawn - 1)
            else -> next()
        }
        phase = if (resume) Phase.SHOWING else Phase.TYPING
        typed = if (resume) length.toDouble() else 0.0
        if (resume) budget = newBudget()
    }

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
                    budget = if (quote === TUTORIAL) PX_PER_TURN else newBudget()
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

    private fun next(): Quote = quoteAt(drawn++)

    /** Every round shows each quote once, in a new order. */
    private fun quoteAt(k: Int): Quote {
        val order = QUOTES.indices.shuffled(Random(seed + k / QUOTES.size * 7919))
        return QUOTES[order[k % QUOTES.size]]
    }

    private fun newBudget() = random.nextDouble(3.0, 7.0) * PX_PER_TURN

    private companion object {
        const val TYPE_SPEED = 26.0
        const val FADE_TIME = 0.7
    }
}
