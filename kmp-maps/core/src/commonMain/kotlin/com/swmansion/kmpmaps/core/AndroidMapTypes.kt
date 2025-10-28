package com.swmansion.kmpmaps.core

/**
 * Android-specific map behavior and appearance configuration options.
 *
 * @param isIndoorEnabled Whether indoor maps are enabled
 * @param maxZoomPreference Maximum zoom level preference
 * @param minZoomPreference Minimum zoom level preference
 * @param mapStyleOptions Custom map styling options
 */
public data class AndroidMapProperties(
    val isIndoorEnabled: Boolean = true,
    val maxZoomPreference: Float? = null,
    val minZoomPreference: Float? = null,
    val mapStyleOptions: GoogleMapsMapStyleOptions? = null,
)

/**
 * Android-specific UI settings that control interactive elements and gestures on the map.
 *
 * @param indoorLevelPickerEnabled Whether indoor level picker is enabled
 * @param mapToolbarEnabled Whether the map toolbar is enabled
 * @param scrollGesturesEnabledDuringRotateOrZoom Whether scroll gestures work during
 *   rotate/zoom
 * @param tiltGesturesEnabled Whether tilt gestures are enabled
 * @param zoomControlsEnabled Whether zoom controls are enabled
 */
public data class AndroidUISettings (
    val indoorLevelPickerEnabled: Boolean = true,
    val mapToolbarEnabled: Boolean = true,
    val scrollGesturesEnabledDuringRotateOrZoom: Boolean = true,
    val tiltGesturesEnabled: Boolean = true,
    val zoomControlsEnabled: Boolean = true,
)

/**
 * Android-specific options for customizing a marker.
 *
 * @param anchor The anchor point for the marker
 * @param draggable Whether the marker can be dragged by the user
 * @param snippet Additional text displayed below the title
 * @param zIndex The z-index for layering markers
 */
public data class AndroidMarkerOptions (
    val anchor: GoogleMapsAnchor? = null,
    val draggable: Boolean = false,
    val snippet: String? = null,
    val zIndex: Float? = null,
)

/**
 * Android-specific options for the camera position and orientation of the map.
 *
 * @param bearing The bearing (rotation) of the camera in degrees
 * @param tilt The tilt angle of the camera in degrees
 */
public data class AndroidCameraPosition (
    val bearing: Float? = null,
    val tilt: Float? = null,
)
