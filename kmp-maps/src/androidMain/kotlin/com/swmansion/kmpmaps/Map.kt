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
public actual fun Map(
    modifier: Modifier,
    cameraPosition: CameraPosition?,
    properties: MapProperties,
    uiSettings: MapUISettings,
    markers: List<MapMarker>,
    circles: List<MapCircle>,
    polygons: List<MapPolygon>,
    polylines: List<MapPolyline>,
    onCameraMove: ((CameraPosition) -> Unit)?,
    onMarkerClick: ((MapMarker) -> Unit)?,
    onCircleClick: ((MapCircle) -> Unit)?,
    onPolygonClick: ((MapPolygon) -> Unit)?,
    onPolylineClick: ((MapPolyline) -> Unit)?,
    onMapClick: ((Coordinates) -> Unit)?,
    onMapLongClick: ((Coordinates) -> Unit)?,
    onPOIClick: ((Coordinates) -> Unit)?,
    onMapLoaded: (() -> Unit)?,
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
        properties = properties.toGoogleMapsProperties(),
        uiSettings = uiSettings.toGoogleMapsUiSettings(),
        onMapClick =
            onMapClick?.let { callback ->
                { latLng -> callback(Coordinates(latLng.latitude, latLng.longitude)) }
            },
        onMapLongClick =
            onMapLongClick?.let { callback ->
                { latLng -> callback(Coordinates(latLng.latitude, latLng.longitude)) }
            },
        onPOIClick =
            onPOIClick?.let { callback ->
                { poi -> callback(Coordinates(poi.latLng.latitude, poi.latLng.longitude)) }
            },
        onMapLoaded = onMapLoaded,
    ) {
        markers.forEach { marker ->
            Marker(
                state = marker.toGoogleMapsMarkerState(),
                title = marker.title,
                anchor = marker.androidAnchor.toOffset(),
                draggable = marker.androidDraggable,
                snippet = marker.androidSnippet,
                zIndex = marker.androidZIndex ?: 0.0f,
                onClick = {
                    onMarkerClick?.invoke(marker)
                    onMarkerClick == null
                },
            )
        }

        circles.forEach { circle ->
            Circle(
                center = circle.center.toGoogleLatLng(),
                radius = circle.radius.toDouble(),
                strokeColor =
                    Color(
                        circle.lineColor?.hexColor?.toGoogleColor() ?: android.graphics.Color.BLACK
                    ),
                strokeWidth = circle.lineWidth ?: 10f,
                fillColor =
                    Color(
                        circle.color?.hexColor?.toGoogleColor()
                            ?: android.graphics.Color.TRANSPARENT
                    ),
                clickable = true,
                onClick = {
                    if (onCircleClick != null) {
                        onCircleClick.invoke(circle)
                    } else {
                        onMapClick?.invoke(circle.center)
                    }

                }
            )
        }

        polygons.forEach { polygon ->
            Polygon(
                points = polygon.coordinates.map { it.toGoogleLatLng() },
                strokeColor =
                    Color(
                        polygon.lineColor?.hexColor?.toGoogleColor() ?: android.graphics.Color.BLACK
                    ),
                strokeWidth = polygon.lineWidth,
                fillColor =
                    Color(
                        polygon.color?.hexColor?.toGoogleColor()
                            ?: android.graphics.Color.TRANSPARENT
                    ),
                clickable = true,
                onClick = {
                    if (onPolygonClick != null) {
                        onPolygonClick.invoke(polygon)
                    } else {
                        onMapClick?.invoke(polygon.coordinates[0])
                    }

                },
            )
        }

        polylines.forEach { polyline ->
            Polyline(
                points = polyline.coordinates.map { it.toGoogleLatLng() },
                color =
                    Color(
                        polyline.lineColor?.hexColor?.toGoogleColor()
                            ?: android.graphics.Color.BLACK
                    ),
                width = polyline.width,
                clickable = true,
                onClick = {
                    if (onPolylineClick != null) {
                        onPolylineClick.invoke(polyline)
                    } else {
                        onMapClick?.invoke(polyline.coordinates[0])
                    }
                },
            )
        }
    }

    LaunchedEffect(cameraPositionState.position) {
        onCameraMove?.invoke(cameraPositionState.position.toCameraPosition())
    }
}
