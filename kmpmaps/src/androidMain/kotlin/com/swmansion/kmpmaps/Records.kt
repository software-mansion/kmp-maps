package com.swmansion.kmpmaps

data class GoogleMapsProperties(
    override val mapType: MapType = MapType.STANDARD,
    override val isMyLocationEnabled: Boolean = false,
    override val isTrafficEnabled: Boolean = false,
    override val showsBuildings: Boolean = true,
    val isIndoorEnabled: Boolean = true,
    val minZoomPreference: Float? = null,
    val maxZoomPreference: Float? = null,
) : MapProperties

data class GoogleMapsUISettings(
    override val compassEnabled: Boolean = true,
    override val myLocationButtonEnabled: Boolean = true,
    val zoomControlsEnabled: Boolean = true,
    override val scrollGesturesEnabled: Boolean = true,
    override val zoomGesturesEnabled: Boolean = true,
    override val tiltGesturesEnabled: Boolean = true,
    override val rotateGesturesEnabled: Boolean = true,
) : MapUISettings

data class GoogleMapsMarker(
    override val coordinates: Coordinates,
    override val title: String? = null,
    override val subtitle: String? = null,
    val description: String? = null,
    val anchor: MapAnchor? = null,
    val draggable: Boolean = false,
    val flat: Boolean = false,
    val rotation: Float = 0f,
    val opacity: Float = 1f,
    val visible: Boolean = true,
) : MapMarker

data class GoogleMapsCircle(
    override val center: Coordinates,
    override val radius: Double,
    override val strokeColor: String? = null,
    override val strokeWidth: Float = 0f,
    override val fillColor: String? = null,
    val visible: Boolean = true,
) : MapCircle

data class GoogleMapsPolygon(
    override val coordinates: List<Coordinates>,
    override val strokeColor: String? = null,
    override val strokeWidth: Float = 0f,
    override val fillColor: String? = null,
    val visible: Boolean = true,
) : MapPolygon {
    constructor(commonPolygon: CommonMapPolygon) : this(
        coordinates = commonPolygon.coordinates,
        strokeColor = commonPolygon.strokeColor,
        strokeWidth = commonPolygon.strokeWidth,
        fillColor = commonPolygon.fillColor,
        visible = commonPolygon.visible
    )
}
data class GoogleMapsPolyline(
    override val coordinates: List<Coordinates>,
    override val strokeColor: String? = null,
    override val strokeWidth: Float = 0f,
    val visible: Boolean = true,
) : MapPolyline {
    constructor(commonPolyline: CommonMapPolyline) : this(
        coordinates = commonPolyline.coordinates,
        strokeColor = commonPolyline.strokeColor,
        strokeWidth = commonPolyline.strokeWidth,
        visible = commonPolyline.visible
    )
}

data class MapAnchor(val x: Float, val y: Float)

fun MapType.toGoogleMapsMapType(): com.google.maps.android.compose.MapType {
    return when (this) {
        MapType.STANDARD -> com.google.maps.android.compose.MapType.NORMAL
        MapType.SATELLITE -> com.google.maps.android.compose.MapType.SATELLITE
        MapType.HYBRID -> com.google.maps.android.compose.MapType.HYBRID
        MapType.TERRAIN -> com.google.maps.android.compose.MapType.TERRAIN
    }
}


enum class GoogleMapsMapType {
    HYBRID,
    NORMAL,
    SATELLITE,
    TERRAIN,
}
