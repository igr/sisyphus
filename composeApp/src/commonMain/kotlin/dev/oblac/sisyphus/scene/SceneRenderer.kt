package dev.oblac.sisyphus.scene

import dev.oblac.sisyphus.game.Game
import dev.oblac.sisyphus.game.Line
import dev.oblac.sisyphus.game.Place
import dev.oblac.sisyphus.gfx.PixelFont
import dev.oblac.sisyphus.gfx.Pixels
import dev.oblac.sisyphus.gfx.bayer
import dev.oblac.sisyphus.gfx.hash
import dev.oblac.sisyphus.gfx.hash01
import dev.oblac.sisyphus.gfx.lerpColor
import dev.oblac.sisyphus.gfx.rgb
import dev.oblac.sisyphus.gfx.WHITE
import dev.oblac.sisyphus.gfx.shade
import dev.oblac.sisyphus.gfx.sprite
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

private const val SKY_LEVELS = 12
private const val PROP_SLOT = 40
private const val ISLAND_PERIOD = 220

/** At their highest, the sun and the moon stand this far below the top of the display: a little under the obol bar. */
private const val ZENITH = 24

internal val GROUND = intArrayOf(
    rgb(0xB4C46A), rgb(0x82A048), rgb(0x5A7E38),
    rgb(0xB08A56), rgb(0x94704A), rgb(0x765840),
    rgb(0xD2C4A6), rgb(0x7C6A54),
)
private const val GRASS_LIGHT = 0
private const val GRASS = 1
private const val GRASS_DARK = 2
private const val SOIL = 3
private const val STONE_LIGHT = 6
private const val STONE_DARK = 7

internal val FLOWERS = intArrayOf(rgb(0xE04A32), rgb(0xF2C640), rgb(0xF4F0E8), rgb(0xB86ACC))

private val ROCK = intArrayOf(
    rgb(0x3E3834), rgb(0x5A524A), rgb(0x7A7066), rgb(0x9A9084), rgb(0xBAB0A2), rgb(0xD6CEC0),
)
private val ROCK_OUTLINE = rgb(0x221C18)

private val MOSS = rgb(0x7E8A58)

/**
 * One look of the rock: its [colors] from dark to light, and the [mark] painted on it, which can ask for the [accent] color.
 * A [polished] rock has a shine and no marks.
 */
internal class Rock(val colors: IntArray, val mark: (Double, Double) -> Int = { _, _ -> 0 }, val polished: Boolean = false, val accent: Int = MOSS)

/** The rock that every climb starts with. */
internal val FIRST_ROCK = Rock(ROCK, ::rockMark)

/** The rock for each tier of the shop in Corinth: today's rock, the river stone, the pumice, the sphere of Daedalus. */
internal val ROCKS = listOf(
    FIRST_ROCK,
    Rock(intArrayOf(rgb(0x353A3E), rgb(0x4E555A), rgb(0x6B7378), rgb(0x8C9498), rgb(0xAEB4B6), rgb(0xD2D6D6)), ::riverMark),
    Rock(intArrayOf(rgb(0x6E665C), rgb(0x8C8376), rgb(0xA89E8F), rgb(0xC2B8A8), rgb(0xD8CFC0), rgb(0xEDE6DA)), ::pumiceMark),
    Rock(intArrayOf(rgb(0x5C5248), rgb(0x80766A), rgb(0xA69C8E), rgb(0xC9C0B2), rgb(0xE6DED2), rgb(0xFFFFFF)), polished = true),
)

private val STAR = rgb(0xFFFFFF)
private val STAR_DIM = rgb(0x9098C0)
private val MOON = rgb(0xF2F0DE)
private val MOON_SHADE = rgb(0xC8C6B2)
private val TEXT = rgb(0xF1E4C8)
private val TEXT_SHADOW = rgb(0x1A120C)

/** The top of the hill, with the number of climbs beside it under the day. */
private val PEAK = sprite(mapOf('#' to TEXT), "...#...\n..###..\n.#####.\n#######").outlined(TEXT_SHADOW)

/** The Pleiades, the seven sisters. The last one is Merope, the faintest: she married a mortal. */
internal val PLEIADES = listOf(0 to 0, 3 to -1, 5 to 1, 2 to 3, 6 to 4, 8 to 2, 4 to 6)

