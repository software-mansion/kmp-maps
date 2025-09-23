package com.swmansion.kmpmaps

import androidx.compose.ui.graphics.ImageBitmap

data class MapProperties(
    val isMyLocationEnabled: Boolean,
    val isTrafficEnabled: Boolean,
    val selectionEnabled: Boolean,
    val appleMapType: AppleMapsMapType?,
    val appleElevation: AppleMapsMapStyleElevation?,
    val appleEmphasis: AppleMapsMapStyleEmphasis?,
    val applePointsOfInterest: AppleMapsPointOfInterestCategories?,
    val applePolylineTapThreshold: Float?,
    val androidMapType: GoogleMapsMapType,
    val androidIsBuildingEnabled: Boolean,
    val androidIsIndoorEnabled: Boolean,
    val androidMaxZoomPreference: Float?,
    val androidMinZoomPreference: Float?
)

data class MapUISettings(
    val compassEnabled: Boolean,
    val myLocationButtonEnabled: Boolean,
    val scaleBarEnabled: Boolean,
    val togglePitchEnabled: Boolean,
    val androidIndoorLevelPickerEnabled: Boolean,
    val androidMapToolbarEnabled: Boolean,
    val androidRotationGesturesEnabled: Boolean,
    val androidScrollGesturesEnabled: Boolean,
    val androidScrollGesturesEnabledDuringRotateOrZoom: Boolean,
    val androidTiltGesturesEnabled: Boolean,
    val androidTogglePitchEnabled: Boolean,
    val androidZoomControlsEnabled: Boolean,
    val androidZoomGesturesEnabled: Boolean,
)

data class MapMarker(
    val coordinates: Coordinates,
    val title: String?,
    val appleSystemImage: String?,
    val appleTintColor: String?,
    val androidAnchor: GoogleMapsAnchor?,
    val androidDraggable: Boolean,
    val androidIcon: ImageBitmap?,
    val androidShowCallout: Boolean?,
    val androidSnippet: String?,
    val androidZIndex: Float?,
)

data class MapCircle(
    val center: Coordinates,
    val color: String?,
    val lineColor: String?,
    val lineWidth: Float?,
    val radius: Float,
)

data class MapPolygon(
    val coordinates: List<Coordinates>,
    val color: String?,
    val lineColor: String?,
    val lineWidth: Float,
)

data class MapPolyline(
    val coordinates: List<Coordinates>,
    val color: String?,
    val width: Float,
    val strokeColor: String?,
    val appleContourStyle: AppleMapsContourStyle?,
)

data class Coordinates(
    val latitude: Double,
    val longitude: Double
)

data class CameraPosition(
    val coordinates: Coordinates,
    val zoom: Float,
)
