package dev.oblac.sisyphus.gfx

/**
 * A small proportional pixel font. Capitals are 7 pixels tall,
 * descenders take 2 more rows below the baseline.
 */
object PixelFont {
    const val CAP_HEIGHT = 7
    const val LINE_HEIGHT = 11
    private const val SPACE_ADVANCE = 3

    private class Glyph(val width: Int, val rows: Array<String>)

    private val glyphs: Map<Char, Glyph> = GLYPHS.mapValues { (_, art) ->
        val rows = art.split('|').toTypedArray()
        Glyph(rows.maxOf { it.length }, rows)
    }

    fun hasGlyph(ch: Char) = ch == ' ' || glyphs.containsKey(ch)

    private fun glyph(ch: Char): Glyph = glyphs[ch] ?: glyphs.getValue('?')

    private fun advance(ch: Char): Int = if (ch == ' ') SPACE_ADVANCE else glyph(ch).width + 1

    fun width(text: String): Int = if (text.isEmpty()) 0 else text.sumOf { advance(it) } - 1

    /**
     * Draws [text] with its top-left corner at ([x], [y]).
     * Pixels whose dither threshold is below [dissolve] are skipped, which fades the text out.
     */
    fun draw(px: Pixels, text: String, x: Int, y: Int, color: Int, dissolve: Float = 0f) {
        var cx = x
        for (ch in text) {
            if (ch != ' ') {
                val g = glyph(ch)
                g.rows.forEachIndexed { row, bits ->
                    bits.forEachIndexed { col, bit ->
                        val tx = cx + col
                        val ty = y + row
                        if (bit == '#' && (dissolve <= 0f || bayer(tx, ty) >= dissolve)) px[tx, ty] = color
                    }
                }
            }
            cx += advance(ch)
        }
    }

    /** Draws [text] with a one pixel [outline] all around, readable on any background. */
    fun drawOutlined(px: Pixels, text: String, x: Int, y: Int, color: Int, outline: Int, dissolve: Float = 0f) {
        for (dy in -1..1) for (dx in -1..1) if (dx != 0 || dy != 0) draw(px, text, x + dx, y + dy, outline, dissolve)
        draw(px, text, x, y, color, dissolve)
    }

    /**
     * The texts already wrapped, by width: the wise words, the bubbles and the questions are drawn in every frame, but their
     * texts seldom change. A width keeps at most [WRAPS_KEPT] texts; the width depends on the phone, and changes only on a resize.
     */
    private val wrapped = arrayOfNulls<HashMap<String, List<String>>>(1024)
    private const val WRAPS_KEPT = 512

    /** [text] in lines no wider than [maxWidth], broken between words. The lines of a text and a width are made once. */
    fun wrap(text: String, maxWidth: Int): List<String> {
        if (maxWidth !in wrapped.indices) return wrapNow(text, maxWidth)
        val byText = wrapped[maxWidth] ?: HashMap<String, List<String>>().also { wrapped[maxWidth] = it }
        byText[text]?.let { return it }
        if (byText.size >= WRAPS_KEPT) byText.clear()
        return wrapNow(text, maxWidth).also { byText[text] = it }
    }

    private fun wrapNow(text: String, maxWidth: Int): List<String> {
        val lines = mutableListOf<String>()
        var line = ""
        for (word in text.split(' ').filter { it.isNotEmpty() }) {
            val candidate = if (line.isEmpty()) word else "$line $word"
            if (line.isNotEmpty() && width(candidate) > maxWidth) {
                lines += line
                line = word
            } else {
                line = candidate
            }
        }
        if (line.isNotEmpty()) lines += line
        return lines
    }
}

