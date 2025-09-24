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
            lineColor = Color(hexColor = "#FF0000", appleUIColor = AppleColors.DARK_GRAY),
            lineWidth = 3f,
            color = Color(hexColor = "#80FF0000", appleUIColor = AppleColors.LIGHT_GRAY),
        ),
        MapCircle(
            center = Coordinates(latitude = 50.0647, longitude = 19.9450),
            radius = 300.0f,
            lineColor = Color(hexColor = "#00FF00", appleUIColor = AppleColors.BROWN),
            lineWidth = 2f,
            color = Color(hexColor = "#8000FF11", appleUIColor = AppleColors.PURPLE),
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
            lineColor = Color(hexColor = "#FF00FF"),
            lineWidth = 3f,
            color = Color(hexColor = "#80FF00FF"),
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
            lineColor = Color(hexColor = "#FFA500"),
            width = 10f,
        )
    )
