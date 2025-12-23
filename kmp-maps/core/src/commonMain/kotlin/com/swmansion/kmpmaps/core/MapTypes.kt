package com.swmansion.kmpmaps.core

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import kotlinx.serialization.Serializable

/**
 * Theme options for map appearance.
 *
 * @property SYSTEM Follows the system's dark theme setting
 * @property LIGHT Always uses light theme
 * @property DARK Always uses dark theme
 */
public enum class MapTheme {
    SYSTEM,
    LIGHT,
    DARK,
}

/**
 * Configuration properties for map behavior and appearance.
 *
 * @property isMyLocationEnabled Whether to show the user's current location on the map
 * @property isTrafficEnabled Whether to display traffic information on the map
 * @property isBuildingEnabled Whether to show 3D buildings on the map
 * @property mapType The type of map to display
 * @property mapTheme The theme for the map appearance
 * @property androidMapProperties Android-specific map behavior and appearance configuration options
 * @property iosMapProperties iOS-specific map behavior and appearance configuration options
 */
public data class MapProperties(
    val isMyLocationEnabled: Boolean = false,
    val isTrafficEnabled: Boolean = true,
    val isBuildingEnabled: Boolean = true,
    val mapType: MapType? = MapType.NORMAL,
    val mapTheme: MapTheme = MapTheme.SYSTEM,
    val androidMapProperties: AndroidMapProperties = AndroidMapProperties(),
    val iosMapProperties: IosMapProperties = IosMapProperties(),
)

/**
 * UI settings that control interactive elements and gestures on the map.
 *
 * @property compassEnabled Whether to show the compass control
 * @property myLocationButtonEnabled Whether to show the "My Location" button
 * @property scaleBarEnabled Whether to show the scale bar
 * @property togglePitchEnabled Whether pitch gestures are enabled
 * @property scrollEnabled Whether scroll gestures are enabled
 * @property zoomEnabled Whether zoom gestures are enabled
 * @property rotateEnabled Whether rotation gestures are enabled
 * @property androidUISettings Android-specific UI settings
 * @property iosUISettings iOS-specific UI settings
 */
public data class MapUISettings(
    val compassEnabled: Boolean = false,
    val myLocationButtonEnabled: Boolean = false,
    val scaleBarEnabled: Boolean = true,
    val togglePitchEnabled: Boolean = true,
    val scrollEnabled: Boolean = true,
    val zoomEnabled: Boolean = true,
    val rotateEnabled: Boolean = true,
    val androidUISettings: AndroidUISettings = AndroidUISettings(),
    val iosUISettings: IosUISettings = IosUISettings(),
)

/**
 * Represents a marker on the map.
 *
 * @property coordinates The geographical coordinates where the marker should be placed
 * @property title The title text displayed when the marker is tapped
 * @property androidMarkerOptions Android-specific options for customizing a marker
 * @property iosMarkerOptions iOS-specific options for customizing a marker
 * @property contentId Optional identifier for custom Compose content. When provided, this ID is
 *   used to look up the corresponding Composable from the Map's `customMarkerContent` parameter. If
 *   null or not found, the marker uses the default native rendering
 */
public data class Marker(
    val coordinates: Coordinates,
    val title: String? = "No title was provided",
    val androidMarkerOptions: AndroidMarkerOptions = AndroidMarkerOptions(),
    val iosMarkerOptions: IosMarkerOptions? = null,
    val contentId: String? = null,
)

/**
 * Represents a circle overlay on the map.
 *
 * @property center The center coordinates of the circle
 * @property radius The radius of the circle in meters
 * @property color The fill color of the circle
 * @property lineColor The color of the circle's border
 * @property lineWidth The width of the circle's border
 */
public data class Circle(
    val center: Coordinates,
    val radius: Float,
    val color: Color? = null,
    val lineColor: Color? = null,
    val lineWidth: Float? = null,
)

/**
 * Represents a polygon overlay on the map.
 *
 * @property coordinates List of coordinates that define the polygon's vertices
 * @property lineWidth The width of the polygon's border
 * @property color The fill color of the polygon
 * @property lineColor The color of the polygon's border
 */
public data class Polygon(
    val coordinates: List<Coordinates>,
    val lineWidth: Float,
    val color: Color? = null,
    val lineColor: Color? = null,
)

/**
 * Represents a polyline overlay on the map.
 *
 * @property coordinates List of coordinates that define the polyline's path
 * @property width The width of the polyline
 * @property lineColor The color of the polyline
 */
public data class Polyline(
    val coordinates: List<Coordinates>,
    val width: Float,
    val lineColor: Color? = null,
)

/**
 * Represents geographical coordinates (latitude and longitude).
 *
 * @property latitude The latitude coordinate in decimal degrees (-90 to 90)
 * @property longitude The longitude coordinate in decimal degrees (-180 to 180)
 */
@Serializable public data class Coordinates(val latitude: Double, val longitude: Double)

/**
 * Represents the camera position and orientation of the map.
 *
 * @property coordinates The center coordinates of the camera view
 * @property zoom The zoom level of the map (typically 0-20)
 * @property androidCameraPosition Android-specific options for the camera position and orientation
 * @property iosCameraPosition iOS-specific options for the camera position and orientation
 */
public data class CameraPosition(
    val coordinates: Coordinates,
    val zoom: Float,
    val androidCameraPosition: AndroidCameraPosition? = null,
    val iosCameraPosition: IosCameraPosition? = null,
)

/**
 * Represents a group of markers that have been combined into a single cluster.
 *
 * @property coordinates The coordinates of the cluster
 * @property size The number of markers contained within this cluster
 * @property items The list of [Marker] that make up this cluster
 */
public data class Cluster(val coordinates: Coordinates, val size: Int, val items: List<Marker>)

/**
 * Configuration options for marker clustering.
 *
 * @property enabled Enables marker clustering
 * @property onClusterClick Callback invoked on cluster click. Return `true` to consume the event or
 *   `false` to allow the default platform behavior
 * @property clusterContent Optional Composable to render custom cluster UI
 */
public data class ClusterSettings(
    val enabled: Boolean = false,
    val onClusterClick: ((Cluster) -> Boolean)? = null,
    val clusterContent: (@Composable (Cluster) -> Unit)? = null,
)
