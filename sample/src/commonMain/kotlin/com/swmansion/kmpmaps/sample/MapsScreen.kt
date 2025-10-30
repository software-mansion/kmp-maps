package com.swmansion.kmpmaps.sample

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.ListItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.swmansion.kmpmaps.core.AndroidMapProperties
import com.swmansion.kmpmaps.core.AndroidUISettings
import com.swmansion.kmpmaps.core.CameraPosition
import com.swmansion.kmpmaps.core.Circle
import com.swmansion.kmpmaps.core.Coordinates
import com.swmansion.kmpmaps.core.GeoJsonLayer
import com.swmansion.kmpmaps.core.LineJoin
import com.swmansion.kmpmaps.core.Map as CoreMap
import com.swmansion.kmpmaps.core.MapProperties
import com.swmansion.kmpmaps.core.MapTheme
import com.swmansion.kmpmaps.core.MapType
import com.swmansion.kmpmaps.core.MapUISettings
import com.swmansion.kmpmaps.core.Marker
import com.swmansion.kmpmaps.core.Polygon
import com.swmansion.kmpmaps.core.Polyline
import com.swmansion.kmpmaps.googlemaps.Map as GoogleMap

@Composable
internal fun MapsScreen() {
    var selectedMapType by remember { mutableStateOf(MapType.NORMAL) }
    var selectedMapTheme by remember { mutableStateOf(MapTheme.SYSTEM) }
    var showUserLocation by remember { mutableStateOf(false) }
    var currentCameraPosition by remember {
        mutableStateOf(
            CameraPosition(
                coordinates = Coordinates(latitude = 50.0619, longitude = 19.9373),
                zoom = 13f,
            )
        )
    }
    var showAllComponents by remember { mutableStateOf(true) }
    var useGoogleMapsMapView by remember { mutableStateOf(true) }
    var showGeoJson by remember { mutableStateOf(false) }
    val geoJsonLayer =
        remember(showGeoJson) {
            if (showGeoJson)
                GeoJsonLayer(
                    geoJson = OFFICE_GEO_JSON,
                    opacity = 0.1f,
                    pointColor = Color.Magenta,
                    lineColor = Color.Blue,
                    lineJoin = LineJoin.Bevel,
                    lineWidth = 10f,
                    fillColor = Color.Blue,
                )
            else null
        }

    Column(Modifier.fillMaxHeight(), Arrangement.Bottom) {
        Map(
            modifier = Modifier.weight(0.55f),
            mapProvider = if (useGoogleMapsMapView) MapProvider.GOOGLE_MAPS else MapProvider.NATIVE,
            cameraPosition = currentCameraPosition,
            properties =
                MapProperties(
                    mapType = selectedMapType,
                    mapTheme = selectedMapTheme,
                    isMyLocationEnabled = showUserLocation,
                    isTrafficEnabled = true,
                    isBuildingEnabled = true,
                    androidMapProperties =
                        AndroidMapProperties(
                            isIndoorEnabled = true,
                            minZoomPreference = 3f,
                            maxZoomPreference = 21f,
                        ),
                ),
            uiSettings =
                MapUISettings(
                    compassEnabled = true,
                    myLocationButtonEnabled = showUserLocation,
                    scaleBarEnabled = true,
                    androidUISettings = AndroidUISettings(zoomControlsEnabled = false),
                ),
            markers = if (showAllComponents) exampleMarkers else emptyList(),
            circles = if (showAllComponents) getExampleCircles() else emptyList(),
            polygons = if (showAllComponents) getExamplePolygons() else emptyList(),
            polylines = if (showAllComponents) getExamplePolylines() else emptyList(),
            onCameraMove = { position -> println("Camera moved: $position") },
            onCircleClick = { println("Circle clicked: ${it.center}") },
            onPolygonClick = { println("Polygon clicked: ${it.coordinates}") },
            onPolylineClick = { println("Polyline clicked: ${it.coordinates}") },
            onPOIClick = { println("POI clicked: $it") },
            onMapLoaded = { println("Map loaded") },
            onMapLongClick = { println("Map long clicked: $it") },
            onMarkerClick = { marker -> println("Marker clicked: ${marker.title}") },
            onMapClick = { coordinates -> println("Map clicked at: $coordinates") },
            geoJsonLayer = geoJsonLayer,
        )
        Surface(modifier = Modifier.weight(0.45f)) {
            Column(
                Modifier.fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.safeDrawing.exclude(WindowInsets.statusBars))
                    .padding(vertical = 16.dp),
                Arrangement.spacedBy(8.dp),
                Alignment.CenterHorizontally,
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        onClick = { selectedMapType = MapType.NORMAL },
                        label = { Text("Normal") },
                        selected = selectedMapType == MapType.NORMAL,
                    )
                    FilterChip(
                        onClick = { selectedMapType = MapType.SATELLITE },
                        label = { Text("Satellite") },
                        selected = selectedMapType == MapType.SATELLITE,
                    )
                    FilterChip(
                        onClick = { selectedMapType = MapType.HYBRID },
                        label = { Text("Hybrid") },
                        selected = selectedMapType == MapType.HYBRID,
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        onClick = { selectedMapTheme = MapTheme.SYSTEM },
                        label = { Text("System") },
                        selected = selectedMapTheme == MapTheme.SYSTEM,
                    )
                    FilterChip(
                        onClick = { selectedMapTheme = MapTheme.LIGHT },
                        label = { Text("Light") },
                        selected = selectedMapTheme == MapTheme.LIGHT,
                    )
                    FilterChip(
                        onClick = { selectedMapTheme = MapTheme.DARK },
                        label = { Text("Dark") },
                        selected = selectedMapTheme == MapTheme.DARK,
                    )
                }
                if (isIOS()) {
                    ListItem(
                        headlineContent = { Text("Use Google Maps") },
                        modifier =
                            Modifier.height(48.dp).clickable {
                                useGoogleMapsMapView = !useGoogleMapsMapView
                            },
                        trailingContent = {
                            Switch(
                                checked = useGoogleMapsMapView,
                                onCheckedChange = { useGoogleMapsMapView = it },
                            )
                        },
                    )
                }
                ListItem(
                    headlineContent = { Text("Show annotations") },
                    modifier =
                        Modifier.height(48.dp).clickable { showAllComponents = !showAllComponents },
                    trailingContent = {
                        Switch(
                            checked = showAllComponents,
                            onCheckedChange = { showAllComponents = it },
                        )
                    },
                )
                ListItem(
                    headlineContent = { Text("Show my location") },
                    modifier =
                        Modifier.height(48.dp).clickable { showUserLocation = !showUserLocation },
                    trailingContent = {
                        Switch(
                            checked = showUserLocation,
                            onCheckedChange = { showUserLocation = it },
                        )
                    },
                )
                ListItem(
                    headlineContent = { Text("Show GeoJSON Layer") },
                    modifier = Modifier.height(48.dp).clickable { showGeoJson = !showGeoJson },
                    trailingContent = {
                        Switch(checked = showGeoJson, onCheckedChange = { showGeoJson = it })
                    },
                )
            }
        }
    }
}

