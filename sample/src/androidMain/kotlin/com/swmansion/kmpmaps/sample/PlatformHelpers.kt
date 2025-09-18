package com.swmansion.kmpmaps.sample

import com.swmansion.kmpmaps.GoogleMapsProperties
import com.swmansion.kmpmaps.GoogleMapsUISettings
import com.swmansion.kmpmaps.MapProperties
import com.swmansion.kmpmaps.MapType
import com.swmansion.kmpmaps.MapUISettings

actual fun createMapProperties(
    mapType: MapType,
    showUserLocation: Boolean
): MapProperties {
    return GoogleMapsProperties(
        mapType = mapType,
        isMyLocationEnabled = showUserLocation,
        isTrafficEnabled = false,
        showsBuildings = true,
        isIndoorEnabled = true
    )
}

actual fun createMapUISettings(
    showUserLocation: Boolean
): MapUISettings {
    return GoogleMapsUISettings(
        compassEnabled = true,
        myLocationButtonEnabled = showUserLocation,
        zoomControlsEnabled = true,
        scrollGesturesEnabled = true,
        zoomGesturesEnabled = true,
        tiltGesturesEnabled = true,
        rotateGesturesEnabled = true
    )
}
