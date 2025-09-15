package com.swmansion.kmpmaps

public data class Coordinates(val latitude: Double, val longitude: Double)

public data class CameraPosition(
    val coordinates: Coordinates,
    val zoom: Float,
    val bearing: Float = 0f,
    val tilt: Float = 0f,
)

public data class GoogleMapsCircle(
    val center: Coordinates,
    val radius: Double,
    val strokeColor: String? = null,
    val strokeWidth: Float = 0f,
    val fillColor: String? = null,
    val visible: Boolean = true,
)

public data class GoogleMapsMarker(
    val coordinates: Coordinates,
    val title: String? = null,
    val subtitle: String? = null,
    val description: String? = null,
    val anchor: MapAnchor? = null,
    val draggable: Boolean = false,
    val flat: Boolean = false,
    val rotation: Float = 0f,
    val opacity: Float = 1f,
    val visible: Boolean = true,
)

public data class GoogleMapsPolygon(
    val coordinates: List<Coordinates>,
    val strokeColor: String? = null,
    val strokeWidth: Float = 0f,
    val fillColor: String? = null,
    val visible: Boolean = true,
)

public data class GoogleMapsPolyline(
    val coordinates: List<Coordinates>,
    val strokeColor: String? = null,
    val strokeWidth: Float = 0f,
    val visible: Boolean = true,
)

public data class GoogleMapsProperties(
    val mapType: GoogleMapsMapType = GoogleMapsMapType.NORMAL,
    val isMyLocationEnabled: Boolean = false,
    val isTrafficEnabled: Boolean = false,
    val isBuildingsEnabled: Boolean = true,
    val isIndoorEnabled: Boolean = true,
    val minZoomPreference: Float? = null,
    val maxZoomPreference: Float? = null,
)

public data class GoogleMapsUISettings(
    val compassEnabled: Boolean = true,
    val myLocationButtonEnabled: Boolean = true,
    val zoomControlsEnabled: Boolean = true,
    val scrollGesturesEnabled: Boolean = true,
    val zoomGesturesEnabled: Boolean = true,
    val tiltGesturesEnabled: Boolean = true,
    val rotateGesturesEnabled: Boolean = true,
)

public data class MapAnchor(val x: Float, val y: Float)

public enum class GoogleMapsMapType {
    HYBRID,
    NORMAL,
    SATELLITE,
    TERRAIN,
}
