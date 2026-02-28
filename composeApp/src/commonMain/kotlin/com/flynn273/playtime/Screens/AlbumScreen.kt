package com.flynn273.playtime.Screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.flynn273.playtime.AlbumResult
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
            LazyColumn(
                state = scrollState,
            ) {
                items(result?.tracks ?: emptyList()) {
                    TrackItem(it, nc, false)
                }
            }
            this@Box.PlatformScrollBar(listState = scrollState)
        }
    }
}