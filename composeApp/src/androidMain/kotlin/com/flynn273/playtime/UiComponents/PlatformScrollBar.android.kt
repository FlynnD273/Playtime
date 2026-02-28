package com.flynn273.playtime.UiComponents

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable

@Composable
actual fun BoxScope.PlatformScrollBar(
    listState: LazyListState?,
    gridState: LazyGridState?
) {
    // Android doesn't support scrollbars
}