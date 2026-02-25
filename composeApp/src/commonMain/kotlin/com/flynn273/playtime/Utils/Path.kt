package com.flynn273.playtime.Utils

import io.github.vinceglb.filekit.*
import java.text.Normalizer

expect fun getConfigFile(): PlatformFile

fun getIndexDb(): PlatformFile {
    val file = FileKit.databasesDir / "index"
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
    return Normalizer.normalize("${meta.artist}.${meta.album}.jpg", Normalizer.Form.NFD)
        .replace(Regex("""[#~*:"!?&\\/]"""), "")
        .replace(Regex("""\p{InCombiningDiacriticalMarks}+"""), "")
        .replace(Regex("""[^\p{ASCII}]"""), "")
}