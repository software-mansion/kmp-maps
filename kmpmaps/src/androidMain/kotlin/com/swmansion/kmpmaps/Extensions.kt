package com.swmansion.kmpmaps

import android.graphics.Color
import androidx.core.graphics.toColorInt
import com.google.android.gms.maps.model.CameraPosition as GoogleCameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.MapProperties as GoogleMapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings as GoogleMapUiSettings
import com.google.maps.android.compose.MarkerState

public fun GoogleMapsMapType.toGoogleMapType(): MapType {
    return when (this) {
        GoogleMapsMapType.NORMAL -> MapType.NORMAL
        GoogleMapsMapType.SATELLITE -> MapType.SATELLITE
        GoogleMapsMapType.HYBRID -> MapType.HYBRID
        GoogleMapsMapType.TERRAIN -> MapType.TERRAIN
    }
}

public fun CameraPosition.toGoogleCameraPosition(): GoogleCameraPosition {
    return GoogleCameraPosition.Builder()
        .target(LatLng(coordinates.latitude, coordinates.longitude))
        .zoom(zoom)
        .bearing(bearing)
        .tilt(tilt)
        .build()
}

public fun GoogleCameraPosition.toCameraPosition(): CameraPosition {
    return CameraPosition(
        coordinates = Coordinates(latitude = target.latitude, longitude = target.longitude),
        zoom = zoom,
        bearing = bearing,
        tilt = tilt,
    )
}

public fun GoogleMapsMarker.toGoogleMarkerState(): MarkerState {
    return MarkerState(position = LatLng(coordinates.latitude, coordinates.longitude))
}

public fun Coordinates.toGoogleLatLng(): LatLng {
    return LatLng(latitude, longitude)
}

public fun String.toGoogleColor(): Int {
    return try {
        toColorInt()
    } catch (_: IllegalArgumentException) {
        Color.BLACK
    }
}

public fun GoogleMapsProperties.toGoogleMapProperties(): GoogleMapProperties {
    return GoogleMapProperties(
        mapType = mapType.toGoogleMapType(),
        isMyLocationEnabled = isMyLocationEnabled,
        isTrafficEnabled = isTrafficEnabled,
        isIndoorEnabled = isIndoorEnabled,
        minZoomPreference = minZoomPreference ?: 0f,
        maxZoomPreference = maxZoomPreference ?: 20f,
    )
}

public fun GoogleMapsUISettings.toGoogleMapUiSettings(): GoogleMapUiSettings {
    return GoogleMapUiSettings(
        compassEnabled = compassEnabled,
        myLocationButtonEnabled = myLocationButtonEnabled,
        zoomControlsEnabled = zoomControlsEnabled,
        scrollGesturesEnabled = scrollGesturesEnabled,
        zoomGesturesEnabled = zoomGesturesEnabled,
        tiltGesturesEnabled = tiltGesturesEnabled,
    )
}
