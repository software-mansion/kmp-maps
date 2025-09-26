package com.swmansion.kmpmaps

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.google.android.gms.maps.model.CameraPosition as GoogleCameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.MapProperties as GoogleMapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings as GoogleMapUiSettings
import com.google.maps.android.compose.MarkerState

/**
 * Converts CameraPosition to Google Maps CameraPosition.
 *
 * @return GoogleCameraPosition with coordinates, zoom, bearing, and tilt
 */
internal fun CameraPosition.toGoogleCameraPosition() =
    GoogleCameraPosition.Builder()
        .target(LatLng(coordinates.latitude, coordinates.longitude))
        .zoom(zoom)
        .bearing(androidBearing ?: 0f)
        .tilt(androidTilt ?: 0f)
        .build()

/**
 * Converts Google Maps CameraPosition back to CameraPosition.
 *
 * @return CameraPosition with coordinates, zoom, bearing, and tilt
 */
internal fun GoogleCameraPosition.toCameraPosition() =
    CameraPosition(
        coordinates = Coordinates(latitude = target.latitude, longitude = target.longitude),
        zoom = zoom,
        androidBearing = bearing,
        androidTilt = tilt,
    )

/**
 * Converts MapMarker to Google Maps MarkerState.
 *
 * @return MarkerState with position coordinates
 */
internal fun MapMarker.toGoogleMapsMarkerState() =
    MarkerState(position = LatLng(coordinates.latitude, coordinates.longitude))

/**
 * Converts Coordinates to Google Maps LatLng.
 *
 * @return LatLng with latitude and longitude
 */
internal fun Coordinates.toGoogleLatLng(): LatLng = LatLng(latitude, longitude)

/**
 * Converts androidx Color to Android graphics Color.
 *
 * @return Android graphics Color corresponding to the androidx Color object
 */
internal fun Color?.toAndroidColor() =
    when (this) {
        null -> android.graphics.Color.TRANSPARENT
        else -> this.toArgb()
    }

/**
 * Converts MapProperties to Google Maps Properties.
 *
 * @return GoogleMapProperties with map configuration
 */
internal fun MapProperties.toGoogleMapsProperties() =
    GoogleMapProperties(
        mapType = mapType.toGoogleMapsMapType(),
        isMyLocationEnabled = isMyLocationEnabled,
        isTrafficEnabled = isTrafficEnabled,
        isBuildingEnabled = isBuildingEnabled,
        isIndoorEnabled = androidIsIndoorEnabled,
        minZoomPreference = androidMinZoomPreference ?: 0f,
        maxZoomPreference = androidMaxZoomPreference ?: 20f,
        mapStyleOptions = androidMapStyleOptions.toNativeStyleOptions(),
    )

/**
 * Converts MapUISettings to Google Maps UI Settings.
 *
 * @return GoogleMapUiSettings with UI configuration
 */
internal fun MapUISettings.toGoogleMapsUiSettings() =
    GoogleMapUiSettings(
        compassEnabled = compassEnabled,
        myLocationButtonEnabled = myLocationButtonEnabled,
        indoorLevelPickerEnabled = androidIndoorLevelPickerEnabled,
        mapToolbarEnabled = androidMapToolbarEnabled,
        rotationGesturesEnabled = androidRotationGesturesEnabled,
        scrollGesturesEnabled = scrollEnabled,
        scrollGesturesEnabledDuringRotateOrZoom = androidScrollGesturesEnabledDuringRotateOrZoom,
        tiltGesturesEnabled = androidTiltGesturesEnabled,
        zoomControlsEnabled = androidZoomControlsEnabled,
        zoomGesturesEnabled = zoomEnabled,
    )

/**
 * Converts MapType enum to Google Maps MapType.
 *
 * @return Google Maps MapType corresponding to the enum value
 */
internal fun com.swmansion.kmpmaps.MapType?.toGoogleMapsMapType() =
    when (this) {
        com.swmansion.kmpmaps.MapType.HYBRID -> MapType.HYBRID
        com.swmansion.kmpmaps.MapType.NORMAL -> MapType.NORMAL
        com.swmansion.kmpmaps.MapType.SATELLITE -> MapType.SATELLITE
        com.swmansion.kmpmaps.MapType.TERRAIN -> MapType.TERRAIN
        else -> MapType.NORMAL
    }

/**
 * Converts GoogleMapsMapStyleOptions to native MapStyleOptions.
 *
 * @return MapStyleOptions from JSON string, or null if no JSON provided
 */
internal fun GoogleMapsMapStyleOptions?.toNativeStyleOptions() = this?.json?.let(::MapStyleOptions)

/**
 * Converts GoogleMapsAnchor to Compose Offset.
 *
 * @return Offset with x and y coordinates (defaults to 0.5f, 1.0f if null)
 */
internal fun GoogleMapsAnchor?.toOffset() = Offset(this?.x ?: 0.5f, this?.y ?: 1.0f)