private enum class MapProvider {
    NATIVE,
    GOOGLE_MAPS,
}

@Composable
private fun Map(
    modifier: Modifier = Modifier,
    mapProvider: MapProvider,
    cameraPosition: CameraPosition? = null,
    properties: MapProperties = MapProperties(),
    uiSettings: MapUISettings = MapUISettings(),
    markers: List<Marker> = emptyList(),
    circles: List<Circle> = emptyList(),
    polygons: List<Polygon> = emptyList(),
    polylines: List<Polyline> = emptyList(),
    onCameraMove: ((CameraPosition) -> Unit)? = null,
    onMarkerClick: ((Marker) -> Unit)? = null,
    onCircleClick: ((Circle) -> Unit)? = null,
    onPolygonClick: ((Polygon) -> Unit)? = null,
    onPolylineClick: ((Polyline) -> Unit)? = null,
    onMapClick: ((Coordinates) -> Unit)? = null,
    onMapLongClick: ((Coordinates) -> Unit)? = null,
    onPOIClick: ((Coordinates) -> Unit)? = null,
    onMapLoaded: (() -> Unit)? = null,
    geoJsonLayer: GeoJsonLayer? = null,
) {
    when (mapProvider) {
        MapProvider.NATIVE ->
            CoreMap(
                modifier = modifier,
                cameraPosition = cameraPosition,
                properties = properties,
                uiSettings = uiSettings,
                markers = markers,
                circles = circles,
                polygons = polygons,
                polylines = polylines,
                onCameraMove = onCameraMove,
                onMarkerClick = onMarkerClick,
                onCircleClick = onCircleClick,
                onPolygonClick = onPolygonClick,
                onPolylineClick = onPolylineClick,
                onMapClick = onMapClick,
                onMapLongClick = onMapLongClick,
                onPOIClick = onPOIClick,
                onMapLoaded = onMapLoaded,
                geoJsonLayer = geoJsonLayer,
            )
        MapProvider.GOOGLE_MAPS ->
            GoogleMap(
                modifier = modifier,
                cameraPosition = cameraPosition,
                properties = properties,
                uiSettings = uiSettings,
                markers = markers,
                circles = circles,
                polygons = polygons,
                polylines = polylines,
                onCameraMove = onCameraMove,
                onMarkerClick = onMarkerClick,
                onCircleClick = onCircleClick,
                onPolygonClick = onPolygonClick,
                onPolylineClick = onPolylineClick,
                onMapClick = onMapClick,
                onMapLongClick = onMapLongClick,
                onPOIClick = onPOIClick,
                onMapLoaded = onMapLoaded,
                geoJsonLayer = geoJsonLayer,
            )
    }
}