private val GLYPHS: Map<Char, String> = mapOf(
    'A' to ".##.|#..#|#..#|####|#..#|#..#|#..#",
    'B' to "###.|#..#|#..#|###.|#..#|#..#|###.",
    'C' to ".##.|#..#|#...|#...|#...|#..#|.##.",
    'D' to "###.|#..#|#..#|#..#|#..#|#..#|###.",
    'E' to "####|#...|#...|###.|#...|#...|####",
    'F' to "####|#...|#...|###.|#...|#...|#...",
    'G' to ".##.|#..#|#...|#.##|#..#|#..#|.###",
    'H' to "#..#|#..#|#..#|####|#..#|#..#|#..#",
    'I' to "###|.#.|.#.|.#.|.#.|.#.|###",
    'J' to "...#|...#|...#|...#|...#|#..#|.##.",
    'K' to "#..#|#..#|#.#.|##..|#.#.|#..#|#..#",
    'L' to "#...|#...|#...|#...|#...|#...|####",
    'M' to "#...#|##.##|#.#.#|#.#.#|#...#|#...#|#...#",
    'N' to "#..#|##.#|##.#|#.##|#.##|#..#|#..#",
    'O' to ".##.|#..#|#..#|#..#|#..#|#..#|.##.",
    'P' to "###.|#..#|#..#|###.|#...|#...|#...",
    'Q' to ".##.|#..#|#..#|#..#|#..#|#.#.|.#.#",
    'R' to "###.|#..#|#..#|###.|#.#.|#..#|#..#",
    'S' to ".###|#...|#...|.##.|...#|...#|###.",
    'T' to "#####|..#..|..#..|..#..|..#..|..#..|..#..",
    'U' to "#..#|#..#|#..#|#..#|#..#|#..#|.##.",
    'V' to "#...#|#...#|#...#|#...#|.#.#.|.#.#.|..#..",
    'W' to "#...#|#...#|#...#|#.#.#|#.#.#|##.##|#...#",
    'X' to "#...#|#...#|.#.#.|..#..|.#.#.|#...#|#...#",
    'Y' to "#...#|#...#|.#.#.|..#..|..#..|..#..|..#..",
    'Z' to "####|...#|...#|..#.|.#..|#...|####",

    'a' to "....|....|.##.|...#|.###|#..#|.###",
    'b' to "#...|#...|###.|#..#|#..#|#..#|###.",
    'c' to "...|...|.##|#..|#..|#..|.##",
    'd' to "...#|...#|.###|#..#|#..#|#..#|.###",
    'e' to "....|....|.##.|#..#|####|#...|.###",
    'f' to ".##|#..|###|#..|#..|#..|#..",
    'g' to "....|....|.###|#..#|#..#|#..#|.###|...#|.##.",
    'h' to "#...|#...|###.|#..#|#..#|#..#|#..#",
    'i' to "#|.|#|#|#|#|#",
    'j' to ".#|..|.#|.#|.#|.#|.#|.#|#.",
    'k' to "#...|#...|#..#|#.#.|##..|#.#.|#..#",
    'l' to "#.|#.|#.|#.|#.|#.|.#",
    'm' to ".....|.....|####.|#.#.#|#.#.#|#.#.#|#.#.#",
    'n' to "....|....|###.|#..#|#..#|#..#|#..#",
    'o' to "....|....|.##.|#..#|#..#|#..#|.##.",
    'p' to "....|....|###.|#..#|#..#|#..#|###.|#...|#...",
    'q' to "....|....|.###|#..#|#..#|#..#|.###|...#|...#",
    'r' to "....|....|#.##|##..|#...|#...|#...",
    's' to "....|....|.###|#...|.##.|...#|###.",
    't' to "...|#..|###|#..|#..|#..|.##",
    'u' to "....|....|#..#|#..#|#..#|#..#|.###",
    'v' to ".....|.....|#...#|#...#|.#.#.|.#.#.|..#..",
    'w' to ".....|.....|#...#|#...#|#.#.#|#.#.#|.#.#.",
    'x' to "....|....|#..#|#..#|.##.|#..#|#..#",
    'y' to "....|....|#..#|#..#|#..#|#..#|.###|...#|.##.",
    'z' to "....|....|####|...#|.##.|#...|####",

    '0' to ".##.|#..#|#..#|#..#|#..#|#..#|.##.",
    '1' to ".#.|##.|.#.|.#.|.#.|.#.|###",
    '2' to ".##.|#..#|...#|..#.|.#..|#...|####",
    '3' to "###.|...#|...#|.##.|...#|...#|###.",
    '4' to "#..#|#..#|#..#|####|...#|...#|...#",
    '5' to "####|#...|#...|###.|...#|...#|###.",
    '6' to ".##.|#...|#...|###.|#..#|#..#|.##.",
    '7' to "####|...#|...#|..#.|..#.|.#..|.#..",
    '8' to ".##.|#..#|#..#|.##.|#..#|#..#|.##.",
    '9' to ".##.|#..#|#..#|.###|...#|...#|.##.",

    '.' to ".|.|.|.|.|.|#",
    ',' to "..|..|..|..|..|..|.#|#.",
    '!' to "#|#|#|#|#|.|#",
    '?' to ".##.|#..#|...#|..#.|.#..|....|.#..",
    '\'' to "#|#",
    '’' to "#|#",
    '"' to "#.#|#.#",
    '-' to "...|...|...|###",
    '+' to "...|...|.#.|###|.#.",
    '—' to ".....|.....|.....|#####",
    ':' to ".|.|.|#|.|.|#",
    ';' to "..|..|..|.#|..|..|.#|#.",
    '(' to ".#|#.|#.|#.|#.|#.|.#",
    ')' to "#.|.#|.#|.#|.#|.#|#.",
    '[' to "##|#.|#.|#.|#.|#.|##",
    ']' to "##|.#|.#|.#|.#|.#|##",
    '/' to "..#|..#|.#.|.#.|.#.|#..|#..",
    '&' to ".##..|#..#.|#.#..|.#...|#.#.#|#..#.|.##.#",
    '·' to ".|.|.|#",
    '$' to "..#..|.####|#.#..|.###.|..#.#|####.|..#..",
    '€' to "..###|.#...|####.|.#...|####.|.#...|..###",
    '£' to "..##.|.#..#|.#...|###..|.#...|.#...|#####",
)
