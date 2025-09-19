package com.swmansion.kmpmaps

import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.MarkerState

internal fun MapType.toGoogleMapType(): com.google.maps.android.compose.MapType {
    return when (this) {
        MapType.STANDARD -> com.google.maps.android.compose.MapType.NORMAL
        MapType.SATELLITE -> com.google.maps.android.compose.MapType.SATELLITE
        MapType.HYBRID -> com.google.maps.android.compose.MapType.HYBRID
    }
}

internal fun MapRegion.toCameraPosition(): CameraPosition {
    return CameraPosition.fromLatLngZoom(LatLng(coordinates.latitude, coordinates.longitude), zoom)
}

internal fun MapAnnotation.toMarkerState(): MarkerState {
    return MarkerState(position = LatLng(coordinates.latitude, coordinates.longitude))
}

internal fun CameraPosition.toMapRegion(): MapRegion {
    return MapRegion(
        coordinates = Coordinates(latitude = target.latitude, longitude = target.longitude),
        zoom = zoom,
    )
}