private class Star(val x: Int, val y: Int, val threshold: Float, val bright: Boolean, val speed: Double, val phase: Double)

private class Island(val center: Int, val half: Double, val height: Double)

private val ISLANDS = listOf(Island(24, 13.0, 4.0), Island(70, 6.0, 2.0), Island(150, 18.0, 6.0))

/** A body in the sky. [altitude] is 1 at its highest and below 0 under the horizon. */
private class Orb(val x: Double, val y: Double, val altitude: Double)

/** Paints the display: sky, sea, the endless slope, the rock and Sisyphus himself. */
class SceneRenderer(private val geo: SceneGeometry) {
    private val hero = HeroSprites(geo.rockDx, geo.rockDy, geo.rockR)
    private val skyLevels = IntArray(SKY_LEVELS + 1)
    private val ground = IntArray(GROUND.size)
    private val blades = IntArray(2)
    private val stars = List(geo.width * geo.horizon / 150) { i ->
        Star(
            x = (hash01(i, 1, 11) * geo.width).toInt(),
            y = (hash01(i, 2, 11) * (geo.horizon - 10)).toInt(),
            threshold = hash01(i, 3, 11),
            bright = hash01(i, 4, 11) < 0.12f,
            speed = 1.5 + hash01(i, 5, 11) * 3,
            phase = hash01(i, 6, 11) * 2 * PI,
        )
    }

    /** At the top, [rolled] is how far the rock has rolled back down. */
    fun render(px: Pixels, game: Game, world: World, banner: Double, hud: Boolean = true, rolled: Double? = null) {
        val look = lookOf(game.place)
        val t = game.timeOfDay
        val atm = atmosphere(t, world.overcast, game.place)
        val p = floor(game.position).toInt()
        val sun = orb((t - 0.25) / 0.5)
        val moon = orb((t - 0.75 + 1.0) % 1.0 / 0.5)

        val beauty = game.shop.tier(Line.BEAUTY)
        drawSky(px, atm, sun, world.overcast, look.sun)
        drawStars(px, atm, game.clock, world)
        if (game.place == Place.CORINTH && beauty >= 3) drawPleiades(px, atm, game.clock)
        moon?.let { drawMoon(px, it, game.day, atm, world.overcast) }
        sun?.let { drawSun(px, it, game.clock, atm, world.overcast, look.sun) }
        drawClouds(px, atm, world)
        when (game.place) {
            Place.CORINTH -> {
                drawFlyer(px, world, game.clock, atm, PEGASUS_UP, PEGASUS_DOWN)
                drawIslands(px, atm, p)
                drawSea(px, atm, p, sun, moon, game.clock)
                drawDolphins(px, world, atm)
            }
            Place.ETNA -> {
                drawSummit(px, geo, atm, game.clock)
                drawSparks(px, world)
                drawFlyer(px, world, game.clock, atm, EAGLE_UP, EAGLE_DOWN)
                drawFields(px, geo, atm, p)
                if (beauty >= 3) drawTorches(px, geo, atm, game.clock)
            }
            Place.UNDERWORLD -> {
                drawRoof(px, atm)
                drawFlyer(px, world, game.clock, atm, PSYCHE_UP, PSYCHE_DOWN)
                drawFarShore(px, geo, atm, game.clock)
                drawStyx(px, geo, atm, p, game.clock)
                drawFerry(px, world, atm)
            }
        }
        drawGround(px, atm, p, look)
        drawProps(px, atm, p, look.props)
        drawBirds(px, world, game.clock, look)
        if (game.place == Place.UNDERWORLD && beauty >= 3) drawLights(px, geo, atm, p, game.clock)
        drawRock(px, atm, game.position, look.rocks[game.shop.tier(Line.ROCK)], rolled)
        drawHero(px, atm, game, p)
        drawParticles(px, world, atm)
        drawRain(px, world, atm, look.ash)
        if (hud) drawDay(px, game.day, game.climbs, banner)
    }

    private fun orb(p: Double): Orb? {
        if (p < -0.1 || p > 1.1) return null
        val altitude = sin(PI * p)
        return Orb(4.0 + (geo.width + 10.0) * p, geo.horizon - altitude * (geo.horizon - ZENITH), altitude)
    }

