package com.swmansion.kmpmaps.sample

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.swmansion.kmpmaps.*

val softwareMansionPin = Coordinates(latitude = 50.0486, longitude = 19.9654)
val royalCastlePin = Coordinates(latitude = 50.0647, longitude = 19.9450)
val jewishQuarterPin = Coordinates(latitude = 50.0515, longitude = 19.9449)

val exampleMarkers =
    listOf(
        MapMarker(
            coordinates = softwareMansionPin,
            title = "Software Mansion",
            androidSnippet = "Software house",
        ),
        MapMarker(coordinates = royalCastlePin, title = "Wawel", androidSnippet = "Royal castle"),
        MapMarker(
            coordinates = jewishQuarterPin,
            title = "Kazimierz",
            androidSnippet = "Jewish quarter",
        ),
    )

@Composable
fun getExampleCircles() =
    listOf(
        MapCircle(
            center = Coordinates(latitude = 50.0486, longitude = 19.9654),
            radius = 500.0f,
            lineColor = MaterialTheme.colorScheme.primary,
            lineWidth = 1f,
            color = MaterialTheme.colorScheme.primaryContainer,
        ),
        MapCircle(
            center = Coordinates(latitude = 50.0647, longitude = 19.9450),
            radius = 300.0f,
            lineColor = MaterialTheme.colorScheme.secondary,
            lineWidth = 1f,
            color = MaterialTheme.colorScheme.secondaryContainer,
        ),
    )

@Composable
fun getExamplePolygons() =
    listOf(
        MapPolygon(
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
            color = MaterialTheme.colorScheme.tertiaryContainer,
        )
    )

@Composable
fun getExamplePolylines() =
    listOf(
        MapPolyline(
            coordinates = listOf(softwareMansionPin, royalCastlePin, jewishQuarterPin),
            lineColor = MaterialTheme.colorScheme.error,
            width = 1f,
        )
    )
