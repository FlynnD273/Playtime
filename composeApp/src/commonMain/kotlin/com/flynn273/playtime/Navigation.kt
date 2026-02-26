package com.flynn273.playtime

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FeaturedPlayList
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.flynn273.playtime.Screens.*
import com.flynnd273.playtime.SharedViewModel
import io.github.oshai.kotlinlogging.KotlinLogging

enum class Destination(
    val route: String,
    val screen: (SharedViewModel) -> @Composable () -> Unit,
    val label: String? = null,
    val icon: ImageVector? = null,
    val contentDescription: String? = null
) {
    LIBRARY("library", { vm -> { LibraryScreen(vm) } }, "Library", Icons.Default.Album, "Library"),
    SEARCH("search", { vm -> { SearchScreen(vm) } }, "Search", Icons.Default.Search, "Search"),
    PLAYLISTS(
        "playlists", { vm -> { PlaylistScreen(vm) } },
        "Playlists",
        Icons.AutoMirrored.Filled.FeaturedPlayList,
        "Playlists"
    ),
    TRACKS("tracks", { vm -> { AllTracksScreen(vm) } }),
    ALBUMS("albums", { vm -> { AllAlbumsScreen(vm) } }),
    ARTISTS("artists", { vm -> { AllArtistsScreen(vm) } }),
}

private val logger = KotlinLogging.logger {}

@Composable
fun AppNavController(
    navController: NavHostController,
    startDestination: Destination,
    viewModel: SharedViewModel,
    m: Modifier
) {
    NavHost(navController = navController, startDestination = startDestination.route, modifier = m) {
        Destination.entries.forEach { destination ->
            logger.debug { "Making destination $destination" }
            composable(destination.route) {
                destination.screen(viewModel)()
            }
        }
    }
}