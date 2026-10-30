package dev.oblac.sisyphus.ui

import dev.oblac.sisyphus.game.Category
import dev.oblac.sisyphus.game.JOKERS
import dev.oblac.sisyphus.game.LABORS
import dev.oblac.sisyphus.game.TALES
import dev.oblac.sisyphus.gfx.PixelFont
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class Rect(val x: Int, val y: Int, val w: Int, val h: Int) {
    val right get() = x + w
    val bottom get() = y + h
    fun inset(d: Int) = Rect(x + d, y + d, w - 2 * d, h - 2 * d)
    fun offset(dx: Int, dy: Int) = Rect(x + dx, y + dy, w, h)
}

/**
 * Splits the screen, in virtual pixels: the display takes the top half,
 * the wise words come below it, and the knob takes the rest.
 * The Agora, the shop, covers all of it when open.
 */
class Layout(val width: Int, val height: Int, safeTop: Int, safeBottom: Int, safeLeft: Int, safeRight: Int) {
    private val left = safeLeft + 4
    private val right = width - safeRight - 4
    private val top = safeTop + 3
    private val bottom = height - safeBottom - 3

    val display = Rect(left, top, right - left, ((bottom - top) * 0.5).roundToInt())
    val scene = display.inset(BEZEL)
    val band = Rect(left, display.bottom + 4, right - left, BAND_HEIGHT)
    val quote = Rect(left + 2, band.bottom + 4, right - left - 4, PixelFont.LINE_HEIGHT * 4)

    /**
     * The obols, in the top-right corner of the display across from the day. They open and close the Agora,
     * and stay in the same place there. The Worlds screen shows no obols.
     */
    val coinIcon = (COIN_SIZE + 2 + PixelFont.width("9999")).let { w -> Rect(scene.right - 4 - w, scene.y + 4, w, COIN_SIZE) }

    /** In the middle of the display's top row, between the day and the obols, the Greek key that turns bronze up to the next obol. */
    val obolBar = Rect(scene.x + (scene.w - OBOL_BAR) / 2, coinIcon.y, OBOL_BAR, COIN_SIZE)

    val agoraTitle = Rect(left + 2, coinIcon.y, coinIcon.right - left - 2, PixelFont.LINE_HEIGHT)

    /** After the title of the Agora, how many of its items are bought, such as "3/15". */
    val agoraCount = Rect(agoraTitle.x + PixelFont.width(AGORA_TITLE) + 6, agoraTitle.y, PixelFont.width("15/15"), PixelFont.CAP_HEIGHT)

    /**
     * In the display's bottom-right corner, the sidekick, the god and the blessing bought so far, outlined, in that order:
     * [slot] 0 is the corner, 1 is next to it, 2 is next to that.
     */
    fun helper(slot: Int) = Rect(scene.right - 3 - (slot + 1) * HELPER - slot * 2, scene.bottom - 3 - HELPER, HELPER, HELPER)

    /** In the bottom-left corner of the main screen, "(?)" opens the help. */
    val helpIcon = Rect(left + 2, bottom - SPEAKER_HEIGHT - 1, PixelFont.width("(?)"), PixelFont.CAP_HEIGHT)

    /** In the bottom-right corner of the main screen, across from the help, the gear opens the settings. */
    val settingsIcon = Rect(coinIcon.right - GEAR, helpIcon.y, GEAR, GEAR)

    /** Where the scene draws "DAY n" in its top-left corner, outline included. */
    fun dayLabel(day: Int) = Rect(scene.x + 3, scene.y + 3, PixelFont.width("DAY $day") + 2, PixelFont.CAP_HEIGHT + 2)

    /** In the Agora, the speaker in the bottom-right corner turns the sound on and off. */
    val agoraSound = Rect(coinIcon.right - SPEAKER_WIDTH, bottom - SPEAKER_HEIGHT - 1, SPEAKER_WIDTH, SPEAKER_HEIGHT)

