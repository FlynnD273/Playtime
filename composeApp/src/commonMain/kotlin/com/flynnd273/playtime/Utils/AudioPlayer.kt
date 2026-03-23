package com.flynnd273.playtime.Utils

import com.flynnd273.playtime.Database.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

expect class AudioPlayer(scope: CoroutineScope, onSongEnd: (() -> Unit)) {
    val state: StateFlow<PlayerState>

    fun play()
    fun play(track: Track)
    fun pause()
    fun togglePlaying()
    fun stop()
    fun seekTo(positionMs: Long)
}

data class PlayerState(
    val isPlaying: Boolean = false,
    val currentPositionMs: Long = 0,
    val durationMs: Long = 0,
)