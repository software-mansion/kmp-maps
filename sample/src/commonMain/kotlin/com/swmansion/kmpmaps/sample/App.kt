package com.swmansion.kmpmaps.sample

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.swmansion.kmpmaps.core.MapConfiguration
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    LaunchedEffect(Unit) {
        MapConfiguration.initialize(googleMapsApiKey = BuildKonfig.MAPS_API_KEY)
    }

    MaterialTheme(if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()) {
        MapsScreen()
    }
}
