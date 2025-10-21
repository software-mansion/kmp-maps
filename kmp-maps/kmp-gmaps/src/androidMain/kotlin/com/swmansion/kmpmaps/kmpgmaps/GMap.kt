package com.swmansion.kmpmaps.kmpgmaps

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.swmansion.kmpmaps.kmpmaps.CameraPosition
import com.swmansion.kmpmaps.kmpmaps.Circle
import com.swmansion.kmpmaps.kmpmaps.Coordinates
import com.swmansion.kmpmaps.kmpmaps.Map
import com.swmansion.kmpmaps.kmpmaps.MapProperties
import com.swmansion.kmpmaps.kmpmaps.MapUISettings
import com.swmansion.kmpmaps.kmpmaps.Marker
import com.swmansion.kmpmaps.kmpmaps.Polygon
import com.swmansion.kmpmaps.kmpmaps.Polyline

@Composable
public actual fun GMap(
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
    Map(
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