    /** The sky, with a glow around the sun in the last two of the sun's [colors]. */
    private fun drawSky(px: Pixels, atm: Atmosphere, sun: Orb?, overcast: Float, colors: IntArray) {
        for (k in 0..SKY_LEVELS) skyLevels[k] = atm.sky(k / SKY_LEVELS.toFloat())
        val alt = sun?.altitude?.coerceIn(0.0, 1.0) ?: 0.0
        val glow = if (sun != null && sun.altitude > -0.15) 1f - overcast else 0f
        val glowR = 14 + (1 - alt) * 18
        val glowColor = lerpColor(colors[4], colors[5], alt.toFloat())
        val bottom = min(geo.horizon, px.height)
        for (y in 0 until bottom) {
            val g = y.toFloat() / geo.horizon * SKY_LEVELS
            for (x in 0 until px.width) {
                var c = skyLevels[(g + bayer(x, y)).toInt().coerceAtMost(SKY_LEVELS)]
                if (glow > 0f && sun != null) {
                    val dx = x + 0.5 - sun.x
                    val dy = y + 0.5 - sun.y
                    if (abs(dx) < glowR && abs(dy) < glowR) {
                        val d = sqrt(dx * dx + dy * dy)
                        if (d < glowR && bayer(x + 2, y + 1) < (1 - d / glowR) * glow) c = lerpColor(c, glowColor, 0.28f)
                    }
                }
                px.data[y * px.width + x] = c
            }
        }
    }

    private fun drawStars(px: Pixels, atm: Atmosphere, clock: Double, world: World) {
        if (atm.stars > 0.02f) {
            for (s in stars) {
                if (s.threshold > atm.stars) continue
                val tw = sin(clock * s.speed + s.phase)
                px[s.x, s.y] = if (tw > 0.65) STAR_DIM else STAR
                if (s.bright && tw > -0.2) {
                    px[s.x - 1, s.y] = STAR_DIM
                    px[s.x + 1, s.y] = STAR_DIM
                    px[s.x, s.y - 1] = STAR_DIM
                    px[s.x, s.y + 1] = STAR_DIM
                }
            }
        }
        val s = world.shootingStar ?: return
        val len = 6
        for (k in 0 until len) {
            val f = k / len.toDouble()
            val x = floor(s.x - s.vx * f * 0.08).toInt()
            val y = floor(s.y - s.vy * f * 0.08).toInt()
            if (y < geo.horizon) px[x, y] = if (k < 2) STAR else STAR_DIM
        }
    }

    private fun drawPleiades(px: Pixels, atm: Atmosphere, clock: Double) {
        if (atm.stars <= 0.15f) return
        val x0 = (geo.width * 0.7).toInt()
        val y0 = (geo.horizon * 0.2).toInt()
        PLEIADES.forEachIndexed { k, (dx, dy) ->
            val merope = k == PLEIADES.size - 1
            px[x0 + dx, y0 + dy] = if (merope || sin(clock * (2.0 + k) + k) > 0.65) STAR_DIM else STAR
        }
    }

    /** The flyer of the second beauty item, wings [up] and [down]. */
    private fun drawFlyer(px: Pixels, world: World, clock: Double, atm: Atmosphere, up: Pixels, down: Pixels) {
        val f = world.flyer ?: return
        val sprite = if (sin((clock + f.flap) * 2 * PI * 3) > 0) up else down
        px.draw(sprite, floor(f.x).toInt() - sprite.width / 2, floor(f.y).toInt() - sprite.height / 2, flipX = f.vx < 0, light = atm.light)
    }

    private fun drawDolphins(px: Pixels, world: World, atm: Atmosphere) {
        for (d in world.dolphins) {
            val f = d.t / LEAP_TIME
            val x = d.x + d.dir * 8 * f
            val y = geo.horizon + 2 - sin(PI * f) * 7
            val frame = when {
                f < 0.35 -> DOLPHIN_RISE
                f < 0.65 -> DOLPHIN_TOP
                else -> DOLPHIN_DIVE
            }
            px.draw(frame, floor(x).toInt() - 3, floor(y).toInt() - 2, flipX = d.dir < 0, light = atm.light)
        }
    }

