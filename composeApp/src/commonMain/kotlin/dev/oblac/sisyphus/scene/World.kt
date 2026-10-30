package dev.oblac.sisyphus.scene

import dev.oblac.sisyphus.game.Place
import dev.oblac.sisyphus.game.Weather
import dev.oblac.sisyphus.gfx.rgb
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

class Cloud(var x: Double, val y: Int, val w: Int, val h: Int, val cells: ByteArray, val depth: Double) {
    companion object {
        const val LIGHT: Byte = 1
        const val BODY: Byte = 2
        const val SHADOW: Byte = 3
    }
}

class Bird(var x: Double, val y: Double, val vx: Double, val flap: Double)

/** [stick] is 1 for things lying on the ground: they slide away with the hill. */
class Particle(
    var x: Double, var y: Double,
    var vx: Double, var vy: Double,
    var life: Double, val maxLife: Double,
    val gravity: Double, val stick: Double, val color: Int,
)

class Drop(var x: Double, var y: Double, val speed: Double)

/**
 * A dolphin's leap out of the sea, starting at [x], going [dir]; in Cairo, a hippopotamus that rises from the Nile at [x], facing [dir];
 * on Mount Fuji, a koi's leap out of the lake.
 */
class Leap(val x: Double, val dir: Double) {
    var t = 0.0
}

/** Something crossing the display: Pegasus, the eagle of Zeus or Psyche in the sky, or Charon's boat on the Styx. */
class Flyer(var x: Double, val y: Double, val vx: Double, val flap: Double)

class ShootingStar(var x: Double, var y: Double, val vx: Double, val vy: Double, var life: Double)

internal val DUST = rgb(0xC8A878)
private val SWEAT = rgb(0xBFE4FF)
private val SPLASH = rgb(0xB8C8E0)
private const val DROPS = 90

/** Seconds a dolphin is out of the water. */
const val LEAP_TIME = 1.2

/** Ash falls this much slower than rain. */
private const val ASH_FALL = 0.16

/** Sand falls this much slower than rain, and the wind blows it this many times faster sideways than down. */
private const val SAND_FALL = 0.1
private const val SAND_WIND = 12.0

/** Seconds a hippopotamus is out of the water. */
const val HIPPO_TIME = 4.0

/** Seconds a koi is out of the water. */
const val KOI_TIME = 0.9

/** Seconds the crater throws fire. */
private const val BURST_TIME = 1.6

private val FIRE = intArrayOf(rgb(0xFFE070), rgb(0xFFA040), rgb(0xF0602A), rgb(0xC83A20))

/** Everything in the display that moves by itself: clouds, birds, rain, dust. */
class World(seed: Int) {
    private val random = Random(seed)

    var geo = SceneGeometry(1, 1)
        private set
    val clouds = ArrayList<Cloud>()
    val birds = ArrayList<Bird>()
    val particles = ArrayList<Particle>()
    val drops = ArrayList<Drop>()
    val dolphins = ArrayList<Leap>()
    /** On Etna, the fire that the crater throws up. */
    val sparks = ArrayList<Particle>()
    /** In The Underworld, Charon's boat crossing the Styx. */
    var ferry: Flyer? = null
        private set
    /** In Cairo, a hippopotamus out of the Nile. */
    var hippo: Leap? = null
        private set
    /** On Mount Fuji, a koi out of the lake. */
    var koi: Leap? = null
        private set
    var flyer: Flyer? = null
        private set
    var shootingStar: ShootingStar? = null
        private set
    /** 0 is dry, 1 is pouring. Follows the weather slowly. */
    var rain = 0.0
        private set
    var overcast = 0f
        private set

    private var dust = 0.0
    /** Seconds to the next sight of the first beauty item: a dolphin's leap, fire from the crater, Charon's boat, a hippopotamus, or a koi. */
    private var nextLeap = 0.0
    private var nextFlight = 0.0
    private var burst = 0.0

    fun reset(geo: SceneGeometry, weather: Weather) {
        this.geo = geo
        clouds.clear()
        repeat(weather.clouds) { clouds += cloud(random.nextDouble(-8.0, geo.width - 6.0), weather) }
        drops.clear()
        repeat(DROPS) {
            drops += Drop(
                random.nextDouble(0.0, geo.width + 20.0),
                random.nextDouble(-geo.height.toDouble(), geo.height.toDouble()),
                random.nextDouble(80.0, 110.0),
            )
        }
        birds.clear()
        particles.clear()
        dolphins.clear()
        sparks.clear()
        burst = 0.0
        ferry = null
        hippo = null
        koi = null
        flyer = null
        nextLeap = random.nextDouble(5.0, 20.0)
        nextFlight = random.nextDouble(10.0, 45.0)
        rain = if (weather.rain) 1.0 else 0.0
        overcast = weather.overcast
    }

