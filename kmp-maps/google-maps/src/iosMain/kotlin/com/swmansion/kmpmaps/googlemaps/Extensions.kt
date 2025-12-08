package com.swmansion.kmpmaps.googlemaps

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import cocoapods.GoogleMaps.GMSCircle
import cocoapods.GoogleMaps.GMSMapView
import cocoapods.GoogleMaps.GMSMutablePath
import cocoapods.GoogleMaps.GMSPolygon
import cocoapods.GoogleMaps.GMSPolyline
import cocoapods.GoogleMaps.kGMSTypeHybrid
import cocoapods.GoogleMaps.kGMSTypeNormal
import cocoapods.GoogleMaps.kGMSTypeSatellite
import cocoapods.GoogleMaps.kGMSTypeTerrain
import cocoapods.Google_Maps_iOS_Utils.GMSCameraPosition
import cocoapods.Google_Maps_iOS_Utils.GMSMapStyle
import cocoapods.Google_Maps_iOS_Utils.GMSMapView as UtilsGMSMapView
import cocoapods.Google_Maps_iOS_Utils.GMSMarker
import cocoapods.Google_Maps_iOS_Utils.GMUGeoJSONParser
import cocoapods.Google_Maps_iOS_Utils.GMUGeometryRenderer
import com.swmansion.kmpmaps.core.CameraPosition
import com.swmansion.kmpmaps.core.Circle
import com.swmansion.kmpmaps.core.GoogleMapsMapStyleOptions
import com.swmansion.kmpmaps.core.MapType
import com.swmansion.kmpmaps.core.MapUISettings
import com.swmansion.kmpmaps.core.Marker
import com.swmansion.kmpmaps.core.Polygon
import com.swmansion.kmpmaps.core.Polyline
import com.swmansion.kmpmaps.core.toAppleMapsColor
import kotlin.collections.set
import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreLocation.CLLocationCoordinate2DMake
import platform.Foundation.NSDictionary
import platform.Foundation.NSNumber
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.dataUsingEncoding
import platform.UIKit.UIColor
import platform.UIKit.UIUserInterfaceStyle

/**
 * Updates Google Maps markers by removing existing markers and adding new ones.
 *
 * @param mapView Google Maps map view
 * @param markers List of MapMarker objects to display
 * @param markerMapping MutableMap mapping GMSMarker to MapMarker for click handling
 */
@OptIn(ExperimentalForeignApi::class)
internal fun updateGoogleMapsMarkers(
    mapView: UtilsGMSMapView,
    markers: List<Marker>,
    markerMapping: MutableMap<GMSMarker, Marker>,
    customMarkerContent: Map<String, @Composable () -> Unit>,
) {
    markerMapping.keys.forEach { marker -> marker.setMap(null) }
    markerMapping.clear()

    markers.forEach { marker ->
        val gmsMarker = GMSMarker()

        gmsMarker.setPosition(
            CLLocationCoordinate2DMake(
                latitude = marker.coordinates.latitude,
                longitude = marker.coordinates.longitude,
            )
        )

        customMarkerContent[marker.contentId]?.let { content ->
            val iconView = CustomMarkers(gmsMarker)
            iconView.setContent(content)
            gmsMarker.setIconView(iconView)
        }

        gmsMarker.setTitle( marker.title)
        gmsMarker.setMap(mapView)
        markerMapping[gmsMarker] = marker
    }
}

/**
 * Updates Google Maps circles by removing existing circles and adding new ones.
 *
 * @param mapView Google Maps map view
 * @param circles List of MapCircle objects to display
 * @param circleMapping MutableMap mapping GMSCircle to MapCircle for styling
 */
@OptIn(ExperimentalForeignApi::class)
internal fun updateGoogleMapsCircles(
    mapView: UtilsGMSMapView,
    circles: List<Circle>,
    circleMapping: MutableMap<GMSCircle, Circle>,
) {
    circleMapping.keys.forEach { circle -> circle.map = null }
    circleMapping.clear()

    circles.forEach { circle ->
        val gmsCircle = GMSCircle()
        gmsCircle.position =
            CLLocationCoordinate2DMake(circle.center.latitude, circle.center.longitude)
        gmsCircle.radius = circle.radius.toDouble()
        gmsCircle.fillColor = circle.color?.toAppleMapsColor()
        gmsCircle.strokeColor = circle.lineColor?.toAppleMapsColor()
        gmsCircle.strokeWidth = (circle.lineWidth ?: 1).toDouble()
        gmsCircle.map = mapView as GMSMapView
        gmsCircle.tappable = true
        circleMapping[gmsCircle] = circle
    }
}

