package com.swmansion.kmpmaps.googlemaps

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import cocoapods.GoogleMaps.GMSCircle
import cocoapods.GoogleMaps.GMSMapStyle
import cocoapods.GoogleMaps.GMSMapView
import cocoapods.GoogleMaps.GMSMarker
import cocoapods.GoogleMaps.GMSMutablePath
import cocoapods.GoogleMaps.GMSPolygon
import cocoapods.GoogleMaps.GMSPolyline
import cocoapods.GoogleMaps.kGMSTypeHybrid
import cocoapods.GoogleMaps.kGMSTypeNormal
import cocoapods.GoogleMaps.kGMSTypeSatellite
import cocoapods.GoogleMaps.kGMSTypeTerrain
import com.swmansion.kmpmaps.core.Circle
import com.swmansion.kmpmaps.core.GoogleMapsMapStyleOptions
import com.swmansion.kmpmaps.core.MapType
import com.swmansion.kmpmaps.core.MapUISettings
import com.swmansion.kmpmaps.core.Marker
import com.swmansion.kmpmaps.core.Polygon
import com.swmansion.kmpmaps.core.Polyline
import kotlin.collections.set
import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreLocation.CLLocationCoordinate2DMake
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
    mapView: GMSMapView,
    markers: List<Marker>,
    markerMapping: MutableMap<GMSMarker, Marker>,
) {
    markerMapping.keys.forEach { marker -> marker.map = null }
    markerMapping.clear()

    markers.forEach { marker ->
        val gmsMarker = GMSMarker()
        gmsMarker.position =
            CLLocationCoordinate2DMake(marker.coordinates.latitude, marker.coordinates.longitude)
        gmsMarker.title = marker.title
        gmsMarker.map = mapView
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
    mapView: GMSMapView,
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
        gmsCircle.map = mapView
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
    mapView: GMSMapView,
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
        gmsPolygon.map = mapView
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
    mapView: GMSMapView,
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
        gmsPolyline.map = mapView
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
 * Converts androidx Color to Apple UIKit's UIColor.
 *
 * @return UIColor corresponding to the androidx Color object
 */
@OptIn(ExperimentalForeignApi::class)
internal fun Color.toAppleMapsColor(): UIColor {
    val argb = this.toArgb()
    return UIColor.colorWithRed(
        red = ((argb shr 16) and 0xFF) / 255.0,
        green = ((argb shr 8) and 0xFF) / 255.0,
        blue = (argb and 0xFF) / 255.0,
        alpha = ((argb shr 24) and 0xFF) / 255.0,
    )
}

/**
 * Switches between light and dark mode for the map.
 *
 * @param isDarkModeEnabled true for dark mode, false for light mode
 */
@OptIn(ExperimentalForeignApi::class)
internal fun GMSMapView.switchTheme(isDarkModeEnabled: Boolean) {
    overrideUserInterfaceStyle =
        if (isDarkModeEnabled) {
            UIUserInterfaceStyle.UIUserInterfaceStyleDark
        } else {
            UIUserInterfaceStyle.UIUserInterfaceStyleLight
        }
}

/**
 * Updates Google Maps settings based on MapUISettings.
 *
 * @param mapView Google Maps map view
 */
@OptIn(ExperimentalForeignApi::class)
internal fun MapUISettings.toGoogleMapsSettings(mapView: GMSMapView) {
    mapView.settings.scrollGestures = scrollEnabled
    mapView.settings.zoomGestures = zoomEnabled
    mapView.settings.tiltGestures = iosGmsTiltGesturesEnabled
    mapView.settings.rotateGestures = rotateEnabled
    mapView.settings.compassButton = compassEnabled
    mapView.settings.myLocationButton = myLocationButtonEnabled
    mapView.settings.indoorPicker = iosGmsIndoorPicker
    mapView.settings.allowScrollGesturesDuringRotateOrZoom =
        iosGmsScrollGesturesEnabledDuringRotateOrZoom
    mapView.settings.consumesGesturesInView = iosGmsConsumesGesturesInView
}

/**
 * Converts GoogleMapsMapStyleOptions to native GMSMapStyle.
 *
 * @return GMSMapStyle from JSON string, or null if no JSON provided
 */
@OptIn(ExperimentalForeignApi::class)
internal fun GoogleMapsMapStyleOptions?.toNativeStyleOptions() =
    this?.json?.let { GMSMapStyle.styleWithJSONString(it, error = null) }
