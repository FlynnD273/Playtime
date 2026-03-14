package com.flynn273.playtime.Screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.flynn273.playtime.Database.Album
import com.flynn273.playtime.FontSizes
import com.flynn273.playtime.Navigation.AlbumRoute
import com.flynn273.playtime.Padding
import com.flynn273.playtime.Sizes
import com.flynn273.playtime.UiComponents.PlatformScrollBar
import com.flynnd273.playtime.SharedViewModel

@Composable
fun ArtistScreen(viewModel: SharedViewModel, nc: NavHostController, artistId: Int) {
    val result = viewModel.library.getArtist(artistId)
    Scaffold(topBar = {
        Column {
            Text("Artist")
            Text(result.artist?.name ?: "No album fount")
        }
    }) {
        Box(modifier = Modifier.padding(it)) {
            val scrollState = rememberLazyGridState()
            LazyVerticalGrid(
                modifier = Modifier,
                columns = GridCells.Adaptive(Sizes.LargeImage),
                state = scrollState,
            ) {
                items(result.albums) {
                    AlbumItem(it, nc)
                }
            }
            PlatformScrollBar(gridState = scrollState)
        }
    }
}

@Composable
fun AlbumItem(album: Album, nc: NavHostController) {
    Column(modifier = Modifier.width(Sizes.Image).padding(Padding.MediumPadding).clickable(onClick = {
        nc.navigate(
            AlbumRoute(album.id.value)
        )
    })) {
        AsyncImage(
            model = album.artPath,
            contentDescription = album.name,
            modifier = Modifier.aspectRatio(1f).fillMaxWidth().clip(CardDefaults.shape)
        )
        Text(
            album.name,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            fontWeight = FontWeight.Bold,
            fontSize = FontSizes.medium,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}