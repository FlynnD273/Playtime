package com.flynn273.playtime

import com.akuleshov7.ktoml.Toml
import com.akuleshov7.ktoml.source.decodeFromStream
import com.flynn273.playtime.Utils.getLibraryStateFile
import io.github.vinceglb.filekit.exists
import io.github.vinceglb.filekit.source
import io.github.vinceglb.filekit.writeString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.io.asInputStream
import kotlinx.io.buffered
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString

@OptIn(FlowPreview::class)
class LibraryState(scope: CoroutineScope) {
    private val libraryStateFile = getLibraryStateFile()
    private val _state = MutableStateFlow(loadState())
    val state = _state.asStateFlow()
    val libraryHash = state.map { it.libraryHash }.distinctUntilChanged()
    val playlistHash = state.map { it.playlistHash }.distinctUntilChanged()

    fun setLibraryHash(value: String) {
        _state.update { it.copy(libraryHash = value) }
    }

    fun setPlaylistHash(value: String) {
        _state.update { it.copy(playlistHash = value) }
    }

    fun updateState(newState: LibraryStateObject) {
        _state.update { newState }
    }

    init {
        scope.launch {
            _state
                .debounce(100)
                .collectLatest { saveState(it) }
        }
    }

    private fun loadState(): LibraryStateObject {
        if (!libraryStateFile.exists()) {
            return LibraryStateObject()
        }
        val source = libraryStateFile.source().buffered()
        source.use { bufferedSource ->
            return Toml.decodeFromStream<LibraryStateObject>(bufferedSource.asInputStream())
        }
    }

    private suspend fun saveState(state: LibraryStateObject) {
        libraryStateFile.writeString(Toml.encodeToString(state))
    }
}

@Serializable
data class LibraryStateObject(val libraryHash: String = "", val playlistHash: String = "") {
}