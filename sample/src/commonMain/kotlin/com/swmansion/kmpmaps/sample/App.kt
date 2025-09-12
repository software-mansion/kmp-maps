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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.swmansion.kmpmaps.Coordinates
import com.swmansion.kmpmaps.Map
import com.swmansion.kmpmaps.MapAnnotation
import com.swmansion.kmpmaps.MapRegion
import com.swmansion.kmpmaps.MapType
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
                coordinates = Coordinates(latitude = 50.0619, longitude = 19.9373),
                zoom = 13f,
            )
        )
    }
    val exampleAnnotation =
        MapAnnotation(
            coordinates = Coordinates(latitude = 50.0486, longitude = 19.9654),
            title = "Software Mansion",
            subtitle = "React Native Company",
        )
    var annotations by remember { mutableStateOf(listOf(exampleAnnotation)) }

    Box(modifier = Modifier.fillMaxSize()) {
        Map(
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
                    verticalAlignment = Alignment.CenterVertically,
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