    /** The [colors] of the sun: its core low and high in the sky, then its edge low and high. */
    private fun drawSun(px: Pixels, sun: Orb, clock: Double, atm: Atmosphere, overcast: Float, colors: IntArray) {
        val alt = sun.altitude.coerceIn(0.0, 1.0)
        val r = 5.0 + (1 - alt) * 1.5
        val warm = sqrt(alt).toFloat()
        val behind = atm.sky((sun.y / geo.horizon).toFloat().coerceIn(0f, 1f))
        val core = lerpColor(lerpColor(colors[0], colors[1], warm), behind, overcast * 0.95f)
        val edge = lerpColor(lerpColor(colors[2], colors[3], warm), behind, overcast * 0.95f)
        px.disc(sun.x, sun.y, r, edge)
        px.disc(sun.x, sun.y, r - 1.3, core)
        if (alt < 0.25 || overcast > 0.4f) return
        val swap = floor(clock / 0.6).toInt() % 2 == 0
        for (k in 0 until 8) {
            val a = k * PI / 4
            val long = (k % 2 == 0) == swap
            dot(px, sun.x + cos(a) * (r + 2), sun.y + sin(a) * (r + 2), edge)
            if (long) dot(px, sun.x + cos(a) * (r + 3), sun.y + sin(a) * (r + 3), edge)
        }
    }

    private fun drawMoon(px: Pixels, moon: Orb, day: Int, atm: Atmosphere, overcast: Float) {
        val r = 4.3
        val k = abs((day - 1).mod(8) - 4) / 4.0
        val off = 2 * r * (1 - k * 0.78)
        val behind = atm.sky((moon.y / geo.horizon).toFloat().coerceIn(0f, 1f))
        val light = lerpColor(MOON, behind, overcast * 0.6f)
        val shade = lerpColor(MOON_SHADE, behind, overcast * 0.6f)
        for (j in floor(moon.y - r).toInt()..ceil(moon.y + r).toInt()) {
            for (i in floor(moon.x - r).toInt()..ceil(moon.x + r).toInt()) {
                val dx = i + 0.5 - moon.x
                val dy = j + 0.5 - moon.y
                if (dx * dx + dy * dy > r * r) continue
                val sx = dx + off
                if (sx * sx + dy * dy < r * r) continue
                val crater = (dx - 1.2) * (dx - 1.2) + (dy + 1.0) * (dy + 1.0) < 1.4 ||
                    (dx + 0.6) * (dx + 0.6) + (dy - 1.8) * (dy - 1.8) < 0.9
                px[i, j] = if (crater) shade else light
            }
        }
    }

    private fun drawClouds(px: Pixels, atm: Atmosphere, world: World) {
        for (c in world.clouds) {
            val cx = floor(c.x).toInt()
            for (y in 0 until c.h) for (x in 0 until c.w) {
                val color = when (c.cells[y * c.w + x]) {
                    Cloud.LIGHT -> atm.cloudLight
                    Cloud.BODY -> atm.cloudBody
                    Cloud.SHADOW -> atm.cloudShadow
                    else -> continue
                }
                px[cx + x, c.y + y] = color
            }
        }
    }

    private fun drawIslands(px: Pixels, atm: Atmosphere, p: Int) {
        val color = lerpColor(atm.skyHorizon, atm.sea, 0.6f)
        val shift = floor(p * 0.04).toInt()
        for (x in 0 until px.width) {
            val u = (x + shift).mod(ISLAND_PERIOD)
            var h = 0
            for (isl in ISLANDS) {
                val f = (u - isl.center) / isl.half
                if (abs(f) < 1) h = max(h, (isl.height * (1 - f * f)).roundToInt())
            }
            for (k in 1..h) px[x, geo.horizon - k] = color
        }
    }

