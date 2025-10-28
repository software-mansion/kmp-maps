package com.swmansion.kmpmaps.core

import androidx.compose.ui.graphics.Color

/**
 * iOS-specific map behavior and appearance configuration options.
 *
 * @param showPOI Whether to show points of interest (POIs) on the map (Apple Maps only)
 * @param pointsOfInterest POI categories to include/exclude (Apple Maps only)
 * @param polylineTapThreshold Threshold for polyline tap detection (Apple Maps only)
 * @param gmsMaxZoomPreference Maximum zoom level preference (Google Maps only)
 * @param gmsMinZoomPreference Minimum zoom level preference (Google Maps only)
 * @param gmsIsIndoorEnabled Whether indoor maps are enabled (Google Maps only)
 * @param gmsMapStyleOptions Custom map styling options (Google Maps only)
 */
public data class IosMapProperties(
    val showPOI: Boolean = true,
    val pointsOfInterest: AppleMapsPointOfInterestCategories? = null,
    val polylineTapThreshold: Float? = null,
    val gmsMaxZoomPreference: Float? = null,
    val gmsMinZoomPreference: Float? = null,
    val gmsIsIndoorEnabled: Boolean = true,
    val gmsMapStyleOptions: GoogleMapsMapStyleOptions? = null,
)

/**
 * iOS-specific UI settings that control interactive elements and gestures on the map.
 *
 * @param gmsIndoorPicker Whether indoor level picker is enabled (Google Maps only)
 * @param gmsScrollGesturesEnabledDuringRotateOrZoom Whether scroll gestures work during
 *   rotate/zoom
 * @param gmsTiltGesturesEnabled Whether tilt gestures are enabled (Google Maps only)
 * @param gmsConsumesGesturesInView Whether the map consumes gestures in view (Google Maps
 *   only)
 */
public data class IosUISettings(
    val gmsIndoorPicker: Boolean = true,
    val gmsScrollGesturesEnabledDuringRotateOrZoom: Boolean = true,
    val gmsTiltGesturesEnabled: Boolean = true,
    val gmsConsumesGesturesInView: Boolean = true,
)

/**
 * iOS-specific options for customizing a marker.
 *
 * @param tintColor The tint color for the marker (Apple Maps only)
 */
public data class IosMarkerOptions(
    val tintColor: Color? = null,
)

/**
 * iOS-specific options for the camera position and orientation of the map.
 *
 * @param gmsBearing The bearing (rotation) of the camera in degrees (Google Maps only)
 * @param viewingAngle The viewing angle of the camera in degrees (Google Maps only)
 */
public data class IosCameraPosition(
    val gmsBearing: Float? = null,
    val viewingAngle: Float? = null,
)
