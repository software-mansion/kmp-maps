package com.swmansion.kmpmaps

public data class MapProperties(
    val isMyLocationEnabled: Boolean = false,
    val isTrafficEnabled: Boolean = true,
    val isBuildingEnabled: Boolean = true,
    val mapType: MapType? = MapType.NORMAL,
    val applePointsOfInterest: AppleMapsPointOfInterestCategories? = null,
    val applePolylineTapThreshold: Float? = null,
    val androidIsIndoorEnabled: Boolean = true,
    val androidMaxZoomPreference: Float? = null,
    val androidMinZoomPreference: Float? = null,
    val androidMapStyleOptions: GoogleMapsMapStyleOptions? = null,
)

public data class MapUISettings(
    val compassEnabled: Boolean = false,
    val myLocationButtonEnabled: Boolean = false,
    val scaleBarEnabled: Boolean = true,
    val togglePitchEnabled: Boolean = true,
    val scrollEnabled: Boolean = true,
    val zoomEnabled: Boolean = true,
    val appleRotateGesturesEnabled: Boolean = true,
    val androidIndoorLevelPickerEnabled: Boolean = true,
    val androidMapToolbarEnabled: Boolean = true,
    val androidRotationGesturesEnabled: Boolean = true,
    val androidScrollGesturesEnabledDuringRotateOrZoom: Boolean = true,
    val androidTiltGesturesEnabled: Boolean = true,
    val androidZoomControlsEnabled: Boolean = true,
)

public data class MapMarker(
    val coordinates: Coordinates,
    val title: String? = "No title was provided",
    val appleTintColor: Color? = null,
    val androidAnchor: GoogleMapsAnchor? = null,
    val androidDraggable: Boolean = false,
    val androidSnippet: String? = null,
    val androidZIndex: Float? = null,
)

public data class MapCircle(
    val center: Coordinates,
    val radius: Float,
    val color: Color? = null,
    val lineColor: Color? = null,
    val lineWidth: Float? = null,
)

public data class MapPolygon(
    val coordinates: List<Coordinates>,
    val lineWidth: Float,
    val color: Color? = null,
    val lineColor: Color? = null,
)

public data class MapPolyline(
    val coordinates: List<Coordinates>,
    val width: Float,
    val lineColor: Color? = null,
)

public data class Coordinates(val latitude: Double, val longitude: Double)

public data class CameraPosition(
    val coordinates: Coordinates,
    val zoom: Float,
    val androidBearing: Float? = null,
    val androidTilt: Float? = null,
)

public data class Color(
    val hexColor: String? = null,
    val appleColor: AppleColors? = null,
    val androidColor: AndroidColors? = null,
)