/**
 * Updates Google Maps polygons by removing existing polygons and adding new ones.
 *
 * @param mapView Google Maps map view
 * @param polygons List of MapPolygon objects to display
 * @param polygonMapping MutableMap mapping GMSPolygon to MapPolygon for styling
 */
@OptIn(ExperimentalForeignApi::class)
internal fun updateGoogleMapsPolygons(
    mapView: UtilsGMSMapView,
    polygons: List<Polygon>,
    polygonMapping: MutableMap<GMSPolygon, Polygon>,
) {
    polygonMapping.keys.forEach { polygon -> polygon.map = null }
    polygonMapping.clear()

    polygons.forEach { polygon ->
        val gmsPolygon = GMSPolygon()
        val path = GMSMutablePath()
        polygon.coordinates.forEach { coord ->
            path.addCoordinate(CLLocationCoordinate2DMake(coord.latitude, coord.longitude))
        }
        gmsPolygon.path = path
        gmsPolygon.fillColor = polygon.color?.toAppleMapsColor()
        gmsPolygon.strokeColor = polygon.lineColor?.toAppleMapsColor()
        gmsPolygon.strokeWidth = polygon.lineWidth.toDouble()
        gmsPolygon.map = mapView as GMSMapView
        gmsPolygon.tappable = true
        polygonMapping[gmsPolygon] = polygon
    }
}

/**
 * Updates Google Maps polylines by removing existing polylines and adding new ones.
 *
 * @param mapView Google Maps map view
 * @param polylines List of MapPolyline objects to display
 * @param polylineMapping MutableMap mapping GMSPolyline to MapPolyline for styling
 */
@OptIn(ExperimentalForeignApi::class)
internal fun updateGoogleMapsPolylines(
    mapView: UtilsGMSMapView,
    polylines: List<Polyline>,
    polylineMapping: MutableMap<GMSPolyline, Polyline>,
) {
    polylineMapping.keys.forEach { polyline -> polyline.map = null }
    polylineMapping.clear()

    polylines.forEach { polyline ->
        val gmsPolyline = GMSPolyline()
        val path = GMSMutablePath()
        polyline.coordinates.forEach { coord ->
            path.addCoordinate(CLLocationCoordinate2DMake(coord.latitude, coord.longitude))
        }
        gmsPolyline.path = path
        gmsPolyline.strokeWidth = polyline.width.toDouble()
        gmsPolyline.strokeColor = polyline.lineColor?.toAppleMapsColor() ?: UIColor.blackColor()
        gmsPolyline.map = mapView as GMSMapView
        gmsPolyline.tappable = true
        polylineMapping[gmsPolyline] = polyline
    }
}

@OptIn(ExperimentalForeignApi::class)
internal fun MapType?.toGoogleMapsMapType() =
    when (this) {
        MapType.HYBRID -> kGMSTypeHybrid
        MapType.NORMAL -> kGMSTypeNormal
        MapType.SATELLITE -> kGMSTypeSatellite
        MapType.TERRAIN -> kGMSTypeTerrain
        else -> kGMSTypeNormal
    }

/**
 * Switches between light and dark mode for the map.
 *
 * @param isDarkModeEnabled true for dark mode, false for light mode
 */
@OptIn(ExperimentalForeignApi::class)
internal fun UtilsGMSMapView.switchTheme(isDarkModeEnabled: Boolean) {
    setOverrideUserInterfaceStyle(
        if (isDarkModeEnabled) {
            UIUserInterfaceStyle.UIUserInterfaceStyleDark
        } else {
            UIUserInterfaceStyle.UIUserInterfaceStyleLight
        }
    )
}

/**
 * Updates Google Maps settings based on MapUISettings.
 *
 * @param mapView Google Maps map view
 */
@OptIn(ExperimentalForeignApi::class)
internal fun MapUISettings.toGoogleMapsSettings(mapView: UtilsGMSMapView) {
    mapView.settings().setScrollGestures(scrollEnabled)
    mapView.settings().setZoomGestures(zoomEnabled)
    mapView.settings().setTiltGestures(iosUISettings.gmsTiltGesturesEnabled)
    mapView.settings().setRotateGestures(rotateEnabled)
    mapView.settings().setCompassButton(compassEnabled)
    mapView.settings().setMyLocationButton(myLocationButtonEnabled)
    mapView.settings().setIndoorPicker(iosUISettings.gmsIndoorPicker)
    mapView.settings().setAllowScrollGesturesDuringRotateOrZoom(
        iosUISettings.gmsScrollGesturesEnabledDuringRotateOrZoom
    )
    mapView.settings().setConsumesGesturesInView(iosUISettings.gmsConsumesGesturesInView)
}

