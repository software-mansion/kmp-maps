package com.swmansion.kmpmaps

import androidx.compose.ui.geometry.Offset
import androidx.core.graphics.toColorInt
import com.google.android.gms.maps.model.CameraPosition as GoogleCameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.MapProperties as GoogleMapProperties
import com.google.maps.android.compose.MapUiSettings as GoogleMapUiSettings
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.MapType

internal fun CameraPosition.toGoogleCameraPosition(): GoogleCameraPosition {
    return GoogleCameraPosition.Builder()
        .target(LatLng(coordinates.latitude, coordinates.longitude))
        .zoom(zoom)
        .bearing(androidBearing ?: 0f)
        .tilt(androidTilt ?: 0f)
        .build()
}

internal fun GoogleCameraPosition.toCameraPosition(): CameraPosition {
    return CameraPosition(
        coordinates = Coordinates(latitude = target.latitude, longitude = target.longitude),
        zoom = zoom,
        androidBearing = bearing,
        androidTilt = tilt,
    )
}

internal fun MapMarker.toGoogleMapsMarkerState(): MarkerState {
    return MarkerState(position = LatLng(coordinates.latitude, coordinates.longitude))
}

internal fun Coordinates.toGoogleLatLng(): LatLng {
    return LatLng(latitude, longitude)
}

internal fun String.toGoogleColor(): Int = toColorInt()

internal fun MapProperties.toGoogleMapsProperties(): GoogleMapProperties {
    return GoogleMapProperties(
        mapType = mapType.toGoogleMapsMapType(),
        isMyLocationEnabled = isMyLocationEnabled,
        isTrafficEnabled = isTrafficEnabled,
        isBuildingEnabled = isBuildingEnabled,
        isIndoorEnabled = androidIsIndoorEnabled,
        minZoomPreference = androidMinZoomPreference ?: 0f,
        maxZoomPreference = androidMaxZoomPreference ?: 20f,
        mapStyleOptions = androidMapStyleOptions.toNativeStyleOptions(),
    )
}

internal fun MapUISettings.toGoogleMapsUiSettings(): GoogleMapUiSettings {
    return GoogleMapUiSettings(
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
}

internal fun com.swmansion.kmpmaps.MapType?.toGoogleMapsMapType(): MapType {
    return when(this) {
        com.swmansion.kmpmaps.MapType.HYBRID -> MapType.HYBRID
        com.swmansion.kmpmaps.MapType.NORMAL -> MapType.NORMAL
        com.swmansion.kmpmaps.MapType.SATELLITE -> MapType.SATELLITE
        com.swmansion.kmpmaps.MapType.TERRAIN -> MapType.TERRAIN
        else -> MapType.NORMAL
    }
}

internal fun GoogleMapsMapStyleOptions?.toNativeStyleOptions(): MapStyleOptions? {
    return this?.json?.let { MapStyleOptions(it) }
}

internal fun GoogleMapsAnchor?.toOffset(): Offset {
    return Offset(this?.x ?: 0.5f, this?.y ?: 1.0f)
}
