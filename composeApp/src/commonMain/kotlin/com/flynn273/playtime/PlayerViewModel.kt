package com.flynnd273.playtime

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flynn273.playtime.Config
import com.flynn273.playtime.Library
import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.FileKitDialogSettings
import io.github.vinceglb.filekit.dialogs.openDirectoryPicker
import io.github.vinceglb.filekit.path
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

private val logger = KotlinLogging.logger {}

class PlayerViewModel() : ViewModel() {
    val config = Config(viewModelScope)
    val library = Library(viewModelScope)

    init {
        viewModelScope.launch {
            config.settings
                .map { it.searchPaths }
                .distinctUntilChanged()
                .collectLatest { paths ->
                    library.indexLibrary(paths)
                }
        }
    }

    private val _isPickingFolder = MutableStateFlow(false)
    val isPickingFolder = _isPickingFolder.asStateFlow()
    fun chooseFolder(dialogSettings: FileKitDialogSettings) {
        if (isPickingFolder.value) {
            return
        }
        viewModelScope.launch {
            _isPickingFolder.update { true }
            val file = FileKit.openDirectoryPicker(
                dialogSettings = dialogSettings
            )
            logger.debug { file?.path ?: "No file selected!" }
            _isPickingFolder.update { false }
        }
    }
}
