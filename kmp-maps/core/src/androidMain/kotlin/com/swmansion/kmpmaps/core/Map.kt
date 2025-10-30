package com.swmansion.kmpmaps.core

import android.Manifest
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapEffect
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polygon
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.data.geojson.GeoJsonLayer as GoogleGeoJsonLayer
import org.json.JSONObject

/** Android implementation of the Map composable using Google Maps. */
@OptIn(ExperimentalPermissionsApi::class, MapsComposeExperimentalApi::class)
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
    geoJsonLayer: GeoJsonLayer?,
) {
    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    LaunchedEffect(properties.isMyLocationEnabled) {
        if (properties.isMyLocationEnabled && !locationPermissionState.status.isGranted) {
            locationPermissionState.launchPermissionRequest()
        }
    }

    val cameraPositionState = rememberCameraPositionState {
        cameraPosition?.let { position = it.toGoogleMapsCameraPosition() }
    }

    GoogleMap(
        mapColorScheme = properties.mapTheme.toGoogleMapsTheme(),
        modifier = modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = properties.toGoogleMapsProperties(locationPermissionState),
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
        var androidGeoJsonLayer by remember { mutableStateOf<GoogleGeoJsonLayer?>(null) }

        MapEffect(geoJsonLayer?.geoJson) { map ->
            runCatching {
                    androidGeoJsonLayer?.removeLayerFromMap()
                    androidGeoJsonLayer = null

                    val geo = geoJsonLayer ?: return@MapEffect
                    if (!geo.visible) return@MapEffect

                    val json =
                        runCatching { JSONObject(geo.geoJson) }
                            .getOrElse {
                                Log.e("KMPMaps", "Invalid GeoJSON JSON", it)
                                return@MapEffect
                            }

                    val layer = GoogleGeoJsonLayer(map, json)

                    layer.defaultLineStringStyle.color =
                        geo.lineColor?.toArgb() ?: Color.Magenta.toArgb()
                    layer.defaultLineStringStyle.width = geo.lineWidth ?: 5f
                    layer.defaultLineStringStyle.zIndex = geo.zIndex

                    layer.defaultPolygonStyle.fillColor =
                        geo.fillColor?.toArgb() ?: Color.Transparent.toArgb()
                    layer.defaultPolygonStyle.strokeColor =
                        geo.lineColor?.toArgb() ?: Color.Magenta.toArgb()
                    layer.defaultPolygonStyle.strokeWidth = geo.lineWidth ?: 5f
                    layer.defaultPolygonStyle.zIndex = geo.zIndex
                    layer

                    layer.addLayerToMap()
                    androidGeoJsonLayer = layer
                }
                .onFailure { t -> Log.e("KMPMaps", "Failed to render GeoJSON layer", t) }
        }

        DisposableEffect(Unit) {
            onDispose {
                androidGeoJsonLayer?.removeLayerFromMap()
                androidGeoJsonLayer = null
            }
        }

        markers.forEach { marker ->
            Marker(
                state = marker.toGoogleMapsMarkerState(),
                title = marker.title,
                anchor = marker.androidMarkerOptions.anchor.toOffset(),
                draggable = marker.androidMarkerOptions.draggable,
                snippet = marker.androidMarkerOptions.snippet,
                zIndex = marker.androidMarkerOptions.zIndex ?: 0.0f,
                onClick = {
                    onMarkerClick?.invoke(marker)
                    onMarkerClick == null
                },
            )
        }

        circles.forEach { circle ->
            Circle(
                center = circle.center.toGoogleMapsLatLng(),
                radius = circle.radius.toDouble(),
                strokeColor = Color(circle.lineColor?.toArgb() ?: android.graphics.Color.BLACK),
                strokeWidth = circle.lineWidth ?: 10f,
                fillColor = Color(circle.color?.toArgb() ?: android.graphics.Color.TRANSPARENT),
                clickable = true,
                onClick = {
                    if (onCircleClick != null) {
                        onCircleClick(circle)
                    } else {
                        onMapClick?.invoke(circle.center)
                    }
                },
            )
        }

        polygons.forEach { polygon ->
            Polygon(
                points = polygon.coordinates.map { it.toGoogleMapsLatLng() },
                strokeColor = Color(polygon.lineColor?.toArgb() ?: android.graphics.Color.BLACK),
                strokeWidth = polygon.lineWidth,
                fillColor = Color(polygon.color?.toArgb() ?: android.graphics.Color.TRANSPARENT),
                clickable = true,
                onClick = {
                    if (onPolygonClick != null) {
                        onPolygonClick(polygon)
                    } else {
                        onMapClick?.invoke(polygon.coordinates[0])
                    }
                },
            )
        }

        polylines.forEach { polyline ->
            Polyline(
                points = polyline.coordinates.map { it.toGoogleMapsLatLng() },
                color = Color(polyline.lineColor?.toArgb() ?: android.graphics.Color.BLACK),
                width = polyline.width,
                clickable = true,
                onClick = {
                    if (onPolylineClick != null) {
                        onPolylineClick(polyline)
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