private const val EXAMPLE_GEOJSON =
    """
  {
    "type": "Feature",
    "geometry": {
      "type": "LineString",
      "coordinates": [
        [19.9370, 50.0600],
        [19.9400, 50.0640],
        [19.9430, 50.0620]
      ]
    },
    "properties": {
      "name": "Example Line"
    }
  }
"""

private const val FOOD_GEO_JSON =
    """
        {
  "type": "FeatureCollection",
  "features": [
    {
      "type": "Feature",
      "properties": {},
      "geometry": {
        "coordinates": [
          19.95948931821505,
          50.05038942777688
        ],
        "type": "Point"
      }
    },
    {
      "type": "Feature",
      "properties": {},
      "geometry": {
        "coordinates": [
          19.960003698811732,
          50.04742820536811
        ],
        "type": "Point"
      }
    },
    {
      "type": "Feature",
      "properties": {},
      "geometry": {
        "coordinates": [
          19.96248330639625,
          50.047431323021385
        ],
        "type": "Point"
      }
    },
    {
      "type": "Feature",
      "properties": {},
      "geometry": {
        "coordinates": [
          19.962415157734426,
          50.04733706656535
        ],
        "type": "Point"
      }
    },
    {
      "type": "Feature",
      "properties": {},
      "geometry": {
        "coordinates": [
          19.96429710922257,
          50.04890237300748
        ],
        "type": "Point"
      }
    },
    {
      "type": "Feature",
      "properties": {},
      "geometry": {
        "coordinates": [
          19.959083406925373,
          50.04978124578909
        ],
        "type": "Point"
      }
    },
    {
      "type": "Feature",
      "properties": {},
      "geometry": {
        "coordinates": [
          19.96405060818745,
          50.04874731432909
        ],
        "type": "Point"
      }
    },
    {
      "type": "Feature",
      "properties": {},
      "geometry": {
        "coordinates": [
          19.966124483792726,
          50.04619662329256
        ],
        "type": "Point"
      }
    }
  ]
}
    """

private const val OFFICE_GEO_JSON =
    """
    {
  "type": "FeatureCollection",
  "features": [
    {
      "type": "Feature",
      "properties": {},
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
      "properties": {},
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
