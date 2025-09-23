package com.swmansion.kmpmaps.sample

import com.swmansion.kmpmaps.*


val exampleMarkers = listOf(
    MapMarker(
        coordinates = Coordinates(latitude = 50.0486, longitude = 19.9654),
        title = "Software Mansion",
        subtitle = "React Native Company",
    ),
    MapMarker(
        coordinates = Coordinates(latitude = 50.0647, longitude = 19.9450),
        title = "Wawel",
        subtitle = "Zamek Królewski"
    ),
    MapMarker(
        coordinates = Coordinates(latitude = 50.0596, longitude = 19.9316),
        title = "Kazimierz",
        subtitle = "Dzielnica żydowska"
    )
)

val exampleCircles = listOf(
    MapCircle(
        center = Coordinates(latitude = 50.0486, longitude = 19.9654),
        radius = 500.0f,
        lineColor = "#FF0000",
        lineWidth = 3f,
        color = "#80FF0000",
    ),
    MapCircle(
        center = Coordinates(latitude = 50.0647, longitude = 19.9450),
        radius = 300.0f,
        lineColor = "#00FF00",
        lineWidth = 2f,
        color = "#8000FF11",
    )
)

val examplePolygons = listOf(
    MapPolygon(
        coordinates = listOf(
            Coordinates(latitude = 50.0619, longitude = 19.9373),
            Coordinates(latitude = 50.0629, longitude = 19.9383),
            Coordinates(latitude = 50.0609, longitude = 19.9393),
            Coordinates(latitude = 50.0619, longitude = 19.9373)
        ),
        lineColor = "#00FF00",
        lineWidth = 2f,
        color = "#00FF0020"
    ),
    MapPolygon(
        coordinates = listOf(
            Coordinates(latitude = 50.0647, longitude = 19.9450),
            Coordinates(latitude = 50.0657, longitude = 19.9460),
            Coordinates(latitude = 50.0637, longitude = 19.9470),
            Coordinates(latitude = 50.0647, longitude = 19.9450)
        ),
        lineColor = "#FF00FF",
        lineWidth = 2f,
        color = "#FF00FF20"
    )
)

val examplePolylines = listOf(
    MapPolyline(
        coordinates = listOf(
            Coordinates(latitude = 50.0619, longitude = 19.9373),
            Coordinates(latitude = 50.0647, longitude = 19.9450),
            Coordinates(latitude = 50.0596, longitude = 19.9316)
        ),
        lineColor = "#FFA500",
        width = 4f
    ),
    MapPolyline(
        coordinates = listOf(
            Coordinates(latitude = 50.0600, longitude = 19.9300),
            Coordinates(latitude = 50.0660, longitude = 19.9500)
        ),
        lineColor = "#800080",
        width = 3f
    )
)
