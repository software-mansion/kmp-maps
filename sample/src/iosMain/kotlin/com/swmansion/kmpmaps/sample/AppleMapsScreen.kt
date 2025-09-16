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
import com.swmansion.kmpmaps.AppleMapPointOfInterestCategory
import com.swmansion.kmpmaps.AppleMapsMapType
import com.swmansion.kmpmaps.AppleMapsPointOfInterestCategories
import com.swmansion.kmpmaps.AppleMapsProperties
import com.swmansion.kmpmaps.AppleMapsUISettings
import com.swmansion.kmpmaps.AppleMapsView
import com.swmansion.kmpmaps.CameraPosition
import com.swmansion.kmpmaps.Coordinates

@Composable
public actual fun MapsScreen() {
    var selectedMapType by remember { mutableStateOf(AppleMapsMapType.STANDARD) }
    var showUserLocation by remember { mutableStateOf(false) }
    var currentCameraPosition by remember {
        mutableStateOf(
            CameraPosition(
                coordinates = Coordinates(latitude = 50.0619, longitude = 19.9373),
                zoom = 13f,
            ),
        )
    }
    var markers by remember { mutableStateOf(exampleAppleMapsMarkers) }
    var annotations by remember { mutableStateOf(exampleAppleMapsAnnotations) }
    var circles by remember { mutableStateOf(exampleAppleMapsCircles) }
    var polygons by remember { mutableStateOf(exampleAppleMapsPolygons) }
    var polylines by remember { mutableStateOf(exampleAppleMapsPolylines) }
    var showAllComponents by remember { mutableStateOf(true) }
    Box(modifier = Modifier.fillMaxSize()) {
        AppleMapsView(
            cameraPosition = currentCameraPosition,
            properties = AppleMapsProperties(
                mapType = selectedMapType,
                isMyLocationEnabled = showUserLocation,
                showsBuildings = true,
                isTrafficEnabled = false,
                pointsOfInterest = AppleMapsPointOfInterestCategories(
                    including = listOf(
                        AppleMapPointOfInterestCategory.RESTAURANT,
                        AppleMapPointOfInterestCategory.CAFE,
                    ),
                ),
            ),
            uiSettings = AppleMapsUISettings(
                compassEnabled = true,
                myLocationButtonEnabled = showUserLocation,
                zoomGesturesEnabled = true,
                scrollGesturesEnabled = true,
                rotateGesturesEnabled = true,
                tiltGesturesEnabled = true,
            ),
            annotations = if (showAllComponents) annotations else emptyList(),
            markers = if (showAllComponents) markers else emptyList(),
            circles = if (showAllComponents) circles else emptyList(),
            polygons = if (showAllComponents) polygons else emptyList(),
            polylines = if (showAllComponents) polylines else emptyList(),
            onCameraMove = { position ->
                currentCameraPosition = position
                println("Camera moved: $position")
            },
            onMarkerClick = { marker ->
                println("Marker clicked: ${marker.title}")
            },
            onMapClick = { coordinates ->
                println("Map clicked at: $coordinates")
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
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    FilterChip(
                        onClick = { selectedMapType = AppleMapsMapType.STANDARD },
                        label = { Text("Normal") },
                        selected = selectedMapType == AppleMapsMapType.STANDARD,
                    )
                    FilterChip(
                        onClick = { selectedMapType = AppleMapsMapType.SATELLITE },
                        label = { Text("Satellite") },
                        selected = selectedMapType == AppleMapsMapType.SATELLITE,
                    )
                    FilterChip(
                        onClick = { selectedMapType = AppleMapsMapType.HYBRID },
                        label = { Text("Hybrid") },
                        selected = selectedMapType == AppleMapsMapType.HYBRID,
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    FilterChip(
                        onClick = {
                            showAllComponents = !showAllComponents
                        },
                        label = {
                            Text(if (showAllComponents) "Hide All" else "Show All")
                        },
                        selected = showAllComponents,
                    )
                    FilterChip(
                        onClick = { showUserLocation = !showUserLocation },
                        label = { Text("Show My Location aaa") },
                        selected = showUserLocation,
                    )
                }
            }
        }
    }
}
