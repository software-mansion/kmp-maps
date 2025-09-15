package com.swmansion.kmpmaps.sample

import com.swmansion.kmpmaps.AppleMapsAnnotations
import com.swmansion.kmpmaps.AppleMapsCircle
import com.swmansion.kmpmaps.AppleMapsMarker
import com.swmansion.kmpmaps.AppleMapsPolygon
import com.swmansion.kmpmaps.AppleMapsPolyline
import com.swmansion.kmpmaps.Coordinates

public val exampleAppleMapsMarkers: List<AppleMapsMarker> = listOf(
    AppleMapsMarker(
        coordinates = Coordinates(latitude = 50.0486, longitude = 19.9654),
        title = "Software Mansion",
        systemImage = "building.2",
        tintColor = "#FF0000",
    ),
    AppleMapsMarker(
        coordinates = Coordinates(latitude = 50.0619, longitude = 19.9373),
        title = "Kraków Center",
        systemImage = "mappin.circle",
        tintColor = "#00FF00",
    ),
    AppleMapsMarker(
        coordinates = Coordinates(latitude = 50.0755, longitude = 19.9442),
        title = "Wawel Castle",
        systemImage = "castle",
        tintColor = "#0000FF",
    ),
    AppleMapsMarker(
        coordinates = Coordinates(latitude = 50.0647, longitude = 19.9450),
        title = "Kazimierz",
        systemImage = "house",
        tintColor = "#FF8000",
    ),
)
public val exampleAppleMapsAnnotations: List<AppleMapsAnnotations> = listOf(
    AppleMapsAnnotations(
        coordinates = Coordinates(latitude = 50.0486, longitude = 19.9654),
        backgroundColor = "#FF0000",
        text = "SM",
        textColor = "#FFFFFF",
    ),
    AppleMapsAnnotations(
        coordinates = Coordinates(latitude = 50.0619, longitude = 19.9373),
        backgroundColor = "#00FF00",
        text = "KC",
        textColor = "#000000",
    ),
)
public val exampleAppleMapsCircles: List<AppleMapsCircle> = listOf(
    AppleMapsCircle(
        center = Coordinates(latitude = 50.0486, longitude = 19.9654),
        radius = 500.0,
        lineColor = "#FF0000",
        lineWidth = 3f,
        color = "#80FF0000",
    ),
    AppleMapsCircle(
        center = Coordinates(latitude = 50.0619, longitude = 19.9373),
        radius = 300.0,
        lineColor = "#00FF00",
        lineWidth = 2f,
        color = "#8000FF00",
    ),
    AppleMapsCircle(
        center = Coordinates(latitude = 50.0755, longitude = 19.9442),
        radius = 200.0,
        lineColor = "#0000FF",
        lineWidth = 4f,
        color = "#800000FF",
    ),
)
public val exampleAppleMapsPolygons: List<AppleMapsPolygon> = listOf(
    AppleMapsPolygon(
        coordinates = listOf(
            Coordinates(latitude = 50.0600, longitude = 19.9300),
            Coordinates(latitude = 50.0650, longitude = 19.9300),
            Coordinates(latitude = 50.0650, longitude = 19.9400),
            Coordinates(latitude = 50.0600, longitude = 19.9400),
        ),
        lineColor = "#FF00FF",
        lineWidth = 3f,
        color = "#80FF00FF",
    ),
    AppleMapsPolygon(
        coordinates = listOf(
            Coordinates(latitude = 50.0700, longitude = 19.9500),
            Coordinates(latitude = 50.0750, longitude = 19.9450),
            Coordinates(latitude = 50.0800, longitude = 19.9550),
            Coordinates(latitude = 50.0750, longitude = 19.9600),
        ),
        lineColor = "#FFFF00",
        lineWidth = 2f,
        color = "#80FFFF00",
    ),
)
public val exampleAppleMapsPolylines: List<AppleMapsPolyline> = listOf(
    AppleMapsPolyline(
        coordinates = listOf(
            Coordinates(latitude = 50.0486, longitude = 19.9654),
            Coordinates(latitude = 50.0619, longitude = 19.9373),
            Coordinates(latitude = 50.0755, longitude = 19.9442),
        ),
        width = 5f,
    ),
    AppleMapsPolyline(
        coordinates = listOf(
            Coordinates(latitude = 50.0647, longitude = 19.9450),
            Coordinates(latitude = 50.0700, longitude = 19.9500),
            Coordinates(latitude = 50.0750, longitude = 19.9450),
            Coordinates(latitude = 50.0800, longitude = 19.9550),
        ),
        width = 3f,
    ),
)
