package com.swmansion.kmpmaps.sample

import androidx.compose.ui.graphics.Color
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

val exampleCircles =
    listOf(
        MapCircle(
            center = Coordinates(latitude = 50.0486, longitude = 19.9654),
            radius = 500.0f,
            lineColor = Color.Red,
            lineWidth = 1f,
            color = Color(0x10FF0000),
        ),
        MapCircle(
            center = Coordinates(latitude = 50.0647, longitude = 19.9450),
            radius = 300.0f,
            lineColor = Color.Green,
            lineWidth = 1f,
            color = Color(0x3000FF00),
        ),
    )

val examplePolygons =
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
            lineColor = Color(0x10FF0000),
            lineWidth = 1f,
            color = Color(0x10FF0000),
        )
    )

val examplePolylines =
    listOf(
        MapPolyline(
            coordinates = listOf(softwareMansionPin, royalCastlePin, jewishQuarterPin),
            lineColor = Color(0x60FF0000),
            width = 1f,
        )
    )
