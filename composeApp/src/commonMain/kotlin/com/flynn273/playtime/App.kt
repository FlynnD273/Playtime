package com.flynnd273.playtime

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.flynn273.playtime.Navigation.AppNavController
import com.flynn273.playtime.Navigation.Destination
import com.flynnd273.playtime.AppTheme.AppTheme
import io.github.vinceglb.filekit.dialogs.FileKitDialogSettings

@Composable
@Preview
fun App(
    dialogSettings: FileKitDialogSettings = FileKitDialogSettings(),
    viewModel: SharedViewModel = viewModel { SharedViewModel() }
) {
    AppTheme {
        val navController = rememberNavController()
        val startDestination = Destination.LIBRARY
        var selectedDestination by rememberSaveable { mutableIntStateOf(startDestination.ordinal) }
        Scaffold(
            topBar = {
                val isPicking by viewModel.isPickingFolder.collectAsState()
                Button(
                    enabled = !isPicking,
                    onClick = { viewModel.chooseFolder(dialogSettings) }) {
                    Text(if (isPicking) "Picking directory..." else "Choose music folder")
                }

            },
            bottomBar = {
                NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
                    Destination.entries.filter { it.icon != null }.forEachIndexed { index, destination ->
                        NavigationBarItem(
                            selected = index == selectedDestination,
                            onClick = {
                                navController.navigate(route = destination.route)
                                selectedDestination = index
                            },
                            icon = {
                                Icon(
                                    imageVector = destination.icon!!,
                                    contentDescription = destination.contentDescription
                                )
                            },
                            label = { Text(destination.label!!) }
                        )
                    }
                }
            }) {
            AppNavController(navController, startDestination, viewModel, Modifier.padding(it))
        }
    }
}
