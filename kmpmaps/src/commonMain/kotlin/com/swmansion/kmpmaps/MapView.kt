package com.swmansion.kmpmaps

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun Map(
    cameraPosition: CameraPosition? = null,
    properties: MapProperties,
    uiSettings: MapUISettings,
    markers: List<MapMarker> = emptyList(),
    circles: List<MapCircle> = emptyList(),
    polygons: List<MapPolygon> = emptyList(),
    polylines: List<MapPolyline> = emptyList(),
    onCameraMove: ((CameraPosition) -> Unit)? = null,
    onMarkerClick: ((MapMarker) -> Unit)? = null,
    onCircleClick: ((MapCircle) -> Unit)? = null,
    onPolygonClick: ((MapPolygon) -> Unit)? = null,
    onPolylineClick: ((MapPolyline) -> Unit)? = null,
    onMapClick: ((Coordinates) -> Unit)? = null,
    onMapLongClick: ((Coordinates) -> Unit)? = null,
    onPOIClick: ((Coordinates) -> Unit)? = null,
    onMapLoaded: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
)
