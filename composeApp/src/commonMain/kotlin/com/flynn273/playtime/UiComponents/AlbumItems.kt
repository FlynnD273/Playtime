package com.flynn273.playtime.UiComponents

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import coil3.compose.AsyncImage
import com.flynn273.playtime.Database.Album

fun LazyGridScope.AlbumItems(albums: List<Album>) {
    items(albums) {
        Card(modifier = Modifier.padding(4.dp).fillMaxSize()) {
            Column {
                AsyncImage(
                    model = it.artPath,
                    contentDescription = null,
                    modifier = Modifier.fillMaxHeight().aspectRatio(1f)
                )
                Text(
                    it.name,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    fontWeight = FontWeight.Bold,
                    fontSize = 1.25.em
                )
                Text(it.artistName, overflow = TextOverflow.Ellipsis, maxLines = 1)
            }
        }
    }
}