    /** Moves everything on for [dt] seconds, in [place]. */
    fun update(dt: Double, moved: Double, speed: Double, p: Int, weather: Weather, timeOfDay: Double, stars: Float, beauty: Int, place: Place = Place.CORINTH) {
        val look = lookOf(place)
        val ease = 1 - exp(-dt * 0.5)
        rain += ((if (weather.rain) 1.0 else 0.0) - rain) * ease
        overcast += ((weather.overcast - overcast) * ease).toFloat()
        updateClouds(dt, moved, weather)
        updateBirds(dt, moved, weather, timeOfDay)
        updateRain(dt, moved, p, look.fall)
        updateParticles(dt, moved, speed, look.dust)
        updateShootingStar(dt, stars)
        when (place) {
            Place.CORINTH -> updateDolphins(dt, beauty)
            Place.ETNA -> updateFountain(dt, beauty)
            Place.UNDERWORLD -> updateFerry(dt, moved, beauty)
            Place.CAIRO -> updateHippo(dt, beauty)
            Place.FUJI -> updateKoi(dt, beauty)
        }
        updateFlyer(dt, moved, beauty)
    }

    private fun updateClouds(dt: Double, moved: Double, weather: Weather) {
        val it = clouds.iterator()
        while (it.hasNext()) {
            val c = it.next()
            c.x -= weather.wind * (0.5 + c.depth) * dt + moved * c.depth * 0.3
            if (c.x + c.w < -6) it.remove()
            else if (c.x > geo.width + 40) c.x = -c.w - 4.0 - random.nextDouble(0.0, 20.0)
        }
        if (clouds.size < weather.clouds && random.nextDouble() < dt * 0.25) {
            clouds += cloud(geo.width + random.nextDouble(2.0, 16.0), weather)
        }
    }

    private fun cloud(x: Double, weather: Weather): Cloud {
        val big = weather.rain || (weather == Weather.CLOUDY && random.nextBoolean())
        val h = if (big) random.nextInt(9, 14) else random.nextInt(6, 11)
        val w = (h * random.nextDouble(2.2, 3.4)).toInt()
        val maxY = max(3, geo.horizon - h - 24)
        return Cloud(x, random.nextInt(2, maxY + 1), w, h, cloudCells(w, h), 0.2 + w / 60.0)
    }

    /** A cumulus: round bumps sitting on a flat base, the biggest in the middle. */
    private fun cloudCells(w: Int, h: Int): ByteArray {
        val cells = ByteArray(w * h)
        val bumps = 3 + random.nextInt(3)
        val mid = (bumps - 1) / 2.0
        val r = DoubleArray(bumps) {
            val center = 1 - abs(it - mid) / (mid + 1)
            (h - 0.4) * (0.22 + 0.27 * center) + random.nextDouble(-0.3, 0.3)
        }
        val cx = DoubleArray(bumps) { r[it] + 0.5 + (w - 1 - 2 * r[it]) * it / (bumps - 1) }
        for (y in 0 until h) for (x in 0 until w) {
            val px = x + 0.5
            val py = y + 0.5
            var inside = py > h - 2.5 && px > cx[0] && px < cx[bumps - 1]
            for (k in 0 until bumps) {
                val dx = px - cx[k]
                val dy = py - (h - r[k] - 0.2)
                if (dx * dx + dy * dy <= r[k] * r[k]) inside = true
            }
            if (inside) cells[y * w + x] = Cloud.BODY
        }
        fun empty(x: Int, y: Int) = y < 0 || cells[y * w + x] == 0.toByte()
        for (y in 0 until h) for (x in 0 until w) {
            if (empty(x, y)) continue
            cells[y * w + x] = when {
                y >= h - 2 || (y < h - 1 && empty(x, y + 1)) -> Cloud.SHADOW
                empty(x, y - 1) || (h >= 8 && empty(x, y - 2) && x < w * 0.7) -> Cloud.LIGHT
                else -> Cloud.BODY
            }
        }
        return cells
    }

    private fun updateBirds(dt: Double, moved: Double, weather: Weather, timeOfDay: Double) {
        if (timeOfDay in 0.3..0.72 && birds.isEmpty() && random.nextDouble() < weather.birds * dt) flock(weather)
        val it = birds.iterator()
        while (it.hasNext()) {
            val b = it.next()
            b.x += b.vx * dt - moved * 0.35
            if (b.x < -30 || b.x > geo.width + 30) it.remove()
        }
    }

