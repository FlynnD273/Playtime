package com.flynn273.playtime.Utils

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.nameWithoutExtension
import io.github.vinceglb.filekit.parent
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey

private val logger = KotlinLogging.logger {}

actual fun getMetadata(file: PlatformFile): AudioMetadata {
    try {
        val f = AudioFileIO.read(file.file)
        val tag = f.getTag()
        return AudioMetadata(
            tag.getFirst(FieldKey.ALBUM_ARTISTS)
                .ifBlank {
                    tag.getFirst(FieldKey.ALBUM_ARTIST).ifBlank {
                        tag.getFirst(FieldKey.ARTIST).ifBlank { file.parent()!!.name }
                    }
                },
            tag.getFirst(FieldKey.ALBUM).ifBlank { "Unknown" },
            tag.getFirst(FieldKey.TITLE).ifBlank { file.nameWithoutExtension },
            tag.firstArtwork?.binaryData ?: byteArrayOf(),
            tag.getFirst(FieldKey.DISC_NO).ifBlank { "1" }.toInt(),
            tag.getFirst(FieldKey.DISC_TOTAL).ifBlank { "1" }.toInt(),
            tag.getFirst(FieldKey.ORIGINALRELEASEDATE).ifBlank { "1" }.toInt(),
            tag.getFirst(FieldKey.TRACK).ifBlank { "1" }.toInt(),
            file
        )
    } catch (e: Exception) {
        // Should really only happen if a song has no header
        return AudioMetadata(
            file.parent()!!.name,
            "Unknown",
            file.nameWithoutExtension,
            byteArrayOf(),
            1,
            1,
            1,
            1,
            file
        )
    }
}