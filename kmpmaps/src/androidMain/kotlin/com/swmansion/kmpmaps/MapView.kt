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
actual fun Map(
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
    modifier: Modifier,
) {
    val context = LocalContext.current
    val locationPermissionHandler = remember { LocationPermissionHandler(context) }

    val convertedProperties = when (properties) {
        is GoogleMapsProperties -> properties
        else -> GoogleMapsProperties(
            mapType = properties.mapType,
            isMyLocationEnabled = properties.isMyLocationEnabled,
            isTrafficEnabled = properties.isTrafficEnabled,
            showsBuildings = properties.showsBuildings
        )
    }

    val convertedUISettings = when (uiSettings) {
        is GoogleMapsUISettings -> uiSettings
        else -> GoogleMapsUISettings(
            compassEnabled = uiSettings.compassEnabled,
            myLocationButtonEnabled = uiSettings.myLocationButtonEnabled,
            scrollGesturesEnabled = uiSettings.scrollGesturesEnabled,
            zoomGesturesEnabled = uiSettings.zoomGesturesEnabled,
            tiltGesturesEnabled = uiSettings.tiltGesturesEnabled,
            rotateGesturesEnabled = uiSettings.rotateGesturesEnabled
        )
    }

    val convertedMapsMarkers = markers.map { marker ->
        when (marker) {
            is GoogleMapsMarker -> marker
            else -> GoogleMapsMarker(
                coordinates = marker.coordinates,
                title = marker.title,
                subtitle = marker.subtitle
            )
        }
    }

    val convertedPolygons = polygons.map { polygon ->
        when (polygon) {
            is GoogleMapsPolygon -> polygon
            else -> GoogleMapsPolygon(
                coordinates = polygon.coordinates,
                strokeColor = polygon.strokeColor,
                strokeWidth = polygon.strokeWidth,
                fillColor = polygon.fillColor
            )
        }
    }

    val convertedPolylines = polylines.map { polyline ->
        when (polyline) {
            is GoogleMapsPolyline -> polyline
            else -> GoogleMapsPolyline(
                coordinates = polyline.coordinates,
                strokeColor = polyline.strokeColor,
                strokeWidth = polyline.strokeWidth
            )
        }
    }

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
        properties = convertedProperties.copy(
            isMyLocationEnabled = convertedProperties.isMyLocationEnabled && locationPermissionHandler.hasPermission(),
        ).toComposeMapProperties(),
        uiSettings = convertedUISettings.toComposeMapUiSettings(),
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
        convertedMapsMarkers.forEach { marker ->
            Marker(
                state = marker.toComposeMarkerState(),
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

        convertedPolygons.forEach { polygon ->
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

        convertedPolylines.forEach { polyline ->
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
