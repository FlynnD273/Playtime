package com.flynnd273.playtime.Utils

import com.flynnd273.playtime.Database.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import uk.co.caprica.vlcj.factory.MediaPlayerFactory

actual class AudioPlayer actual constructor(val scope: CoroutineScope) {
    private val factory: MediaPlayerFactory = MediaPlayerFactory()
    private val _state = MutableStateFlow(PlayerState())
    actual val state = _state.asStateFlow()

    actual fun play() {
    }

    actual fun play(track: Track) {
    }

    actual fun pause() {
    }

    actual fun stop() {
    }

    actual fun seekTo(positionMs: Long) {
    }
}