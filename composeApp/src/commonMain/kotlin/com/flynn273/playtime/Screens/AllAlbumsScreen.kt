package com.flynn273.playtime.Screens

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flynn273.playtime.UiComponents.AlbumItems
import com.flynnd273.playtime.SharedViewModel

@Composable
fun AllAlbumsScreen(viewModel: SharedViewModel) {
    Box(modifier = Modifier.fillMaxSize()) {
        val albums by viewModel.library.albums.collectAsState()
        val scrollState = rememberLazyGridState()
        LazyVerticalGrid(
            modifier = Modifier,
            columns = GridCells.Adaptive(192.dp),
            state = scrollState,
        ) {
            AlbumItems(albums)
        }
        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
            adapter = rememberScrollbarAdapter(
                scrollState = scrollState
            )
        )
    }
}