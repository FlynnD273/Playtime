package com.flynn273.playtime

import com.flynn273.playtime.Utils.*
import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.vinceglb.filekit.*
import kotlinx.coroutines.*
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase
import java.security.MessageDigest

private val logger = KotlinLogging.logger {}

class Library(val scope: CoroutineScope) {
    val supportedExtensions = setOf("mp3", "m4a", "m4p", "ogg", "vorbis", "flac", "wav", "aif", "dsf", "wma")
    val libraryDb = R2dbcDatabase.connect("r2dbc:h2:file://" + getIndexDb().path)
    val albumArtCache = getImageCache()
    val libraryState = LibraryState(scope)

    fun hashLibrary(paths: List<PlatformFile>): String {
        logger.debug { "Hashing library... $paths" }
        val digest = MessageDigest.getInstance("SHA-256")
        for (path in paths) {
            hashSingleFolder(digest, path)
        }
        logger.debug { "Finished hashing!" }
        return digest.digest().joinToString("")
    }

    private fun hashSingleFolder(digest: MessageDigest, file: PlatformFile) {
        if (file.isDirectory()) {
            if (file.name[0] == '.') {
                return
            }
            file.list().forEach { hashSingleFolder(digest, it) }
        } else {
            digest.update(file.path.toByteArray())
        }
    }

    suspend fun indexLibrary(paths: List<PlatformFile>) {
        logger.debug { "Indexing library... $paths" }
        val hash = hashLibrary(paths)
        if (libraryState.state.value.libraryHash != hash) {
            libraryState.updateState(LibraryStateObject(hash))
            coroutineScope { albumArtCache.list().map { async { it.delete() } }.awaitAll() }
            for (path in paths) {
                indexSingleFolder(path)
            }
        }
        logger.debug { "Finished indexing!" }
    }

    private fun indexSingleFolder(file: PlatformFile) {
        if (file.isDirectory()) {
            if (file.name[0] == '.') {
                return
            }
            file.list().forEach { indexSingleFolder(it) }
        } else {
            if (supportedExtensions.contains(file.extension)) {
                val metadata = getMetadata(file)
                cacheCoverImage(metadata)
            }
        }
    }

    private fun cacheCoverImage(metadata: AudioMetadata) {
        val albumArtFile = albumArtCache / getImagePathName(metadata)
        if (!albumArtFile.exists()) {
            if (metadata.albumArt.isNotEmpty()) {
                scope.launch { albumArtFile.write(metadata.albumArt) }
            } else {
                for (img in metadata.file.parent()!!.list()) {
                    if (img.isDirectory()) {
                        continue
                    }
                    if (img.extension == "jpg" || img.extension == "jpeg") {
                        scope.launch { img.copyTo(albumArtFile) }
                        break
                    }
                }
            }
            if (!albumArtFile.exists()) {
                scope.launch { albumArtFile.write(byteArrayOf()) }
            }
        }
    }
}

object Artists : IntIdTable("artists") {
    val name = varchar("name", 128).index()
}

object Albums : IntIdTable("albums") {
    val name = varchar("name", 128).index()
    val disc = integer("disc")
    val artPath = varchar("artPath", 256)
    val artist = reference("artist", Artists.name)
}

object Tracks : IntIdTable("tracks") {
    val name = varchar("name", 128).index()
    val artPath = varchar("artPath", 256)
    val albums = reference("albums", Albums.name)
}