package com.swmansion.kmpmaps.sample

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.ListItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.swmansion.kmpmaps.core.AndroidMapProperties
import com.swmansion.kmpmaps.core.AndroidUISettings
import com.swmansion.kmpmaps.core.CameraPosition
import com.swmansion.kmpmaps.core.Circle
import com.swmansion.kmpmaps.core.Coordinates
import com.swmansion.kmpmaps.core.GeoJsonLayer
import com.swmansion.kmpmaps.core.Map as CoreMap
import com.swmansion.kmpmaps.core.MapProperties
import com.swmansion.kmpmaps.core.MapTheme
import com.swmansion.kmpmaps.core.MapType
import com.swmansion.kmpmaps.core.MapUISettings
import com.swmansion.kmpmaps.core.Marker
import com.swmansion.kmpmaps.core.PointStyle
import com.swmansion.kmpmaps.core.Polygon
import com.swmansion.kmpmaps.core.Polyline
import com.swmansion.kmpmaps.googlemaps.Map as GoogleMap
import kotlin.random.Random

@Composable
internal fun MapsScreen() {
    var selectedMapType by remember { mutableStateOf(MapType.NORMAL) }
    var selectedMapTheme by remember { mutableStateOf(MapTheme.SYSTEM) }
    var showUserLocation by remember { mutableStateOf(false) }
    var currentCameraPosition by remember {
        mutableStateOf(
            CameraPosition(
                coordinates = Coordinates(latitude = 50.0619, longitude = 19.9373),
                zoom = 13f,
            )
        )
    }
    var showAllComponents by remember { mutableStateOf(true) }
    var useGoogleMapsMapView by remember { mutableStateOf(true) }
    var showPointGeoJson by remember { mutableStateOf(false) }
    var showPolygonGeoJson by remember { mutableStateOf(false) }
    var showLineGeoJson by remember { mutableStateOf(false) }

    val geoJsonLayers =
        remember(showPointGeoJson, showPolygonGeoJson, showLineGeoJson) {
            buildList {
                if (showPointGeoJson) {
                    add(
                        GeoJsonLayer(
                            geoJson = EXAMPLE_POINT_GEO_JSON,
                            pointStyle =
                                PointStyle(
                                    snippet = "Recommended food places",
                                    infoWindowAnchorU = 0.1f,
                                    infoWindowAnchorV = 0.7f,
                                ),
                        )
                    )
                }
                if (showPolygonGeoJson) {
                    add(GeoJsonLayer(geoJson = EXAMPLE_POLYGON_GEO_JSON))
                }
                if (showLineGeoJson) {
                    add(GeoJsonLayer(geoJson = EXAMPLE_LINE_GEO_JSON))
                }
            }
        }

    val customMarkerContent =
        mapOf(
            "custom_blue_circle_marker" to
                @Composable {
                    Box(modifier = Modifier.size(50.dp).background(Color.Blue, CircleShape)) {
                        Text("Custom")
                    }
                }
        )

    Column(Modifier.fillMaxHeight(), Arrangement.Bottom) {
        Map(
            modifier = Modifier.weight(1f),
            mapProvider = if (useGoogleMapsMapView) MapProvider.GOOGLE_MAPS else MapProvider.NATIVE,
            cameraPosition = currentCameraPosition,
            properties =
                MapProperties(
                    mapType = selectedMapType,
                    mapTheme = selectedMapTheme,
                    isMyLocationEnabled = showUserLocation,
                    isTrafficEnabled = true,
                    isBuildingEnabled = true,
                    androidMapProperties =
                        AndroidMapProperties(
                            isIndoorEnabled = true,
                            minZoomPreference = 3f,
                            maxZoomPreference = 21f,
                        ),
                ),
            uiSettings =
                MapUISettings(
                    compassEnabled = true,
                    myLocationButtonEnabled = showUserLocation,
                    scaleBarEnabled = true,
                    androidUISettings = AndroidUISettings(zoomControlsEnabled = false),
                ),
            markers = if (showAllComponents) exampleMarkers else emptyList(),
            circles = if (showAllComponents) getExampleCircles() else emptyList(),
            polygons = if (showAllComponents) getExamplePolygons() else emptyList(),
            polylines = if (showAllComponents) getExamplePolylines() else emptyList(),
            onCameraMove = { position -> println("Camera moved: $position") },
            onCircleClick = { println("Circle clicked: ${it.center}") },
            onPolygonClick = { println("Polygon clicked: ${it.coordinates}") },
            onPolylineClick = { println("Polyline clicked: ${it.coordinates}") },
            onPOIClick = { println("POI clicked: $it") },
            onMapLoaded = { println("Map loaded") },
            onMapLongClick = { println("Map long clicked: $it") },
            onMarkerClick = { marker -> println("Marker clicked: ${marker.title}") },
            onMapClick = { coordinates -> println("Map clicked at: $coordinates") },
            geoJsonLayers = geoJsonLayers,
            customMarkerContent = customMarkerContent,
        )
        Surface {
            Column(
                Modifier.fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.safeDrawing.exclude(WindowInsets.statusBars))
                    .padding(vertical = 16.dp),
                Arrangement.spacedBy(8.dp),
                Alignment.CenterHorizontally,
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        onClick = { selectedMapType = MapType.NORMAL },
                        label = { Text("Normal") },
                        selected = selectedMapType == MapType.NORMAL,
                    )
                    FilterChip(
                        onClick = { selectedMapType = MapType.SATELLITE },
                        label = { Text("Satellite") },
                        selected = selectedMapType == MapType.SATELLITE,
                    )
                    FilterChip(
                        onClick = { selectedMapType = MapType.HYBRID },
                        label = { Text("Hybrid") },
                        selected = selectedMapType == MapType.HYBRID,
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        onClick = { selectedMapTheme = MapTheme.SYSTEM },
                        label = { Text("System") },
                        selected = selectedMapTheme == MapTheme.SYSTEM,
                    )
                    FilterChip(
                        onClick = { selectedMapTheme = MapTheme.LIGHT },
                        label = { Text("Light") },
                        selected = selectedMapTheme == MapTheme.LIGHT,
                    )
                    FilterChip(
                        onClick = { selectedMapTheme = MapTheme.DARK },
                        label = { Text("Dark") },
                        selected = selectedMapTheme == MapTheme.DARK,
                    )
                }
                if (isIOS()) {
                    ListItem(
                        headlineContent = { Text("Use Google Maps") },
                        modifier =
                            Modifier.height(48.dp).clickable {
                                useGoogleMapsMapView = !useGoogleMapsMapView
                            },
                        trailingContent = {
                            Switch(
                                checked = useGoogleMapsMapView,
                                onCheckedChange = { useGoogleMapsMapView = it },
                            )
                        },
                    )
                }
                ListItem(
                    headlineContent = { Text("Show annotations") },
                    modifier =
                        Modifier.height(48.dp).clickable { showAllComponents = !showAllComponents },
                    trailingContent = {
                        Switch(
                            checked = showAllComponents,
                            onCheckedChange = { showAllComponents = it },
                        )
                    },
                )
                ListItem(
                    headlineContent = { Text("Show my location") },
                    modifier =
                        Modifier.height(48.dp).clickable { showUserLocation = !showUserLocation },
                    trailingContent = {
                        Switch(
                            checked = showUserLocation,
                            onCheckedChange = { showUserLocation = it },
                        )
                    },
                )
                ListItem(
                    headlineContent = { Text("GeoJSON") },
                    modifier = Modifier.height(48.dp),
                    trailingContent = {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = showPointGeoJson,
                                onClick = { showPointGeoJson = !showPointGeoJson },
                                label = { Text("Point") },
                            )
                            FilterChip(
                                selected = showPolygonGeoJson,
                                onClick = { showPolygonGeoJson = !showPolygonGeoJson },
                                label = { Text("Area") },
                            )
                            FilterChip(
                                selected = showLineGeoJson,
                                onClick = { showLineGeoJson = !showLineGeoJson },
                                label = { Text("Line") },
                            )
                        }
                    },
                )
                Button(
                    onClick = { currentCameraPosition = getRandomPosition() },
                    content = { Text("Random location") },
                )
            }
        }
    }
}

