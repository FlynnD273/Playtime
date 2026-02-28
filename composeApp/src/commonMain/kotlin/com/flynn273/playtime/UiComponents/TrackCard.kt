package com.flynn273.playtime.UiComponents

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.em
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.flynn273.playtime.Database.Track
import com.flynn273.playtime.Padding
import com.flynn273.playtime.Sizes

@Composable
fun TrackCard(track: Track, nc: NavHostController) {
    Column(modifier = Modifier.width(Sizes.Image).padding(Padding.MediumPadding)) {
        AsyncImage(
            model = track.artPath,
            contentDescription = track.name,
            modifier = Modifier.aspectRatio(1f).fillMaxWidth()
        )
        Text(
            track.name,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            fontWeight = FontWeight.Bold,
            fontSize = 1.25.em,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Text(
            track.artistName,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}