    /** At the bottom of the Agora, across from the speaker, a small Sisyphus and the note that all 15 items lead to the top. */
    val agoraNote = Rect(agoraTitle.x, agoraSound.y, agoraSound.x - 2 - agoraTitle.x, PixelFont.CAP_HEIGHT)

    /** One of the five lines of the Agora, from the top. */
    fun agoraRow(index: Int): Rect {
        val y0 = agoraTitle.bottom + 6
        val h = (agoraSound.y - 6 - y0) / 5
        return Rect(agoraTitle.x, y0 + index * h, agoraTitle.w, h)
    }

    /** In the dev screen, the button for one of [DEV_STEPS], below the "Obols" title. */
    fun devStep(index: Int): Rect {
        val r = agoraRow(0)
        val w = (r.w - 2 * (DEV_STEPS.size - 1)) / DEV_STEPS.size
        return Rect(r.x + index * (w + 2), r.y + PixelFont.LINE_HEIGHT, w, BUTTON_HEIGHT)
    }

    /** In the dev screen, the button that empties the shop, below the "Shop" title. */
    val devResetShop = agoraRow(1).let { Rect(it.x, it.y + PixelFont.LINE_HEIGHT, PixelFont.width("Reset") + 8, BUTTON_HEIGHT) }

    /** In the dev screen, below the "Climb" title, the button that starts the climb again: day 1, no items and no obols. */
    val devResetClimb = agoraRow(2).let { Rect(it.x, it.y + PixelFont.LINE_HEIGHT, PixelFont.width("Reset") + 8, BUTTON_HEIGHT) }

    /** In the dev screen, the button that takes him to a few clicks of the knob before the next obol. */
    val devNextObol = Rect(devResetClimb.right + 2, devResetClimb.y, PixelFont.width("Obol") + 8, BUTTON_HEIGHT)

    /** In the dev screen, the button that buys everything and takes him to two turns below the top. */
    val devTop = Rect(devNextObol.right + 2, devResetClimb.y, PixelFont.width("Top") + 8, BUTTON_HEIGHT)

    /** In the dev screen, after Top, the button that sets the next stone of the tale just ahead, so that its popup shows. */
    val devStone = Rect(devTop.right + 2, devResetClimb.y, PixelFont.width("Stone") + 8, BUTTON_HEIGHT)

    /** In the dev screen, the button that opens every world without the store, and gives the store's answer back. */
    val devWorlds = agoraRow(3).let { Rect(it.x, it.y + PixelFont.LINE_HEIGHT, PixelFont.width("As bought") + 8, BUTTON_HEIGHT) }

    /** In the dev screen, after the worlds, the button that brings the next joker, a turn into his bubble. */
    val devJoker = Rect(devWorlds.right + 2, devWorlds.y, PixelFont.width("Joker") + 8, BUTTON_HEIGHT)

    /** In the dev screen, under the voices, the button that brings a vision at once. */
    val devVision = agoraRow(4).let { Rect(it.x, it.y + PixelFont.LINE_HEIGHT, PixelFont.width("Now") + 8, BUTTON_HEIGHT) }

    /** In the dev screen, next to Now, the button that brings a vision at the next click of the knob. */
    val devNextVision = Rect(devVision.right + 2, devVision.y, PixelFont.width("Next turn") + 8, BUTTON_HEIGHT)

    /** In the dev screen, after Next turn, the Labor button that does the next labor of the knob turns at the next click of the knob. */
    val devNextLabor = Rect(devNextVision.right + 2, devVision.y, PixelFont.width("Labor") + 8, BUTTON_HEIGHT)

    private val offerHeight = 5 + OFFER_ICON + 5 + PixelFont.LINE_HEIGHT + 3 + OFFER_LINES * PixelFont.LINE_HEIGHT +
        4 + PixelFont.LINE_HEIGHT + 4 + BUTTON_HEIGHT + 5

    /** The offer: a window over the Agora that shows an item and asks whether to buy it. */
    val offer = Rect(agoraTitle.x, agoraTitle.bottom + (agoraSound.y - agoraTitle.bottom - offerHeight) / 2, agoraTitle.w, offerHeight)

