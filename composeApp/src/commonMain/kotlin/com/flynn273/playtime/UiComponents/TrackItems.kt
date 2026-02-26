package com.flynn273.playtime.UiComponents

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.flynn273.playtime.Database.Track

fun LazyListScope.TrackItems(tracks: List<Track>) {
    items(tracks) {
        ListItem(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 16.dp, vertical = 4.dp),
            leadingContent = {
                AsyncImage(
                    model = it.artPath,
                    contentDescription = null,
                    modifier = Modifier.fillMaxHeight().aspectRatio(1f)
                )
            },
            headlineContent = { Text(it.name) },
            supportingContent = { Text(it.artistName) }
        )
        HorizontalDivider()
    }
}