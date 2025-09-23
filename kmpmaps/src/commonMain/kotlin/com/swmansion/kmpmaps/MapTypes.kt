package com.swmansion.kmpmaps

import androidx.compose.ui.graphics.ImageBitmap

data class MapProperties(
    val isMyLocationEnabled: Boolean = false,
    val isTrafficEnabled: Boolean = false,
    val selectionEnabled: Boolean = false,
    val mapType: MapType? = MapType.NORMAL,
    val appleElevation: AppleMapsMapStyleElevation? = AppleMapsMapStyleElevation.AUTOMATIC,
    val appleEmphasis: AppleMapsMapStyleEmphasis? = AppleMapsMapStyleEmphasis.AUTOMATIC,
    val applePointsOfInterest: AppleMapsPointOfInterestCategories? = null,
    val applePolylineTapThreshold: Float? = null,
    val androidIsBuildingEnabled: Boolean = false,
    val androidIsIndoorEnabled: Boolean = false,
    val androidMaxZoomPreference: Float? = null,
    val androidMinZoomPreference: Float? = null,
    val androidMapStyleOptions: GoogleMapsMapStyleOptions? = null,
)

data class MapUISettings(
    val compassEnabled: Boolean = false,
    val myLocationButtonEnabled: Boolean = false,
    val scaleBarEnabled: Boolean = false,
    val togglePitchEnabled: Boolean = false,
    val androidIndoorLevelPickerEnabled: Boolean = false,
    val androidMapToolbarEnabled: Boolean = false,
    val androidRotationGesturesEnabled: Boolean = false,
    val androidScrollGesturesEnabled: Boolean = false,
    val androidScrollGesturesEnabledDuringRotateOrZoom: Boolean = false,
    val androidTiltGesturesEnabled: Boolean = false,
    val androidTogglePitchEnabled: Boolean = false,
    val androidZoomControlsEnabled: Boolean = false,
    val androidZoomGesturesEnabled: Boolean = false,
)

data class MapMarker(
    val coordinates: Coordinates,
    val title: String? = "No title was provided",
    val subtitle: String? = "No subtitle was provided",
    val appleSystemImage: String? = null,
    val appleTintColor: String? = null,
    val androidAnchor: GoogleMapsAnchor? = null,
    val androidDraggable: Boolean = false,
    val androidIcon: ImageBitmap? = null,  // todo: not working
    val androidSnippet: String? = null,
    val androidZIndex: Float? = null,
)

data class MapCircle(
    val center: Coordinates,
    val radius: Float,
    val color: String? = null,
    val lineColor: String? = null,
    val lineWidth: Float? = null,
)

data class MapPolygon(
    val coordinates: List<Coordinates>,
    val lineWidth: Float,
    val color: String? = null,
    val lineColor: String? = null,
)

data class MapPolyline(
    val coordinates: List<Coordinates>,
    val width: Float,
    val color: String? = null,
    val lineColor: String? = null,
    val appleContourStyle: AppleMapsContourStyle? = null,
)

data class Coordinates(
    val latitude: Double,
    val longitude: Double
)

data class CameraPosition(
    val coordinates: Coordinates,
    val zoom: Float,
    val androidBearing: Float? = null,
    val androidTilt: Float? = null
)
