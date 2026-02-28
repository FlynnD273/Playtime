package com.flynn273.playtime.UiComponents

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.flynn273.playtime.Database.Track
import com.flynn273.playtime.Padding
import com.flynn273.playtime.Sizes

@Composable
fun TrackItem(track: Track, nc: NavHostController, showImage: Boolean = true) {
    ListItem(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Padding.LargePadding, vertical = Padding.MediumPadding),
        leadingContent = {
            if (showImage) {
                AsyncImage(
                    model = track.artPath,
                    contentDescription = null,
                    modifier = Modifier.width(Sizes.SmallImage).aspectRatio(1f)
                )
            } else {
                Text("${track.number}.")
            }
        },
        headlineContent = { Text(track.name) },
        supportingContent = { Text(track.artistName) }
    )
    HorizontalDivider()
}