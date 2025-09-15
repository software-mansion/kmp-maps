package com.swmansion.kmpmaps

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polygon
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
public fun GoogleMap(
    cameraPosition: CameraPosition? = null,
    properties: GoogleMapsProperties = GoogleMapsProperties(),
    uiSettings: GoogleMapsUISettings = GoogleMapsUISettings(),
    markers: List<GoogleMapsMarker> = emptyList(),
    circles: List<GoogleMapsCircle> = emptyList(),
    polygons: List<GoogleMapsPolygon> = emptyList(),
    polylines: List<GoogleMapsPolyline> = emptyList(),
    onCameraMove: ((CameraPosition) -> Unit)? = null,
    onMarkerClick: ((GoogleMapsMarker) -> Unit)? = null,
    onCircleClick: ((GoogleMapsCircle) -> Unit)? = null,
    onPolygonClick: ((GoogleMapsPolygon) -> Unit)? = null,
    onPolylineClick: ((GoogleMapsPolyline) -> Unit)? = null,
    onMapClick: ((Coordinates) -> Unit)? = null,
    onMapLongClick: ((Coordinates) -> Unit)? = null,
    onPOIClick: ((Coordinates) -> Unit)? = null,
    onMapLoaded: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val locationPermissionHandler = remember { LocationPermissionHandler(context) }

    LaunchedEffect(properties.isMyLocationEnabled) {
        if (properties.isMyLocationEnabled) {
            if (!locationPermissionHandler.checkPermission()) {
                locationPermissionHandler.requestPermission()
            }
        }
    }
    val cameraPositionState = rememberCameraPositionState {
        cameraPosition?.let { pos -> position = pos.toGoogleCameraPosition() }
    }

    GoogleMap(
        modifier = modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = properties.copy(
            isMyLocationEnabled = properties.isMyLocationEnabled && locationPermissionHandler.hasPermission(),
        ).toGoogleMapProperties(),
        uiSettings = uiSettings.toGoogleMapUiSettings(),
        onMapClick = onMapClick?.let { callback ->
            { latLng -> callback(Coordinates(latLng.latitude, latLng.longitude)) }
        },
        onMapLongClick = onMapLongClick?.let { callback ->
            { latLng -> callback(Coordinates(latLng.latitude, latLng.longitude)) }
        },
        onPOIClick = onPOIClick?.let { callback ->
            { poi ->
                callback(
                    Coordinates(
                        poi.latLng.latitude,
                        poi.latLng.longitude,
                    ),
                )
            }
        },
        onMapLoaded = onMapLoaded,
    ) {
        markers.forEach { marker ->
            Marker(
                state = marker.toGoogleMarkerState(),
                title = marker.title,
                snippet = marker.subtitle,
                onClick = {
                    onMarkerClick?.invoke(marker)
                    onMarkerClick == null
                },
            )
        }

        circles.forEach { circle ->
            Circle(
                center = circle.center.toGoogleLatLng(),
                radius = circle.radius,
                strokeColor = Color(
                    circle.strokeColor?.toGoogleColor() ?: android.graphics.Color.BLACK,
                ),
                strokeWidth = circle.strokeWidth,
                fillColor = Color(
                    circle.fillColor?.toGoogleColor() ?: android.graphics.Color.TRANSPARENT,
                ),
                onClick = onCircleClick?.let { callback ->
                    { callback(circle) }
                } ?: { false },
            )
        }

        polygons.forEach { polygon ->
            Polygon(
                points = polygon.coordinates.map { it.toGoogleLatLng() },
                strokeColor = Color(
                    polygon.strokeColor?.toGoogleColor() ?: android.graphics.Color.BLACK,
                ),
                strokeWidth = polygon.strokeWidth,
                fillColor = Color(
                    polygon.fillColor?.toGoogleColor() ?: android.graphics.Color.TRANSPARENT,
                ),
                onClick = onPolygonClick?.let { callback ->
                    { callback(polygon) }
                } ?: { false },
            )
        }

        polylines.forEach { polyline ->
            Polyline(
                points = polyline.coordinates.map { it.toGoogleLatLng() },
                color = Color(
                    polyline.strokeColor?.toGoogleColor() ?: android.graphics.Color.BLACK,
                ),
                width = polyline.strokeWidth,
                onClick = onPolylineClick?.let { callback ->
                    { callback(polyline) }
                } ?: { false },
            )
        }
    }

    LaunchedEffect(cameraPositionState.position) {
        onCameraMove?.invoke(cameraPositionState.position.toCameraPosition())
    }
}
