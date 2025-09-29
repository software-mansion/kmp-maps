package com.swmansion.kmpmaps.sample

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme(
        colorScheme =
            lightColorScheme(
                primary = Color(0xFF6750A4),
                secondary = Color(0xFF625B71),
                tertiary = Color(0xFF7D5260),
                primaryContainer = Color(0xFFEADDFF),
                secondaryContainer = Color(0xFFE8DEF8),
                tertiaryContainer = Color(0xFFFFD8E4),
            )
    ) {
        MapsScreen()
    }
}