    /** In the offer, the item's picture at twice its size. */
    val offerIcon = Rect(offer.x + (offer.w - OFFER_ICON) / 2, offer.y + 5, OFFER_ICON, OFFER_ICON)

    /** In the offer, the room for the item's details, below its name. */
    val offerText = Rect(offer.x + 5, offerIcon.bottom + 5 + PixelFont.LINE_HEIGHT + 3, offer.w - 10, OFFER_LINES * PixelFont.LINE_HEIGHT)

    val offerYes = Rect(offer.x + offer.w / 2 - 4 - OFFER_BUTTON, offer.bottom - 5 - BUTTON_HEIGHT, OFFER_BUTTON, BUTTON_HEIGHT)
    val offerNo = Rect(offer.x + offer.w / 2 + 4, offerYes.y, OFFER_BUTTON, BUTTON_HEIGHT)

    /** On the Worlds screen, the window that shows the list of worlds. The list scrolls when they do not fit. */
    val worldList = Rect(agoraTitle.x, agoraTitle.bottom + 6, agoraTitle.w, agoraSound.y - agoraTitle.bottom - 12)

    /** How far a list of [count] worlds can scroll. */
    fun worldScroll(count: Int) = max(0, count * (WORLD_ROW + 6) - 6 - worldList.h)

    /** One world in the list, before it scrolls: its picture, and its name and note below. Tapping it asks to go there. */
    fun worldRow(index: Int) = Rect(worldList.x, worldList.y + index * (WORLD_ROW + 6), worldList.w, WORLD_ROW)

    /** The picture of a world, in a frame, at the top of its row. */
    fun worldPreview(index: Int) = worldRow(index).let { Rect(it.x, it.y, it.w, WORLD_PREVIEW + 2) }

    /** The name and the note of a world, below its picture. */
    fun worldText(index: Int) = worldPreview(index).let { Rect(it.x, it.bottom + 3, it.w, 2 * PixelFont.LINE_HEIGHT) }

    /**
     * In the title row of the Worlds screen, its three tabs. The Worlds screen shows no obols, so they fit the smallest phone.
     * "Labors" and "Cards" have room for the dot after them.
     */
    val worldsTab = Rect(agoraTitle.x, agoraTitle.y, PixelFont.width(WORLDS_TITLE), PixelFont.CAP_HEIGHT)
    val laborsTab = Rect(worldsTab.right + 8, agoraTitle.y, PixelFont.width(LABORS_TITLE) + 5, PixelFont.CAP_HEIGHT)
    val cardsTab = Rect(laborsTab.right + 4, agoraTitle.y, PixelFont.width(CARDS_TITLE) + 5, PixelFont.CAP_HEIGHT)

    /** On the Cards tab, before the list scrolls, under the title row: "Jokers met", and how many of them the player has met, such as "3/10". */
    val jokersMet = Rect(worldList.x, worldList.y, worldList.w, PixelFont.CAP_HEIGHT)

    /** The cards of the jokers, by id: rows of [CARDS_IN_A_ROW], like a hand of playing cards. */
    val jokerCards: List<Rect> = ((worldList.w - (CARDS_IN_A_ROW - 1) * CARD_GAP) / CARDS_IN_A_ROW).let { w ->
        List(JOKERS.size) { i ->
            Rect(worldList.x + i % CARDS_IN_A_ROW * (w + CARD_GAP), jokersMet.bottom + 6 + i / CARDS_IN_A_ROW * (CARD_HEIGHT + 4), w, CARD_HEIGHT)
        }
    }

    /** Under the cards of the jokers: "Tales told", and how many of the tales the player has heard to the end, such as "3/14". */
    val talesTold = Rect(worldList.x, jokerCards.last().bottom + 8, worldList.w, PixelFont.CAP_HEIGHT)