    private fun drawSea(px: Pixels, atm: Atmosphere, p: Int, sun: Orb?, moon: Orb?, clock: Double) {
        val tick = floor(clock * 2.5).toInt()
        val crest = lerpColor(atm.sea, atm.skyHorizon, 0.55f)
        val glint = lerpColor(atm.skyHorizon, rgb(0xFFF6D8), 0.6f)
        val moonGlint = lerpColor(atm.sea, MOON, 0.55f)
        for (x in 0 until px.width) {
            val bottom = min(px.height, geo.surfaceY(x, p))
            for (y in geo.horizon until bottom) {
                val k = y - geo.horizon
                var c = lerpColor(atm.skyHorizon, atm.sea, min(1f, k / 5f + 0.35f))
                if (hash01((x + tick * (1 + k % 2)).floorDiv(3), k, 21) < 0.1f) c = crest
                if (k % 2 == 0 && sun != null && sun.altitude > 0 && sun.altitude < 0.6 &&
                    abs(x + 0.5 - sun.x) < 1.2 + k * 0.15 && hash01(x.floorDiv(2), k + tick, 23) < 0.5f
                ) c = glint
                if (k % 3 == 0 && moon != null && moon.altitude > 0 && moon.altitude < 0.7 &&
                    abs(x + 0.5 - moon.x) < 0.8 + k * 0.08 && hash01(x, k + tick, 25) < 0.4f
                ) c = moonGlint
                px.data[y * px.width + x] = c
            }
        }
    }

    private fun drawGround(px: Pixels, atm: Atmosphere, p: Int, look: Look) {
        for (i in ground.indices) ground[i] = shade(look.ground[i], atm.light)
        for (i in blades.indices) blades[i] = shade(look.blades[i], atm.light)
        for (x in 0 until px.width) {
            val s = geo.surfaceY(x, p)
            val wx = geo.worldX(x, p)
            for (y in max(0, s) until px.height) px.data[y * px.width + x] = ground[groundIndex(wx, y - s)]
            val hb = hash01(wx, 5, 31)
            val blade = when {
                hb < 0.04f * look.grass -> 3
                hb < 0.12f * look.grass -> 2
                hb < 0.3f * look.grass -> 1
                else -> 0
            }
            for (k in 1..blade) px[x, s - k] = blades[if (k == blade) 0 else 1]
            val hf = hash(wx, 9, 31)
            if ((hf and 0xFF) < 7) {
                px[x, s - 1] = blades[1]
                px[x, s - 2] = shade(look.flowers[(hf shr 8) and 3], atm.light)
            }
        }
    }

    private fun groundIndex(wx: Int, d: Int): Int {
        if (d == 0) return if (hash01(wx, 1, 41) < 0.35f) GRASS_LIGHT else GRASS
        if (d == 1) return if (hash01(wx, 2, 41) < 0.3f) GRASS_DARK else GRASS
        if (d == 2) return if (bayer(wx, d) < 0.55f) GRASS_DARK else SOIL
        val cx = wx.floorDiv(6)
        val cy = (d - 3).floorDiv(5)
        val h = hash(cx, cy, 51)
        if ((h and 0xFF) < 100) {
            val sx = cx * 6 + ((h shr 8) and 3)
            val sy = 3 + cy * 5 + ((h shr 10) and 1)
            val sw = 2 + ((h shr 12) and 1)
            if (wx >= sx && wx < sx + sw) {
                if (d == sy) return STONE_LIGHT
                if (d == sy + 1) return STONE_DARK
            }
        }
        val band = ((d - 3) + bayer(wx, d) * 4f).toInt() / 7
        return SOIL + band.coerceAtMost(2)
    }

    private fun drawProps(px: Pixels, atm: Atmosphere, p: Int, props: List<Prop>) {
        val from = (geo.worldX(0, p) - 20).floorDiv(PROP_SLOT)
        val to = (geo.worldX(px.width, p) + 20).floorDiv(PROP_SLOT)
        for (slot in from..to) {
            val h = hash(slot, 0, 61)
            if ((h and 0xFF) > 110) continue
            val prop = propFor(h ushr 8, props)
            val sprite = prop.sprite
            val x = slot * PROP_SLOT + (hash(slot, 1, 61) and 0x1F) - p + geo.heroX
            val top = geo.surfaceY(x, p) - (sprite.height - 1)
            px.draw(sprite, x - sprite.width / 2, top, flipX = hash(slot, 2, 61) < 0, light = atm.light)
        }
    }

    private fun drawBirds(px: Pixels, world: World, clock: Double, look: Look) {
        for (b in world.birds) {
            val up = sin((clock + b.flap) * 2 * PI * 2.8) > 0
            val bird = if (up) look.birdUp else look.birdFlat
            px.draw(bird, floor(b.x).toInt() - bird.width / 2, floor(b.y).toInt() - bird.height / 2, flipX = b.vx < 0)
        }
    }

