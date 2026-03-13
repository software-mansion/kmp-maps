package com.swmansion.kmpmaps.core

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.get
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.useContents
import platform.CoreLocation.CLLocationCoordinate2D
import platform.Foundation.NSArray
import platform.Foundation.NSDictionary
import platform.Foundation.NSJSONSerialization
import platform.Foundation.NSMakeRange
import platform.Foundation.NSNumber
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.dataUsingEncoding
import platform.Foundation.enumerateKeysAndObjectsUsingBlock
import platform.MapKit.*
import platform.UIKit.UIColor

/**
 * Style for a GeoJSON LineString/Polyline rendered on Apple Maps (MapKit).
 *
 * @param color Stroke color (UIKit UIColor).
 * @param width Stroke width in screen points.
 */
public data class AppleMapsGeoJsonLineStyle(val color: UIColor, val width: Double)

/**
 * Style for a GeoJSON Polygon rendered on Apple Maps (MapKit).
 *
 * @param strokeColor Outline color (UIKit UIColor).
 * @param strokeWidth Outline width in screen points.
 * @param fillColor Optional fill color (UIKit UIColor). Use null to disable fill.
 */
public data class AppleMapsGeoJsonPolygonStyle(
    val strokeColor: UIColor,
    val strokeWidth: Double,
    val fillColor: UIColor?,
)

/**
 * Style for a GeoJSON Point rendered on Apple Maps (MapKit).
 *
 * @param visible Controls marker visibility.
 */
public data class AppleMapsGeoJsonPointStyle(val visible: Boolean = true)

/**
 * Handle for a rendered GeoJSON layer on MKMapView. Keep this instance and call clear(from:) to
 * remove it later.
 */
public class MKGeoJsonRenderedLayer(
    internal val overlays: List<MKOverlayProtocol>,
    internal val extractedMarkers: List<Marker>,
    internal val polylineStyles: Map<MKOverlayProtocol, AppleMapsGeoJsonLineStyle> = emptyMap(),
    internal val polygonStyles: Map<MKOverlayProtocol, AppleMapsGeoJsonPolygonStyle> = emptyMap(),
    internal val pointStyles: Map<MKPointAnnotation, AppleMapsGeoJsonPointStyle> = emptyMap(),
    internal val featureData: Map<MKOverlayProtocol, GeoJsonFeatureClicked> = emptyMap(),
    internal val hitTestPolygons: Map<MKOverlayProtocol, Polygon> = emptyMap(),
    internal val hitTestPolylines: Map<MKOverlayProtocol, Polyline> = emptyMap(),
) {
    @OptIn(ExperimentalForeignApi::class)
    public fun clear(from: MKMapView) {
        if (overlays.isNotEmpty()) from.removeOverlays(overlays)
    }
}

/**
 * Renders a single GeoJSON document on an MKMapView.
 *
 * @param layer GeoJSON layer to render.
 * @param clusterSettings Settings for cluster rendering.
 * @return A handle to the rendered layer, or null if decoding fails.
 */
@OptIn(ExperimentalForeignApi::class)
public fun MKMapView.renderGeoJson(
    layer: GeoJsonLayer,
    clusterSettings: ClusterSettings,
): MKGeoJsonRenderedLayer? {
    val data = (layer.geoJson as NSString).dataUsingEncoding(NSUTF8StringEncoding) ?: return null
    val decoder = MKGeoJSONDecoder()
    val objects = decoder.geoJSONObjectsWithData(data, error = null) ?: return null

    val overlays = mutableListOf<MKOverlayProtocol>()
    val extractedMarkers = mutableListOf<Marker>()

    val polylineStyles = mutableMapOf<MKOverlayProtocol, AppleMapsGeoJsonLineStyle>()
    val polygonStyles = mutableMapOf<MKOverlayProtocol, AppleMapsGeoJsonPolygonStyle>()
    val pointStyles = mutableMapOf<MKPointAnnotation, AppleMapsGeoJsonPointStyle>()

    val featureData = mutableMapOf<MKOverlayProtocol, GeoJsonFeatureClicked>()
    val hitTestPolygons = mutableMapOf<MKOverlayProtocol, Polygon>()
    val hitTestPolylines = mutableMapOf<MKOverlayProtocol, Polyline>()

    objects.forEach { obj ->
        collectAndAdd(
            obj = obj,
            mapView = this,
            overlays = overlays,
            extractedMarkers = extractedMarkers,
            polylineStyles = polylineStyles,
            polygonStyles = polygonStyles,
            pointStyles = pointStyles,
            defaults = layer,
            featureProps = null,
            featureData = featureData,
            hitTestPolygons = hitTestPolygons,
            hitTestPolylines = hitTestPolylines,
            featureId = null,
            stringProps = null,
            clusterSettings = clusterSettings,
        )
    }

    return MKGeoJsonRenderedLayer(
        overlays = overlays,
        extractedMarkers = extractedMarkers,
        polylineStyles = polylineStyles,
        polygonStyles = polygonStyles,
        pointStyles = pointStyles,
        featureData = featureData,
        hitTestPolygons = hitTestPolygons,
        hitTestPolylines = hitTestPolylines,
    )
}

