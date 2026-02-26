package com.flynnd273.playtime

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flynn273.playtime.Config
import com.flynn273.playtime.Library
import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.FileKitDialogSettings
import io.github.vinceglb.filekit.dialogs.openDirectoryPicker
import io.github.vinceglb.filekit.exists
import io.github.vinceglb.filekit.path
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val logger = KotlinLogging.logger {}

class SharedViewModel() : ViewModel() {
    val config = Config(viewModelScope)
    val library = Library(viewModelScope)

    init {
        viewModelScope.launch {
            config.searchPaths
                .collectLatest { paths ->
                    withContext(Dispatchers.IO) {
                        library.indexLibrary(paths)
                    }
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
            if (file?.exists() ?: false) {
                config.setSearchPaths(listOf(file))
            }
            _isPickingFolder.update { false }
        }
    }
}
