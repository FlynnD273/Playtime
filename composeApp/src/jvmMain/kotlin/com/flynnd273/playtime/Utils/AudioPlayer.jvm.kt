package com.flynnd273.playtime.Utils

import com.flynnd273.playtime.Database.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction
import uk.co.caprica.vlcj.media.Media
import uk.co.caprica.vlcj.player.base.MediaPlayer
import uk.co.caprica.vlcj.player.component.AudioPlayerComponent
import kotlin.time.Clock

class CustomPlayer(val state: MutableStateFlow<PlayerState>, val onSongEnd: () -> Unit) : AudioPlayerComponent() {
    override fun playing(mediaPlayer: MediaPlayer?) {
        state.update { state.value.copy(isPlaying = true) }
    }

    override fun paused(mediaPlayer: MediaPlayer?) {
        state.update { state.value.copy(isPlaying = false) }
    }

    override fun stopped(mediaPlayer: MediaPlayer?) {
        state.update { PlayerState(false, 0, 1) }
        onSongEnd()
    }

    override fun timeChanged(mediaPlayer: MediaPlayer?, newTime: Long) {
        state.update { state.value.copy(currentPositionMs = newTime) }
    }

    override fun mediaDurationChanged(media: Media, newDuration: Long) {
        state.update { state.value.copy(durationMs = newDuration) }
    }
}

actual class AudioPlayer actual constructor(val scope: CoroutineScope, val onSongEnd: (() -> Unit)) {
    private val _state = MutableStateFlow(PlayerState())
    actual val state = _state.asStateFlow()
    private val _player = CustomPlayer(_state, onSongEnd)

    actual fun play() {
        _player.mediaPlayer().controls().play()
    }

    actual fun play(track: Track) {
        _player.mediaPlayer().media().play(track.filePath)
        scope.launch {
            suspendTransaction {
                track.lastPlayed = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            }
        }
    }

    actual fun pause() {
        _player.mediaPlayer().controls().pause()
    }

    actual fun stop() {
        _player.mediaPlayer().controls().stop()
    }

    actual fun seekTo(positionMs: Long) {
        _player.mediaPlayer().controls().setTime(positionMs)
        if (!state.value.isPlaying) {
            _state.update { state.value.copy(currentPositionMs = positionMs) }
        }
    }

    actual fun togglePlaying() {
        if (state.value.isPlaying) {
            pause()
        } else {
            play()
        }
    }
}