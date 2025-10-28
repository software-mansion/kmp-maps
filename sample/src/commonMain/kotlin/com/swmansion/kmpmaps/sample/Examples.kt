package com.swmansion.kmpmaps.sample

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.swmansion.kmpmaps.core.AndroidMarkerOptions
import com.swmansion.kmpmaps.core.Circle
import com.swmansion.kmpmaps.core.Coordinates
import com.swmansion.kmpmaps.core.Marker
import com.swmansion.kmpmaps.core.Polygon
import com.swmansion.kmpmaps.core.Polyline

val softwareMansionPin = Coordinates(latitude = 50.0486, longitude = 19.9654)
val cracowMainStationPin = Coordinates(latitude = 50.06839615782847, longitude = 19.947491884231567)
val jewishQuarterPin = Coordinates(latitude = 50.0515, longitude = 19.9449)

val exampleMarkers =
    listOf(
        Marker(
            coordinates = softwareMansionPin,
            title = "Software Mansion",
            androidMarkerOptions = AndroidMarkerOptions(snippet = "Software house"),
        ),
        Marker(
            coordinates = cracowMainStationPin,
            title = "Kraków Main",
            androidMarkerOptions = AndroidMarkerOptions(snippet = "Transit station"),
        ),
        Marker(
            coordinates = jewishQuarterPin,
            title = "Kazimierz",
            androidMarkerOptions = AndroidMarkerOptions(snippet = "Jewish quarter"),
        ),
    )

@Composable
fun getExampleCircles() =
    listOf(
        Circle(
            center = softwareMansionPin,
            radius = 500.0f,
            lineColor = MaterialTheme.colorScheme.primary,
            lineWidth = 1f,
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
        ),
        Circle(
            center = cracowMainStationPin,
            radius = 300.0f,
            lineColor = MaterialTheme.colorScheme.secondary,
            lineWidth = 1f,
            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f),
        ),
    )

@Composable
fun getExamplePolygons() =
    listOf(
        Polygon(
            coordinates =
                listOf(
                    Coordinates(latitude = 50.0550, longitude = 19.9400),
                    Coordinates(latitude = 50.0550, longitude = 19.9500),
                    Coordinates(latitude = 50.0480, longitude = 19.9500),
                    Coordinates(latitude = 50.0480, longitude = 19.9400),
                    Coordinates(latitude = 50.0550, longitude = 19.9400),
                ),
            lineColor = MaterialTheme.colorScheme.tertiary,
            lineWidth = 1f,
            color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.2f),
        )
    )

@Composable
fun getExamplePolylines() =
    listOf(
        Polyline(
            coordinates = listOf(softwareMansionPin, cracowMainStationPin, jewishQuarterPin),
            lineColor = MaterialTheme.colorScheme.error,
            width = 1f,
        )
    )
