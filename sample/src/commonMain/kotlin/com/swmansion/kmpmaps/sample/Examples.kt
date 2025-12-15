package com.swmansion.kmpmaps.sample

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.swmansion.kmpmaps.core.AndroidMarkerOptions
import com.swmansion.kmpmaps.core.Circle
import com.swmansion.kmpmaps.core.Coordinates
import com.swmansion.kmpmaps.core.Marker
import com.swmansion.kmpmaps.core.Polygon
import com.swmansion.kmpmaps.core.Polyline

val softwareMansionPin = Coordinates(latitude = 50.0486, longitude = 19.9654)
val cracowMainStationPin = Coordinates(latitude = 50.06839615782847, longitude = 19.947491884231567)
val jewishQuarterPin = Coordinates(latitude = 50.0515, longitude = 19.9449)

val exampleMarkers =
    listOf(
        Marker(
            coordinates = softwareMansionPin,
            title = "Software Mansion",
            androidMarkerOptions = AndroidMarkerOptions(snippet = "Software house"),
            contentId = "swmansion_marker",
        ),
        Marker(
            coordinates = cracowMainStationPin,
            title = "Kraków Main",
            androidMarkerOptions = AndroidMarkerOptions(snippet = "Transit station"),
            contentId = "colored_pin_marker",
        ),
        Marker(
            coordinates = jewishQuarterPin,
            title = "Kazimierz",
            androidMarkerOptions = AndroidMarkerOptions(snippet = "Jewish quarter"),
            contentId = "colored_pin_marker",
        ),
        Marker(
            coordinates = Coordinates(50.0540, 19.9354),
            title = "Wawel Royal Castle",
            androidMarkerOptions = AndroidMarkerOptions(snippet = "Castle"),
            contentId = "colored_pin_marker",
        ),
    )

val clusterMarkers =
    listOf(
        Marker(
            coordinates = Coordinates(50.25306369740463, 19.01123497635126),
            title = "Katowice Point 1",
            androidMarkerOptions = AndroidMarkerOptions(snippet = "Time: 09:33:27"),
        ),
        Marker(
            coordinates = Coordinates(50.25596431517816, 19.009420461952686),
            title = "Katowice Point 2",
            androidMarkerOptions = AndroidMarkerOptions(snippet = "Time: 09:33:27"),
        ),
        Marker(
            coordinates = Coordinates(50.260024797656214, 19.00771927088499),
            title = "Katowice Point 3",
            androidMarkerOptions = AndroidMarkerOptions(snippet = "Time: 09:33:27"),
        ),
        Marker(
            coordinates = Coordinates(50.262678510942685, 19.010191597044468),
            title = "Katowice Point 4",
            androidMarkerOptions = AndroidMarkerOptions(snippet = "Time: 09:33:28"),
        ),
        Marker(
            coordinates = Coordinates(50.25654437467036, 19.02547985315323),
            title = "Katowice Point 5",
            androidMarkerOptions = AndroidMarkerOptions(snippet = "Time: 09:33:28"),
        ),
        Marker(
            coordinates = Coordinates(50.252976661136856, 19.025615975260735),
            title = "Katowice Point 6",
            androidMarkerOptions = AndroidMarkerOptions(snippet = "Time: 09:33:29"),
        ),
        Marker(
            coordinates = Coordinates(50.25090274993564, 19.01395708322525),
            title = "Katowice Point 7",
            androidMarkerOptions = AndroidMarkerOptions(snippet = "Time: 09:33:29"),
        ),
        Marker(
            coordinates = Coordinates(50.2528171656916, 19.008240960538387),
            title = "Katowice Point 8",
            androidMarkerOptions = AndroidMarkerOptions(snippet = "Time: 09:33:30"),
        ),
        Marker(
            coordinates = Coordinates(50.260938321008624, 19.006698690354824),
            title = "Katowice Point 9",
            androidMarkerOptions = AndroidMarkerOptions(snippet = "Time: 09:33:30"),
        ),
        Marker(
            coordinates = Coordinates(50.26391090161824, 19.00928433984518),
            title = "Katowice Point 10",
            androidMarkerOptions = AndroidMarkerOptions(snippet = "Time: 09:33:31"),
        ),
    ) + exampleMarkers

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