private enum class MapProvider {
    NATIVE,
    GOOGLE_MAPS,
}

@Composable
private fun Map(
    modifier: Modifier = Modifier,
    mapProvider: MapProvider,
    cameraPosition: CameraPosition? = null,
    properties: MapProperties = MapProperties(),
    uiSettings: MapUISettings = MapUISettings(),
    markers: List<Marker> = emptyList(),
    circles: List<Circle> = emptyList(),
    polygons: List<Polygon> = emptyList(),
    polylines: List<Polyline> = emptyList(),
    onCameraMove: ((CameraPosition) -> Unit)? = null,
    onMarkerClick: ((Marker) -> Unit)? = null,
    onCircleClick: ((Circle) -> Unit)? = null,
    onPolygonClick: ((Polygon) -> Unit)? = null,
    onPolylineClick: ((Polyline) -> Unit)? = null,
    onMapClick: ((Coordinates) -> Unit)? = null,
    onMapLongClick: ((Coordinates) -> Unit)? = null,
    onPOIClick: ((Coordinates) -> Unit)? = null,
    onMapLoaded: (() -> Unit)? = null,
    geoJsonLayers: List<GeoJsonLayer> = emptyList(),
    customMarkerContent: Map<String, @Composable () -> Unit> = emptyMap(),
) {
    when (mapProvider) {
        MapProvider.NATIVE ->
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
                geoJsonLayers = geoJsonLayers,
                customMarkerContent = customMarkerContent,
            )
        MapProvider.GOOGLE_MAPS ->
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
                geoJsonLayers = geoJsonLayers,
            )
    }
}

private fun getRandomPosition() =
    CameraPosition(
        coordinates =
            Coordinates(
                latitude = Random.nextDouble(50.0, 52.0),
                longitude = Random.nextDouble(50.0, 52.0),
            ),
        zoom = 13f,
    )