    private fun flock(weather: Weather) {
        val n = random.nextInt(2, 6)
        val dir = if (random.nextBoolean()) 1.0 else -1.0
        val vx = dir * random.nextDouble(9.0, 14.0) - weather.wind * 0.5
        val y0 = random.nextDouble(8.0, max(10.0, geo.horizon - 34.0))
        val x0 = if (dir > 0) -8.0 else geo.width + 8.0
        for (i in 0 until n) {
            val rank = (i + 1) / 2
            val side = if (i % 2 == 0) 1 else -1
            birds += Bird(x0 - dir * rank * 6, y0 + side * rank * 3.0, vx, random.nextDouble(0.0, 1.0))
        }
    }

    /** Rain, slow ash, or sand that the wind blows by, as [fall] says. Only rain splashes. */
    private fun updateRain(dt: Double, moved: Double, p: Int, fall: Fall) {
        val active = (drops.size * rain).toInt()
        val span = geo.width + 8.0
        val down = when (fall) {
            Fall.RAIN -> 1.0
            Fall.ASH -> ASH_FALL
            Fall.SAND -> SAND_FALL
        }
        val side = if (fall == Fall.SAND) SAND_WIND else 0.3
        for (i in drops.indices) {
            val d = drops[i]
            d.y += d.speed * down * dt
            d.x -= d.speed * down * side * dt + moved * 0.5
            if (d.x < -4) d.x += span
            if (d.x > geo.width + 4) d.x -= span
            val xi = floor(d.x).toInt()
            val ground = if (xi in 0 until geo.width) min(geo.surfaceY(xi, p), geo.height) else geo.height
            if (d.y >= ground) {
                if (fall == Fall.RAIN && i < active && ground < geo.height && random.nextDouble() < 0.6) splash(xi, ground - 1)
                d.y -= geo.height + random.nextDouble(0.0, 30.0)
            }
        }
    }

    private fun splash(x: Int, y: Int) {
        repeat(2) {
            val dir = if (it == 0) -1 else 1
            particles += Particle(
                x + 0.5, y + 0.5, dir * random.nextDouble(8.0, 16.0), -random.nextDouble(10.0, 18.0),
                0.18, 0.18, 60.0, 1.0, SPLASH,
            )
        }
    }

    private fun updateParticles(dt: Double, moved: Double, speed: Double, dustColor: Int) {
        if (moved > 0) {
            dust += moved
            while (dust > 2.5) {
                dust -= 2.5
                val life = random.nextDouble(0.35, 0.7)
                particles += Particle(
                    geo.heroX - 3.5 + random.nextDouble(-1.0, 1.5), geo.baseY + 1.0,
                    random.nextDouble(-9.0, -3.0), random.nextDouble(-7.0, -2.0),
                    life, life, 14.0, 1.0, dustColor,
                )
            }
        }
        if (speed > 22 && random.nextDouble() < dt * 1.6) {
            particles += Particle(
                geo.heroX + 4.5, geo.baseY - 17.0,
                random.nextDouble(-12.0, -5.0), random.nextDouble(-16.0, -8.0),
                0.6, 0.6, 45.0, 0.0, SWEAT,
            )
        }
        val it = particles.iterator()
        while (it.hasNext()) {
            val q = it.next()
            q.life -= dt
            if (q.life <= 0) {
                it.remove()
                continue
            }
            q.vy += q.gravity * dt
            q.x += q.vx * dt - moved * q.stick
            q.y += q.vy * dt + moved * q.stick * 0.5
        }
    }

    /** Bought with the first beauty item: now and then a dolphin leaps in the visible part of the sea. */
    private fun updateDolphins(dt: Double, beauty: Int) {
        val it = dolphins.iterator()
        while (it.hasNext()) {
            val d = it.next()
            d.t += dt
            if (d.t >= LEAP_TIME) {
                splash((d.x + d.dir * 8).toInt(), geo.horizon + 1)
                it.remove()
            }
        }
        if (beauty < 1) return
        nextLeap -= dt
        if (nextLeap > 0) return
        nextLeap = random.nextDouble(15.0, 40.0)
        val far = geo.heroX - 20.0
        if (far - 3 < 6) return
        val d = Leap(random.nextDouble(3.0, far), if (random.nextBoolean()) 1.0 else -1.0)
        dolphins += d
        splash(d.x.toInt(), geo.horizon + 1)
    }