    /** The cards of the tales, by id, under "Tales told", in the rows and columns of the cards of the jokers. */
    val taleCards: List<Rect> = List(TALES.size) { i ->
        Rect(jokerCards[i % CARDS_IN_A_ROW].x, talesTold.bottom + 6 + i / CARDS_IN_A_ROW * (CARD_HEIGHT + 4), jokerCards[0].w, CARD_HEIGHT)
    }

    /** How far the list of the cards can scroll, down to the last tale. */
    val cardScroll = max(0, taleCards.last().bottom - worldList.bottom)

    /** An open card, over the cards: the joker at twice his size, his name, his chance, who he is, and how often the player met him. */
    val openCard = Rect(
        worldList.x, jokerCards[0].y, worldList.w,
        6 + OPEN_SPRITE + 5 + 2 * PixelFont.LINE_HEIGHT + 3 + ABOUT_LINES * PixelFont.LINE_HEIGHT + 3 + PixelFont.LINE_HEIGHT + 4,
    )

    /** In the open card, the room for who he is, under his chance. */
    val openCardText = Rect(openCard.x + 6, openCard.y + 6 + OPEN_SPRITE + 5 + 2 * PixelFont.LINE_HEIGHT + 3, openCard.w - 12, ABOUT_LINES * PixelFont.LINE_HEIGHT)

    /** An open tale, over the cards, on a big stone: its relief, its name, one text, and the part of the tale between two arrows. */
    val openTale = Rect(
        worldList.x, jokerCards[0].y, worldList.w,
        6 + 7 * OPEN_RELIEF + 5 + PixelFont.LINE_HEIGHT + 4 + TALE_LINES * PixelFont.LINE_HEIGHT + 4 + PixelFont.LINE_HEIGHT + 3,
    )

    /** In the open tale, the room for its text, under its name. */
    val openTaleText = Rect(openTale.x + 6, openTale.y + 6 + 7 * OPEN_RELIEF + 5 + PixelFont.LINE_HEIGHT + 4, openTale.w - 12, TALE_LINES * PixelFont.LINE_HEIGHT)

    /** The width of the text in a row of the list of labors. */
    val laborText = worldList.w - 2 * LABOR_PAD

    /** In the list of labors, before it scrolls: the header of each category, and its card under it. */
    val laborHeaders: List<Rect>
    val laborCards: List<Rect>

    init {
        val headers = mutableListOf<Rect>()
        val cards = mutableListOf<Rect>()
        var y = worldList.y
        for (category in Category.entries) {
            if (headers.isNotEmpty()) y += 8
            headers += Rect(worldList.x, y, worldList.w, PixelFont.CAP_HEIGHT)
            y += PixelFont.LINE_HEIGHT
            cards += Rect(worldList.x, y, worldList.w, LABOR_CARD)
            y += LABOR_CARD
        }
        laborHeaders = headers
        laborCards = cards
    }

    /** How far the list of labors can scroll. */
    val laborScroll = max(0, laborCards.last().bottom - worldList.bottom)

    private val confirmHeight = 5 + PixelFont.LINE_HEIGHT + 4 + CONFIRM_LINES * PixelFont.LINE_HEIGHT + 4 + BUTTON_HEIGHT + 5

    /** The question before going to another world, in the middle of the Worlds screen. */
    val confirm = Rect(agoraTitle.x, agoraTitle.bottom + (agoraSound.y - agoraTitle.bottom - confirmHeight) / 2, agoraTitle.w, confirmHeight)

    /** In the question, the room for what going there means, below its title. */
    val confirmText = Rect(confirm.x + 5, confirm.y + 5 + PixelFont.LINE_HEIGHT + 4, confirm.w - 10, CONFIRM_LINES * PixelFont.LINE_HEIGHT)

    val confirmYes = Rect(confirm.x + confirm.w / 2 - 4 - OFFER_BUTTON, confirm.bottom - 5 - BUTTON_HEIGHT, OFFER_BUTTON, BUTTON_HEIGHT)
    val confirmNo = Rect(confirm.x + confirm.w / 2 + 4, confirmYes.y, OFFER_BUTTON, BUTTON_HEIGHT)

