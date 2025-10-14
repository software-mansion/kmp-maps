package com.swmansion.kmpmaps

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.swmansion.kmpmaps.apple.AppleMap
import com.swmansion.kmpmaps.google.GoogleMap
import kotlinx.cinterop.ExperimentalForeignApi

/** iOS implementation of the Map composable using Apple Maps or Google Maps. */
@OptIn(ExperimentalForeignApi::class)
@Composable
public actual fun Map(
    modifier: Modifier,
    mapPlatform: MapPlatform,
    cameraPosition: CameraPosition?,
    properties: MapProperties,
    uiSettings: MapUISettings,
    markers: List<Marker>,
    circles: List<Circle>,
    polygons: List<Polygon>,
    polylines: List<Polyline>,
    onCameraMove: ((CameraPosition) -> Unit)?,
    onMarkerClick: ((Marker) -> Unit)?,
    onCircleClick: ((Circle) -> Unit)?,
    onPolygonClick: ((Polygon) -> Unit)?,
    onPolylineClick: ((Polyline) -> Unit)?,
    onMapClick: ((Coordinates) -> Unit)?,
    onMapLongClick: ((Coordinates) -> Unit)?,
    onPOIClick: ((Coordinates) -> Unit)?,
    onMapLoaded: (() -> Unit)?,
) {
    when (mapPlatform) {
        MapPlatform.NATIVE ->
            AppleMap(
                modifier = modifier,
                cameraPosition = cameraPosition,
                properties = properties,
                uiSettings = uiSettings,
                markers = markers,
                circles = circles,
                polygons = polygons,
                polylines = polylines,
                onCameraMove = onCameraMove,
                onMarkerClick = onMarkerClick,
                onCircleClick = onCircleClick,
                onPolygonClick = onPolygonClick,
                onPolylineClick = onPolylineClick,
                onMapClick = onMapClick,
                onMapLongClick = onMapLongClick,
                onPOIClick = onPOIClick,
                onMapLoaded = onMapLoaded,
            )
        MapPlatform.GOOGLE_MAPS -> {
            GoogleMap(
                modifier = modifier,
                cameraPosition = cameraPosition,
                properties = properties,
                uiSettings = uiSettings,
                markers = markers,
                circles = circles,
                polygons = polygons,
                polylines = polylines,
                onCameraMove = onCameraMove,
                onMarkerClick = onMarkerClick,
                onCircleClick = onCircleClick,
                onPolygonClick = onPolygonClick,
                onPolylineClick = onPolylineClick,
                onMapClick = onMapClick,
                onMapLongClick = onMapLongClick,
                onPOIClick = onPOIClick,
                onMapLoaded = onMapLoaded,
            )
        }
    }
}
