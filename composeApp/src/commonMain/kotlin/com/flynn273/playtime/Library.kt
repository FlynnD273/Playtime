package com.flynn273.playtime

import com.flynn273.playtime.Utils.getIndexDb
import com.flynn273.playtime.Utils.getMetadata
import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.vinceglb.filekit.*
import kotlinx.coroutines.CoroutineScope
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase

private val logger = KotlinLogging.logger {}

class Library(scope: CoroutineScope) {
    val supportedExtensions = setOf("mp3", "m4a", "m4p", "ogg", "vorbis", "flac", "wav", "aif", "dsf", "wma")
    val libraryDb = R2dbcDatabase.connect("r2dbc:h2:file://" + getIndexDb().path)

    fun indexLibrary(paths: List<PlatformFile>) {
        logger.debug { "Indexing DB... $paths" }
        for (path in paths) {
            indexSingleFolder(path)
        }
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
                logger.debug { metadata }
            }
        }
    }
}

object Artists : IntIdTable("artists") {
    val name = varchar("name", 128).index()
    val artPath = varchar("artPath", 256)
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