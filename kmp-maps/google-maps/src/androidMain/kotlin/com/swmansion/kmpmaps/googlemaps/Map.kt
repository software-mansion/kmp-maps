package com.swmansion.kmpmaps.googlemaps

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.swmansion.kmpmaps.core.CameraPosition
import com.swmansion.kmpmaps.core.Circle
import com.swmansion.kmpmaps.core.Coordinates
import com.swmansion.kmpmaps.core.Map as CoreMap
import com.swmansion.kmpmaps.core.MapProperties
import com.swmansion.kmpmaps.core.MapUISettings
import com.swmansion.kmpmaps.core.Marker
import com.swmansion.kmpmaps.core.Polygon
import com.swmansion.kmpmaps.core.Polyline

@Composable
public actual fun Map(
    modifier: Modifier,
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
): Unit =
    CoreMap(
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
