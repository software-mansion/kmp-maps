package com.swmansion.kmpmaps.sample

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.swmansion.kmpmaps.core.AndroidMarkerOptions
import com.swmansion.kmpmaps.core.Circle
import com.swmansion.kmpmaps.core.Cluster
import com.swmansion.kmpmaps.core.Coordinates
import com.swmansion.kmpmaps.core.Marker
import com.swmansion.kmpmaps.core.Polygon
import com.swmansion.kmpmaps.core.Polyline
import kmp_maps.sample.generated.resources.Res
import kmp_maps.sample.generated.resources.swmansion_logo
import org.jetbrains.compose.resources.painterResource

val softwareMansionPin = Coordinates(latitude = 50.0486, longitude = 19.9654)
val cracowMainStationPin = Coordinates(latitude = 50.06839615782847, longitude = 19.947491884231567)
val jewishQuarterPin = Coordinates(latitude = 50.0515, longitude = 19.9449)

val exampleMarkers =
    listOf(
        Marker(
            coordinates = softwareMansionPin,
            title = "Software Mansion",
            androidMarkerOptions =
                AndroidMarkerOptions(snippet = "Software house", draggable = true),
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
            contentId = "web_shop_marker",
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

val customMarkerContent =
    mapOf<String, @Composable (Marker) -> Unit>(
        "swmansion_marker" to
            {
                Box(modifier = Modifier.height(40.dp).width(80.dp)) {
                    Image(
                        painter = painterResource(Res.drawable.swmansion_logo),
                        contentDescription = "Software Mansion logo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit,
                    )
                }
            },
        "colored_pin_marker" to
            { marker ->
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Pin",
                        tint = Color.Magenta,
                        modifier = Modifier.size(48.dp),
                    )
                    Surface(
                        color = Color.Black.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(top = 2.dp),
                    ) {
                        Text(
                            text = marker.title.orEmpty(),
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        )
                    }
                }
            },
    )

val customWebMarkerContent =
    mapOf<String, (Marker) -> String>(
        "web_shop_marker" to
            { _ ->
                val iconColor = "#FFFFFF"
                """
            <div style="
                display: flex;
                flex-direction: column;
                align-items: center;
            ">
                <div style="
                    background: #4285F4;
                    width: 36px;
                    height: 36px;
                    border-radius: 50%;
                    border: 2px solid white;
                    box-shadow: 0 2px 4px rgba(0,0,0,0.4);
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    font-size: 18px;
                ">
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="$iconColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <circle cx="9" cy="21" r="1"></circle>
                    <circle cx="20" cy="21" r="1"></circle>
                    <path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"></path>
            </svg>
                </div>
                
                <div style="
                    width: 0; 
                    height: 0; 
                    border-left: 6px solid transparent;
                    border-right: 6px solid transparent;
                    border-top: 6px solid white;
                    margin-top: -1px;
                "></div>
            </div>
            """
            },
        "colored_pin_marker" to
            { marker ->
                val title = marker.title.orEmpty()
                val magentaColor = "#FF00FF"
                """
            <div style="
                display: flex;
                flex-direction: column;
                align-items: center;
                font-family: 'Roboto', Arial, sans-serif;
            ">
                <svg width="48" height="48" viewBox="0 0 24 24" fill="$magentaColor">
                    <path d="M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7zm0 9.5c-1.38 0-2.5-1.12-2.5-2.5s1.12-2.5 2.5-2.5 2.5 1.12 2.5 2.5-1.12 2.5-2.5 2.5z"/>
                </svg>
            
                ${if (title.isNotEmpty()) """
                <div style="
                    background-color: rgba(0, 0, 0, 0.7);
                    color: white;
                    padding: 4px 8px;
                    border-radius: 8px;
                    font-size: 12px;
                    font-weight: 600;
                    margin-top: 2px;
                    white-space: nowrap;
                    box-shadow: 0 1px 3px rgba(0,0,0,0.3);
                ">
                    $title
                </div>
                """ else ""}
            </div>
            """
            },
    )

val webClusterContent = { cluster: Cluster ->
    """
    <div style="
        background-color: #4285F4;
        color: white;
        width: 32px;
        height: 32px;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        font-family: sans-serif;
        font-size: 14px;
        font-weight: bold;
        border: 2px solid white;
    ">
        ${cluster.size}
    </div>
    """
}

val customClusterContent =
    @Composable { cluster: Cluster ->
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(48.dp)) {
            Box(
                modifier =
                    Modifier.fillMaxSize()
                        .background(color = Color.Black.copy(alpha = 0.2f), shape = CircleShape)
            )
            Box(
                modifier =
                    Modifier.size(36.dp)
                        .shadow(4.dp, CircleShape)
                        .background(Color.Black, CircleShape)
                        .border(2.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = if (cluster.size > 99) "99+" else "${cluster.size}",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp,
                )
            }
        }
    }

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
        "title": "Random 1"
      },
      "geometry": {
        "coordinates": [
          19.912189144770718,
          50.06865411437797
        ],
        "type": "Point"
      },
      "id": 0
    },
    {
      "type": "Feature",
      "properties": {
        "title": "Random 2"
      },
      "geometry": {
        "coordinates": [
          19.912786665903937,
          50.06756184910992
        ],
        "type": "Point"
      },
      "id": 1
    },
    {
      "type": "Feature",
      "properties": {
        "title": "Random 3"
      },
      "geometry": {
        "coordinates": [
          19.916507529421125,
          50.07029941818287
        ],
        "type": "Point"
      },
      "id": 2
    },
    {
      "type": "Feature",
      "properties": {
        "title": "Random 4"
      },
      "geometry": {
        "coordinates": [
          19.9137020847733,
          50.07131293218115
        ],
        "type": "Point"
      },
      "id": 3
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
    },
    {
      "type": "Feature",
      "properties": {
        "name": "Multi-part complex",
        "stroke": "#FF0000",
        "stroke-width": 4,
        "fill-opacity": 0.3
      },
      "geometry": {
        "type": "MultiPolygon",
        "coordinates": [
          [
            [
              [19.9670, 50.0495],
              [19.9675, 50.0495],
              [19.9675, 50.0498],
              [19.9670, 50.0498],
              [19.9670, 50.0495]
            ]
          ],
          [
            [
              [19.9680, 50.0495],
              [19.9685, 50.0495],
              [19.9685, 50.0498],
              [19.9680, 50.0498],
              [19.9680, 50.0495]
            ]
          ]
        ]
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
    },
    {
      "type": "Feature",
      "properties": {
        "stroke-width": 5,
        "stroke": "#00FF00",
        "name": "Multi-segment bicycle path"
      },
      "geometry": {
        "type": "MultiLineString",
        "coordinates": [
          [
            [19.9660, 50.0498],
            [19.9665, 50.0498],
            [19.9665, 50.0495]
          ],
          [
            [19.9670, 50.0492],
            [19.9675, 50.0492],
            [19.9675, 50.0489]
          ]
        ]
      },
      "id": 1
    }
  ]
}
"""
