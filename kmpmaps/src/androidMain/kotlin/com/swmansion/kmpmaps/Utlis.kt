package com.swmansion.kmpmaps

import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings

fun createMapUiSettings(showUserLocation: Boolean): MapUiSettings {
    return MapUiSettings(
        myLocationButtonEnabled = showUserLocation,
        zoomControlsEnabled = true,
    )
}

fun createMapProperties(
    mapType: MapType,
    showUserLocation: Boolean
): MapProperties {
    return MapProperties(
        mapType = mapType.toGoogleMapType(),
        isMyLocationEnabled = showUserLocation,
    )
}

