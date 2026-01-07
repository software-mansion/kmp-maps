package com.swmansion.kmpmaps.core

import androidx.compose.ui.graphics.Color
import kotlinx.serialization.Serializable

/**
 * iOS-specific map behavior and appearance configuration options.
 *
 * @property showPOI Whether to show points of interest (POIs) on the map (Apple Maps only)
 * @property pointsOfInterest POI categories to include/exclude (Apple Maps only)
 * @property polylineTapThreshold Threshold for polyline tap detection (Apple Maps only)
 * @property gmsMaxZoomPreference Maximum zoom level preference (Google Maps only)
 * @property gmsMinZoomPreference Minimum zoom level preference (Google Maps only)
 * @property gmsIsIndoorEnabled Whether indoor maps are enabled (Google Maps only)
 * @property gmsMapStyleOptions Custom map styling options (Google Maps only)
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
 * @property gmsIndoorPicker Whether indoor level picker is enabled (Google Maps only)
 * @property gmsScrollGesturesEnabledDuringRotateOrZoom Whether scroll gestures work during
 *   rotate/zoom (Google Maps only)
 * @property gmsTiltGesturesEnabled Whether tilt gestures are enabled (Google Maps only)
 * @property gmsConsumesGesturesInView Whether the map consumes gestures in view (Google Maps only)
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
 * @property tintColor The tint color for the marker (Apple Maps only)
 */
public data class IosMarkerOptions(val tintColor: Color? = null)

/**
 * iOS-specific options for the camera position and orientation of the map.
 *
 * @property gmsBearing The bearing (rotation) of the camera in degrees (Google Maps only)
 * @property gmsViewingAngle The viewing angle of the camera in degrees (Google Maps only)
 */
@Serializable
public data class IosCameraPosition(
    val gmsBearing: Float? = null,
    val gmsViewingAngle: Float? = null,
)
