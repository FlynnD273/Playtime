package com.flynn273.playtime.Utils

import io.github.vinceglb.filekit.*

expect fun getConfigFile(): PlatformFile

fun getIndexDb(): PlatformFile {
    val file = FileKit.databasesDir / "index.db"
    if (!file.exists()) {
        file.parent()!!.createDirectories()
    }
    return file
}
