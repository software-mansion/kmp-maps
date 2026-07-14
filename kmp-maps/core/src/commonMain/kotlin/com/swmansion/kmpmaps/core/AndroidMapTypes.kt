package com.swmansion.kmpmaps.core

import kotlinx.serialization.Serializable

/**
 * Android-specific map behavior and appearance configuration options.
 *
 * @property isIndoorEnabled Whether indoor maps are enabled
 * @property maxZoomPreference Maximum zoom level preference
 * @property minZoomPreference Minimum zoom level preference
 * @property mapStyleOptions Custom map styling options
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
 * @property indoorLevelPickerEnabled Whether indoor level picker is enabled
 * @property mapToolbarEnabled Whether the map toolbar is enabled
 * @property scrollGesturesEnabledDuringRotateOrZoom Whether scroll gestures work during rotate/zoom
 * @property tiltGesturesEnabled Whether tilt gestures are enabled
 * @property zoomControlsEnabled Whether zoom controls are enabled
 */
public data class AndroidUISettings(
    val indoorLevelPickerEnabled: Boolean = true,
    val mapToolbarEnabled: Boolean = true,
    val scrollGesturesEnabledDuringRotateOrZoom: Boolean = true,
    val tiltGesturesEnabled: Boolean = true,
    val zoomControlsEnabled: Boolean = true,
)

/**
 * Android-specific options for customizing a marker.
 *
 * @property anchor The anchor point for the marker
 * @property draggable Whether the marker can be dragged by the user
 * @property snippet Additional text displayed below the title
 * @property zIndex The z-index for layering markers
 * @property rotation The rotation of the marker in degrees, clockwise. Combined with a stable
 *   [Marker.id] this rotates a marker in place (e.g. a driving puck turning to its heading)
 *   without re-rendering its bitmap. `null` leaves the marker unrotated.
 * @property flat When `true` the marker is drawn flat against the map so its [rotation] is
 *   relative to north and it respects the map's tilt/rotation (billboard behaviour when `false`).
 *
 * Note: In the current Android implementation, draggability is only supported when
 * [ClusterSettings.enabled] is set to `false`.
 */
@Serializable
public data class AndroidMarkerOptions(
    val anchor: GoogleMapsAnchor? = null,
    val draggable: Boolean = false,
    val snippet: String? = null,
    val zIndex: Float? = null,
    val rotation: Float? = null,
    val flat: Boolean = false,
)

/**
 * Android-specific options for the camera position and orientation of the map.
 *
 * @property bearing The bearing (rotation) of the camera in degrees
 * @property tilt The tilt angle of the camera in degrees
 */
@Serializable
public data class AndroidCameraPosition(val bearing: Float? = null, val tilt: Float? = null)
