package com.swmansion.kmpmaps.sample

import com.swmansion.kmpmaps.AppleMapPointOfInterestCategory
import com.swmansion.kmpmaps.AppleMapsPointOfInterestCategories
import com.swmansion.kmpmaps.MapProperties
import com.swmansion.kmpmaps.MapType
import com.swmansion.kmpmaps.MapUISettings

actual fun createMapProperties(mapType: MapType, showUserLocation: Boolean): MapProperties =
    MapProperties(
        mapType = mapType,
        isMyLocationEnabled = showUserLocation,
        isTrafficEnabled = false,
        applePointsOfInterest =
            AppleMapsPointOfInterestCategories(
                including = listOf(AppleMapPointOfInterestCategory.RESTAURANT)
            ),
    )

actual fun createMapUISettings(showUserLocation: Boolean): MapUISettings {
    return MapUISettings(compassEnabled = true, myLocationButtonEnabled = showUserLocation)
}
