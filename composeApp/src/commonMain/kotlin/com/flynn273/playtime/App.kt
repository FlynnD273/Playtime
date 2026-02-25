package com.flynnd273.playtime

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
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
                .background(MaterialTheme.colorScheme.background)
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
            val tracks by viewModel.library.tracks.collectAsState()
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 8.dp)
            ) {
                items(tracks) {
                    ListItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        leadingContent = {
                            AsyncImage(
                                model = it.artPath,
                                contentDescription = null,
                                modifier = Modifier.fillMaxHeight().aspectRatio(1f)
                            )
                        },
                        headlineContent = { Text(it.name) },
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}
