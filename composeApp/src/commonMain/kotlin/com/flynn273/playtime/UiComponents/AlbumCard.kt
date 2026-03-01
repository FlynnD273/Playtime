package com.flynn273.playtime.UiComponents

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CardDefaults
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

@Composable
fun AlbumCard(album: Album, nc: NavHostController) {
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
        Text(
            album.artistName,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}