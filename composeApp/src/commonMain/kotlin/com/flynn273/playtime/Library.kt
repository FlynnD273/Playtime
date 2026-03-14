package com.flynn273.playtime

import com.flynn273.playtime.Database.*
import com.flynn273.playtime.Utils.*
import com.flynnd273.playtime.logger
import io.github.vinceglb.filekit.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.notInSubQuery
import org.jetbrains.exposed.v1.exceptions.ExposedSQLException
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.deleteAll
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import playtime.composeapp.generated.resources.Res
import java.security.MessageDigest
import java.util.Collections.emptyList
import kotlin.time.Clock

private const val TOP = 20

data class AlbumResult(val artist: Artist? = null, val album: Album? = null, val discs: List<List<Track>> = emptyList())
data class ArtistResult(val artist: Artist? = null, val albums: List<Album> = emptyList())


class Library(val scope: CoroutineScope) {
    private val _topTracks = MutableStateFlow<List<Track>>(emptyList())
    val topTracks: StateFlow<List<Track>> = _topTracks
    private val _topAlbums = MutableStateFlow<List<Album>>(emptyList())
    val topAlbums: StateFlow<List<Album>> = _topAlbums
    private val _topArtists = MutableStateFlow<List<Artist>>(emptyList())
    val topArtists: StateFlow<List<Artist>> = _topArtists

    private val _tracks = MutableStateFlow<List<Track>>(emptyList())
    val tracks: StateFlow<List<Track>> = _tracks
    private val _albums = MutableStateFlow<List<Album>>(emptyList())
    val albums: StateFlow<List<Album>> = _albums
    private val _artists = MutableStateFlow<List<Artist>>(emptyList())
    val artists: StateFlow<List<Artist>> = _artists


    val supportedExtensions = setOf("mp3", "m4a", "m4p", "ogg", "vorbis", "flac", "wav", "aif", "dsf", "wma")
    val imageExtensions = setOf("jpg", "jpeg", "png")
    val libraryDb = Database.connect("jdbc:h2:file://" + getIndexDb().path + ";IGNORECASE=TRUE")
    val albumArtCache = getImageCache()
    val libraryState = LibraryState(scope)

    fun refreshAll() {
        scope.launch {
            suspendTransaction {
                _topTracks.value = Track.all().sortedByDescending { it.lastPlayed }.take(TOP)
                _topAlbums.value = Album.all().sortedByDescending { it.lastPlayed }.take(TOP)
                _topArtists.value = Artist.all().sortedByDescending { it.lastPlayed }.take(TOP)

                _tracks.value = Track.all().sortedBy { it.name }
                _albums.value = Album.all().sortedBy { it.name }
                _artists.value = Artist.all().sortedBy { it.name }
            }
        }
    }

    fun getAlbum(albumId: Int): AlbumResult {
        var artist: Artist? = null
        var album: Album? = null
        var tracks: List<Track> = emptyList()
        transaction {
            album = Album.findById(albumId)
            if (album != null) {
                artist = album!!.artist
                tracks = Track.find { Tracks.album eq albumId }
                    .orderBy(Tracks.discNumber to SortOrder.ASC, Tracks.number to SortOrder.ASC).toList()
            }
        }
        val discs = mutableListOf<MutableList<Track>>()
        for (track in tracks) {
            if (discs.isEmpty() || discs[discs.size - 1][0].discNumber != track.discNumber) {
                discs.add(mutableListOf(track))
            } else {
                discs[discs.size - 1].add(track)
            }
        }
        return AlbumResult(artist!!, album!!, discs)
    }

