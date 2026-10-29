package dev.oblac.sisyphus.scene

import dev.oblac.sisyphus.gfx.Pixels
import dev.oblac.sisyphus.gfx.rgb
import dev.oblac.sisyphus.gfx.sprite

private val STONE_COLORS = mapOf(
    'w' to rgb(0xECE6D8),
    'W' to rgb(0xB8B0A0),
    'D' to rgb(0x5A4636),
    'p' to rgb(0xD8D0BE),
    'C' to rgb(0x9C9482),
)

private val RELIEF_COLORS = mapOf('c' to rgb(0xA8A08E), 'k' to rgb(0x7E7666))

/** A stone of the tale: pale limestone with a rounded top, a panel for its relief at (1, 1), and carved letters below. */
internal val TALE_STONE: Pixels = sprite(
    STONE_COLORS,
    """
    ..wwwwW..
    .wpppppW.
    wpppppppW
    wpppppppW
    wpppppppW
    wpppppppW
    wpppppppW
    wpppppppW
    wwwwwwwWW
    wCwCCwCwW
    wwwwwwwWW
    wCCwCwCCW
    wwwwwwwWW
    wwwwwwwWW
    WWWWWWWWD
    """,
)

/** The reliefs of the tale of Sisyphus, 7 by 7, carved on its stones in the order of the parts. */
internal val RELIEFS: List<Pixels> = listOf(
    // the old walls
    """
    .k.k.k.
    kkkkkkk
    k.k.k.k
    kkkkkkk
    kk...kk
    kk...kk
    kkkkkkk
    """,
    // the two harbors: a ship
    """
    ...k...
    ...kk..
    ...kck.
    ...kcck
    ...k...
    kkkkkkk
    .kkkkk.
    """,
    // the palace: a crown
    """
    .......
    k..k..k
    kk.k.kk
    kkkkkkk
    kckckck
    kkkkkkk
    .......
    """,
    // the cattle pen: a bull
    """
    k.....k
    kk...kk
    .kkkkk.
    .kckck.
    .kkkkk.
    ..kck..
    ..kkk..
    """,
    // the hoofprints: a hoof
    """
    .......
    .kk.kk.
    kkk.kkk
    kkk.kkk
    kkk.kkk
    .kk.kk.
    .......
    """,
    // the pine: a dolphin
    """
    ...kk..
    ..kkkkk
    .kk..kc
    kk.....
    k......
    kkk....
    k......
    """,
    // the stadium: a crown of pine
    """
    ..kck..
    .c...c.
    k.....k
    c.....c
    k.....k
    .c...c.
    ..k.k..
    """,
    // the cliff: an eagle
    """
    ...k...
    k.kkk.k
    kkkkkkk
    .kkkkk.
    ..kkk..
    ..k.k..
    .......
    """,
    // Peirene's spring: water
    """
    .......
    .k.k.k.
    k.k.k.k
    .......
    .k.k.k.
    k.k.k.k
    .......
    """,
    // the dark road: the wings of Death
    """
    k.....k
    kk...kk
    ckk.kkc
    .ckkkc.
    ..ckc..
    ...k...
    .......
    """,
    // the chains
    """
    kk.....
    k.k....
    .kkk...
    ..k.k..
    ...kkk.
    ....k.k
    .....kk
    """,
    // the battlefield: a helmet of Corinth
    """
    ..kkk..
    .kkkkk.
    kkkkkkk
    kk.k.kk
    kk...kk
    kk...kk
    k.....k
    """,
    // the empty grave
    """
    .......
    ...k...
    ...k...
    ..kck..
    .kccck.
    kcccccc
    kkkkkkk
    """,
    // the gate below: a pomegranate
    """
    ..k.k..
    ...k...
    .kkkkk.
    kkckckk
    kckckck
    .kkkkk.
    ..kkk..
    """,
    // this hill: the rock
    """
    .......
    ..kkk..
    .kcckk.
    .kcckk.
    ..kkk..
    ....kkk
    ..kkkkk
    """,
).map { sprite(RELIEF_COLORS, it) }