@OptIn(ExperimentalForeignApi::class)
private fun extractCoordinates(overlay: MKOverlayProtocol): List<Coordinates> {
    val list = mutableListOf<Coordinates>()
    when (overlay) {
        is MKMultiPoint -> {
            memScoped {
                val count = overlay.pointCount
                if (count > 0uL) {
                    val coordsPtr = allocArray<CLLocationCoordinate2D>(count.toInt())
                    overlay.getCoordinates(coordsPtr, NSMakeRange(0uL, count))

                    for (i in 0 until count.toInt()) {
                        val coord = coordsPtr[i]
                        list.add(Coordinates(coord.latitude, coord.longitude))
                    }
                }
            }
        }
        is MKMultiPolygon -> {
            overlay.polygons.forEach { poly ->
                if (poly is MKPolygon) list.addAll(extractCoordinates(poly))
            }
        }
        is MKMultiPolyline -> {
            overlay.polylines.forEach { line ->
                if (line is MKPolyline) list.addAll(extractCoordinates(line))
            }
        }
    }
    return list
}

/**
 * Recursively collects MapKit objects produced from a GeoJSON object and adds them to the provided
 * lists/maps, applying defaults and per‑feature overrides.
 *
 * @param obj GeoJSON object (feature, geometry, or array of geometries).
 * @param mapView Target map view (passed for API parity if needed).
 * @param overlays Destination list for overlays.
 * @param polylineStyles Destination map for polyline styles.
 * @param polygonStyles Destination map for polygon styles.
 * @param pointStyles Destination map for point styles.
 * @param defaults Optional layer-wide default styling.
 * @param featureProps Decoded properties of a GeoJSON feature.
 */
@OptIn(ExperimentalForeignApi::class)
private fun collectAndAdd(
    obj: Any?,
    mapView: MKMapView,
    overlays: MutableList<MKOverlayProtocol>,
    extractedMarkers: MutableList<Marker>,
    polylineStyles: MutableMap<MKOverlayProtocol, AppleMapsGeoJsonLineStyle>,
    polygonStyles: MutableMap<MKOverlayProtocol, AppleMapsGeoJsonPolygonStyle>,
    pointStyles: MutableMap<MKPointAnnotation, AppleMapsGeoJsonPointStyle>,
    defaults: GeoJsonLayer?,
    featureProps: Map<String, Any?>?,
    featureData: MutableMap<MKOverlayProtocol, GeoJsonFeatureClicked>,
    hitTestPolygons: MutableMap<MKOverlayProtocol, Polygon>,
    hitTestPolylines: MutableMap<MKOverlayProtocol, Polyline>,
    featureId: String?,
    stringProps: Map<String, String>?,
    clusterSettings: ClusterSettings,
) {
    when (obj) {
        is MKGeoJSONFeature -> {
            val props = obj.readProperties()
            val extractedFeatureId = obj.identifier?.toString()
            val extractedStringProps =
                props.entries.mapNotNull { (k, v) -> v?.let { k to it.toString() } }.toMap()
            obj.geometry.forEach { g ->
                collectAndAdd(
                    g,
                    mapView,
                    overlays,
                    extractedMarkers,
                    polylineStyles,
                    polygonStyles,
                    pointStyles,
                    defaults,
                    props,
                    featureData,
                    hitTestPolygons,
                    hitTestPolylines,
                    extractedFeatureId,
                    extractedStringProps,
                    clusterSettings,
                )
            }
        }
        is MKPolygon,
        is MKMultiPolygon -> {
            val protocol = obj as MKOverlayProtocol
            overlays += protocol

            val style = buildPolygonStyle(defaults, featureProps)
            polygonStyles[protocol] = style

            val type = if (protocol is MKMultiPolygon) "MultiPolygon" else "Polygon"
            featureData[protocol] =
                GeoJsonFeatureClicked(featureId, type, stringProps ?: emptyMap())

            hitTestPolygons[protocol] =
                Polygon(
                    coordinates = extractCoordinates(protocol),
                    lineWidth = style.strokeWidth.toFloat(),
                )
        }
        is MKPolyline,
        is MKMultiPolyline -> {
            val protocol = obj as MKOverlayProtocol
            overlays += protocol

            val style = buildLineStyle(defaults, featureProps)
            polylineStyles[protocol] = style

            val type = if (protocol is MKMultiPolyline) "MultiLineString" else "LineString"
            featureData[protocol] =
                GeoJsonFeatureClicked(featureId, type, stringProps ?: emptyMap())

            hitTestPolylines[protocol] =
                Polyline(coordinates = extractCoordinates(protocol), width = style.width.toFloat())
        }
        is MKPointAnnotation -> {
            val coordinates = obj.coordinate.useContents { Coordinates(latitude, longitude) }
            val title = featureProps?.string("title")

            val marker = Marker(coordinates = coordinates, title = title)
            extractedMarkers.add(marker)
        }
        is NSArray -> {
            val n = obj.count.toInt()
            for (i in 0 until n) {
                val any = obj.objectAtIndex(i.toULong())
                collectAndAdd(
                    any,
                    mapView,
                    overlays,
                    extractedMarkers,
                    polylineStyles,
                    polygonStyles,
                    pointStyles,
                    defaults,
                    featureProps,
                    featureData,
                    hitTestPolygons,
                    hitTestPolylines,
                    featureId,
                    stringProps,
                    clusterSettings,
                )
            }
        }
        else -> Unit
    }
}

