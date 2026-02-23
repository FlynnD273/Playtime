package com.flynn273.playtime.Utils

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.vinceglb.filekit.PlatformFile
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey

private val logger = KotlinLogging.logger {}

actual fun getMetadata(file: PlatformFile): AudioMetadata {
    val f = AudioFileIO.read(file.file)
    val tag = f.getTag()
    return AudioMetadata(
        tag.getFirst(FieldKey.ALBUM_ARTISTS)
            .ifBlank {
                tag.getFirst(FieldKey.ALBUM_ARTIST).ifBlank {
                    tag.getFirst(FieldKey.ARTIST).ifBlank { "Unknown" }
                }
            },
        tag.getFirst(FieldKey.ALBUM).ifBlank { "Unknown" },
        tag.getFirst(FieldKey.TITLE).ifBlank { "Unknown" },
        tag.getFirstField(FieldKey.COVER_ART)?.rawContent ?: byteArrayOf(),
        tag.getFirst(FieldKey.DISC_NO).ifBlank { "1" }.toInt(),
        tag.getFirst(FieldKey.DISC_TOTAL).ifBlank { "1" }.toInt(),
        tag.getFirst(FieldKey.ORIGINALRELEASEDATE).ifBlank { "1" }.toInt(),
        tag.getFirst(FieldKey.TRACK).ifBlank { "1" }.toInt()
    )
}