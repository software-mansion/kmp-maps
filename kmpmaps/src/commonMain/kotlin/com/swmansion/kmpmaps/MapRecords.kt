package com.swmansion.kmpmaps

data class Coordinates(
    val latitude: Double,
    val longitude: Double,
)
data class MapAnnotation(
    val coordinates: Coordinates,
    val title: String? = null,
    val subtitle: String? = null,
)

data class MapRegion(
    val coordinates: Coordinates,
    val zoom: Float
)

enum class MapType {
    STANDARD,
    SATELLITE,
    HYBRID,
}
