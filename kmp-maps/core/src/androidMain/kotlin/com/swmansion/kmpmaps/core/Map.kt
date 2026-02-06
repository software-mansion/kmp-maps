package com.swmansion.kmpmaps.core

import android.Manifest
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapEffect
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.Polygon
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.clustering.Clustering
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.data.Layer
import com.google.maps.android.data.geojson.GeoJsonLayer as GoogleGeoJsonLayer

/** Android implementation of the Map composable using Google Maps. */
@OptIn(ExperimentalPermissionsApi::class, MapsComposeExperimentalApi::class)
@Composable
public actual fun Map(
    modifier: Modifier,
    cameraPosition: CameraPosition?,
    properties: MapProperties,
    uiSettings: MapUISettings,
    clusterSettings: ClusterSettings,
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
    geoJsonLayers: List<GeoJsonLayer>,
    customMarkerContent: Map<String, @Composable (Marker) -> Unit>,
    webCustomMarkerContent: Map<String, (Marker) -> String>,
) {
    var mapLoaded by remember { mutableStateOf(false) }
    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val cameraPositionState = rememberCameraPositionState {
        cameraPosition?.let { position = it.toGoogleMapsCameraPosition() }
    }

    LaunchedEffect(properties.isMyLocationEnabled) {
        if (properties.isMyLocationEnabled && !locationPermissionState.status.isGranted) {
            locationPermissionState.launchPermissionRequest()
        }
    }

    LaunchedEffect(cameraPosition, mapLoaded) {
        if (mapLoaded && cameraPosition != null) {
            cameraPositionState.move(
                CameraUpdateFactory.newCameraPosition(cameraPosition.toGoogleMapsCameraPosition())
            )
        }
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
        onMapLoaded = {
            mapLoaded = true
            onMapLoaded?.invoke()
        },
    ) {
        var androidGeoJsonLayers by remember {
            mutableStateOf<Map<Int, GoogleGeoJsonLayer>>(emptyMap())
        }

        var geoJsonExtractedMarkers by remember {
            mutableStateOf<Map<Int, List<Marker>>>(emptyMap())
        }

        MapEffect(geoJsonLayers) { map ->
            runCatching {
                    val desiredKeys = geoJsonLayers.indices.toSet()
                    val keysToRemove = androidGeoJsonLayers.keys - desiredKeys
                    keysToRemove.forEach { k -> androidGeoJsonLayers[k]?.removeLayerFromMap() }

                    androidGeoJsonLayers = androidGeoJsonLayers.filterKeys(desiredKeys::contains)
                    geoJsonExtractedMarkers =
                        geoJsonExtractedMarkers.filterKeys(desiredKeys::contains)

                    geoJsonLayers.forEachIndexed { index, geo ->
                        if (geo.visible == false) {
                            androidGeoJsonLayers[index]?.removeLayerFromMap()
                            androidGeoJsonLayers = androidGeoJsonLayers - index
                            geoJsonExtractedMarkers = geoJsonExtractedMarkers - index
                            return@forEachIndexed
                        }

                        androidGeoJsonLayers[index]?.removeLayerFromMap()

                        map.renderGeoJsonLayer(geo, clusterSettings)?.let {
                            androidGeoJsonLayers = androidGeoJsonLayers + (index to it.layer)
                            geoJsonExtractedMarkers =
                                geoJsonExtractedMarkers + (index to it.extractedMarkers)
                        }
                    }
                }
                .onFailure { t -> Log.e("KMPMaps", "Failed to render GeoJSON layers", t) }
        }

        DisposableEffect(Unit) {
            onDispose { androidGeoJsonLayers.values.forEach(Layer::removeLayerFromMap) }
        }

        if (clusterSettings.enabled) {
            val clusterItems =
                remember(markers, geoJsonExtractedMarkers) {
                    (markers + geoJsonExtractedMarkers.values.flatten()).map(::MarkerClusterItem)
                }

            Clustering(
                items = clusterItems,
                onClusterClick = { androidCluster ->
                    clusterSettings.onClusterClick?.invoke(androidCluster.toNativeCluster())
                        ?: false
                },
                onClusterItemClick = { clusterItem ->
                    onMarkerClick?.invoke(clusterItem.marker)
                    onMarkerClick == null
                },
                clusterContent = { androidCluster ->
                    if (clusterSettings.clusterContent != null) {
                        clusterSettings.clusterContent.invoke(androidCluster.toNativeCluster())
                    } else {
                        DefaultCluster(size = androidCluster.size)
                    }
                },
                clusterItemContent = { clusterItem ->
                    customMarkerContent[clusterItem.marker.contentId]?.invoke(clusterItem.marker)
                        ?: DefaultPin(clusterItem.marker)
                },
            )
        } else {
            markers.forEach { marker ->
                key(marker.getId(), marker.contentId) {
                    val content = customMarkerContent[marker.contentId]

                    if (content != null) {
                        MarkerComposable(
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
                            content = { content(marker) },
                        )
                    } else {
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
                }
            }
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
                points = polygon.coordinates.map(Coordinates::toGoogleMapsLatLng),
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
                points = polyline.coordinates.map(Coordinates::toGoogleMapsLatLng),
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

        LaunchedEffect(cameraPositionState.position) {
            onCameraMove?.invoke(cameraPositionState.position.toCameraPosition())
        }
    }
}