/**
 * Decodes the feature's properties JSON into a Kotlin Map.
 *
 * @return Properties map or empty map when absent or invalid.
 */
@OptIn(ExperimentalForeignApi::class)
private fun MKGeoJSONFeature.readProperties(): Map<String, Any?> {
    val data = properties ?: return emptyMap()
    val json = NSJSONSerialization.JSONObjectWithData(data, options = 0u, error = null)
    return if (json is NSDictionary) json.toKotlinStringAnyMap() else emptyMap()
}

/**
 * Converts an NSDictionary into Map<String, Any?> using only String/NSString keys.
 *
 * @return Kotlin Map with string keys.
 * @receiver NSDictionary to convert.
 */
@OptIn(ExperimentalForeignApi::class)
private fun NSDictionary.toKotlinStringAnyMap(): Map<String, Any?> {
    val out = mutableMapOf<String, Any?>()
    this.enumerateKeysAndObjectsUsingBlock { k, v, _ ->
        val key: String =
            when (k) {
                is String -> k
                is NSString -> k.toString()
                else -> return@enumerateKeysAndObjectsUsingBlock
            }
        out[key] = v
    }
    return out
}

/**
 * Builds a line style from layer defaults and feature properties.
 *
 * @param defaults Optional layer‑wide defaults.
 * @param props Optional per‑feature properties.
 * @return Resolved [AppleMapsGeoJsonLineStyle].
 */
private fun buildLineStyle(
    defaults: GeoJsonLayer?,
    props: Map<String, Any?>?,
): AppleMapsGeoJsonLineStyle {
    val defaultColor =
        defaults?.lineStringStyle?.lineColor?.toAppleMapsColor() ?: UIColor.blackColor
    val defaultWidth =
        (defaults?.lineStringStyle?.lineWidth ?: DEFAULT_STROKE_WIDTH_FALLBACK).toDouble()

    val strokeHex = props?.string("stroke")
    val strokeWidth = props?.double("stroke-width")

    val color = strokeHex?.toUIColor() ?: defaultColor
    val width = strokeWidth ?: defaultWidth
    return AppleMapsGeoJsonLineStyle(color = color, width = width)
}

/**
 * Builds a polygon style from layer defaults and feature properties.
 *
 * @param defaults Optional layer‑wide defaults.
 * @param props Optional per‑feature properties.
 * @return Resolved [AppleMapsGeoJsonPolygonStyle].
 */
