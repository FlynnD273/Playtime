package com.flynn273.playtime.Navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
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
    }
}