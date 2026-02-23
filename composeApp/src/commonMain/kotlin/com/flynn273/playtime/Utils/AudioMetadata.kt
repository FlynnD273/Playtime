package com.flynn273.playtime.Utils

import io.github.vinceglb.filekit.PlatformFile

data class AudioMetadata(
    val artist: String,
    val album: String,
    val track: String,
    val albumArt: ByteArray,
    val discNum: Int,
    val discTotal: Int,
    val releaseYear: Int,
    val trackNum: Int,
    val file: PlatformFile
) {

    override fun toString(): String {
        return "$artist | $album | $track (disc $discNum/$discTotal)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AudioMetadata

        if (discNum != other.discNum) return false
        if (discTotal != other.discTotal) return false
        if (releaseYear != other.releaseYear) return false
        if (trackNum != other.trackNum) return false
        if (artist != other.artist) return false
        if (album != other.album) return false
        if (track != other.track) return false
        if (!albumArt.contentEquals(other.albumArt)) return false
        if (file != other.file) return false

        return true
    }

    override fun hashCode(): Int {
        var result = discNum
        result = 31 * result + discTotal
        result = 31 * result + releaseYear
        result = 31 * result + trackNum
        result = 31 * result + artist.hashCode()
        result = 31 * result + album.hashCode()
        result = 31 * result + track.hashCode()
        result = 31 * result + albumArt.contentHashCode()
        result = 31 * result + file.hashCode()
        return result
    }
}

expect fun getMetadata(file: PlatformFile): AudioMetadata