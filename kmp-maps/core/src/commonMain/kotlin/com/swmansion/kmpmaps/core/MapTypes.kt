package com.swmansion.kmpmaps.core

import androidx.compose.ui.graphics.Color

/**
 * Theme options for map appearance.
 *
 * @param SYSTEM Follows the system's dark theme setting
 * @param LIGHT Always uses light theme
 * @param DARK Always uses dark theme
 */
public enum class MapTheme {
    SYSTEM,
    LIGHT,
    DARK,
}

/**
 * Configuration properties for map behavior and appearance.
 *
 * @param isMyLocationEnabled Whether to show the user's current location on the map
 * @param isTrafficEnabled Whether to display traffic information on the map
 * @param isBuildingEnabled Whether to show 3D buildings on the map
 * @param mapType The type of map to display
 * @param mapTheme The theme for the map appearance
 * @param androidMapProperties Android-specific map behavior and appearance configuration options
 * @param iosMapProperties iOS-specific map behavior and appearance configuration options.
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
 * @param compassEnabled Whether to show the compass control
 * @param myLocationButtonEnabled Whether to show the "My Location" button
 * @param scaleBarEnabled Whether to show the scale bar
 * @param togglePitchEnabled Whether pitch gestures are enabled
 * @param scrollEnabled Whether scroll gestures are enabled
 * @param zoomEnabled Whether zoom gestures are enabled
 * @param rotateEnabled Whether rotation gestures are enabled
 * @param androidUISettings Android-specific settings
 * @param iosUISettings iOS-specific settings
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
 * @param coordinates The geographical coordinates where the marker should be placed
 * @param title The title text displayed when the marker is tapped
 * @param androidMarkerOptions Android-specific options for customizing a marker
 * @param iosMarkerOptions iOS-specific options for customizing a marker
 */
public data class Marker(
    val coordinates: Coordinates,
    val title: String? = "No title was provided",
    val androidMarkerOptions: AndroidMarkerOptions,
    val iosMarkerOptions: IosMarkerOptions? = null,
)

/**
 * Represents a circle overlay on the map.
 *
 * @param center The center coordinates of the circle
 * @param radius The radius of the circle in meters
 * @param color The fill color of the circle
 * @param lineColor The color of the circle's border
 * @param lineWidth The width of the circle's border
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
 * @param coordinates List of coordinates that define the polygon's vertices
 * @param lineWidth The width of the polygon's border
 * @param color The fill color of the polygon
 * @param lineColor The color of the polygon's border
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
 * @param coordinates List of coordinates that define the polyline's path
 * @param width The width of the polyline
 * @param lineColor The color of the polyline
 */
public data class Polyline(
    val coordinates: List<Coordinates>,
    val width: Float,
    val lineColor: Color? = null,
)

/**
 * Represents geographical coordinates (latitude and longitude).
 *
 * @param latitude The latitude coordinate in decimal degrees (-90 to 90)
 * @param longitude The longitude coordinate in decimal degrees (-180 to 180)
 */
public data class Coordinates(val latitude: Double, val longitude: Double)

/**
 * Represents the camera position and orientation of the map.
 *
 * @param coordinates The center coordinates of the camera view
 * @param zoom The zoom level of the map (typically 0-20)
 * @param androidCameraPosition Android-specific options for the camera position and orientation
 * @param iosCameraPosition iOS-specific options for the camera position and orientation
 */
public data class CameraPosition(
    val coordinates: Coordinates,
    val zoom: Float,
    val androidCameraPosition: AndroidCameraPosition? = null,
    val iosCameraPosition: IosCameraPosition? = null,
)
