package com.flynn273.playtime.Navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FeaturedPlayList
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import com.flynn273.playtime.Screens.*
import com.flynnd273.playtime.SharedViewModel
import kotlinx.serialization.Serializable

@Serializable
data class TrackRoute(val albumId: Int, val trackId: Int)

@Serializable
data class AlbumRoute(val id: Int)

@Serializable
data class ArtistRoute(val id: Int)

@Serializable
data class PlaylistRoute(val id: Int)

enum class Destination(
    val route: String,
    val screen: (SharedViewModel, NavHostController) -> @Composable () -> Unit,
    val label: String? = null,
    val icon: ImageVector? = null,
    val contentDescription: String? = null
) {
    LIBRARY("library", { vm, nc -> { LibraryScreen(vm, nc) } }, "Library", Icons.Default.Album, "Library"),
    SEARCH("search", { vm, nc -> { SearchScreen(vm, nc) } }, "Search", Icons.Default.Search, "Search"),
    PLAYLISTS(
        "playlists", { vm, nc -> { PlaylistScreen(vm, nc) } },
        "Playlists",
        Icons.AutoMirrored.Filled.FeaturedPlayList,
        "Playlists"
    ),
    TRACKS("tracks", { vm, nc -> { AllTracksScreen(vm, nc) } }),
    ALBUMS("albums", { vm, nc -> { AllAlbumsScreen(vm, nc) } }),
    ARTISTS("artists", { vm, nc -> { AllArtistsScreen(vm, nc) } }),
}