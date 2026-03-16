package com.flynnd273.playtime.Utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

actual class AudioPlayer actual constructor(scope: CoroutineScope) {
    actual val state: StateFlow<PlayerState>
        get() = TODO("Not yet implemented")

    actual fun play() {
    }

    actual fun pause() {
    }

    actual fun stop() {
    }

    actual fun seekTo(positionMs: Long) {
    }
}