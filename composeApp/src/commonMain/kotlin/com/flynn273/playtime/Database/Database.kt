package com.flynn273.playtime.Database

import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass
import org.jetbrains.exposed.v1.datetime.datetime
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

object Artists : IntIdTable("artists") {
    val name = varchar("name", 512).uniqueIndex()
    val lastPlayed = datetime("last_played")
}

object Albums : IntIdTable("albums") {
    val name = varchar("name", 512).index()
    val disc = integer("disc")
    val discTotal = integer("disc_total")
    val artPath = varchar("artPath", 512)
    val artist = reference("artist", Artists.id)
    val lastPlayed = datetime("last_played")

    init {
        uniqueIndex(name, artist)
    }
}

object Tracks : IntIdTable("tracks") {
    val name = varchar("name", 512).index()
    val artPath = varchar("art_path", 512)
    val filePath = varchar("file_path", 512)
    val album = reference("album", Albums.id)
    val lastPlayed = datetime("last_played")
}

object Playlists : IntIdTable("playlists") {
    val name = varchar("name", 128).index()
    val path = varchar("path", 512).uniqueIndex()
    val lastPlayed = datetime("last_played")
}

object PlaylistTracks : IntIdTable("playlist_tracks") {
    val playlist = reference("playlist", Playlists.id)
    val track = reference("track", Tracks.id)
}

data class StaticArtist(val name: String, val lastPlayed: LocalDateTime) {
    override fun toString(): String {
        return "Artist($name)"
    }
}

class Artist(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Artist>(Artists)

    var name by Artists.name
    var lastPlayed by Artists.lastPlayed
    override fun toString(): String {
        return "Artist($name)"
    }
}

class Album(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Album>(Albums)

    var name by Albums.name
    var disc by Albums.disc
    var discTotal by Albums.discTotal
    var artPath by Albums.artPath
    var artist by Artist referencedOn Albums.artist
    var lastPlayed by Albums.lastPlayed
    override fun toString(): String {
        return "Album($name by $artist)"
    }
}

data class StaticAlbum(
    val name: String,
    val disc: Int,
    val discTotal: Int,
    val artPath: String,
    val artist: StaticArtist,
    val lastPlayed: LocalDateTime
) {
    override fun toString(): String {
        return "Album($name by ${artist.name})"
    }
}

class Track(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Track>(Tracks)

    var name by Tracks.name
    var artPath by Tracks.artPath
    var filePath by Tracks.filePath
    var album by Album referencedOn Tracks.album
    var lastPlayed by Tracks.lastPlayed
    override fun toString(): String {
        return "Track($name in $album)"
    }
}

data class StaticTrack(
    val name: String,
    val artPath: String,
    val filePath: String,
    val album: StaticAlbum,
    val lastPlayed: LocalDateTime
) {
    override fun toString(): String {
        return "Track($name in ${album.name} by ${album.artist.name})"
    }
}

class Playlist(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Playlist>(Playlists)

    var name by Playlists.name
    var path by Playlists.path
    var lastPlayed by Playlists.lastPlayed
    override fun toString(): String {
        return "Playlist($name)"
    }
}

class PlaylistTrack(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<PlaylistTrack>(PlaylistTracks)

    var playlist by PlaylistTrack referencedOn PlaylistTracks.playlist
    var track by PlaylistTrack referencedOn PlaylistTracks.track
    override fun toString(): String {
        return "PlaylistTrack(playlist_id=$playlist, track_id=$track)"
    }
}

fun initDb() {
    transaction {
        SchemaUtils.create(Artists)
        SchemaUtils.create(Albums)
        SchemaUtils.create(Tracks)
        SchemaUtils.create(Playlists)
        SchemaUtils.create(PlaylistTracks)
    }
}