package com.swmansion.kmpmaps.sample

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
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
import androidx.compose.ui.unit.dp
import com.swmansion.kmpmaps.core.CameraPosition
import com.swmansion.kmpmaps.core.Coordinates
import com.swmansion.kmpmaps.core.MapProperties
import com.swmansion.kmpmaps.core.MapTheme
import com.swmansion.kmpmaps.core.MapType
import com.swmansion.kmpmaps.core.MapUISettings
import com.swmansion.kmpmaps.googlemaps.Map as GoogleMap

@Composable
fun MapsScreen() {
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

    Column(Modifier.fillMaxHeight(), Arrangement.Bottom) {
        GoogleMap(
            modifier = Modifier.weight(1f),
            cameraPosition = currentCameraPosition,
            properties =
                MapProperties(
                    mapType = selectedMapType,
                    mapTheme = selectedMapTheme,
                    isMyLocationEnabled = showUserLocation,
                    isTrafficEnabled = true,
                    isBuildingEnabled = true,
                    androidIsIndoorEnabled = true,
                    androidMinZoomPreference = 3f,
                    androidMaxZoomPreference = 21f,
                ),
            uiSettings =
                MapUISettings(
                    compassEnabled = true,
                    myLocationButtonEnabled = showUserLocation,
                    scaleBarEnabled = true,
                    androidZoomControlsEnabled = false,
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
            }
        }
    }
}
