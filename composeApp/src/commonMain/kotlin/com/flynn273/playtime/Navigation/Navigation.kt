package com.flynn273.playtime.Navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.flynn273.playtime.Screens.AlbumScreen
import com.flynnd273.playtime.SharedViewModel

val ENTER_TRANS = fadeIn(animationSpec = tween(200))
val EXIT_TRANS = fadeOut(animationSpec = tween(200))

@Composable
fun AppNavController(
    navController: NavHostController,
    startDestination: Destination,
    viewModel: SharedViewModel,
    modifier: Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination.route,
        modifier = modifier,
        enterTransition = { ENTER_TRANS },
        exitTransition = { EXIT_TRANS },
        popEnterTransition = { ENTER_TRANS },
        popExitTransition = { EXIT_TRANS }) {
        Destination.entries.forEach { destination ->
            composable(destination.route) {
                destination.screen(viewModel, navController)()
            }
        }
        composable<AlbumRoute> {
            val route: AlbumRoute = it.toRoute()
            AlbumScreen(viewModel, navController, route.id)
        }
        composable<TrackRoute> {
            val route: TrackRoute = it.toRoute()
            AlbumScreen(viewModel, navController, route.albumId, route.trackId)
        }
    }
}