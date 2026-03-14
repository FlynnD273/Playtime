package com.flynn273.playtime.Database

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass
import org.jetbrains.exposed.v1.datetime.datetime
import org.jetbrains.exposed.v1.jdbc.SchemaUtils

open class ArtistsTable(name: String) : IntIdTable(name) {
    val name = varchar("name", 1024).uniqueIndex()
    val artPath = varchar("artPath", 1024)
    val lastPlayed = datetime("last_played")
}

object Artists : ArtistsTable("artists")

open class AlbumsTable(name: String, artistsTable: ArtistsTable) : IntIdTable(name) {
    val name = varchar("name", 1024).index()
    val artPath = varchar("artPath", 1024)
    val artist = reference("artist", artistsTable.id).index()
    val artistName = varchar("artist_name", 1024)

    val lastPlayed = datetime("last_played")
}

object Albums : AlbumsTable("albums", Artists)

open class TracksTabls(name: String, albumsTable: AlbumsTable) : IntIdTable(name) {
    val name = varchar("name", 1024).index()
    val artPath = varchar("art_path", 1024)
    val filePath = varchar("file_path", 1024)
    val album = reference("album", albumsTable.id).index()
    val albumName = varchar("album_name", 1024)
    val artistName = varchar("artist_name", 1024)
    val number = integer("number")
    val discNumber = integer("disc_number")

    val lastPlayed = datetime("last_played")
}

object Tracks : TracksTabls("tracks", Albums)

open class PlaylistsTable(name: String) : IntIdTable(name) {
    val name = varchar("name", 128).uniqueIndex()
    val path = varchar("path", 1024).uniqueIndex()
    val lastPlayed = datetime("last_played")
}

object Playlists : PlaylistsTable("playlists")

open class PlaylistTracksTabls(name: String) : IntIdTable(name) {
    val playlist = reference("playlist", Playlists.id)
    val track = reference("track", Tracks.id)
}

object PlaylistTracks : PlaylistTracksTabls("playlist_tracks")

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

object Filepaths : IntIdTable("filepaths") {
    val filePath = varchar("file_path", 1024)
}

class Filepath(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Filepath>(Filepaths)

    var filepath by Filepaths.filePath
}


fun initDb() {
    SchemaUtils.create(Artists, Albums, Tracks, Playlists, PlaylistTracks, Filepaths)
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