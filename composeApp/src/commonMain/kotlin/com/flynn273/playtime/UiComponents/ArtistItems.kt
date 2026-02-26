package com.flynn273.playtime.UiComponents

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flynn273.playtime.Database.Artist

fun LazyListScope.ArtistItems(artists: List<Artist>) {
    items(artists) {
        ListItem(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 16.dp, vertical = 4.dp),
            headlineContent = { Text(it.name) }
        )
        HorizontalDivider()
    }
}