    /** Bought with the first beauty item on Etna: now and then the crater throws fire into the sky for a moment. */
    private fun updateFountain(dt: Double, beauty: Int) {
        for (q in sparks) {
            q.life -= dt
            q.vy += q.gravity * dt
            q.x += q.vx * dt
            q.y += q.vy * dt
        }
        sparks.removeAll { it.life <= 0 || it.y >= geo.horizon }
        if (burst > 0) {
            burst -= dt
            repeat((dt * 70 + random.nextDouble()).toInt()) {
                val life = random.nextDouble(0.8, 1.5)
                sparks += Particle(
                    craterX(geo), craterY(geo) + 0.5, random.nextDouble(-12.0, 12.0), random.nextDouble(-40.0, -22.0),
                    life, life, 30.0, 0.0, FIRE[random.nextInt(FIRE.size)],
                )
            }
        }
        if (beauty < 1) return
        nextLeap -= dt
        if (nextLeap > 0) return
        nextLeap = random.nextDouble(15.0, 40.0)
        burst = BURST_TIME
    }

    /** Bought with the first beauty item in The Underworld: now and then Charon rows across the Styx, slowly. */
    private fun updateFerry(dt: Double, moved: Double, beauty: Int) {
        ferry?.let { f ->
            f.x += f.vx * dt - moved * 0.04
            if (f.x > geo.heroX) ferry = null
        }
        if (beauty < 1 || ferry != null) return
        nextLeap -= dt
        if (nextLeap > 0) return
        nextLeap = random.nextDouble(15.0, 40.0)
        ferry = Flyer(-8.0, geo.horizon + 3.0, random.nextDouble(4.0, 6.0), 0.0)
    }

    /** Bought with the first beauty item in Cairo: now and then a hippopotamus rises from the Nile, yawns, and sinks back. */
    private fun updateHippo(dt: Double, beauty: Int) {
        hippo?.let { h ->
            h.t += dt
            if (h.t >= HIPPO_TIME) {
                splash(h.x.toInt(), geo.horizon + NILE_WATER)
                hippo = null
            }
        }
        if (beauty < 1 || hippo != null) return
        nextLeap -= dt
        if (nextLeap > 0) return
        nextLeap = random.nextDouble(15.0, 40.0)
        val far = geo.heroX - 30.0
        if (far < 10) return
        val h = Leap(random.nextDouble(5.0, far), if (random.nextBoolean()) 1.0 else -1.0)
        hippo = h
        splash(h.x.toInt(), geo.horizon + NILE_WATER)
    }

    /** Bought with the first beauty item on Mount Fuji: now and then a koi leaps out of the lake and dives back in. */
    private fun updateKoi(dt: Double, beauty: Int) {
        koi?.let { k ->
            k.t += dt
            if (k.t >= KOI_TIME) {
                splash((k.x + k.dir * 5).toInt(), geo.horizon + LAKE_KOI)
                koi = null
            }
        }
        if (beauty < 1 || koi != null) return
        nextLeap -= dt
        if (nextLeap > 0) return
        nextLeap = random.nextDouble(15.0, 40.0)
        val far = geo.heroX - 30.0
        if (far < 10) return
        val k = Leap(random.nextDouble(5.0, far), if (random.nextBoolean()) 1.0 else -1.0)
        koi = k
        splash(k.x.toInt(), geo.horizon + LAKE_KOI)
    }

    /** Bought with the second beauty item: Pegasus, the eagle of Zeus, Psyche, the Phoenix or the dragon crosses the sky every minute or two, day and night. */
    private fun updateFlyer(dt: Double, moved: Double, beauty: Int) {
        flyer?.let { f ->
            f.x += f.vx * dt - moved * 0.2
            if (f.x < -20 || f.x > geo.width + 20) flyer = null
        }
        if (beauty < 2 || flyer != null) return
        nextFlight -= dt
        if (nextFlight > 0) return
        nextFlight = random.nextDouble(45.0, 120.0)
        val dir = if (random.nextBoolean()) 1.0 else -1.0
        flyer = Flyer(
            if (dir > 0) -14.0 else geo.width + 14.0,
            random.nextDouble(10.0, max(12.0, geo.horizon * 0.4)),
            dir * random.nextDouble(14.0, 20.0),
            random.nextDouble(),
        )
    }

    private fun updateShootingStar(dt: Double, stars: Float) {
        val s = shootingStar
        if (s == null) {
            if (stars > 0.6f && random.nextDouble() < dt / 40) {
                val dir = if (random.nextBoolean()) 1.0 else -1.0
                shootingStar = ShootingStar(
                    random.nextDouble(10.0, geo.width - 10.0), random.nextDouble(4.0, geo.horizon * 0.4),
                    dir * random.nextDouble(45.0, 65.0), random.nextDouble(14.0, 22.0), 0.7,
                )
            }
            return
        }
        s.x += s.vx * dt
        s.y += s.vy * dt
        s.life -= dt
        if (s.life <= 0) shootingStar = null
    }
}
