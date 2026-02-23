package com.flynn273.playtime.Utils

import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.div

actual fun getConfigFile(): PlatformFile {
    val os = System.getProperty("os.name").lowercase()

    return when {
        os.contains("win") -> {
            PlatformFile(System.getenv("APPDATA")) / FileKit.appId / "config.toml"
        }

        os.contains("nix") || os.contains("nux") || os.contains("mac") -> {
            PlatformFile(
                System.getenv("XDG_CONFIG_HOME") ?: System.getenv("HOME")
            ) / ".config" / FileKit.appId / "config.toml"
        }

        else -> error("Unsupported OS: $os")
    }
}