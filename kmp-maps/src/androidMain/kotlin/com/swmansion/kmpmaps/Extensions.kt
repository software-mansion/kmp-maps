package com.swmansion.kmpmaps

import androidx.compose.ui.geometry.Offset
import com.google.android.gms.maps.model.CameraPosition as GoogleCameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.MapProperties as GoogleMapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings as GoogleMapUiSettings
import com.google.maps.android.compose.MarkerState

internal fun CameraPosition.toGoogleCameraPosition() =
    GoogleCameraPosition.Builder()
        .target(LatLng(coordinates.latitude, coordinates.longitude))
        .zoom(zoom)
        .bearing(androidBearing ?: 0f)
        .tilt(androidTilt ?: 0f)
        .build()

internal fun GoogleCameraPosition.toCameraPosition() =
    CameraPosition(
        coordinates = Coordinates(latitude = target.latitude, longitude = target.longitude),
        zoom = zoom,
        androidBearing = bearing,
        androidTilt = tilt,
    )

internal fun MapMarker.toGoogleMapsMarkerState() =
    MarkerState(position = LatLng(coordinates.latitude, coordinates.longitude))

internal fun Coordinates.toGoogleLatLng(): LatLng = LatLng(latitude, longitude)

internal fun Color?.toAndroidColor() =
    when {
        this == null -> android.graphics.Color.TRANSPARENT
        this.hexColor != null && this.hexColor.startsWith("#") -> {
            try {
                val cleanHex = this.hexColor.removePrefix("#")
                val colorValue =
                    when (cleanHex.length) {
                        6 -> cleanHex + "FF"
                        8 -> cleanHex
                        else -> "000000FF"
                    }
                val color = colorValue.toLong(16)
                android.graphics.Color.argb(
                    ((color shr 24) and 0xFF).toInt(),
                    ((color shr 16) and 0xFF).toInt(),
                    ((color shr 8) and 0xFF).toInt(),
                    (color and 0xFF).toInt(),
                )
            } catch (_: Exception) {
                android.graphics.Color.RED
            }
        }
        else -> {
            when (this.androidColor) {
                AndroidColors.BLACK -> android.graphics.Color.BLACK
                AndroidColors.DARK_GRAY -> android.graphics.Color.DKGRAY
                AndroidColors.GRAY -> android.graphics.Color.GRAY
                AndroidColors.LIGHT_GRAY -> android.graphics.Color.LTGRAY
                AndroidColors.WHITE -> android.graphics.Color.WHITE
                AndroidColors.RED -> android.graphics.Color.RED
                AndroidColors.GREEN -> android.graphics.Color.GREEN
                AndroidColors.BLUE -> android.graphics.Color.BLUE
                AndroidColors.YELLOW -> android.graphics.Color.YELLOW
                AndroidColors.CYAN -> android.graphics.Color.CYAN
                AndroidColors.MAGENTA -> android.graphics.Color.MAGENTA
                AndroidColors.TRANSPARENT -> android.graphics.Color.TRANSPARENT
                else -> android.graphics.Color.RED
            }
        }
    }

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

internal fun com.swmansion.kmpmaps.MapType?.toGoogleMapsMapType() =
    when (this) {
        com.swmansion.kmpmaps.MapType.HYBRID -> MapType.HYBRID
        com.swmansion.kmpmaps.MapType.NORMAL -> MapType.NORMAL
        com.swmansion.kmpmaps.MapType.SATELLITE -> MapType.SATELLITE
        com.swmansion.kmpmaps.MapType.TERRAIN -> MapType.TERRAIN
        else -> MapType.NORMAL
    }

internal fun GoogleMapsMapStyleOptions?.toNativeStyleOptions() = this?.json?.let(::MapStyleOptions)

internal fun GoogleMapsAnchor?.toOffset() = Offset(this?.x ?: 0.5f, this?.y ?: 1.0f)
