package com.swmansion.kmpmaps

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
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
    val googleMapType = when (mapType) {
        MapType.STANDARD -> com.google.maps.android.compose.MapType.NORMAL
        MapType.SATELLITE -> com.google.maps.android.compose.MapType.SATELLITE
        MapType.HYBRID -> com.google.maps.android.compose.MapType.HYBRID
    }
    
    val cameraPositionState = rememberCameraPositionState {
        region?.let { reg ->
            position = CameraPosition.fromLatLngZoom(
                LatLng(reg.coordinates.latitude, reg.coordinates.longitude),
                reg.zoom
            )
        }
    }
    
    GoogleMap(
        modifier = modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(
            mapType = googleMapType,
            isMyLocationEnabled = showUserLocation
        ),
        uiSettings = MapUiSettings(
            myLocationButtonEnabled = showUserLocation,
            zoomControlsEnabled = true
        ),
    ) {
        annotations.forEach { annotation ->
            Marker(
                state = MarkerState(
                    position = LatLng(
                        annotation.coordinates.latitude,
                        annotation.coordinates.longitude
                    )
                ),
                title = annotation.title,
                snippet = annotation.subtitle,
                onClick = {
                    onAnnotationPress(annotation)
                    true
                }
            )
        }
    }
    
    LaunchedEffect(cameraPositionState.position) {
        val position = cameraPositionState.position
        val target = position.target
        val zoom = position.zoom
        
        val mapRegion = MapRegion(
            coordinates = Coordinates(
                latitude = target.latitude,
                longitude = target.longitude,
            ),
            zoom = zoom
        )
        
        onRegionChange(mapRegion)
    }
}
