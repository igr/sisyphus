package dev.oblac.sisyphus.ui

import dev.oblac.sisyphus.game.Line
import dev.oblac.sisyphus.gfx.rgb
import dev.oblac.sisyphus.gfx.sprite

/** Kaguya, the moon princess, with the full moon behind her. */
private val KAGUYA = sprite(
    SKIN + mapOf('M' to rgb(0xF8F0C0), 'h' to rgb(0x1E1418), 'r' to rgb(0xC83A4A), 'p' to rgb(0xD8506A), 'P' to rgb(0xA03048)),
    """
    ....MMMMMMMM....
    ..MMMMMMMMMMMM..
    .MMMMhhhhhhMMMM.
    .MMMhhhhhhhhMMM.
    MMMhhkkkkkkhhMMM
    MMMhkekkkkekhMMM
    MMMhkkkkkkkkhMMM
    MMMhkkkrrkkkhMMM
    .MMhhkkkkkkhhMM.
    .MMhhhkkkkhhhMM.
    ..MhhhpppphhhM..
    ...hhppPPpphh...
    ...hpppPPppph...
    ..hpppppPpppph..
    ..pppppPPppppp..
    .ppppppPPpppppp.
    """,
)

/** En no Gyoja, the holy man of the mountains, with his staff of rings, walking on the waves. */
private val GYOJA = sprite(
    SKIN + mapOf(
        'y' to rgb(0xE0B040), 't' to rgb(0x7A5A3A), 'b' to rgb(0x1E1A1A), 'w' to rgb(0xF4F0E8), 'W' to rgb(0xC8C0B4),
        'a' to rgb(0x5A8AC8), 'A' to rgb(0x3A6AA8),
    ),
    """
    .yty............
    y.t.y...bb......
    .yty...bbbb.....
    ..t...kkkkkk....
    ..t..kkekkekk...
    ..t..kkkkkkkk...
    ..t..kwwwwwwk...
    ..t...wwwwww....
    ..tk.WWwwwwWW...
    ..tkWWwwwwwwWW..
    ..t.WwwwwwwwwW..
    ..t.WwwwwwwwwW..
    ..t..WWwwwwWW...
    .aAaAaAaAaAaAaA.
    aAaAaAaAaAaAaAaA
    ................
    """,
)

/** Kintaro, the golden boy, in his red bib, with his axe on his shoulder. */
private val KINTARO = sprite(
    SKIN + mapOf(
        'k' to rgb(0xE89070), 'h' to rgb(0x1E1418), 'r' to rgb(0xC8302A), 'y' to rgb(0xE8C040), 's' to rgb(0xB8BCC4), 't' to rgb(0x7A5A3A),
    ),
    """
    ............tt..
    ..........ssstt.
    ..hhhhhh..ssst..
    .hhhhhhhh.sst...
    .hhkkkkhh..t....
    .hkekkekh.t.....
    .hkkkkkkh.t.....
    ..kkmmkk.t......
    ...kkkkkt.......
    ..rrrrrrkk......
    .krryyrrk.......
    .krryyrrk.......
    ..rrrrrr........
    ..kk..kk........
    ..kk..kk........
    .kkk..kkk.......
    """,
)

/** A tengu: a red face with a long nose and white brows, and the small black cap of the mountain monks. */
private val TENGU = sprite(
    mapOf('r' to rgb(0xC8302A), 'R' to rgb(0x8A1E1A), 'w' to rgb(0xF4F0E8), 'k' to rgb(0x1E1418), 'e' to rgb(0xF0D060), 'b' to rgb(0x1E1A1A)),
    """
    ......bb........
    .....bbbb.......
    ...rrrrrrrr.....
    ..rrrrrrrrrr....
    ..rwwrrrwwrr....
    ..rkerrrkerr....
    ..rrrrrrrrrrrrrr
    ..rrrrrrrrrrrrrr
    ..rrRrrrrrrr....
    ..rrrRRRRrrr....
    ...rrrrrrrr.....
    ...wwwwwwww.....
    ....wwwwww......
    .....wwww.......
    ................
    ................
    """,
)

/** Fujin, the green god of the wind, under his bag of winds, with the wind blowing out. */
private val FUJIN = sprite(
    mapOf(
        'w' to rgb(0xF4F0E8), 'W' to rgb(0xB8B8C8), 'g' to rgb(0x4E9A5A), 'G' to rgb(0x6AB870), 'e' to rgb(0x1E1418), 'm' to rgb(0x8A1E1A),
        'a' to rgb(0x8AC0E8),
    ),
    """
    ..wwwwwwwwwwww..
    .wWwwwwwwwwwWww.
    wWw..........wWw
    ww....gggg....ww
    w....gGGGGg....w
    ....gGeGGeGg....
    ....gGGGGGGg....
    .....gGmmGg.....
    ......gggg......
    ....ggggggg.....
    ...gg.gggg.gg...
    ..aa..gggg..aa..
    .a..a.g..g.a..a.
    ....a.g..g.a....
    ...a..gg.gg..a..
    ................
    """,
)

/** The rock that Izanagi set in the way out of the land of the dead. */
private val YOMI_ROCK = sprite(
    mapOf(
        'k' to rgb(0x14101A), 'K' to rgb(0x3A3640), 's' to rgb(0x8A8680), 'S' to rgb(0x5E5A56), 'g' to rgb(0x6A8A4A), 'G' to rgb(0x4A6A34),
    ),
    """
    ................
    .....kkkkkk.....
    ...kkkkkkkkkk...
    ..kkkKKKKKKkkk..
    .kkKKssssssKKkk.
    .kKssSSssssssKk.
    kkKsSssssssSsKkk
    kKssssssssssssKk
    kKsSssssssssSsKk
    kKssssssSsssssKk
    kKsssSssssssssKk
    kKssssssssssssKk
    kKsssssssSssssKk
    ggssssssssssssgg
    gggggggggggggggg
    GGGGGGGGGGGGGGGG
    """,
)

