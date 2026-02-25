package com.flynn273.playtime

import com.flynn273.playtime.Database.*
import com.flynn273.playtime.Utils.*
import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.vinceglb.filekit.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.exceptions.ExposedSQLException
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.deleteAll
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction
import playtime.composeapp.generated.resources.Res
import java.security.MessageDigest
import kotlin.time.Clock

private val logger = KotlinLogging.logger {}

class Library(val scope: CoroutineScope) {
    private val _tracks = MutableStateFlow<List<Track>>(emptyList())
    val tracks: StateFlow<List<Track>> = _tracks
    val supportedExtensions = setOf("mp3", "m4a", "m4p", "ogg", "vorbis", "flac", "wav", "aif", "dsf", "wma")
    val imageExtensions = setOf("jpg", "jpeg", "png")
    val libraryDb = Database.connect("jdbc:h2:file://" + getIndexDb().path)
    val albumArtCache = getImageCache()
    val libraryState = LibraryState(scope)

    init {
        scope.launch { initDb() }
    }

    fun refreshTracks() {
        scope.launch {
            suspendTransaction {
                _tracks.value = Track.all().sortedByDescending { it.lastPlayed }
            }
        }
    }

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
            if (supportedExtensions.contains(file.extension) || imageExtensions.contains(file.extension)) {
                digest.update(file.path.toByteArray())
            }
        }
    }

    suspend fun indexLibrary(paths: List<PlatformFile>) {
        logger.debug { "Indexing library... $paths" }
        val hash = hashLibrary(paths)
        if (libraryState.state.value.libraryHash != hash) {
            libraryState.setLibraryHash(hash)
            coroutineScope { albumArtCache.list().map { async { it.delete() } }.awaitAll() }
            suspendTransaction {
                Tracks.deleteAll()
                Albums.deleteAll()
                Artists.deleteAll()
                for (path in paths) {
                    indexSingleFolder(path)
                }
            }
        }
        refreshTracks()
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
                val coverImage = cacheCoverImage(metadata)

                val artistRow = Artist.find { Artists.name eq metadata.artist }.firstOrNull() ?: Artist.new {
                    name = metadata.artist
                    lastPlayed = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                }
                val albumRow =
                    Album.find { Albums.name eq metadata.album and (Albums.artist eq artistRow.id) }.firstOrNull()
                        ?: Album.new {
                            name = metadata.album
                            disc = metadata.discNum
                            discTotal = metadata.discTotal
                            artPath = coverImage.path
                            artist = artistRow
                            lastPlayed = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                        }
                try {
                    Track.new {
                        name = metadata.track
                        artPath = coverImage.path
                        filePath = file.path
                        album = albumRow
                        lastPlayed = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                    }
                } catch (e: ExposedSQLException) {
                    logger.debug { "Error adding song: ${metadata.file.path}, $e" }
                }
            }
        }
    }

    private fun cacheCoverImage(metadata: AudioMetadata): PlatformFile {
        var albumArtFile = albumArtCache / getImagePathName(metadata)
        var hasArt = false
        if (!albumArtFile.exists()) {
            if (metadata.coverArt.isNotEmpty()) {
                hasArt = true
                scope.launch { albumArtFile.write(metadata.coverArt) }
            } else {
                var largest: PlatformFile? = null
                for (img in metadata.file.parent()!!.list()) {
                    if (img.isDirectory()) {
                        continue
                    }
                    if (img.extension == "jpg" || img.extension == "jpeg") {
                        if ((largest?.size() ?: 0) < img.size()) {
                            largest = img
                        }
                    }
                }
                if (largest != null) {
                    hasArt = true
                    scope.launch { largest.copyTo(albumArtFile) }
                }
            }
        }
        if (!hasArt) {
            albumArtFile = albumArtCache / "Unknown.jpg"
            if (!albumArtFile.exists()) {
                scope.launch {
                    albumArtFile.write(Res.readBytes("drawable/unknownCover.jpg"))
                }
            }
        }
        return albumArtFile
    }
}