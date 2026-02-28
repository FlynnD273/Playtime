package com.flynnd273.playtime.AppTheme

import androidx.compose.foundation.LocalScrollbarStyle
import androidx.compose.foundation.ScrollbarStyle
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.flynn273.playtime.Padding

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

@Composable
actual fun InjectStyling(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalScrollbarStyle provides ScrollbarStyle(
            minimalHeight = Padding.LargePadding,
            thickness = Padding.MediumPadding,
            shape = RoundedCornerShape(Padding.SmallPadding),
            hoverDurationMillis = 300,
            unhoverColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
            hoverColor = MaterialTheme.colorScheme.primary
        )
    ) {
        content()
    }
}