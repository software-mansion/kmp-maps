package com.swmansion.kmpmaps.kmpmaps

import androidx.compose.ui.geometry.Offset
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.android.gms.maps.model.CameraPosition as GoogleCameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.ComposeMapColorScheme
import com.google.maps.android.compose.MapProperties as GoogleMapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings as GoogleMapUiSettings
import com.google.maps.android.compose.MarkerState

/**
 * Converts CameraPosition to Google Maps CameraPosition.
 *
 * @return GoogleCameraPosition with coordinates, zoom, bearing, and tilt
 */
internal fun CameraPosition.toGoogleMapsCameraPosition() =
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
internal fun Marker.toGoogleMapsMarkerState() =
    MarkerState(position = LatLng(coordinates.latitude, coordinates.longitude))

/**
 * Converts Coordinates to Google Maps LatLng.
 *
 * @return LatLng with latitude and longitude
 */
internal fun Coordinates.toGoogleMapsLatLng(): LatLng = LatLng(latitude, longitude)

/**
 * Converts MapTheme to native ComposeMapColorScheme.
 *
 * @return ComposeMapColorScheme corresponding to the enum value
 */
internal fun MapTheme.toGoogleMapsTheme() =
    when (this) {
        MapTheme.LIGHT -> ComposeMapColorScheme.LIGHT
        MapTheme.DARK -> ComposeMapColorScheme.DARK
        MapTheme.SYSTEM -> ComposeMapColorScheme.FOLLOW_SYSTEM
    }

/**
 * Converts MapProperties to Google Maps Properties.
 *
 * @return GoogleMapProperties with map configuration
 */
@OptIn(ExperimentalPermissionsApi::class)
internal fun MapProperties.toGoogleMapsProperties(locationPermissionState: PermissionState) =
    GoogleMapProperties(
        mapType = mapType.toGoogleMapsMapType(),
        isMyLocationEnabled = isMyLocationEnabled && locationPermissionState.status.isGranted,
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
        rotationGesturesEnabled = rotateEnabled,
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
internal fun com.swmansion.kmpmaps.kmpmaps.MapType?.toGoogleMapsMapType() =
    when (this) {
        com.swmansion.kmpmaps.kmpmaps.MapType.HYBRID -> MapType.HYBRID
        com.swmansion.kmpmaps.kmpmaps.MapType.NORMAL -> MapType.NORMAL
        com.swmansion.kmpmaps.kmpmaps.MapType.SATELLITE -> MapType.SATELLITE
        com.swmansion.kmpmaps.kmpmaps.MapType.TERRAIN -> MapType.TERRAIN
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
