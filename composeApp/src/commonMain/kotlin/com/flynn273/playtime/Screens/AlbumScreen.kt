package com.flynn273.playtime.Screens

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavHostController
import com.flynn273.playtime.AlbumResult
import com.flynn273.playtime.FontSizes
import com.flynn273.playtime.Padding
import com.flynn273.playtime.UiComponents.PlatformScrollBar
import com.flynn273.playtime.UiComponents.TrackItem
import com.flynnd273.playtime.SharedViewModel

@Composable
fun AlbumScreen(viewModel: SharedViewModel, nc: NavHostController, albumId: Int, trackId: Int? = null) {
    val result by produceState<AlbumResult?>(null) { value = viewModel.library.getAlbum(albumId) }
    Scaffold(topBar = {
        Column {
            Text("Album")
            Text(result?.album?.name ?: "No album fount")
        }
    }) {
        Box(modifier = Modifier.padding(it)) {
            val scrollState = rememberLazyListState()
            if (result != null && trackId != null) {
                LaunchedEffect(result) {
                    var total = 1
                    var discIndex = 0
                    var subIndex = 0
                    while (discIndex < result!!.discs.size) {
                        val disc = result!!.discs[discIndex]
                        if (subIndex >= disc.size) {
                            subIndex = 0
                            discIndex++
                            continue
                        }
                        if (disc[subIndex].id.value == trackId) {
                            break
                        }
                        total++
                        subIndex++
                    }
                    if (result!!.discs.size == 1) {
                        total--
                    }
                    scrollState.animateScrollToItem(total)
                }
            }
            LazyColumn(
                state = scrollState,
            ) {
                if (result != null) {
                    if (result!!.discs?.size == 1) {
                        items(result?.discs[0] ?: emptyList()) {
                            TrackItem(it, nc, false)
                        }
                    } else {
                        result!!.discs.forEach {
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
                                TrackItem(it, nc, false)
                            }
                        }
                    }
                }
            }
            this@Box.PlatformScrollBar(listState = scrollState)
        }
    }
}