/**
 * Converts GoogleMapsMapStyleOptions to native GMSMapStyle.
 *
 * @return GMSMapStyle from JSON string, or null if no JSON provided
 */
@OptIn(ExperimentalForeignApi::class)
internal fun GoogleMapsMapStyleOptions?.toNativeStyleOptions() =
    this?.json?.let { GMSMapStyle.styleWithJSONString(it, error = null) }

/**
 * Renders a GeoJSON layer on an iOS Google Map using Google Maps Utils.
 *
 * @param geoJson A UTF‑8 encoded GeoJSON document.
 * @return The created GMUGeometryRenderer, or null if encoding, parsing, or casting fails.
 */
@OptIn(ExperimentalForeignApi::class)
public fun UtilsGMSMapView.renderGeoJson(geoJson: String): GMUGeometryRenderer? {
    val dataString: NSString = geoJson as NSString
    val data = dataString.dataUsingEncoding(NSUTF8StringEncoding) ?: return null
    val parser = GMUGeoJSONParser(data = data)
    parser.parse()

    val renderer = GMUGeometryRenderer(map = this, geometries = parser.features)
    renderer.render()
    return renderer
}

/**
 * Converts the [CameraPosition] to a native [GMSCameraPosition] and applies it to this map view.
 *
 * @param position The camera position to convert
 */
@OptIn(ExperimentalForeignApi::class)
public fun UtilsGMSMapView.setUpGMSCameraPosition(position: CameraPosition) {
    setCamera(
        GMSCameraPosition.cameraWithTarget(
            target =
                CLLocationCoordinate2DMake(
                    position.coordinates.latitude,
                    position.coordinates.longitude,
                ),
            zoom = position.zoom,
            bearing = (position.iosCameraPosition?.gmsBearing ?: 0f).toDouble(),
            viewingAngle = (position.iosCameraPosition?.gmsViewingAngle ?: 0f).toDouble(),
        )
    )
}

/**
 * Converts an optional Compose Color to a UIColor, using the provided fallback when null.
 *
 * @param fallback Color to use when this Color is null.
 * @return UIColor created from this Color or the fallback.
 */
internal fun Color?.toUIColor(fallback: UIColor): UIColor =
    if (this == null) {
        fallback
    } else {
        UIColor(
            red = this.red.toDouble(),
            green = this.green.toDouble(),
            blue = this.blue.toDouble(),
            alpha = this.alpha.toDouble(),
        )
    }

/**
 * Safely reads a String value from an NSDictionary by key.
 *
 * @param dict Source NSDictionary (e.g., GeoJSON feature.properties).
 * @param key Key to read.
 * @return String value or null.
 */
internal fun getString(dict: NSDictionary?, key: String): String? {
    val v = dict?.objectForKey(key)
    return when (v) {
        is String -> v
        is NSString -> v.toString()
        else -> null
    }
}

/**
 * Safely reads a Double value from an NSDictionary by key.
 *
 * @param dict Source NSDictionary (e.g., GeoJSON feature.properties).
 * @param key Key to read.
 * @return Double value or null.
 */
internal fun getDouble(dict: NSDictionary?, key: String): Double? {
    val v = dict?.objectForKey(key)
    return when (v) {
        is Number -> v.toDouble()
        is NSNumber -> v.doubleValue
        is String -> v.toDoubleOrNull()
        is NSString -> v.toString().toDoubleOrNull()
        else -> null
    }
}

/**
 * Parses a hex color string into a UIColor.
 *
 * @param hexInput Hex color string with or without the leading '#'.
 * @return Parsed UIColor or null.
 */
internal fun parseHexToUIColor(hexInput: String?): UIColor? {
    if (hexInput == null) return null
    val hex = hexInput.trim().removePrefix("#")
    return when (hex.length) {
        6 -> {
            val r = hex.substring(0, 2).toIntOrNull(16) ?: return null
            val g = hex.substring(2, 4).toIntOrNull(16) ?: return null
            val b = hex.substring(4, 6).toIntOrNull(16) ?: return null
            UIColor(red = r / 255.0, green = g / 255.0, blue = b / 255.0, alpha = 1.0)
        }
        8 -> {
            val a = hex.substring(0, 2).toIntOrNull(16) ?: return null
            val r = hex.substring(2, 4).toIntOrNull(16) ?: return null
            val g = hex.substring(4, 6).toIntOrNull(16) ?: return null
            val b = hex.substring(6, 8).toIntOrNull(16) ?: return null
            UIColor(red = r / 255.0, green = g / 255.0, blue = b / 255.0, alpha = a / 255.0)
        }
        else -> null
    }
}