    /**
     * The rock rests ahead of him on the slope, or on the flat top once it gets there.
     * At the top it rolls back [rolled] pixels: along the top, past him, and down the slope.
     */
    private fun drawRock(px: Pixels, atm: Atmosphere, position: Double, rock: Rock, rolled: Double?) {
        val r = geo.rockR
        val slope = sqrt(1.25)
        var x = geo.heroX + geo.rockDx
        var y = geo.baseY + geo.rockDy
        val p = floor(position).toInt()
        val top = geo.top
        if (top != null && p + geo.rockDx >= top) y = geo.surfaceY(x.toInt(), p) - r
        if (rolled != null) {
            val flat = x - geo.heroX
            if (rolled <= flat) {
                x -= rolled
            } else {
                val dx = (rolled - flat) / slope
                x = geo.heroX - dx
                y = geo.baseY + dx / 2 - r * slope
            }
        }
        paintRock(px, x, y, r, (position - (rolled ?: 0.0)) / r, rock, atm.light)
    }

    private fun drawHero(px: Pixels, atm: Atmosphere, game: Game, p: Int) {
        val frame = if (game.stillFor > 0.5) hero.idle[if (sin(game.clock * 2.2) > 0.2) 1 else 0]
        else hero.walk[p.mod(HeroSprites.WALK_FRAMES)]
        px.draw(frame, geo.heroX - HeroSprites.ORIGIN_X, geo.baseY - HeroSprites.ORIGIN_Y, light = atm.light)
    }

    private fun drawParticles(px: Pixels, world: World, atm: Atmosphere) {
        for (q in world.particles) {
            val x = floor(q.x).toInt()
            val y = floor(q.y).toInt()
            if (bayer(x, y) > q.life / q.maxLife + 0.25) continue
            px[x, y] = shade(q.color, atm.light)
        }
    }

    /** Rain, or flakes of ash where [ash] is true. */
    private fun drawRain(px: Pixels, world: World, atm: Atmosphere, ash: Boolean) {
        val n = (world.drops.size * world.rain).toInt()
        if (n == 0) return
        if (ash) {
            val light = lerpColor(atm.skyHorizon, rgb(0xFFFFFF), 0.3f)
            val dark = lerpColor(atm.skyHorizon, rgb(0x2A2624), 0.55f)
            for (i in 0 until n) {
                val d = world.drops[i]
                px[floor(d.x).toInt(), floor(d.y).toInt()] = if (i % 3 == 0) light else dark
            }
            return
        }
        val c = lerpColor(atm.skyHorizon, rgb(0xFFFFFF), 0.35f)
        val tail = lerpColor(atm.skyHorizon, rgb(0xFFFFFF), 0.12f)
        for (i in 0 until n) {
            val d = world.drops[i]
            val x = floor(d.x).toInt()
            val y = floor(d.y).toInt()
            px[x, y] = c
            px[x, y - 1] = c
            px[x + 1, y - 2] = tail
        }
    }

    private fun drawDay(px: Pixels, day: Int, climbs: Int, banner: Double) {
        PixelFont.drawOutlined(px, "DAY $day", 4, 4, TEXT, TEXT_SHADOW)
        if (climbs > 0) {
            px.draw(PEAK, 3, 16)
            PixelFont.drawOutlined(px, "$climbs", 4 + PEAK.width, 14, TEXT, TEXT_SHADOW)
        }
        if (banner <= 0) return
        val text = "Day $day"
        val fade = when {
            banner > 2.4 -> ((banner - 2.4) / 0.6).toFloat()
            banner < 0.6 -> (1 - banner / 0.6).toFloat()
            else -> 0f
        }
        val x = (px.width - PixelFont.width(text)) / 2
        val y = geo.horizon / 3
        PixelFont.drawOutlined(px, text, x, y, TEXT, TEXT_SHADOW, fade)
    }

    private fun dot(px: Pixels, x: Double, y: Double, c: Int) {
        px[floor(x).toInt(), floor(y).toInt()] = c
    }

}

