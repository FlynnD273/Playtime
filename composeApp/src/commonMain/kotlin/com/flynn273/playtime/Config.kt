package com.flynn273.playtime

import com.akuleshov7.ktoml.Toml
import com.akuleshov7.ktoml.source.decodeFromStream
import com.flynn273.playtime.Utils.getConfigFile
import io.github.vinceglb.filekit.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import kotlinx.io.asInputStream
import kotlinx.io.buffered
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString

@OptIn(FlowPreview::class)
class Config(scope: CoroutineScope) {
    private val configPath = getConfigFile()
    private val _settings = MutableStateFlow(loadConfig())
    val settings = _settings.asStateFlow()

    init {
        scope.launch {
            _settings
                .debounce(500)
                .collectLatest { saveConfig(it) }
        }
    }

    private fun loadConfig(): ConfigState {
        if (!configPath.exists()) {
            return ConfigState(listOf(PlatformFile("/mnt/tertiary/Sample Music")))
        }
        val source = configPath.source().buffered()
        source.use { bufferedSource ->
            return Toml.decodeFromStream<ConfigState>(bufferedSource.asInputStream())
        }
    }

    private suspend fun saveConfig(state: ConfigState) {
        configPath.parent()!!.createDirectories()
        configPath.writeString(Toml.encodeToString(state))
    }
}

@Serializable
data class ConfigState(val searchPaths: List<PlatformFile>) {
}