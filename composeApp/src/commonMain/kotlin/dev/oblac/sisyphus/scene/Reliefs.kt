package dev.oblac.sisyphus.scene

/** The reliefs of the tales, 7 by 7, by the id of the tale, in the order of its parts: `k` is carved deep, `c` less deep. */
internal val RELIEF_ART: List<List<String>> = listOf(
    // The tale of Sisyphus
    listOf(
        // city walls
        """
        .k.k.k.
        kkkkkkk
        k.k.k.k
        kkkkkkk
        kk...kk
        kk...kk
        kkkkkkk
        """,
        // a ship
        """
        ...k...
        ...kk..
        ...kck.
        ...kcck
        ...k...
        kkkkkkk
        .kkkkk.
        """,
        // a crown
        """
        .......
        k..k..k
        kk.k.kk
        kkkkkkk
        kckckck
        kkkkkkk
        .......
        """,
        // a bull
        """
        k.....k
        kk...kk
        .kkkkk.
        .kckck.
        .kkkkk.
        ..kck..
        ..kkk..
        """,
        // a hoof
        """
        .......
        .kk.kk.
        kkk.kkk
        kkk.kkk
        kkk.kkk
        .kk.kk.
        .......
        """,
        // a dolphin
        """
        ...kk..
        ..kkkkk
        .kk..kc
        kk.....
        k......
        kkk....
        k......
        """,
        // a crown of pine
        """
        ..kck..
        .c...c.
        k.....k
        c.....c
        k.....k
        .c...c.
        ..k.k..
        """,
        // an eagle
        """
        ...k...
        k.kkk.k
        kkkkkkk
        .kkkkk.
        ..kkk..
        ..k.k..
        .......
        """,
        // water
        """
        .......
        .k.k.k.
        k.k.k.k
        .......
        .k.k.k.
        k.k.k.k
        .......
        """,
        // the wings of Death
        """
        k.....k
        kk...kk
        ckk.kkc
        .ckkkc.
        ..ckc..
        ...k...
        .......
        """,
        // chains
        """
        kk.....
        k.k....
        .kkk...
        ..k.k..
        ...kkk.
        ....k.k
        .....kk
        """,
        // a helmet of Corinth
        """
        ..kkk..
        .kkkkk.
        kkkkkkk
        kk.k.kk
        kk...kk
        kk...kk
        k.....k
        """,
        // a grave
        """
        .......
        ...k...
        ...k...
        ..kck..
        .kccck.
        kcccccc
        kkkkkkk
        """,
        // a pomegranate
        """
        ..k.k..
        ...k...
        .kkkkk.
        kkckckk
        kckckck
        .kkkkk.
        ..kkk..
        """,
        // a rock
        """
        .......
        ..kkk..
        .kcckk.
        .kcckk.
        ..kkk..
        ....kkk
        ..kkkkk
        """,
    ),
    // The tale of Typhon
    listOf(
        // a broken spear
        """
        .....kk
        ....kkk
        .....k.
        .......
        ..k....
        .k.....
        k......
        """,
        // a star
        """
        ...k...
        ...k...
        kkkkkkk
        .kkkkk.
        ..kkk..
        .kk.kk.
        .k...k.
        """,
        // a snake
        """
        ..kk...
        .kckk..
        .k.....
        ..kkk..
        .....k.
        .k...k.
        ..kkk..
        """,
        // a flame
        """
        ...k...
        ..kk...
        ..kkk..
        .kkckk.
        .kcckk.
        kkccckk
        .kkkkk.
        """,
        // a cat
        """
        k.k....
        kkk....
        kck....
        kkkk...
        .kkkk.k
        .kkkk.k
        .kkkkkk
        """,
        // lightning
        """
        ....kk.
        ...kk..
        ..kk...
        .kkkkk.
        ...kk..
        ..kk...
        .kk....
        """,
        // coils
        """
        kkkkkkk
        k.....k
        k.kkk.k
        k.k.k.k
        k.k...k
        k.kkkkk
        k......
        """,
        // a cave
        """
        .......
        ...k...
        ..kkk..
        .kkkkk.
        kk...kk
        kk...kk
        kkkkkkk
        """,
        // a winged sandal
        """
        k......
        kk.....
        kkk....
        .kk....
        .kkkkk.
        kkkkkkk
        .......
        """,
        // a winged horse
        """
        .k.....
        .kk..k.
        ..kkkkk
        .kkkkk.
        kkkkk..
        .k..k..
        .k..k..
        """,
        // a fruit
        """
        ...k...
        ...kk..
        .kkkkk.
        kkckkkk
        kckkkkk
        kkkkkkk
        .kkkkk.
        """,
        // a mountain
        """
        .......
        ...k...
        ..kkk..
        ..kkkk.
        .kkkkk.
        .kkkkkk
        kkkkkkk
        """,
        // a drop
        """
        ...k...
        ...k...
        ..kkk..
        .kkkkk.
        .kckkk.
        .kkkkk.
        ..kkk..
        """,
        // a wave
        """
        .......
        ..kkk..
        .k...k.
        k..kk.k
        k.k....
        kkkkkkk
        .......
        """,
        // a volcano
        """
        .k...k.
        ..k.k..
        ...k...
        ..kck..
        .kkckk.
        kkkkkkk
        kkkkkkk
        """,
    ),
    // The tale of Orpheus
    listOf(
        // a lyre
        """
        k.....k
        k.....k
        .k...k.
        .kkkkk.
        .k.k.k.
        .k.k.k.
        .kkkkk.
        """,
        // a ship
        """
        ...k...
        ...kk..
        ...kck.
        ...k...
        kkkkkkk
        .kkkkk.
        .k.k.k.
        """,
        // a torch
        """
        ...k...
        ..kck..
        ..kck..
        ...k...
        ..kkk..
        ...k...
        ...k...
        """,
        // a snake
        """
        ...kkk.
        ..k..kk
        ..k....
        ...kk..
        .....k.
        .....k.
        .kkkk..
        """,
        // a gate
        """
        kkkkkkk
        k.....k
        k.k.k.k
        k.k.k.k
        k.k.k.k
        k.k.k.k
        kkkkkkk
        """,
        // a dog with three heads
        """
        k.k.k..
        kkkkk..
        .kkk...
        .kkkkkk
        .kkkkk.
        .k...k.
        .k...k.
        """,
        // a throne
        """
        .kkkkk.
        .k...k.
        .k...k.
        kkkkkkk
        k.....k
        k.....k
        k.....k
        """,
        // an open hand
        """
        .k.k.k.
        .k.k.k.
        .kkkkk.
        kkkkkk.
        .kkkkk.
        ..kkkk.
        ..kkk..
        """,
        // a rock
        """
        .......
        ..kkk..
        .kcckk.
        .kcckk.
        ..kkk..
        ....kkk
        ..kkkkk
        """,
        // a footprint
        """
        .k.k.k.
        .......
        ..kkk..
        .kkkkk.
        .kkkkk.
        ..kkk..
        ..kk...
        """,
        // the sun
        """
        k..k..k
        .k.k.k.
        ..kkk..
        kkkckkk
        ..kkk..
        .k.k.k.
        k..k..k
        """,
        // steps
        """
        .....kk
        .....k.
        ...kkk.
        ...k...
        .kkk...
        .k.....
        kk.....
        """,
        // an eye
        """
        .......
        ..kkk..
        .k...k.
        k..k..k
        .k...k.
        ..kkk..
        .......
        """,
        // a tear
        """
        ...k...
        ...k...
        ..kkk..
        .kkkkk.
        .kcckk.
        .kkkkk.
        ..kkk..
        """,
        // two people
        """
        .k...k.
        kkk.kkk
        .kkkkk.
        .k...k.
        .k...k.
        k.k.k.k
        .......
        """,
    ),
    // The tale of Isis and Osiris
    listOf(
        // wheat
        """
        ..k.k..
        ...k...
        ..k.k..
        ...k...
        ..k.k..
        ...k...
        ...k...
        """,
        // a chest
        """
        .......
        .kkkkk.
        kkkkkkk
        k.....k
        kkkckkk
        k.....k
        kkkkkkk
        """,
        // a cup
        """
        .......
        kkkkkkk
        k.....k
        .k...k.
        ..kkk..
        ...k...
        ..kkk..
        """,
        // water
        """
        .......
        kk..kk.
        ..kk..k
        .......
        kk..kk.
        ..kk..k
        .......
        """,
        // a ship
        """
        .......
        ...k...
        ..kk...
        k..k..k
        kkkkkkk
        .kkkkk.
        .......
        """,
        // a tree
        """
        .kkkkk.
        kkkkkkk
        .kkkkk.
        ...k...
        ...k...
        ...k...
        ..kkk..
        """,
        // a throne, the sign of Isis
        """
        .......
        kk.....
        .k.....
        .k.....
        .kkkkk.
        .k...k.
        .kkkkk.
        """,
        // a swallow
        """
        k.....k
        .k...k.
        ..kkk..
        .kkckk.
        ...k...
        ..k.k..
        .k...k.
        """,
        // papyrus
        """
        k..k..k
        .k.k.k.
        ..kkk..
        ...k...
        ...k...
        ...k...
        ...k...
        """,
        // the moon
        """
        ..kkk..
        .kk....
        kk.....
        kk.....
        kk.....
        .kk....
        ..kkk..
        """,
        // pieces
        """
        kk...kk
        k.....k
        ...k...
        ..kkk..
        ...k...
        k.....k
        kk...kk
        """,
        // a crocodile
        """
        .......
        .......
        .....k.
        kkkkkkk
        .kkkkkk
        ..k.k..
        .......
        """,
        // a tomb
        """
        .......
        ...k...
        ..kkk..
        .kkkkk.
        .kk.kk.
        kkk.kkk
        kkkkkkk
        """,
        // the eye of Horus
        """
        .kkkkk.
        .......
        .kkkkk.
        kk.k.kk
        .kkkkk.
        ...k..k
        ..kk...
        """,
        // scales
        """
        ...k...
        kkkkkkk
        k..k..k
        k..k..k
        kk.k.kk
        ...k...
        .kkkkk.
        """,
    ),
)
