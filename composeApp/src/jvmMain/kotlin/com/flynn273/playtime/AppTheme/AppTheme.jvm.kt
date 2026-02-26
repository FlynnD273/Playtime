package com.flynnd273.playtime.AppTheme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

@Composable
actual fun AppTheme(
    darkTheme: Boolean,
    content: @Composable (() -> Unit)
) {
    val colorScheme = when {
        darkTheme -> darkColorScheme()
        else -> lightColorScheme()
    }

    MaterialTheme(
        colorScheme = colorScheme
    ) {
        InjectStyling { content() }
    }
}
