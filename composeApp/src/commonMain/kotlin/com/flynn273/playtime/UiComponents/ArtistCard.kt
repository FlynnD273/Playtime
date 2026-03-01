package com.flynn273.playtime.UiComponents

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.flynn273.playtime.Database.Artist
import com.flynn273.playtime.FontSizes
import com.flynn273.playtime.Padding
import com.flynn273.playtime.Sizes

@Composable
fun ArtistCard(artist: Artist, nc: NavHostController) {
    Column(modifier = Modifier.width(Sizes.Image).padding(Padding.MediumPadding)) {
        AsyncImage(
            model = artist.artPath,
            contentDescription = artist.name,
            modifier = Modifier.aspectRatio(1f).fillMaxWidth().clip(CircleShape)
        )
        Text(
            artist.name,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            fontWeight = FontWeight.Bold,
            fontSize = FontSizes.medium,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}