private fun buildPolygonStyle(
    defaults: GeoJsonLayer?,
    props: Map<String, Any?>?,
): AppleMapsGeoJsonPolygonStyle {
    val jsonStrokeHex = props?.string("stroke")
    val jsonStrokeWidth = props?.double("stroke-width")
    val jsonFillHex = props?.string("fill")
    val jsonFillOpacity = props?.double("fill-opacity")?.coerceIn(0.0, 1.0)

    val strokeColor =
        jsonStrokeHex?.toUIColor()
            ?: defaults?.polygonStyle?.strokeColor?.toAppleMapsColor()
            ?: UIColor.blackColor

    val strokeWidth =
        jsonStrokeWidth
            ?: defaults?.polygonStyle?.strokeWidth?.toDouble()
            ?: DEFAULT_STROKE_WIDTH_FALLBACK

    val fillBase = jsonFillHex?.toUIColor() ?: defaults?.polygonStyle?.fillColor?.toAppleMapsColor()

    val fillColor =
        if (jsonFillOpacity != null && fillBase != null) {
            fillBase.colorWithAlphaComponent(jsonFillOpacity)
        } else {
            fillBase
        }

    return AppleMapsGeoJsonPolygonStyle(
        strokeColor = strokeColor,
        strokeWidth = strokeWidth,
        fillColor = fillColor,
    )
}

/**
 * Builds a point style from layer defaults and feature properties.
 *
 * @param defaults Optional layer‑wide defaults.
 * @param props Optional per‑feature properties.
 * @return Resolved [AppleMapsGeoJsonPointStyle].
 */
private fun buildPointStyle(
    defaults: GeoJsonLayer?,
    props: Map<String, Any?>?,
): AppleMapsGeoJsonPointStyle {
    val visible = props?.bool("visible") ?: (defaults?.visible ?: true)

    return AppleMapsGeoJsonPointStyle(visible = visible)
}

/**
 * Returns a non‑blank String value for [key], supporting Kotlin String and NSString.
 *
 * @param key Property name.
 * @return Trimmed non‑blank string or null.
 * @receiver Source properties map.
 */
private fun Map<String, Any?>.string(key: String): String? =
    when (val v = this[key]) {
        is String -> v.takeIf(String::isNotBlank)
        is NSString -> v.toString().takeIf(String::isNotBlank)
        else -> null
    }

/**
 * Returns a Double value for [key] if it can be parsed from common types.
 *
 * @param key Property name.
 * @return Parsed Double or null.
 * @receiver Source properties map.
 */
private fun Map<String, Any?>.double(key: String): Double? =
    when (val v = this[key]) {
        is NSNumber -> v.doubleValue
        is String -> v.toDoubleOrNull()
        is NSString -> v.toString().toDoubleOrNull()
        is Double -> v
        is Number -> v.toDouble()
        else -> null
    }

/**
 * Returns a Boolean value for [key] if it can be parsed from common types.
 *
 * @param key Property name.
 * @return Parsed Boolean or null.
 * @receiver Source properties map.
 */
private fun Map<String, Any?>.bool(key: String): Boolean? =
    when (val v = this[key]) {
        is NSNumber -> v.boolValue
        is String -> v.equals("true", ignoreCase = true)
        is NSString -> v.toString().equals("true", ignoreCase = true)
        is Boolean -> v
        else -> null
    }

/**
 * Parses a hex color string into a UIColor.
 *
 * @return Parsed [UIColor] or [UIColor.blackColor] on error.
 * @receiver Hex string to parse.
 */
private fun String.toUIColor(): UIColor {
    val hex = trim().removePrefix("#")
    val value = hex.toLongOrNull(16) ?: return UIColor.blackColor
    return when (hex.length) {
        6 -> {
            val r = ((value shr 16) and 0xFF).toDouble() / 255.0
            val g = ((value shr 8) and 0xFF).toDouble() / 255.0
            val b = (value and 0xFF).toDouble() / 255.0
            UIColor.colorWithRed(r, green = g, blue = b, alpha = 1.0)
        }
        8 -> {
            val a = ((value shr 24) and 0xFF).toDouble() / 255.0
            val r = ((value shr 16) and 0xFF).toDouble() / 255.0
            val g = ((value shr 8) and 0xFF).toDouble() / 255.0
            val b = (value and 0xFF).toDouble() / 255.0
            UIColor.colorWithRed(r, green = g, blue = b, alpha = a)
        }
        else -> UIColor.blackColor
    }
}

private const val DEFAULT_STROKE_WIDTH_FALLBACK = 2.0
