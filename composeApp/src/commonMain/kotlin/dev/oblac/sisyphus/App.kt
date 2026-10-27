package dev.oblac.sisyphus

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import dev.oblac.sisyphus.audio.loadClick
import dev.oblac.sisyphus.audio.loadCoin
import dev.oblac.sisyphus.audio.loadFanfare
import dev.oblac.sisyphus.audio.loadUi
import dev.oblac.sisyphus.audio.loadWin
import dev.oblac.sisyphus.game.Game
import dev.oblac.sisyphus.ui.BACKGROUND

private const val BUSY_FRAME = 14_000_000L
private const val IDLE_FRAME = 31_000_000L

@Composable
fun SisyphusApp(game: Game, haptics: Haptics = NoHaptics, sound: Sound = Silence, screen: Screen = remember(game) { Screen(game) }) {
    val uploader = remember { frameUploader() }
    var frame by remember { mutableLongStateOf(0L) }

    DisposableEffect(game, haptics, sound, screen) {
        game.onTick = {
            haptics.tick()
            sound.tick(game.soundOn)
        }
        screen.onCoin = { sound.obol(game.soundOn) }
        onDispose {
            game.onTick = {}
            screen.onCoin = {}
        }
    }

    LaunchedEffect(sound) { sound.load(loadClick(), loadUi(), loadWin(), loadFanfare(), loadCoin()) }

    LaunchedEffect(screen) {
        var last = 0L
        var drawn = 0L
        while (true) {
            withFrameNanos { now ->
                if (last != 0L) screen.update(((now - last) / 1e9).coerceIn(0.0, 0.1))
                last = now
                // Pixel art needs no more than 60 frames while turning, and 30 when the knob rests.
                val busy = game.knob.held || game.knob.velocity != 0.0
                if (now - drawn >= if (busy) BUSY_FRAME else IDLE_FRAME) {
                    drawn = now
                    frame = now
                }
            }
        }
    }

    val insets = WindowInsets.safeDrawing
    Canvas(
        Modifier
            .fillMaxSize()
            .background(Color(BACKGROUND))
            .pointerInput(screen) {
                awaitEachGesture {
                    val down = awaitFirstDown()
                    val touch = screen.press(down.position.x, down.position.y)
                    sound.play(touch, game.soundOn)
                    when (touch) {
                        Touch.NONE -> return@awaitEachGesture
                        Touch.KNOB -> haptics.prepare()
                        Touch.LIST -> {}
                        else -> {
                            down.consume()
                            haptics.tick()
                            return@awaitEachGesture
                        }
                    }
                    down.consume()
                    try {
                        while (true) {
                            val change = awaitPointerEvent().changes.firstOrNull { it.id == down.id } ?: break
                            if (!change.pressed) break
                            screen.move(change.position.x, change.position.y)
                            change.consume()
                        }
                    } finally {
                        val lifted = screen.release()
                        if (lifted != Touch.NONE) {
                            haptics.tick()
                            sound.play(lifted, game.soundOn)
                        }
                    }
                }
            },
    ) {
        if (frame == 0L) return@Canvas
        screen.resize(
            size.width.toInt(), size.height.toInt(),
            insets.getTop(this), insets.getBottom(this),
            insets.getLeft(this, layoutDirection), insets.getRight(this, layoutDirection),
        )
        val px = screen.render()
        drawImage(
            uploader.upload(px),
            srcOffset = IntOffset.Zero,
            srcSize = IntSize(px.width, px.height),
            dstOffset = IntOffset(screen.offsetX, screen.offsetY),
            dstSize = IntSize(px.width * screen.scale, px.height * screen.scale),
            filterQuality = FilterQuality.None,
        )
    }
}
