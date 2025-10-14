package com.swmansion.kmpmaps.sample

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.swmansion.kmpmaps.*

val softwareMansionPin = Coordinates(latitude = 50.0486, longitude = 19.9654)
val cracowMainStationPin = Coordinates(latitude = 50.06839615782847, longitude = 19.947491884231567)
val jewishQuarterPin = Coordinates(latitude = 50.0515, longitude = 19.9449)

val exampleMarkers =
    listOf(
        Marker(
            coordinates = softwareMansionPin,
            title = "Software Mansion",
            androidSnippet = "Software house",
        ),
        Marker(
            coordinates = cracowMainStationPin,
            title = "Kraków Main",
            androidSnippet = "Transit station",
        ),
        Marker(
            coordinates = jewishQuarterPin,
            title = "Kazimierz",
            androidSnippet = "Jewish quarter",
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

val jsonMapStyle =
    "[\n" +
        "  {\n" +
        "    \"featureType\": \"all\",\n" +
        "    \"elementType\": \"geometry\",\n" +
        "    \"stylers\": [\n" +
        "      {\n" +
        "        \"color\": \"#242f3e\"\n" +
        "      }\n" +
        "    ]\n" +
        "  },\n" +
        "  {\n" +
        "    \"featureType\": \"all\",\n" +
        "    \"elementType\": \"labels.text.stroke\",\n" +
        "    \"stylers\": [\n" +
        "      {\n" +
        "        \"lightness\": -80\n" +
        "      }\n" +
        "    ]\n" +
        "  },\n" +
        "  {\n" +
        "    \"featureType\": \"administrative\",\n" +
        "    \"elementType\": \"labels.text.fill\",\n" +
        "    \"stylers\": [\n" +
        "      {\n" +
        "        \"color\": \"#746855\"\n" +
        "      }\n" +
        "    ]\n" +
        "  },\n" +
        "  {\n" +
        "    \"featureType\": \"administrative.locality\",\n" +
        "    \"elementType\": \"labels.text.fill\",\n" +
        "    \"stylers\": [\n" +
        "      {\n" +
        "        \"color\": \"#d59563\"\n" +
        "      }\n" +
        "    ]\n" +
        "  },\n" +
        "  {\n" +
        "    \"featureType\": \"poi\",\n" +
        "    \"elementType\": \"labels.text.fill\",\n" +
        "    \"stylers\": [\n" +
        "      {\n" +
        "        \"color\": \"#d59563\"\n" +
        "      }\n" +
        "    ]\n" +
        "  },\n" +
        "  {\n" +
        "    \"featureType\": \"poi.park\",\n" +
        "    \"elementType\": \"geometry\",\n" +
        "    \"stylers\": [\n" +
        "      {\n" +
        "        \"color\": \"#263c3f\"\n" +
        "      }\n" +
        "    ]\n" +
        "  },\n" +
        "  {\n" +
        "    \"featureType\": \"poi.park\",\n" +
        "    \"elementType\": \"labels.text.fill\",\n" +
        "    \"stylers\": [\n" +
        "      {\n" +
        "        \"color\": \"#6b9a76\"\n" +
        "      }\n" +
        "    ]\n" +
        "  },\n" +
        "  {\n" +
        "    \"featureType\": \"road\",\n" +
        "    \"elementType\": \"geometry.fill\",\n" +
        "    \"stylers\": [\n" +
        "      {\n" +
        "        \"color\": \"#2b3544\"\n" +
        "      }\n" +
        "    ]\n" +
        "  },\n" +
        "  {\n" +
        "    \"featureType\": \"road\",\n" +
        "    \"elementType\": \"labels.text.fill\",\n" +
        "    \"stylers\": [\n" +
        "      {\n" +
        "        \"color\": \"#9ca5b3\"\n" +
        "      }\n" +
        "    ]\n" +
        "  },\n" +
        "  {\n" +
        "    \"featureType\": \"road.arterial\",\n" +
        "    \"elementType\": \"geometry.fill\",\n" +
        "    \"stylers\": [\n" +
        "      {\n" +
        "        \"color\": \"#38414e\"\n" +
        "      }\n" +
        "    ]\n" +
        "  },\n" +
        "  {\n" +
        "    \"featureType\": \"road.arterial\",\n" +
        "    \"elementType\": \"geometry.stroke\",\n" +
        "    \"stylers\": [\n" +
        "      {\n" +
        "        \"color\": \"#212a37\"\n" +
        "      }\n" +
        "    ]\n" +
        "  },\n" +
        "  {\n" +
        "    \"featureType\": \"road.highway\",\n" +
        "    \"elementType\": \"geometry.fill\",\n" +
        "    \"stylers\": [\n" +
        "      {\n" +
        "        \"color\": \"#746855\"\n" +
        "      }\n" +
        "    ]\n" +
        "  },\n" +
        "  {\n" +
        "    \"featureType\": \"road.highway\",\n" +
        "    \"elementType\": \"geometry.stroke\",\n" +
        "    \"stylers\": [\n" +
        "      {\n" +
        "        \"color\": \"#1f2835\"\n" +
        "      }\n" +
        "    ]\n" +
        "  },\n" +
        "  {\n" +
        "    \"featureType\": \"road.highway\",\n" +
        "    \"elementType\": \"labels.text.fill\",\n" +
        "    \"stylers\": [\n" +
        "      {\n" +
        "        \"color\": \"#f3d19c\"\n" +
        "      }\n" +
        "    ]\n" +
        "  },\n" +
        "  {\n" +
        "    \"featureType\": \"road.local\",\n" +
        "    \"elementType\": \"geometry.fill\",\n" +
        "    \"stylers\": [\n" +
        "      {\n" +
        "        \"color\": \"#38414e\"\n" +
        "      }\n" +
        "    ]\n" +
        "  },\n" +
        "  {\n" +
        "    \"featureType\": \"road.local\",\n" +
        "    \"elementType\": \"geometry.stroke\",\n" +
        "    \"stylers\": [\n" +
        "      {\n" +
        "        \"color\": \"#212a37\"\n" +
        "      }\n" +
        "    ]\n" +
        "  },\n" +
        "  {\n" +
        "    \"featureType\": \"transit\",\n" +
        "    \"elementType\": \"geometry\",\n" +
        "    \"stylers\": [\n" +
        "      {\n" +
        "        \"color\": \"#2f3948\"\n" +
        "      }\n" +
        "    ]\n" +
        "  },\n" +
        "  {\n" +
        "    \"featureType\": \"transit.station\",\n" +
        "    \"elementType\": \"labels.text.fill\",\n" +
        "    \"stylers\": [\n" +
        "      {\n" +
        "        \"color\": \"#d59563\"\n" +
        "      }\n" +
        "    ]\n" +
        "  },\n" +
        "  {\n" +
        "    \"featureType\": \"water\",\n" +
        "    \"elementType\": \"geometry\",\n" +
        "    \"stylers\": [\n" +
        "      {\n" +
        "        \"color\": \"#17263c\"\n" +
        "      }\n" +
        "    ]\n" +
        "  },\n" +
        "  {\n" +
        "    \"featureType\": \"water\",\n" +
        "    \"elementType\": \"labels.text.fill\",\n" +
        "    \"stylers\": [\n" +
        "      {\n" +
        "        \"color\": \"#515c6d\"\n" +
        "      }\n" +
        "    ]\n" +
        "  },\n" +
        "  {\n" +
        "    \"featureType\": \"water\",\n" +
        "    \"elementType\": \"labels.text.stroke\",\n" +
        "    \"stylers\": [\n" +
        "      {\n" +
        "        \"lightness\": -20\n" +
        "      }\n" +
        "    ]\n" +
        "  }\n" +
        "]"
