package com.flynnd273.playtime

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.flynn273.playtime.Navigation.AppNavController
import com.flynn273.playtime.Navigation.Destination
import com.flynn273.playtime.UiComponents.BottomNavigationBar
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
                BottomNavigationBar(selectedDestination, navController)
            }) {
            AppNavController(navController, startDestination, viewModel, Modifier.padding(it))
        }
    }
}