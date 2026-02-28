package com.flynn273.playtime.Screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.flynn273.playtime.Padding
import com.flynn273.playtime.UiComponents.PlatformScrollBar
import com.flynn273.playtime.UiComponents.TrackItem
import com.flynnd273.playtime.SharedViewModel

@Composable
fun AllTracksScreen(
    viewModel: SharedViewModel,
    nc: NavHostController
) {
    Box(modifier = Modifier.fillMaxSize().padding(Padding.LargePadding)) {
        val tracks by viewModel.library.tracks.collectAsState()
        val scrollState = rememberLazyListState()
        LazyColumn(
            modifier = Modifier,
            scrollState,
        ) {
            items(tracks) {
                TrackItem(it, nc)
            }
        }
        PlatformScrollBar(listState = scrollState)
    }
}