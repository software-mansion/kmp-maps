package com.swmansion.kmpmaps.sample

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.swmansion.kmpmaps.MapAnnotation
import com.swmansion.kmpmaps.MapRegion
import com.swmansion.kmpmaps.MapType
import com.swmansion.kmpmaps.MapView
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme { MapScreen() }
}

@Composable
fun MapScreen() {
    var selectedMapType by remember { mutableStateOf(MapType.STANDARD) }
    var showUserLocation by remember { mutableStateOf(false) }
    var currentRegion by remember {
        mutableStateOf(
            MapRegion(
                latitude = 50.0619,
                longitude = 19.9373,
                latitudeDelta = 0.1,
                longitudeDelta = 0.1,
            )
        )
    }
    val exampleAnnotation = MapAnnotation(
        id = "1",
        latitude = 50.0486,
        longitude = 19.9654,
        title = "Software Mansion",
        subtitle = "React Native Company",
    )
    var annotations by remember {
        mutableStateOf(
            listOf(
                exampleAnnotation
            )
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        MapView(
            region = currentRegion,
            mapType = selectedMapType,
            annotations = annotations,
            showUserLocation = showUserLocation,
            onRegionChange = { region ->
                currentRegion = region
                println("Region changed: $region")
            },
            onAnnotationPress = { annotation ->
                println("Annotation pressed: ${annotation.title}")
            },
            modifier = Modifier.fillMaxSize(),
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilterChip(
                        onClick = { selectedMapType = MapType.STANDARD },
                        label = { Text("Standard") },
                        selected = selectedMapType == MapType.STANDARD,
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
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilterChip(
                        onClick = {
                            annotations =
                                if (annotations.isEmpty()) {
                                    listOf(exampleAnnotation)
                                } else {
                                    emptyList()
                                }
                        },
                        label = {
                            Text(if (annotations.isEmpty()) "Generate Pins" else "Delete Pins")
                        },
                        selected = annotations.isNotEmpty(),
                    )
                }
            }
        }
    }
}