package com.swmansion.kmpmaps

interface MapProperties {
    val mapType: MapType
    val isMyLocationEnabled: Boolean
    val isTrafficEnabled: Boolean
    val showsBuildings: Boolean
}

interface MapUISettings {
    val compassEnabled: Boolean
    val myLocationButtonEnabled: Boolean
    val zoomGesturesEnabled: Boolean
    val scrollGesturesEnabled: Boolean
    val rotateGesturesEnabled: Boolean
    val tiltGesturesEnabled: Boolean
}

interface MapMarker {
    val coordinates: Coordinates
    val title: String?
    val subtitle: String?
}

interface MapCircle {
    val center: Coordinates
    val radius: Double
    val strokeColor: String?
    val strokeWidth: Float
    val fillColor: String?
}

interface MapPolygon {
    val coordinates: List<Coordinates>
    val strokeColor: String?
    val strokeWidth: Float
    val fillColor: String?
}

interface MapPolyline {
    val coordinates: List<Coordinates>
    val strokeColor: String?
    val strokeWidth: Float
}

enum class MapType {
    STANDARD,
    SATELLITE,
    HYBRID,
    TERRAIN
}

data class Coordinates(
    val latitude: Double,
    val longitude: Double
)

data class CameraPosition(
    val coordinates: Coordinates,
    val zoom: Float,
    val bearing: Float = 0f,
    val tilt: Float = 0f
)

data class CommonMapCircle(
    override val center: Coordinates,
    override val radius: Double,
    override val strokeColor: String? = null,
    override val strokeWidth: Float = 2f,
    override val fillColor: String? = null,
    val visible: Boolean = true,
) : MapCircle

data class CommonMapPolygon(
    override val coordinates: List<Coordinates>,
    override val strokeColor: String? = null,
    override val strokeWidth: Float = 2f,
    override val fillColor: String? = null,
    val visible: Boolean = true,
) : MapPolygon

data class CommonMapPolyline(
    override val coordinates: List<Coordinates>,
    override val strokeColor: String? = null,
    override val strokeWidth: Float = 2f,
    val visible: Boolean = true,
) : MapPolyline

data class CommonMapMarker(
    override val coordinates: Coordinates,
    override val title: String? = null,
    override val subtitle: String? = null,
    val visible: Boolean = true,
) : MapMarker
