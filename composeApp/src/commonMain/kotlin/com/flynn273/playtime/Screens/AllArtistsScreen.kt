package com.flynn273.playtime.Screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.flynn273.playtime.Padding
import com.flynn273.playtime.Sizes
import com.flynn273.playtime.UiComponents.ArtistCard
import com.flynn273.playtime.UiComponents.PlatformScrollBar
import com.flynnd273.playtime.SharedViewModel

@Composable
fun AllArtistsScreen(
    viewModel: SharedViewModel,
    nc: NavHostController
) {
    Box(modifier = Modifier.fillMaxSize().padding(Padding.LargePadding)) {
        val artists by viewModel.library.artists.collectAsState()
        val scrollState = rememberLazyGridState()
        LazyVerticalGrid(
            modifier = Modifier,
            columns = GridCells.Adaptive(Sizes.LargeImage),
            state = scrollState,
        ) {
            items(artists) {
                ArtistCard(it, nc)
            }
        }
        PlatformScrollBar(gridState = scrollState)
    }
}