package com.swmansion.kmpmaps

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
@Composable
actual fun MapView(
    region: MapRegion?,
    mapType: MapType,
    annotations: List<MapAnnotation>,
    showUserLocation: Boolean,
    onRegionChange: (MapRegion) -> Unit,
    onAnnotationPress: (MapAnnotation) -> Unit,
    modifier: Modifier,
) {
    // TODO: Implement with Google Maps when Android support is added
    Box(modifier = modifier.fillMaxSize()) {
        Text(text = "Android MapView - Coming Soon", modifier = Modifier.fillMaxSize())
    }
}
