package com.swmansion.kmpmaps

import android.graphics.Color
import androidx.core.graphics.toColorInt
import com.google.android.gms.maps.model.CameraPosition as GoogleCameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.MapProperties as GoogleMapProperties
import com.google.maps.android.compose.MapUiSettings as GoogleMapUiSettings
import com.google.maps.android.compose.MarkerState

fun CameraPosition.toGoogleCameraPosition(): GoogleCameraPosition {
    return GoogleCameraPosition.Builder()
        .target(LatLng(coordinates.latitude, coordinates.longitude))
        .zoom(zoom)
        .bearing(bearing)
        .tilt(tilt)
        .build()
}

fun GoogleCameraPosition.toCameraPosition(): CameraPosition {
    return CameraPosition(
        coordinates = Coordinates(latitude = target.latitude, longitude = target.longitude),
        zoom = zoom,
        bearing = bearing,
        tilt = tilt,
    )
}

fun GoogleMapsMarker.toComposeMarkerState(): MarkerState {
    return MarkerState(position = LatLng(coordinates.latitude, coordinates.longitude))
}

fun Coordinates.toGoogleLatLng(): LatLng {
    return LatLng(latitude, longitude)
}

fun String.toGoogleColor(): Int {
    return try {
        toColorInt()
    } catch (_: IllegalArgumentException) {
        Color.BLACK
    }
}

fun GoogleMapsProperties.toComposeMapProperties(): GoogleMapProperties {
    return GoogleMapProperties(
        mapType = mapType.toGoogleMapsMapType(),
        isMyLocationEnabled = isMyLocationEnabled,
        isTrafficEnabled = isTrafficEnabled,
        isIndoorEnabled = isIndoorEnabled,
        minZoomPreference = minZoomPreference ?: 0f,
        maxZoomPreference = maxZoomPreference ?: 20f,
    )
}

fun GoogleMapsUISettings.toComposeMapUiSettings(): GoogleMapUiSettings {
    return GoogleMapUiSettings(
        compassEnabled = compassEnabled,
        myLocationButtonEnabled = myLocationButtonEnabled,
        zoomControlsEnabled = zoomControlsEnabled,
        scrollGesturesEnabled = scrollGesturesEnabled,
        zoomGesturesEnabled = zoomGesturesEnabled,
        tiltGesturesEnabled = tiltGesturesEnabled,
    )
}
