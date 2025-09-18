package com.swmansion.kmpmaps.sample

import com.swmansion.kmpmaps.AppleMapPointOfInterestCategory
import com.swmansion.kmpmaps.AppleMapsPointOfInterestCategories
import com.swmansion.kmpmaps.AppleMapsProperties
import com.swmansion.kmpmaps.AppleMapsUISettings
import com.swmansion.kmpmaps.MapProperties
import com.swmansion.kmpmaps.MapType
import com.swmansion.kmpmaps.MapUISettings

actual fun createMapProperties(
    mapType: MapType,
    showUserLocation: Boolean
): MapProperties = AppleMapsProperties(
    mapType = mapType,
    isMyLocationEnabled = showUserLocation,
    isTrafficEnabled = false,
    showsBuildings = true,
    pointsOfInterest = AppleMapsPointOfInterestCategories(
        including = listOf(
            AppleMapPointOfInterestCategory.RESTAURANT
        ),
    )
)

actual fun createMapUISettings(
    showUserLocation: Boolean
): MapUISettings {
    return AppleMapsUISettings(
        compassEnabled = true,
        myLocationButtonEnabled = showUserLocation,
        zoomGesturesEnabled = true,
        scrollGesturesEnabled = true,
        rotateGesturesEnabled = true,
        tiltGesturesEnabled = true
    )
}
