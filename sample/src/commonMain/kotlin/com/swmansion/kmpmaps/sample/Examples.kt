package com.swmansion.kmpmaps.sample

import com.swmansion.kmpmaps.*

val exampleMarkers =
    listOf(
        MapMarker(
            coordinates = Coordinates(latitude = 50.0486, longitude = 19.9654),
            title = "Software Mansion",
            androidSnippet = "React Native Company",
        ),
        MapMarker(
            coordinates = Coordinates(latitude = 50.0647, longitude = 19.9450),
            title = "Wawel",
            androidSnippet = "Zamek Królewski",
        ),
        MapMarker(
            coordinates = Coordinates(latitude = 50.0596, longitude = 19.9316),
            title = "Kazimierz",
            androidSnippet = "Dzielnica żydowska",
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
            color = Color(hexColor = "#5000FF00", appleColor = AppleColors.PURPLE),
        ),
    )

val examplePolygons =
    listOf(
        MapPolygon(
            coordinates =
                listOf(
                    Coordinates(latitude = 50.0600, longitude = 19.9300),
                    Coordinates(latitude = 50.0650, longitude = 19.9300),
                    Coordinates(latitude = 50.0650, longitude = 19.9400),
                    Coordinates(latitude = 50.0600, longitude = 19.9400),
                ),
            lineColor = Color(hexColor = "#10FF0000"),
            lineWidth = 1f,
            color = Color(hexColor = "#10FF0000"),
        )
    )

val examplePolylines =
    listOf(
        MapPolyline(
            coordinates =
                listOf(
                    Coordinates(latitude = 50.0486, longitude = 19.9654),
                    Coordinates(latitude = 50.0647, longitude = 19.9450),
                    Coordinates(latitude = 50.0596, longitude = 19.9316),
                ),
            lineColor = Color(hexColor = "#60FF0000"),
            width = 1f,
        )
    )
