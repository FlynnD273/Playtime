package com.flynn273.playtime.Screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.navigation.NavHostController
import com.flynn273.playtime.Database.Album
import com.flynn273.playtime.Database.Artist
import com.flynn273.playtime.Database.Track
import com.flynn273.playtime.Navigation.Destination
import com.flynn273.playtime.Padding
import com.flynn273.playtime.UiComponents.AlbumCard
import com.flynn273.playtime.UiComponents.ArtistCard
import com.flynn273.playtime.UiComponents.TrackCard
import com.flynnd273.playtime.SharedViewModel
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(viewModel: SharedViewModel, nc: NavHostController) {
    @Composable
    fun <T> SectionCard(
        dest: Destination, label: String, items: StateFlow<List<T>>,
        itemContent: @Composable (T, NavHostController) -> Unit
    ) {
        Card(modifier = Modifier.padding(vertical = Padding.MediumPadding)) {
            Box(modifier = Modifier.padding(horizontal = Padding.LargePadding, vertical = Padding.MediumPadding)) {
                Column {
                    Text(
                        label,
                        modifier = Modifier.fillMaxWidth().clickable(
                            onClick = { nc.navigate(dest.route) },
                            indication = null,
                            interactionSource = null,
                        ),
                        fontWeight = FontWeight.Bold,
                        fontSize = 1.5.em
                    )
                    val artists by items.collectAsState(initial = emptyList())
                    val topArtists = artists.take(20)
                    val scrollState = rememberLazyListState()
                    LazyRow(state = scrollState, modifier = Modifier.clip(CardDefaults.shape)) {
                        items(topArtists) {
                            itemContent(it, nc)
                        }
                    }
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        val scrollState = rememberScrollState()
        Column(modifier = Modifier.verticalScroll(scrollState).padding(Padding.LargePadding)) {
            SectionCard(
                Destination.ARTISTS, "Artists", viewModel.library.topArtists,
                { artist: Artist, controller: NavHostController -> ArtistCard(artist, controller) })
            SectionCard(
                Destination.ALBUMS, "Albums", viewModel.library.topAlbums,
                { album: Album, controller: NavHostController -> AlbumCard(album, controller) })
            SectionCard(
                Destination.TRACKS, "Tracks", viewModel.library.topTracks,
                { track: Track, controller: NavHostController -> TrackCard(track, controller) })
        }
    }
}