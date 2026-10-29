package dev.oblac.sisyphus.game

import kotlin.math.roundToInt
import kotlin.random.Random

/** A joke or a riddle of a joker: its [texts] show one after the other, in a bubble over him. A riddle shows its answer second. */
class Joke(vararg val texts: String)

/**
 * A famous figure of the public domain, who stands by the path and tells his [jokes] in turn. His [id] never changes: saved
 * games keep it. He comes [weight] times in [JOKER_WEIGHTS], and [about] tells who he is on his card.
 * See `docs/jokers.md`.
 */
class Joker(val id: Int, val name: String, val weight: Int, val about: String, val jokes: List<Joke>)

/** The jokers, in the order of their ids, which is the order of their cards. A new joker comes last. */
val JOKERS = listOf(
    Joker(
        0, "Diogenes", 227,
        "The Cynic of Corinth. He lived in a jar, and with a lamp by day he looked for an honest man.",
        listOf(
            Joke("I carry a lamp at noon. I am looking for an honest man.", "You push a rock, and you say that it is a rock. You may be the one."),
            Joke("Alexander the Great asked me what he could do for me.", "I said: Stand out of my sun."),
            Joke("Plato said: a man is an animal on two legs, with no feathers.", "So I brought him a plucked chicken: Behold, Plato's man!"),
            Joke("I ask the statues for money.", "Why? To get used to no."),
            Joke("I threw away my cup when I saw a boy drink from his hands.", "You cannot throw away your rock. It always comes back."),
            Joke("Which wine do I like best?", "Someone else's."),
            Joke("When should a man eat?", "If he is rich, when he wants. If he is poor, when he can."),
            Joke("I live in a jar. It is round, like your rock.", "Mine does not roll. I made sure of that."),
            Joke("Where am I from? I am a citizen of the world.", "Most of the world is flat. Your part of it is not."),
            Joke("They call me the Dog, because I bark at fools.", "Do not worry. At you, I wag."),
        ),
    ),
    Joker(
        1, "Humpty Dumpty", 124,
        "An egg from an old riddle, who talks to Alice in Through the Looking-Glass by Lewis Carroll, 1871.",
        listOf(
            Joke("My words mean just what I choose them to mean.", "Today, rock means feather. There. Lighter already?"),
            Joke("Riddle: what must be broken before you can use it?", "An egg. Please do not look at me like that."),
            Joke("Riddle: what can you break without touching it?", "A promise. Eggs need touching. Do not touch me."),
            Joke("Riddle: the more you take, the more you leave behind.", "Footsteps. You leave a great many."),
            Joke("Riddle: what goes up and never comes down?", "Your age. Not your rock. That one always comes down."),
            Joke("Today is my unbirthday. You have one most days too.", "Happy unbirthday. My present: I will not sit on your rock."),
            Joke("I sit on walls. You push rocks.", "We must both take care not to fall. Shake hands? Gently."),
            Joke("Riddle: what has a head and a tail, but no body?", "A coin. Not an egg. An egg is all body. Ahem."),
        ),
    ),
    Joker(
        2, "Red Riding Hood", 62,
        "A girl who met a wolf on the way to her grandmother, in the tale of Charles Perrault, 1697.",
        listOf(
            Joke("Is this the way to my grandmother's house?", "Past the man with the rock? Oh, that is you. Thank you!"),
            Joke("Grandmother, what big ears you have!", "That was the wolf. Now I look at the ears first."),
            Joke("What do wolves say when they meet?", "Howl do you do?"),
            Joke("I have cake and wine for my grandmother.", "Here, have a crumb. Pushing is hungry work."),
            Joke("My mother said: do not leave the path.", "You never leave the path. She would like you."),
            Joke("Riddle: what is red, has a basket, and is never late?", "Me. Well, once. There was a wolf."),
        ),
    ),
    Joker(
        3, "Don Quixote", 31,
        "A knight who fought windmills as giants, in the book of Miguel de Cervantes, 1605.",
        listOf(
            Joke("Halt! Is that a giant that you push?", "Ah, a rock. Carry on, good knight."),
            Joke("I once fought a giant with four long arms.", "Sancho says it was a windmill. Sancho sees too little."),
            Joke("A knight needs a quest. Yours is a fine one.", "Up, and up again. I will sing of it to Dulcinea."),
            Joke("My helmet is the golden helmet of Mambrino.", "A barber says it is his basin. Barbers know nothing."),
            Joke("Riddle: four arms, no hands, and it turns all day.", "A windmill. Or a giant. It depends on who you ask."),
        ),
    ),
    Joker(
        4, "Pinocchio", 15,
        "A wooden puppet whose nose grows when he lies, in the book of Carlo Collodi, 1883.",
        listOf(
            Joke("I am not made of wood.", "Oops. My nose. Please look away."),
            Joke("Your rock looks very light today.", "There it goes again. Sorry."),
            Joke("Why do I always lose at hide and seek?", "My nose is always the first to be found."),
            Joke("Riddle: what grows when it tells a lie?", "My nose. Too easy? That is the truth. Look, no change."),
        ),
    ),
    Joker(
        5, "Cleopatra", 8,
        "The last queen of Egypt, the friend of Caesar and of Mark Antony.",
        listOf(
            Joke("I came to Caesar rolled up in a carpet.", "Try it with your rock. Make an entrance."),
            Joke("I once melted a pearl in vinegar, and drank it, to win a bet.", "Never bet against a queen."),
            Joke("Why could I never see my mistakes?", "I was the queen of the Nile. And of denial."),
        ),
    ),
    Joker(
        6, "Sherlock Holmes", 4,
        "A detective of Baker Street, London, who sees what others miss. Arthur Conan Doyle, 1887.",
        listOf(
            Joke("I perceive that you push a rock uphill.", "How? You are pushing a rock uphill. Some cases are simple."),
            Joke("Rule out the impossible, and what is left is the truth.", "So: you will push it again tomorrow."),
            Joke("Riddle: the more of it there is, the less you see.", "The dark. Elementary."),
        ),
    ),
    Joker(
        7, "Hamlet", 3,
        "The prince of Denmark, with the skull of the jester Yorick. William Shakespeare, about 1600.",
        listOf(
            Joke("Alas, poor Yorick! I knew him well.", "He told the best jokes. Now his head is the joke."),
            Joke("To push, or not to push: that is the question.", "You always give the same answer. I envy you."),
            Joke("Who builds stronger than a mason, a shipwright or a carpenter?", "The gravedigger. His houses last till doomsday."),
        ),
    ),
    Joker(
        8, "Count Dracula", 2,
        "A count of Transylvania who sleeps by day and drinks blood by night. Bram Stoker, 1897.",
        listOf(
            Joke("Good evening. I came out in the sun to meet you.", "Very brave of me. Also, I am on fire a little. Goodbye!"),
            Joke("What is the favorite fruit of a vampire?", "A neck-tarine."),
        ),
    ),
    Joker(
        9, "Cthulhu", 1,
        "A great old one who sleeps, dreaming, in his sunken city of R'lyeh. H. P. Lovecraft, 1928.",
        listOf(
            Joke("Ph'nglui mglw'nafh Cthulhu R'lyeh wgah'nagl fhtagn.", "It means: nice rock. Roughly."),
            Joke("I sleep for ages. Then I wake. Then I sleep again.", "You push, it rolls down, you push again. We should talk."),
        ),
    ),
    Joker(
        10, "Isaac Newton", 15,
        "An English scholar who found the law of gravity: why an apple falls, and why the moon goes around the Earth. 1687.",
        listOf(
            Joke("What goes up must come down.", "You knew that long before I wrote it down."),
            Joke("A body at rest stays at rest, unless a force moves it.", "You are the force. Your rock is the body. Push on."),
            Joke("I stood on the shoulders of giants.", "You stand on a hill. That works too."),
            Joke("Riddle: what weighs more, a pound of rock or of feathers?", "Neither. But the feathers are kinder to push uphill."),
        ),
    ),
    Joker(
        11, "Scheherazade", 8,
        "Each night she told the king a tale, and stopped at dawn, so that he let her live one more day. The Arabian Nights.",
        listOf(
            Joke("Each night I tell a tale, and I stop at the best part.", "You do the same with your rock. Tomorrow we go on."),
            Joke("Shall I tell you the tale of a man who pushes a rock?", "Ah, you know it. It is the one that never ends."),
            Joke("Riddle: the better you tell it, the shorter the night.", "A story. Yours is very long."),
        ),
    ),
)

/** The weights of all jokers together: a joker comes [Joker.weight] times in this many. */
const val JOKER_WEIGHTS = 500

/** A joker comes once in about this many jokers, such as 8 for 1 in 8. */
val Joker.chance get() = (JOKER_WEIGHTS.toDouble() / weight).roundToInt()

/** A joker at random, each as often as his weight says. */
fun pickJoker(random: Random): Joker {
    var n = random.nextInt(JOKER_WEIGHTS)
    for (joker in JOKERS) {
        n -= joker.weight
        if (n < 0) return joker
    }
    return JOKERS.last()
}
