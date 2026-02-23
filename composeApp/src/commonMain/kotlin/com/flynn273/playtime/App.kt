package com.flynnd273.playtime

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flynnd273.playtime.AppTheme.AppTheme
import io.github.vinceglb.filekit.dialogs.FileKitDialogSettings

@Composable
@Preview
fun App(
    dialogSettings: FileKitDialogSettings = FileKitDialogSettings(),
    viewModel: PlayerViewModel = viewModel { PlayerViewModel() }
) {
    AppTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .safeContentPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val isPicking by viewModel.isPickingFolder.collectAsState()
            Button(
                enabled = !isPicking,
                onClick = { viewModel.chooseFolder(dialogSettings) }) {
                Text(if (isPicking) "Picking directory..." else "Choose music folder")
            }
        }
    }
}
