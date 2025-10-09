package com.swmansion.kmpmaps.sample

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme(if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()) {
        MapsScreen()
    }
}
