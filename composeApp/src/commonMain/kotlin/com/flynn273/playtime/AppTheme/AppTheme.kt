package com.flynnd273.playtime.AppTheme

import androidx.compose.foundation.LocalScrollbarStyle
import androidx.compose.foundation.ScrollbarStyle
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.unit.dp

@Composable
expect fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
)

@Composable
fun InjectStyling(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalScrollbarStyle provides ScrollbarStyle(
            minimalHeight = 16.dp,
            thickness = 8.dp,
            shape = RoundedCornerShape(4.dp),
            hoverDurationMillis = 300,
            unhoverColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
            hoverColor = MaterialTheme.colorScheme.primary
        )
    ) {
        content()
    }
}