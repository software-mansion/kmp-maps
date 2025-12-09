package com.swmansion.kmpmaps.core

import android.Manifest
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
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
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.toColorInt
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
import com.google.maps.android.data.geojson.GeoJsonLineString
import com.google.maps.android.data.geojson.GeoJsonLineStringStyle
import com.google.maps.android.data.geojson.GeoJsonPoint
import com.google.maps.android.data.geojson.GeoJsonPointStyle
import com.google.maps.android.data.geojson.GeoJsonPolygon
import com.google.maps.android.data.geojson.GeoJsonPolygonStyle
import org.json.JSONObject

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
    customMarkerContent: Map<String, @Composable () -> Unit>,
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

    var mapLoaded by remember { mutableStateOf(false) }

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

        MapEffect(geoJsonLayers) { map ->
            runCatching {
                    val desiredKeys = geoJsonLayers.indices.toSet()
                    val keysToRemove = androidGeoJsonLayers.keys - desiredKeys
                    keysToRemove.forEach { k -> androidGeoJsonLayers[k]?.removeLayerFromMap() }
                    androidGeoJsonLayers = androidGeoJsonLayers.filterKeys(desiredKeys::contains)

                    geoJsonLayers.forEachIndexed { index, geo ->
                        if (geo.visible == false) {
                            androidGeoJsonLayers[index]?.removeLayerFromMap()
                            androidGeoJsonLayers = androidGeoJsonLayers - index
                            return@forEachIndexed
                        }

                        androidGeoJsonLayers[index]?.removeLayerFromMap()

                        val json =
                            runCatching { JSONObject(geo.geoJson) }
                                .getOrElse {
                                    Log.e("KMPMaps", "Invalid GeoJSON JSON", it)
                                    return@forEachIndexed
                                }

                        val layer = GoogleGeoJsonLayer(map, json).apply { applyStylesFrom(geo) }

                        layer.addLayerToMap()
                        androidGeoJsonLayers = androidGeoJsonLayers + (index to layer)
                    }
                }
                .onFailure { t -> Log.e("KMPMaps", "Failed to render GeoJSON layers", t) }
        }

        DisposableEffect(Unit) {
            onDispose { androidGeoJsonLayers.values.forEach(Layer::removeLayerFromMap) }
        }

        if (clusterSettings.enabled) {
            val clusterItems = remember(markers) { markers.map { MarkerClusterItem(it) } }

            Clustering(
                items = clusterItems,
                onClusterClick = { androidCluster ->
                    val kmpCluster =
                        Cluster(
                            coordinates =
                                Coordinates(
                                    androidCluster.position.latitude,
                                    androidCluster.position.longitude,
                                ),
                            size = androidCluster.size,
                            items = androidCluster.items.map { it.marker },
                        )
                    clusterSettings.onClusterClick?.invoke(kmpCluster) ?: false
                },
                onClusterItemClick = { clusterItem ->
                    onMarkerClick?.invoke(clusterItem.marker)
                    true
                },
                clusterContent = { androidCluster ->
                    if (clusterSettings.clusterContent != null) {
                        val kmpCluster =
                            Cluster(
                                coordinates =
                                    Coordinates(
                                        androidCluster.position.latitude,
                                        androidCluster.position.longitude,
                                    ),
                                size = androidCluster.size,
                                items = androidCluster.items.map { it.marker },
                            )
                        clusterSettings.clusterContent.invoke(kmpCluster)
                    } else {
                        DefaultCluster(size = androidCluster.size)
                    }
                },
                clusterItemContent = { clusterItem ->
                    val content = customMarkerContent[clusterItem.marker.contentId]

                    if (content != null) {
                        content()
                    } else {
                        DefaultPin()
                    }
                },
            )
        } else {
            markers.forEach { marker ->
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
                        content = content,
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

        LaunchedEffect(cameraPositionState.position) {
            onCameraMove?.invoke(cameraPositionState.position.toCameraPosition())
        }
    }
}

private fun applyAlpha(color: Int, opacity: Float?) =
    if (opacity != null) {
        ColorUtils.setAlphaComponent(color, (opacity.coerceIn(0f, 1f) * 255f).toInt())
    } else {
        color
    }

