package com.swmansion.kmpmaps

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@Composable
expect fun Map(
    region: MapRegion? = null,
    mapType: MapType = MapType.STANDARD,
    annotations: List<MapAnnotation> = emptyList(),
    showUserLocation: Boolean = false,
    onRegionChange: (MapRegion) -> Unit = {},
    onAnnotationPress: (MapAnnotation) -> Unit = {},
    modifier: Modifier = Modifier,
)
