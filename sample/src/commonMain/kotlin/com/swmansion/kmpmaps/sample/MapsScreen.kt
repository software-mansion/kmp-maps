package com.swmansion.kmpmaps.sample

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.swmansion.kmpmaps.CameraPosition
import com.swmansion.kmpmaps.Coordinates
import com.swmansion.kmpmaps.Map
import com.swmansion.kmpmaps.MapType

@Composable
fun MapsScreen() {
    var selectedMapType by remember { mutableStateOf(MapType.NORMAL) }
    var showUserLocation by remember { mutableStateOf(false) }
    var currentCameraPosition by remember {
        mutableStateOf(
            CameraPosition(
                coordinates = Coordinates(latitude = 50.0619, longitude = 19.9373),
                zoom = 13f,
            )
        )
    }
    var markers by remember { mutableStateOf(exampleMarkers) }
    var circles by remember { mutableStateOf(exampleCircles) }
    var polygons by remember { mutableStateOf(examplePolygons) }
    var polylines by remember { mutableStateOf(examplePolylines) }
    var showAllComponents by remember { mutableStateOf(true) }

    Box(modifier = Modifier.fillMaxSize()) {
        Map(
            modifier = Modifier.fillMaxSize(),
            cameraPosition = currentCameraPosition,
            properties = createMapProperties(selectedMapType, showUserLocation),
            uiSettings = createMapUISettings(showUserLocation),
            markers = if (showAllComponents) markers else emptyList(),
            circles = if (showAllComponents) circles else emptyList(),
            polygons = if (showAllComponents) polygons else emptyList(),
            polylines = if (showAllComponents) polylines else emptyList(),
            onCameraMove = { position ->
                currentCameraPosition = position
                println("Camera moved: $position")
            },
            onMarkerClick = { marker -> println("Marker clicked: ${marker.title}") },
            onMapClick = { coordinates -> println("Map clicked at: $coordinates") },
        )

        Card(
            modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
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
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    FilterChip(
                        onClick = { showAllComponents = !showAllComponents },
                        label = { Text(if (showAllComponents) "Hide All" else "Show All") },
                        selected = showAllComponents,
                    )
                    FilterChip(
                        onClick = { showUserLocation = !showUserLocation },
                        label = { Text("Show My Location") },
                        selected = showUserLocation,
                    )
                }
            }
        }
    }
}