/** What a mark returns where the rock takes its accent color. */
internal const val ACCENT = 99
private val CRACK = doubleArrayOf(-6.5, -2.0, -2.0, -0.5, 0.5, 2.0, 4.0, 2.8)

/** The rock of the shop's [tier] in [place], alone in daylight: its picture in the Agora. */
fun rockIcon(place: Place, tier: Int): Pixels = Pixels(16, 16).also { paintRock(it, 8.0, 8.0, 7.5, 0.0, lookOf(place).rocks[tier], WHITE) }

/** Paints [rock] around ([cx], [cy]), turned by [angle] and lit by [light]. */
private fun paintRock(px: Pixels, cx: Double, cy: Double, r: Double, angle: Double, rock: Rock, light: Int) {
    val palette = rock.colors
    val shades = IntArray(palette.size) { shade(palette[it], light) }
    val outline = shade(ROCK_OUTLINE, light)
    val accent = shade(rock.accent, light)
    val ca = cos(angle)
    val sa = sin(angle)
    for (j in floor(cy - r).toInt()..ceil(cy + r).toInt()) {
        for (i in floor(cx - r).toInt()..ceil(cx + r).toInt()) {
            val dx = i + 0.5 - cx
            val dy = j + 0.5 - cy
            val d2 = dx * dx + dy * dy
            if (d2 > r * r) continue
            if (sqrt(d2) > r - 1.0) {
                px[i, j] = outline
                continue
            }
            val nz = sqrt(max(0.0, 1 - d2 / (r * r)))
            val l = (-0.5 * dx - 0.62 * dy) / r + 0.6 * nz
            val u = dx * ca + dy * sa
            val v = -dx * sa + dy * ca
            if (rock.polished && abs(dx + 0.38 * r) < 1 && abs(dy + 0.42 * r) < 1) {
                px[i, j] = shades[palette.size - 1]
                continue
            }
            val mark = rock.mark(u, v)
            if (mark == ACCENT) {
                px[i, j] = accent
                continue
            }
            val tone = ((l + 1) * 2.6 + bayer(i, j) - 0.5).toInt() + mark
            px[i, j] = shades[tone.coerceIn(0, if (rock.polished) palette.size - 2 else palette.size - 1)]
        }
    }
}

/** Spots and a crack painted on the rock, in rock coordinates, so they roll with it. */
private fun rockMark(u: Double, v: Double): Int {
    fun near(x: Double, y: Double, r: Double) = (u - x) * (u - x) + (v - y) * (v - y) < r * r
    if (crack(u, v)) return -3
    if (near(-4.0, 2.5, 1.2)) return -2
    if (near(-4.0, 2.5, 2.6) || near(3.5, -3.0, 2.2) || near(1.0, 5.5, 1.6) || near(-2.5, -6.0, 1.3)) return -1
    if (near(5.2, 3.6, 1.5)) return ACCENT
    if (near(-0.5, -1.5, 1.6)) return 1
    return 0
}

/** The river wore the rock smooth: two soft spots are all that is left. */
private fun riverMark(u: Double, v: Double): Int {
    fun near(x: Double, y: Double, r: Double) = (u - x) * (u - x) + (v - y) * (v - y) < r * r
    return if (near(-3.5, 2.5, 2.4) || near(3.0, -3.0, 2.0)) -1 else 0
}

/** Pumice is full of small holes, about one pixel in nine. */
private fun pumiceMark(u: Double, v: Double): Int =
    if ((hash(floor(u).toInt(), floor(v).toInt(), 71) and 0xFF) < 28) -3 else 0

/** Whether (u, v) lies on the crack through [points], given as x, y pairs. */
internal fun crack(u: Double, v: Double, points: DoubleArray = CRACK): Boolean {
    for (k in 0 until points.size - 2 step 2) {
        val x0 = points[k]
        val y0 = points[k + 1]
        val x1 = points[k + 2]
        val y1 = points[k + 3]
        val vx = x1 - x0
        val vy = y1 - y0
        val t = (((u - x0) * vx + (v - y0) * vy) / (vx * vx + vy * vy)).coerceIn(0.0, 1.0)
        val ex = u - x0 - vx * t
        val ey = v - y0 - vy * t
        if (ex * ex + ey * ey < 0.36) return true
    }
    return false
}
