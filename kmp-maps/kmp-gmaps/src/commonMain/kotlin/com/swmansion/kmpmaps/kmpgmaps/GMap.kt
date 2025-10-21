package com.swmansion.kmpmaps.kmpgmaps

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.swmansion.kmpmaps.kmpmaps.CameraPosition
import com.swmansion.kmpmaps.kmpmaps.Circle
import com.swmansion.kmpmaps.kmpmaps.Coordinates
import com.swmansion.kmpmaps.kmpmaps.MapProperties
import com.swmansion.kmpmaps.kmpmaps.MapUISettings
import com.swmansion.kmpmaps.kmpmaps.Marker
import com.swmansion.kmpmaps.kmpmaps.Polygon
import com.swmansion.kmpmaps.kmpmaps.Polyline

/**
 * A cross-platform map component that provides access to native map APIs on Android and iOS.
 *
 * This composable renders Google Maps on Android and Apple Maps on iOS, providing a unified
 * interface for map functionality across both platforms.
 *
 * @param modifier The modifier to be applied to the map component
 * @param cameraPosition The initial camera position of the map. If null, the map will use default
 *   position
 * @param properties Configuration properties for the map behavior and appearance
 * @param uiSettings UI settings that control interactive elements and gestures
 * @param markers List of markers to display on the map
 * @param circles List of circles to display on the map
 * @param polygons List of polygons to display on the map
 * @param polylines List of polylines to display on the map
 * @param onCameraMove Callback invoked when the map camera position changes due to user interaction
 * @param onMarkerClick Callback invoked when a marker is clicked
 * @param onCircleClick Callback invoked when a circle is clicked
 * @param onPolygonClick Callback invoked when a polygon is clicked
 * @param onPolylineClick Callback invoked when a polyline is clicked
 * @param onMapClick Callback invoked when the user clicks on the map (not on POI or markers)
 * @param onMapLongClick Callback invoked when the user long-clicks on the map
 * @param onPOIClick Callback invoked when the user clicks on a Point of Interest
 * @param onMapLoaded Callback invoked when the map has finished loading
 */
@Composable
public expect fun GMap(
    modifier: Modifier = Modifier,
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
)
