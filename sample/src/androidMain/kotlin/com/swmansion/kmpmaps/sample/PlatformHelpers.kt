package com.swmansion.kmpmaps.sample

import com.swmansion.kmpmaps.MapProperties
import com.swmansion.kmpmaps.MapType
import com.swmansion.kmpmaps.MapUISettings

actual fun createMapProperties(
    mapType: MapType,
    showUserLocation: Boolean
): MapProperties {
    return MapProperties(
        mapType = mapType,
        isMyLocationEnabled = showUserLocation,
        isTrafficEnabled = true,
        selectionEnabled = true,
        androidIsBuildingEnabled = true,
        androidIsIndoorEnabled = true,
        androidMinZoomPreference = 3f,
        androidMaxZoomPreference = 21f,
    )
}

actual fun createMapUISettings(
    showUserLocation: Boolean
): MapUISettings {
    return MapUISettings(
        compassEnabled = true,
        myLocationButtonEnabled = showUserLocation,
        scaleBarEnabled = true,
    )
}