    fun getArtist(artistId: Int): ArtistResult {
        var artist: Artist? = null
        var albums: List<Album> = emptyList()
        transaction {
            artist = Artist.findById(artistId)
            if (artist != null) {
                albums = Album.find { Albums.artist eq artistId }
                    .orderBy(Albums.name to SortOrder.ASC).toList()
            }
        }
        return ArtistResult(artist!!, albums)
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

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun indexLibrary(paths: List<PlatformFile>) {
        transaction {
            initDb()
        }
        logger.debug { "Indexing library... $paths" }
        val hash = hashLibrary(paths)
        if (libraryState.state.value.libraryHash != hash) {
//            coroutineScope { albumArtCache.list().map { async { it.delete() } }.awaitAll() }
            val workerCount = (Runtime.getRuntime().availableProcessors() / 2).coerceAtLeast(1)
            val processedChannel = Channel<Triple<PlatformFile, AudioMetadata, PlatformFile>>(Channel.UNLIMITED)

            val filesChannel = scope.produce(Dispatchers.IO) {
                for (path in paths) collectSingleFolder(path)
                close()
            }

            coroutineScope {
                launch(Dispatchers.IO) {
                    (1..workerCount).map {
                        scope.launch(Dispatchers.IO) {
                            for (file in filesChannel) {
                                val metadata = getMetadata(file)
                                val coverImage = cacheCoverImage(metadata)
                                processedChannel.send(Triple(file, metadata, coverImage))
                            }
                        }
                    }.joinAll()
                    processedChannel.close()
                }

                suspendTransaction {
                    Filepaths.deleteAll()
                    val leftoverTracks = Track.all().map { it.filePath }.toMutableSet()
                    for ((file, metadata, coverImage) in processedChannel) {
                        insertTrack(file, metadata, coverImage, leftoverTracks)
                    }

                    Tracks.deleteWhere { Tracks.filePath notInSubQuery Filepaths.select(Filepaths.filePath) }
                    Albums.deleteWhere { Albums.id notInSubQuery Tracks.select(Tracks.album) }
                    Artists.deleteWhere { Artists.id notInSubQuery Albums.select(Albums.artist) }
                    Filepaths.deleteAll()
                    libraryState.setLibraryHash(hash)
                }
            }
        }
        refreshAll()
        logger.debug { "Finished indexing!" }
    }

    private suspend fun ProducerScope<PlatformFile>.collectSingleFolder(file: PlatformFile) {
        if (file.isDirectory()) {
            if (file.name.startsWith('.')) return
            coroutineScope {
                for (child in file.list()) {
                    collectSingleFolder(child)
                }
            }
        } else {
            if (supportedExtensions.contains(file.extension)) send(file)
        }
    }

    private fun insertTrack(
        file: PlatformFile,
        metadata: AudioMetadata,
        coverImage: PlatformFile,
        leftoverTracks: MutableSet<String>
    ) {
        Filepath.new { filepath = file.path }
        val artistRow = Artist.find { Artists.name eq metadata.artist }.firstOrNull() ?: Artist.new {
            name = metadata.artist
            artPath = coverImage.path
            lastPlayed = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        }
        val albumRow =
            Album.find { Albums.name eq metadata.album and (Albums.artist eq artistRow.id) }.firstOrNull()
                ?: Album.new {
                    name = metadata.album
                    artPath = coverImage.path
                    artist = artistRow
                    artistName = artistRow.name
                    lastPlayed = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                }
        try {
            if (Track.find { Tracks.filePath eq file.path }.empty()) {
                Track.new {
                    name = metadata.track
                    artPath = coverImage.path
                    filePath = file.path
                    discNumber = metadata.discNum ?: 1
                    album = albumRow
                    number = metadata.trackNum
                    albumName = albumRow.name
                    artistName = artistRow.name
                    lastPlayed = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                }
            }
            leftoverTracks -= file.path
        } catch (e: ExposedSQLException) {
            logger.debug { "Error adding song: ${metadata.file.path}, $e" }
        }
    }

    private fun cacheCoverImage(metadata: AudioMetadata): PlatformFile {
        var albumArtFile = albumArtCache / getImagePathName(metadata)
        var hasArt = albumArtFile.exists()
        if (!hasArt) {
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
