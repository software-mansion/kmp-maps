package com.swmansion.kmpmaps

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState


@Composable
actual fun Map(
    region: MapRegion?,
    mapType: MapType,
    annotations: List<MapAnnotation>,
    showUserLocation: Boolean,
    onRegionChange: (MapRegion) -> Unit,
    onAnnotationPress: (MapAnnotation) -> Unit,
    modifier: Modifier,
) {

    val cameraPositionState = rememberCameraPositionState {
        region?.let { reg ->
            position = reg.toCameraPosition()
        }
    }

    GoogleMap(
        modifier = modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = createMapProperties(mapType, showUserLocation),
        uiSettings = createMapUiSettings(showUserLocation),
    ) {
        annotations.forEach { annotation ->
            Marker(
                state = annotation.toMarkerState(),
                title = annotation.title,
                snippet = annotation.subtitle,
                onClick = {
                    onAnnotationPress(annotation)
                    true
                },
            )
        }
    }

    LaunchedEffect(cameraPositionState.position) {
        onRegionChange(cameraPositionState.position.toMapRegion())
    }
}
