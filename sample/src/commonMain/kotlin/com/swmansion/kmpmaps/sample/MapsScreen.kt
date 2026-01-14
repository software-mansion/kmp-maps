package com.swmansion.kmpmaps.sample

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import com.swmansion.kmpmaps.core.MapTheme
import com.swmansion.kmpmaps.core.MapType
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MapsScreen(
    options: MapOptions,
    updateOptions: (MapOptions.() -> MapOptions) -> Unit,
    mapContent: @Composable (Modifier) -> Unit,
) {
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }

    if (isJvm()) {
        mapContent(Modifier.fillMaxSize())
    } else {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = { showBottomSheet = true }) {
                    Icon(imageVector = Icons.Filled.Settings, contentDescription = "Settings")
                }
            }
        ) {
            mapContent(Modifier.fillMaxSize())
        }
        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = bottomSheetState,
            ) {
                Column(
                    Modifier.fillMaxWidth()
                        .windowInsetsPadding(
                            WindowInsets.safeDrawing.exclude(WindowInsets.statusBars)
                        )
                        .padding(vertical = 16.dp),
                    Arrangement.spacedBy(8.dp),
                    Alignment.CenterHorizontally,
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            onClick = { updateOptions { copy(mapType = MapType.NORMAL) } },
                            label = { Text("Normal") },
                            selected = options.mapType == MapType.NORMAL,
                        )
                        FilterChip(
                            onClick = { updateOptions { copy(mapType = MapType.SATELLITE) } },
                            label = { Text("Satellite") },
                            selected = options.mapType == MapType.SATELLITE,
                        )
                        FilterChip(
                            onClick = { updateOptions { copy(mapType = MapType.HYBRID) } },
                            label = { Text("Hybrid") },
                            selected = options.mapType == MapType.HYBRID,
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            onClick = { updateOptions { copy(mapTheme = MapTheme.SYSTEM) } },
                            label = { Text("System") },
                            selected = options.mapTheme == MapTheme.SYSTEM,
                        )
                        FilterChip(
                            onClick = { updateOptions { copy(mapTheme = MapTheme.LIGHT) } },
                            label = { Text("Light") },
                            selected = options.mapTheme == MapTheme.LIGHT,
                        )
                        FilterChip(
                            onClick = { updateOptions { copy(mapTheme = MapTheme.DARK) } },
                            label = { Text("Dark") },
                            selected = options.mapTheme == MapTheme.DARK,
                        )
                    }
                    if (isIOS()) {
                        ListItem(
                            headlineContent = { Text("Use Google Maps") },
                            modifier =
                                Modifier.height(48.dp).clickable {
                                    updateOptions {
                                        copy(useGoogleMapsMapView = !useGoogleMapsMapView)
                                    }
                                },
                            trailingContent = {
                                Switch(
                                    checked = options.useGoogleMapsMapView,
                                    onCheckedChange = { v ->
                                        updateOptions { copy(useGoogleMapsMapView = v) }
                                    },
                                )
                            },
                        )
                    }
                    ListItem(
                        headlineContent = { Text("Show annotations") },
                        modifier =
                            Modifier.height(48.dp).clickable {
                                updateOptions { copy(showAllComponents = !showAllComponents) }
                            },
                        trailingContent = {
                            Switch(
                                checked = options.showAllComponents,
                                onCheckedChange = { v ->
                                    updateOptions { copy(showAllComponents = v) }
                                },
                            )
                        },
                    )
                    ListItem(
                        headlineContent = { Text("Marker clustering") },
                        modifier =
                            Modifier.height(48.dp).clickable {
                                updateOptions { copy(clusteringEnabled = !clusteringEnabled) }
                            },
                        trailingContent = {
                            Switch(
                                checked = options.clusteringEnabled,
                                onCheckedChange = { v ->
                                    updateOptions { copy(clusteringEnabled = v) }
                                },
                            )
                        },
                    )
                    ListItem(
                        headlineContent = { Text("Show my location") },
                        modifier =
                            Modifier.height(48.dp).clickable {
                                updateOptions { copy(showUserLocation = !showUserLocation) }
                            },
                        trailingContent = {
                            Switch(
                                checked = options.showUserLocation,
                                onCheckedChange = { v ->
                                    updateOptions { copy(showUserLocation = v) }
                                },
                            )
                        },
                    )
                    ListItem(
                        headlineContent = { Text("GeoJSON Layers") },
                        modifier = Modifier.height(48.dp),
                        trailingContent = {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                FilterChip(
                                    selected = options.showPointGeoJson,
                                    onClick = {
                                        updateOptions { copy(showPointGeoJson = !showPointGeoJson) }
                                    },
                                    label = { Text("Point") },
                                )
                                FilterChip(
                                    selected = options.showPolygonGeoJson,
                                    onClick = {
                                        updateOptions {
                                            copy(showPolygonGeoJson = !showPolygonGeoJson)
                                        }
                                    },
                                    label = { Text("Area") },
                                )
                                FilterChip(
                                    selected = options.showLineGeoJson,
                                    onClick = {
                                        updateOptions { copy(showLineGeoJson = !showLineGeoJson) }
                                    },
                                    label = { Text("Line") },
                                )
                            }
                        },
                    )
                    Button(
                        onClick = { updateOptions { copy(cameraPosition = getRandomPosition()) } },
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    ) {
                        Text("Random location")
                    }
                }
            }
        }
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