const val EXAMPLE_POINT_GEO_JSON =
    """
{
  "type": "FeatureCollection",
  "features": [
    {
      "type": "Feature",
      "properties": {
        "title": "Talerz"
      },
      "geometry": {
        "coordinates": [
          19.95948931821505,
          50.05038942777688
        ],
        "type": "Point"
      },
      "id": 0
    },
    {
      "type": "Feature",
      "properties": {
        "title": "Orzo"
      },
      "geometry": {
        "coordinates": [
          19.960003698811732,
          50.04742820536811
        ],
        "type": "Point"
      },
      "id": 1
    },
    {
      "type": "Feature",
      "properties": {
        "title": "Yoko"
      },
      "geometry": {
        "coordinates": [
          19.96248330639625,
          50.047431323021385
        ],
        "type": "Point"
      },
      "id": 2
    },
    {
      "type": "Feature",
      "properties": {
        "title": "Hindus"
      },
      "geometry": {
        "coordinates": [
          19.962415157734426,
          50.04733706656535
        ],
        "type": "Point"
      },
      "id": 3
    },
    {
      "type": "Feature",
      "properties": {
        "title": "Hankki"
      },
      "geometry": {
        "coordinates": [
          19.959083406925373,
          50.04978124578909
        ],
        "type": "Point"
      },
      "id": 4
    },
    {
      "type": "Feature",
      "properties": {
        "title": "Emalia"
      },
      "geometry": {
        "coordinates": [
          19.96405060818745,
          50.04874731432909
        ],
        "type": "Point"
      },
      "id": 5
    },
    {
      "type": "Feature",
      "properties": {
        "title": "Mateo",
        "description": "Bar"
      },
      "geometry": {
        "coordinates": [
          19.966124483792726,
          50.04619662329256
        ],
        "type": "Point"
      },
      "id": 6
    }
  ]
}
    """

const val EXAMPLE_POLYGON_GEO_JSON =
    """
{
  "type": "FeatureCollection",
  "features": [
    {
      "type": "Feature",
      "properties": {
        "name": "Office area",
        "stroke": "#0000FF",
        "stroke-width": 6,
        "fill": "#0000FF",
        "fill-opacity": 0.25
      },
      "geometry": {
        "coordinates": [
          [
            [
              19.96499693844865,
              50.048781853657715
            ],
            [
              19.964950203807007,
              50.048641806985216
            ],
            [
              19.96617049725603,
              50.04850509436312
            ],
            [
              19.966321086660287,
              50.04870182703783
            ],
            [
              19.965204647972712,
              50.04883853909956
            ],
            [
              19.965163106067905,
              50.04876518145625
            ],
            [
              19.96499693844865,
              50.048781853657715
            ]
          ]
        ],
        "type": "Polygon"
      }
    },
    {
      "type": "Feature",
      "properties": {
        "name": "Office area",
        "stroke": "#0000FF",
        "stroke-width": 6,
        "fill": "#0000FF",
        "fill-opacity": 0.25
      },
      "geometry": {
        "coordinates": [
          [
            [
              19.965568139637867,
              50.04929202022299
            ],
            [
              19.965521404995116,
              50.04903860482935
            ],
            [
              19.966419748683762,
              50.04893190321056
            ],
            [
              19.966471676064486,
              50.049115296471314
            ],
            [
              19.965568139637867,
              50.04929202022299
            ]
          ]
        ],
        "type": "Polygon"
      }
    }
  ]
}
"""

const val EXAMPLE_LINE_GEO_JSON =
    """
{
  "type": "FeatureCollection",
  "features": [
    {
      "type": "Feature",
      "properties": {
        "stroke-width": 10,
        "stroke": "#FF0000",
        "name": "Route to the office"
      },
      "geometry": {
        "coordinates": [
          [
            19.964725868477615,
            50.04984119906527
          ],
          [
            19.964562032014555,
            50.04962734365236
          ],
          [
            19.96552893573039,
            50.049382443541276
          ],
          [
            19.9653812143288,
            50.04890471226287
          ]
        ],
        "type": "LineString"
      },
      "id": 0
    }
  ]
}
"""
