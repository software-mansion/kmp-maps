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
actual fun MapView(
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
                LatLng(reg.latitude, reg.longitude),
                calculateZoomLevel(reg.latitudeDelta, reg.longitudeDelta)
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
                    position = LatLng(annotation.latitude, annotation.longitude)
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
        
        val latitudeDelta = calculateLatitudeDelta(zoom)
        val longitudeDelta = calculateLongitudeDelta(zoom, target.latitude)
        
        val mapRegion = MapRegion(
            latitude = target.latitude,
            longitude = target.longitude,
            latitudeDelta = latitudeDelta,
            longitudeDelta = longitudeDelta
        )
        
        onRegionChange(mapRegion)
    }
}

private fun calculateZoomLevel(latitudeDelta: Double, longitudeDelta: Double): Float {
    val latZoom = Math.log(360.0 / latitudeDelta) / Math.log(2.0)
    val lngZoom = Math.log(360.0 / longitudeDelta) / Math.log(2.0)
    return Math.min(latZoom, lngZoom).toFloat()
}

private fun calculateLatitudeDelta(zoom: Float): Double {
    return 360.0 / Math.pow(2.0, zoom.toDouble())
}

private fun calculateLongitudeDelta(zoom: Float, latitude: Double): Double {
    val latRad = Math.toRadians(latitude)
    val lngDelta = 360.0 / Math.pow(2.0, zoom.toDouble())
    return lngDelta / Math.cos(latRad)
}