private fun GoogleGeoJsonLayer.applyStylesFrom(geo: GeoJsonLayer) {
    defaultLineStringStyle.pattern = geo.lineStringStyle?.pattern?.toGooglePattern()
    defaultLineStringStyle.isClickable = geo.isClickable == true
    defaultLineStringStyle.color =
        geo.lineStringStyle?.lineColor?.toArgb() ?: DEFAULT_STROKE_COLOR.toArgb()
    defaultLineStringStyle.width = geo.lineStringStyle?.lineWidth ?: DEFAULT_STROKE_WIDTH
    defaultLineStringStyle.zIndex = geo.zIndex
    defaultLineStringStyle.isVisible = geo.visible != false
    defaultLineStringStyle.isGeodesic = geo.isGeodesic == true

    defaultPolygonStyle.fillColor =
        geo.polygonStyle?.fillColor?.toArgb() ?: DEFAULT_FILL_COLOR.toArgb()
    defaultPolygonStyle.strokeColor =
        geo.polygonStyle?.strokeColor?.toArgb() ?: DEFAULT_STROKE_COLOR.toArgb()
    defaultPolygonStyle.strokeWidth = geo.polygonStyle?.strokeWidth ?: DEFAULT_STROKE_WIDTH
    defaultPolygonStyle.zIndex = geo.zIndex
    defaultPolygonStyle.isGeodesic = geo.isGeodesic == true
    defaultPolygonStyle.isClickable = geo.isClickable == true
    defaultPolygonStyle.isVisible = geo.visible != false

    defaultPointStyle.alpha = geo.pointStyle?.alpha ?: 1f
    defaultPointStyle.isDraggable = geo.pointStyle?.isDraggable ?: true
    defaultPointStyle.isFlat = geo.pointStyle?.isFlat ?: false
    defaultPointStyle.rotation = geo.pointStyle?.rotation ?: 0f
    defaultPointStyle.title = geo.pointStyle?.pointTitle
    defaultPointStyle.snippet = geo.pointStyle?.snippet
    defaultPointStyle.isVisible = geo.visible != false
    defaultPointStyle.zIndex = geo.zIndex
    defaultPointStyle.setInfoWindowAnchor(
        geo.pointStyle?.infoWindowAnchorU ?: 0.5f,
        geo.pointStyle?.infoWindowAnchorV ?: 0.5f,
    )
    defaultPointStyle.setAnchor(geo.pointStyle?.anchorU ?: 0.5f, geo.pointStyle?.anchorV ?: 0.5f)

    features.forEach { feature ->
        val strokeHex = feature.getProperty("stroke")
        val strokeWidthJson = feature.getProperty("stroke-width")?.toFloatOrNull()

        val fillHex = feature.getProperty("fill")
        val fillOpacity = feature.getProperty("fill-opacity")?.toFloatOrNull()

        when (feature.geometry) {
            is GeoJsonLineString -> {
                val strokeColor = strokeHex.toColorInt()
                val width = strokeWidthJson ?: DEFAULT_STROKE_WIDTH

                feature.setLineStringStyle(
                    GeoJsonLineStringStyle().apply {
                        color = strokeColor
                        this.width = width
                        isClickable = geo.isClickable == true
                        isVisible = geo.visible != false
                        zIndex = geo.zIndex
                        isGeodesic = geo.isGeodesic == true
                        pattern = geo.lineStringStyle?.pattern?.toGooglePattern()
                    }
                )
            }
            is GeoJsonPolygon -> {
                val strokeColor = strokeHex.toColorInt()
                val strokeWidth = strokeWidthJson ?: DEFAULT_STROKE_WIDTH
                val fillColor =
                    fillHex.toColorInt().let { c ->
                        if (fillOpacity != null) applyAlpha(c, fillOpacity) else c
                    }

                feature.setPolygonStyle(
                    GeoJsonPolygonStyle().apply {
                        this.strokeColor = strokeColor
                        this.strokeWidth = strokeWidth
                        this.fillColor = fillColor
                        isClickable = geo.isClickable == true
                        isVisible = geo.visible != false
                        zIndex = geo.zIndex
                        isGeodesic = geo.isGeodesic == true
                    }
                )
            }
            is GeoJsonPoint -> {
                val titleFromJson =
                    feature.getProperty("title")
                        ?: feature.getProperty("name")
                        ?: geo.pointStyle?.pointTitle
                val snippetFromJson =
                    feature.getProperty("snippet")
                        ?: feature.getProperty("description")
                        ?: geo.pointStyle?.snippet

                feature.setPointStyle(
                    GeoJsonPointStyle().apply {
                        alpha = geo.pointStyle?.alpha ?: 1f
                        isDraggable = geo.pointStyle?.isDraggable ?: true
                        isFlat = geo.pointStyle?.isFlat ?: false
                        rotation = geo.pointStyle?.rotation ?: 0f
                        title = titleFromJson
                        snippet = snippetFromJson
                        isVisible = geo.visible != false
                        zIndex = geo.zIndex
                        setInfoWindowAnchor(
                            geo.pointStyle?.infoWindowAnchorU ?: 0.5f,
                            geo.pointStyle?.infoWindowAnchorV ?: 0.5f,
                        )
                        setAnchor(geo.pointStyle?.anchorU ?: 0.5f, geo.pointStyle?.anchorV ?: 0.5f)
                    }
                )
            }
            else -> Unit
        }
    }
}
