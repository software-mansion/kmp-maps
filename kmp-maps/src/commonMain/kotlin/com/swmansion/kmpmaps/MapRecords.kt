package com.swmansion.kmpmaps

public data class Coordinates(val latitude: Double, val longitude: Double)

public data class MapAnnotation(
    val coordinates: Coordinates,
    val title: String? = null,
    val subtitle: String? = null,
)

public data class MapRegion(val coordinates: Coordinates, val zoom: Float)

public enum class MapType {
    STANDARD,
    SATELLITE,
    HYBRID,
}
