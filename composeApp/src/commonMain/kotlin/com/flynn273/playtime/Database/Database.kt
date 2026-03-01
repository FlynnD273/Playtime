package com.flynn273.playtime.Database

import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass
import org.jetbrains.exposed.v1.datetime.datetime
import org.jetbrains.exposed.v1.jdbc.SchemaUtils

object Artists : IntIdTable("artists") {
    val name = varchar("name", 1024).uniqueIndex()
    val artPath = varchar("artPath", 1024)
    val lastPlayed = datetime("last_played")
}

object Albums : IntIdTable("albums") {
    val name = varchar("name", 1024).index()
    val artPath = varchar("artPath", 1024)
    val artist = reference("artist", Artists.id)
    val artistName = varchar("artist_name", 1024)

    val lastPlayed = datetime("last_played")

    init {
        uniqueIndex(name, artist)
    }
}

object Tracks : IntIdTable("tracks") {
    val name = varchar("name", 1024).index()
    val artPath = varchar("art_path", 1024)
    val filePath = varchar("file_path", 1024)
    val album = reference("album", Albums.id)
    val albumName = varchar("album_name", 1024)
    val artistName = varchar("artist_name", 1024)
    val number = integer("number")
    val discNumber = integer("disc_number")

    val lastPlayed = datetime("last_played")
}

object Playlists : IntIdTable("playlists") {
    val name = varchar("name", 128).uniqueIndex()
    val path = varchar("path", 1024).uniqueIndex()
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
    var artPath by Artists.artPath
    var lastPlayed by Artists.lastPlayed
    override fun toString(): String {
        return "Artist($name)"
    }
}

class Album(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Album>(Albums)

    var name by Albums.name
    var artPath by Albums.artPath
    var artist by Artist referencedOn Albums.artist
    var artistId by Albums.artist
    var artistName by Albums.artistName
    var lastPlayed by Albums.lastPlayed
    override fun toString(): String {
        return "Album($name by $artist)"
    }
}

class Track(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Track>(Tracks)

    var name by Tracks.name
    var artPath by Tracks.artPath
    var filePath by Tracks.filePath
    var album by Album referencedOn Tracks.album
    var albumId by Tracks.album
    var albumName by Tracks.albumName
    var artistName by Tracks.artistName
    var lastPlayed by Tracks.lastPlayed
    var number by Tracks.number
    var discNumber by Tracks.discNumber
    override fun toString(): String {
        return "Track($name in $album)"
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
    SchemaUtils.create(Artists, Albums, Tracks, Playlists, PlaylistTracks)
//    transaction {
//        MigrationUtils.statementsRequiredForDatabaseMigration(
//            Artists,
//            Albums,
//            Tracks,
//            Playlists,
//            PlaylistTracks
//        )
//            .forEach {
//                exec(it)
//            }
//    }
}