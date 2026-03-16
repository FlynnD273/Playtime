package com.flynnd273.playtime

import com.flynnd273.playtime.Database.Track
import com.flynnd273.playtime.Utils.AudioPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

enum class RepeatMode {
    None,
    All,
    Single,
}

enum class ShuffleMode {
    None,
    Normal,
}

class PlayState(scope: CoroutineScope) {
    val player = AudioPlayer(scope)
    private val _currPlaying = MutableStateFlow<Track?>(null)
    val currPlaying = _currPlaying.asStateFlow()

    private val _playNext = MutableStateFlow<List<Track>>(emptyList())
    val playNext = _playNext.asStateFlow()

    private val _unshuffledQueue: List<Track> = emptyList()
    private val _queue = MutableStateFlow<List<Track>>(emptyList())
    val queue = _queue.asStateFlow()

    private val _queueIndex = MutableStateFlow(0)
    val queueIndex = _queueIndex.asStateFlow()

    private val _repeatMode = MutableStateFlow(RepeatMode.None)
    val repeatMode = _repeatMode.asStateFlow()

    private val _shuffleMode = MutableStateFlow(ShuffleMode.None)
    val shuffleMode = _shuffleMode.asStateFlow()

    init {
        scope.launch {
            currPlaying.collectLatest {
                if (it != null) {
                    player.play(it)
                } else {
                    player.seekTo(0)
                    player.pause()
                }
            }
        }

    }

    fun skipNext() {
        if (playNext.value.isNotEmpty()) {
            _currPlaying.value = playNext.value[0]
            _playNext.value = playNext.value.drop(1)
        } else {
            _queueIndex.value++
            if (queueIndex.value < queue.value.size) {
                _currPlaying.value = queue.value[queueIndex.value]
            } else {
                _currPlaying.value = null
            }
        }
    }

    fun skipPrev() {
        if ((player.state.value.currentPositionMs ?: 0) > 5000) {
            player.seekTo(0)
            player.play()
        } else {
            _queueIndex.value--
            if (queueIndex.value >= 0 && queueIndex.value < queue.value.size) {
                _currPlaying.value = queue.value[queueIndex.value]
            }
        }
    }

    fun addToPlayNext(tracks: List<Track>) {
        val shouldImmediatelyPlay = playNext.value.isEmpty() && queue.value.isEmpty()
        _playNext.value = playNext.value + tracks
        if (shouldImmediatelyPlay) {
            _queueIndex.value = -1
            skipNext()
        }
    }

    fun addToQueue(tracks: List<Track>) {
        val shouldImmediatelyPlay = playNext.value.isEmpty() && queue.value.isEmpty()
        _queue.value = queue.value + tracks
        if (shouldImmediatelyPlay) {
            _queueIndex.value = -1
            skipNext()
        }
    }

    fun swapPlayNext(from: Int, to: Int) {
        val newList = playNext.value.toMutableList()
        val track = newList[from]
        newList.removeAt(from)
        newList.add(to, track)
        _playNext.value = newList
    }

    fun swapQueue(from: Int, to: Int) {
        val newList = queue.value.toMutableList()
        val track = newList[from]
        newList.removeAt(from)
        newList.add(to, track)
        _queue.value = newList
    }
}
