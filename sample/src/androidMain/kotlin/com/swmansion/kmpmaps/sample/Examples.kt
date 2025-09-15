package com.swmansion.kmpmaps.sample

import com.swmansion.kmpmaps.Coordinates
import com.swmansion.kmpmaps.GoogleMapsCircle
import com.swmansion.kmpmaps.GoogleMapsMarker
import com.swmansion.kmpmaps.GoogleMapsPolygon
import com.swmansion.kmpmaps.GoogleMapsPolyline


public val exampleMarkers: List<GoogleMapsMarker> = listOf(
    GoogleMapsMarker(
        coordinates = Coordinates(latitude = 50.0486, longitude = 19.9654),
        title = "Software Mansion",
        subtitle = "React Native Company",
    ),
    GoogleMapsMarker(
        coordinates = Coordinates(latitude = 50.0619, longitude = 19.9373),
        title = "Kraków Center",
        subtitle = "Main Square",
    ),
    GoogleMapsMarker(
        coordinates = Coordinates(latitude = 50.0755, longitude = 19.9442),
        title = "Wawel Castle",
        subtitle = "Historic Castle",
    ),
    GoogleMapsMarker(
        coordinates = Coordinates(latitude = 50.0647, longitude = 19.9450),
        title = "Kazimierz",
        subtitle = "Jewish Quarter",
    ),
)

public val exampleCircles: List<GoogleMapsCircle> = listOf(
    GoogleMapsCircle(
        center = Coordinates(latitude = 50.0486, longitude = 19.9654),
        radius = 500.0,
        strokeColor = "#FF0000",
        strokeWidth = 3f,
        fillColor = "#80FF0000",
    ),
    GoogleMapsCircle(
        center = Coordinates(latitude = 50.0619, longitude = 19.9373),
        radius = 300.0,
        strokeColor = "#00FF00",
        strokeWidth = 2f,
        fillColor = "#8000FF00",
    ),
    GoogleMapsCircle(
        center = Coordinates(latitude = 50.0755, longitude = 19.9442),
        radius = 200.0,
        strokeColor = "#0000FF",
        strokeWidth = 4f,
        fillColor = "#800000FF",
    ),
)

public val examplePolygons: List<GoogleMapsPolygon> = listOf(
    GoogleMapsPolygon(
        coordinates = listOf(
            Coordinates(latitude = 50.0600, longitude = 19.9300),
            Coordinates(latitude = 50.0650, longitude = 19.9300),
            Coordinates(latitude = 50.0650, longitude = 19.9400),
            Coordinates(latitude = 50.0600, longitude = 19.9400),
        ),
        strokeColor = "#FF00FF",
        strokeWidth = 3f,
        fillColor = "#80FF00FF",
    ),
    GoogleMapsPolygon(
        coordinates = listOf(
            Coordinates(latitude = 50.0700, longitude = 19.9500),
            Coordinates(latitude = 50.0750, longitude = 19.9450),
            Coordinates(latitude = 50.0800, longitude = 19.9550),
            Coordinates(latitude = 50.0750, longitude = 19.9600),
        ),
        strokeColor = "#FFFF00",
        strokeWidth = 2f,
        fillColor = "#80FFFF00",
    ),
)

public val examplePolylines: List<GoogleMapsPolyline> = listOf(
    GoogleMapsPolyline(
        coordinates = listOf(
            Coordinates(latitude = 50.0486, longitude = 19.9654),
            Coordinates(latitude = 50.0619, longitude = 19.9373),
            Coordinates(latitude = 50.0755, longitude = 19.9442),
        ),
        strokeColor = "#FF8000",
        strokeWidth = 5f,
    ),
    GoogleMapsPolyline(
        coordinates = listOf(
            Coordinates(latitude = 50.0647, longitude = 19.9450),
            Coordinates(latitude = 50.0700, longitude = 19.9500),
            Coordinates(latitude = 50.0750, longitude = 19.9450),
            Coordinates(latitude = 50.0800, longitude = 19.9550),
        ),
        strokeColor = "#8000FF",
        strokeWidth = 3f,
    ),
)
