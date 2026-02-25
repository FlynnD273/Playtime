package com.flynn273.playtime.Utils

import io.github.vinceglb.filekit.PlatformFile

data class AudioMetadata(
    val artist: String,
    val album: String,
    val track: String,
    @Suppress("ArrayInDataClass") val coverArt: ByteArray,
    val discNum: Int,
    val discTotal: Int,
    val releaseYear: Int,
    val trackNum: Int,
    val file: PlatformFile,
) {

    override fun toString(): String {
        return "$artist | $album | $track (disc $discNum/$discTotal)"
    }
}

expect fun getMetadata(file: PlatformFile): AudioMetadata