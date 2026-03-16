package com.flynnd273.playtime.Screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavHostController
import com.flynnd273.playtime.FontSizes
import com.flynnd273.playtime.Padding
import com.flynnd273.playtime.SharedViewModel
import com.flynnd273.playtime.UiComponents.PlatformScrollBar
import com.flynnd273.playtime.UiComponents.TrackItem

@Composable
fun AlbumScreen(viewModel: SharedViewModel, nc: NavHostController, albumId: Int, trackId: Int? = null) {
    val result = viewModel.library.getAlbum(albumId)
    Scaffold(topBar = {
        Column {
            Text("Album")
            Text(result?.album?.name ?: "No album fount")
        }
    }) {
        Box(modifier = Modifier.padding(it)) {
            val scrollState = rememberLazyListState()
            if (trackId != null) {
                LaunchedEffect(result) {
                    scrollState.animateScrollToItem(
                        result.discs
                            .flatMap { listOf(null) + it }
                            .indexOfFirst { it?.id?.value == trackId }
                                - if (result.discs.size == 1) 1 else 0
                    )
                }
            }
            LazyColumn(
                state = scrollState,
            ) {
                if (result.discs.size == 1) {
                    items(result.discs[0] ?: emptyList()) {
                        TrackItem(viewModel, it, nc, false)
                    }
                } else {
                    result.discs.forEach {
                        item {
                            Text(
                                "Disc ${it[0].discNumber}",
                                fontSize = FontSizes.header,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth().padding(vertical = Padding.LargePadding)
                            )
                            HorizontalDivider()
                        }
                        items(it) {
                            TrackItem(viewModel, it, nc, false)
                        }
                    }
                }
            }
            this@Box.PlatformScrollBar(listState = scrollState)
        }
    }
}