    /** In the bottom-left corner of the Worlds screen, the button that asks the store for the worlds bought before. */
    val restore = Rect(left + 2, agoraSound.y, PixelFont.width(RESTORE), PixelFont.CAP_HEIGHT)

    /** In the bottom-left corner of the settings, the link to the privacy policy. */
    val privacyLink = Rect(left + 2, agoraSound.y, PixelFont.width(PRIVACY), PixelFont.CAP_HEIGHT)

    /** In the bottom-right corner of the settings, the player's id for support and the build of the game. */
    val supportId = PixelFont.width("ID 3f9a [999]").let { w -> Rect(coinIcon.right - w, agoraSound.y, w, PixelFont.CAP_HEIGHT) }

    /** The switch of a row of the settings: wide enough for "Off". */
    private val switchWidth = PixelFont.width("Off") + 10

    /** One row of the settings, from the top: the name and the note of a sound at the left, and its switch at the right. */
    fun settingsRow(index: Int) = Rect(agoraTitle.x, agoraTitle.bottom + 6 + index * (SETTINGS_ROW + 8), agoraTitle.w, SETTINGS_ROW)

    /** The switch of a row of the settings, On or Off, at the right of its name. */
    fun settingsSwitch(index: Int) = settingsRow(index).let { Rect(it.right - switchWidth, it.y, switchWidth, BUTTON_HEIGHT) }

    /** The width of the name and the note of a row of the settings, before its switch. */
    val settingsText = agoraTitle.w - switchWidth - 4

    val knobX: Int
    val knobY: Int
    val knobR: Int

    init {
        val knobTop = quote.bottom + 3
        val room = bottom - knobTop
        knobR = min(room / 2 - 6, (right - left) / 2 - 14).coerceIn(14, 44)
        knobX = left + (right - left) / 2
        knobY = knobTop + room / 2
    }

    companion object {
        const val BEZEL = 3
        const val BAND_HEIGHT = 9
        const val SPEAKER_WIDTH = 11
        const val SPEAKER_HEIGHT = 7
        const val GEAR = 7
        /** A row of the settings: its name on the line of its switch, and two lines of its note under it. */
        const val SETTINGS_ROW = 3 + 3 * PixelFont.LINE_HEIGHT
        const val COIN_SIZE = 7
        /** The obol bar: four tiles of the Greek key, each 7 pixels wide. */
        const val OBOL_BAR = 28
        const val BUTTON_HEIGHT = 13
        const val OFFER_ICON = 32
        const val OFFER_LINES = 6
        const val CONFIRM_LINES = 4
        const val WORLD_PREVIEW = 42
        const val WORLD_ROW = WORLD_PREVIEW + 2 + 3 + 2 * PixelFont.LINE_HEIGHT
        const val OFFER_BUTTON = 30
        const val HELPER = 18
        const val LABOR_PAD = 3
        /** A card of the labors, from its top: the next labor at 3, its bar, the dots of the tiers, their goals, and the last labor done. */
        const val LABOR_CARD = 46
        const val LABOR_BAR = 14
        const val LABOR_DOTS = 19
        const val LABOR_TIERS = 26
        const val LABOR_DONE = 36
        const val CARD_GAP = 2
        const val CARDS_IN_A_ROW = 4
        /** A card of a joker, about as tall for its width as a playing card on the smallest phone: the outlined joker stands on its bottom. */
        const val CARD_HEIGHT = 39
        /** In the open card, the room for the outlined joker at twice his size, and for the lines of who he is. */
        const val OPEN_SPRITE = 52
        const val ABOUT_LINES = 5
        /** How many times bigger the relief of a tale is on its card, and on the open tale. */
        const val CARD_RELIEF = 3
        const val OPEN_RELIEF = 4
        /** In the open tale, the room for the lines of a text. */
        const val TALE_LINES = 5
    }
}

/** The tabs of the Worlds screen. */
enum class Tab { WORLDS, LABORS, CARDS }

/** The obols each button of the dev screen adds or takes away, from the left. */
val DEV_STEPS = listOf(-10, -1, 1, 10, 100)
