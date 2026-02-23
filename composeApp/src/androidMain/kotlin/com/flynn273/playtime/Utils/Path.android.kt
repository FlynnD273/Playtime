package com.flynn273.playtime.Utils

import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.div
import io.github.vinceglb.filekit.filesDir

actual fun getConfigFile(): PlatformFile {
    return FileKit.filesDir / "config.toml"
}