/** A bowl of rice, with chopsticks. */
private val RICE_BOWL = sprite(
    mapOf('w' to rgb(0xFFFFF6), 'W' to rgb(0xE0DCD0), 't' to rgb(0x8A5A3A), 'b' to rgb(0x2E5AA8), 'B' to rgb(0xE8ECF4)),
    """
    ................
    ..........tt....
    .........tt.....
    ....wwwwttww....
    ..wwwwwttwwwww..
    .wwWwwtwwwwWwww.
    .wwwwwwwwwwwwww.
    bbbbbbbbbbbbbbbb
    .bBbbbbbbbbbbBb.
    .bbbbBbbbbBbbbb.
    ..bbbbbbbbbbbb..
    ...bbbbbbbbbb...
    .....bbbbbb.....
    .....BBBBBB.....
    ................
    ................
    """,
)

/** The lucky cat, a paw raised, with a bell and a gold coin. */
private val LUCKY_CAT = sprite(
    mapOf(
        'w' to rgb(0xFAF8F2), 'W' to rgb(0xC8C4BC), 'p' to rgb(0xF0A0A8), 'k' to rgb(0x1E1418), 'r' to rgb(0xC8302A), 'y' to rgb(0xE8C040),
        'O' to rgb(0xB08A2A),
    ),
    """
    .ww.............
    wwww..w.....w...
    wwww.wpw...wpw..
    .ww..wwwwwwwww..
    .ww.wwwwwwwwwww.
    .wwwwwkwwwwkwwww
    ..wwwwwwwpwwwwww
    ...wwwwwwwwwwww.
    ....rrrrrrrrrr..
    ....wwwwyywwww..
    ...wwwwwwwwwwww.
    ...wwwyyyyywwww.
    ...wwwyOOOywwww.
    ...wwwyyyyywwww.
    ...wwwwwwwwwwww.
    ....WWWWWWWWWW..
    """,
)

/** The magic mallet of Daikoku, in gold, with a red tassel. */
private val MALLET = sprite(
    mapOf('y' to rgb(0xE8C040), 'Y' to rgb(0xB08A2A), 'r' to rgb(0xC8302A), 't' to rgb(0x8A5A3A)),
    """
    ................
    ..yyyyyyyy......
    .yYYYYYYYYy.....
    yYyyyyyyyyYy....
    yYyrryyyyyYy....
    yYyrryyyyyYy....
    yYyyyyyyyyYy....
    .yYYYYYYYYy.....
    ..yyyyyyyy......
    .....tt.........
    ......tt........
    .......tt.......
    ........tt......
    .........tt..r..
    ..........ttrr..
    ...........rrr..
    """,
)

/** A koi, red and white, over the ripples. */
private val KOI = sprite(
    mapOf('o' to rgb(0xF06A2A), 'w' to rgb(0xFFF4E8), 'e' to rgb(0x1E1418), 'a' to rgb(0x6A9AD0)),
    """
    ................
    ................
    ................
    ......ooooo.....
    ...ooowwwwooo..o
    ..owwwwoooowwooo
    .oewwwoooooowoo.
    .owwwwwooooooooo
    ..oowwwoooooo..o
    ....ooooooo.....
    ......oo........
    ................
    .aaa......aaaa..
    a...aaaaaa....aa
    ................
    ................
    """,
)

/** The head of the dragon, with its red mane and gold whiskers. */
private val DRAGON = sprite(
    mapOf('g' to rgb(0x3A9A6A), 'r' to rgb(0xD8402E), 'y' to rgb(0xE8C050), 'w' to rgb(0xFFF8E0), 'k' to rgb(0x1E1418), 'R' to rgb(0x8A1E1A)),
    """
    ........rr.rr...
    .......rrrrrrr..
    ..yy..rggggggrr.
    ...yy.gggggggrr.
    ....ggwkgggggrr.
    ..gggggggggggr..
    .ggggggggggggr..
    gggwwgggggggg...
    .gggRRRRggggg...
    ..gggggggggggg..
    ...yyyyyggggggg.
    ....yyy...gggggg
    ..........ggggg.
    ...........gggg.
    ............ggg.
    .............gg.
    """,
)

/** Fireflies over the grass at night. */
private val FIREFLIES = sprite(
    mapOf('n' to rgb(0x141C44), 'y' to rgb(0xF4FFA0), 'Y' to rgb(0xA8C860), 'g' to rgb(0x2E5A2E), 'G' to rgb(0x1E3E22)),
    """
    nnnnnnnnnnnnnnnn
    nnnnnnnnnnynnnnn
    nnnynnnnnnnnnnnn
    nnnnnnnnnnnnnynn
    nnnnnnnynnnnnnnn
    nYynnnnnnnnnnnnn
    nnnnnnnnnnnYynnn
    nnnnnynnnnnnnnnn
    nnnnnnnnnnnnnnnn
    nnnnnnnnnnnynnnn
    nnynnnnnnnnnnnnn
    gnngnnngnnnngnng
    gggnggnggngggngg
    gGggggGgggGggggG
    GGgGGgGGgGGgGGgG
    GGGGGGGGGGGGGGGG
    """,
)

internal val FUJI_ICONS = mapOf(
    Line.SIDEKICKS to listOf(KAGUYA, GYOJA, KINTARO),
    Line.GODS to listOf(TENGU, FUJIN, YOMI_ROCK),
    Line.BLESSING to listOf(RICE_BOWL, LUCKY_CAT, MALLET),
    Line.BEAUTY to listOf(KOI, DRAGON, FIREFLIES),
)
