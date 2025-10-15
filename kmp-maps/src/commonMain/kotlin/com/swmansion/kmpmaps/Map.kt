package com.swmansion.kmpmaps

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * A cross-platform map component that provides access to native map APIs on Android and iOS.
 *
 * This composable renders Google Maps on Android and Apple Maps on iOS, providing a unified
 * interface for map functionality across both platforms.
 *
 * ## Platform Support
 * - **Android**: Uses Google Maps SDK
 * - **iOS**: Uses Apple Maps (MapKit)
 *
 * ## Basic Usage
 *
 * ```kotlin
 * @Composable
 * fun MyMapScreen() {
 *     Map(
 *         modifier = Modifier.fillMaxSize(),
 *         properties = MapProperties(
 *             isMyLocationEnabled = true,
 *             mapType = MapType.NORMAL,
 *         ),
 *         uiSettings = MapUISettings(
 *             myLocationButtonEnabled = true,
 *             compassEnabled = true
 *         ),
 *         cameraPosition = CameraPosition(
 *             coordinates = Coordinates(latitude = 50.0619, longitude = 19.9373),
 *             zoom = 13f
 *         ),
 *         markers = listOf(
 *             Marker(
 *                 coordinates = Coordinates(latitude = 50.0486, longitude = 19.9654),
 *                 title = "Software Mansion",
 *                 androidSnippet = "Software house"
 *             )
 *         )
 *     )
 * }
 * ```
 *
 * ## Configuration
 *
 * ### Android - Google Maps API Key
 *
 * To use Google Maps on Android, you need to configure your API key in `AndroidManifest.xml`:
 * ```xml
 * <meta-data
 *     android:name="com.google.android.geo.API_KEY"
 *     android:value="YOUR_API_KEY" />
 * ```
 *
 * ### iOS - Apple Maps
 *
 * No additional configuration is required for Apple Maps on iOS.
 *
 * ## Permissions
 *
 * To display the user's location on the map, you need to declare and request location permissions:
 *
 * ### Android
 * Add the following permissions to your `AndroidManifest.xml`:
 * ```xml
 * <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
 * <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
 * ```
 *
 * ### iOS
 * Add the following key to your `Info.plist`:
 * ```xml
 * <key>NSLocationWhenInUseUsageDescription</key>
 * <string>Allow this app to use your location</string>
 * ```
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
public expect fun Map(
    modifier: Modifier = Modifier,
    mapPlatform: MapPlatform = MapPlatform.NATIVE,
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
