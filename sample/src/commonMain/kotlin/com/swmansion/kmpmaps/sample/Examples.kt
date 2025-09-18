package com.swmansion.kmpmaps.sample

import com.swmansion.kmpmaps.*


val exampleMarkers = listOf(
    CommonMapMarker(
        coordinates = Coordinates(latitude = 50.0486, longitude = 19.9654),
        title = "Software Mansion",
        subtitle = "React Native Company",
    ),
    CommonMapMarker(
        coordinates = Coordinates(latitude = 50.0647, longitude = 19.9450),
        title = "Wawel",
        subtitle = "Zamek Królewski"
    ),
    CommonMapMarker(
        coordinates = Coordinates(latitude = 50.0596, longitude = 19.9316),
        title = "Kazimierz",
        subtitle = "Dzielnica żydowska"
    )
)

val exampleCircles = listOf(
    CommonMapCircle(
        center = Coordinates(latitude = 50.0486, longitude = 19.9654),
        radius = 500.0,
        strokeColor = "#FF0000",
        strokeWidth = 3f,
        fillColor = "#80FF0000",
    ),
    CommonMapCircle(
        center = Coordinates(latitude = 50.0647, longitude = 19.9450),
        radius = 300.0,
        strokeColor = "#00FF00",
        strokeWidth = 2f,
        fillColor = "#8000FF11",
    )
)

val examplePolygons = listOf(
    CommonMapPolygon(
        coordinates = listOf(
            Coordinates(latitude = 50.0619, longitude = 19.9373),
            Coordinates(latitude = 50.0629, longitude = 19.9383),
            Coordinates(latitude = 50.0609, longitude = 19.9393),
            Coordinates(latitude = 50.0619, longitude = 19.9373)
        ),
        strokeColor = "#00FF00",
        strokeWidth = 2f,
        fillColor = "#00FF0020"
    ),
    CommonMapPolygon(
        coordinates = listOf(
            Coordinates(latitude = 50.0647, longitude = 19.9450),
            Coordinates(latitude = 50.0657, longitude = 19.9460),
            Coordinates(latitude = 50.0637, longitude = 19.9470),
            Coordinates(latitude = 50.0647, longitude = 19.9450)
        ),
        strokeColor = "#FF00FF",
        strokeWidth = 2f,
        fillColor = "#FF00FF20"
    )
)

val examplePolylines = listOf(
    CommonMapPolyline(
        coordinates = listOf(
            Coordinates(latitude = 50.0619, longitude = 19.9373),
            Coordinates(latitude = 50.0647, longitude = 19.9450),
            Coordinates(latitude = 50.0596, longitude = 19.9316)
        ),
        strokeColor = "#FFA500",
        strokeWidth = 4f
    ),
    CommonMapPolyline(
        coordinates = listOf(
            Coordinates(latitude = 50.0600, longitude = 19.9300),
            Coordinates(latitude = 50.0660, longitude = 19.9500)
        ),
        strokeColor = "#800080",
        strokeWidth = 3f
    )
)
