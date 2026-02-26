package com.flynn273.playtime.Screens

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.flynn273.playtime.UiComponents.TrackItems
import com.flynnd273.playtime.SharedViewModel

@Composable
fun AllTracksScreen(viewModel: SharedViewModel) {
    Box(modifier = Modifier.fillMaxSize()) {
        val tracks by viewModel.library.tracks.collectAsState()
        val scrollState = rememberLazyListState()
        LazyColumn(
            modifier = Modifier,
            scrollState,
        ) {
            TrackItems(tracks)
        }
        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
            adapter = rememberScrollbarAdapter(
                scrollState = scrollState
            )
        )
    }
}