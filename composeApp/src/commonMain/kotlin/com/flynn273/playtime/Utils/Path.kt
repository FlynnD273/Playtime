package com.flynn273.playtime.Utils

import io.github.vinceglb.filekit.*
import java.text.Normalizer
import kotlin.math.min

expect fun getConfigFile(): PlatformFile

fun getIndexDb(): PlatformFile {
    val file = FileKit.databasesDir / "db"
    val parent = file.parent()!!
    if (!parent.exists()) {
        parent.createDirectories()
    }
    return file
}

fun getImageCache(): PlatformFile {
    val file = FileKit.cacheDir / "coverart"
    if (!file.exists()) {
        file.createDirectories()
    }
    return file
}

fun getLibraryStateFile(): PlatformFile {
    val file = FileKit.cacheDir / "state.toml"
    val parent = file.parent()!!
    if (!parent.exists()) {
        parent.createDirectories()
    }
    return file
}

fun getImagePathName(meta: AudioMetadata): String {
    return Normalizer.normalize(
        "${meta.artist.substring(0, min(meta.artist.length, 10))}.${
            meta.album.substring(
                0,
                min(meta.album.length, 10)
            )
        }.jpg",
        Normalizer.Form.NFD
    )
        .replace(Regex("""\p{InCombiningDiacriticalMarks}+"""), "")
        .replace(Regex("""[^\p{ASCII}]"""), "")
        .replace(Regex("""[^a-zA-Z0-9 .]"""), "")
}