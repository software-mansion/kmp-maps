package com.swmansion.kmpmaps

data class MapAnnotation(
    val id: String,
    val latitude: Double,
    val longitude: Double,
    val title: String? = null,
    val subtitle: String? = null,
)

data class MapRegion(
    val latitude: Double,
    val longitude: Double,
    val latitudeDelta: Double,
    val longitudeDelta: Double,
)

enum class MapType {
    STANDARD,
    SATELLITE,
    HYBRID,
}
