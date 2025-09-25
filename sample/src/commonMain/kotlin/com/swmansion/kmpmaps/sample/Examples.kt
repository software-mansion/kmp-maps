package com.swmansion.kmpmaps.sample

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
            lineColor = Color(hexColor = "#FF0000", appleColor = AppleColors.DARK_GRAY),
            lineWidth = 1f,
            color = Color(hexColor = "#10FF0000", appleColor = AppleColors.LIGHT_GRAY),
        ),
        MapCircle(
            center = Coordinates(latitude = 50.0647, longitude = 19.9450),
            radius = 300.0f,
            lineColor = Color(hexColor = "#00FF00", appleColor = AppleColors.BROWN),
            lineWidth = 1f,
            color = Color(hexColor = "#3000FF00", appleColor = AppleColors.PURPLE),
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
            lineColor = Color(hexColor = "#10FF0000"),
            lineWidth = 1f,
            color = Color(hexColor = "#10FF0000"),
        )
    )

val examplePolylines =
    listOf(
        MapPolyline(
            coordinates = listOf(softwareMansionPin, royalCastlePin, jewishQuarterPin),
            lineColor = Color(hexColor = "#60FF0000"),
            width = 1f,
        )
    )
