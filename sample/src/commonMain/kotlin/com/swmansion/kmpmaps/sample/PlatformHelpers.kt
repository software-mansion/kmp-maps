package com.swmansion.kmpmaps.sample

import com.swmansion.kmpmaps.MapProperties
import com.swmansion.kmpmaps.MapType
import com.swmansion.kmpmaps.MapUISettings

expect fun createMapProperties(mapType: MapType, showUserLocation: Boolean): MapProperties

expect fun createMapUISettings(showUserLocation: Boolean): MapUISettings
