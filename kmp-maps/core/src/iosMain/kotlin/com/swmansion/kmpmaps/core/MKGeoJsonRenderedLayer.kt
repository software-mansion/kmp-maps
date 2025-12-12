package com.swmansion.kmpmaps.core

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.Foundation.NSArray
import platform.Foundation.NSDictionary
import platform.Foundation.NSJSONSerialization
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
    internal val annotations: List<MKAnnotationProtocol>,
    internal val extractedMarkers: List<Marker>,
    internal val polylineStyles: Map<MKPolyline, AppleMapsGeoJsonLineStyle> = emptyMap(),
    internal val polygonStyles: Map<MKPolygon, AppleMapsGeoJsonPolygonStyle> = emptyMap(),
    internal val pointStyles: Map<MKPointAnnotation, AppleMapsGeoJsonPointStyle> = emptyMap(),
) {
    @OptIn(ExperimentalForeignApi::class)
    public fun clear(from: MKMapView) {
        if (overlays.isNotEmpty()) from.removeOverlays(overlays)
        if (annotations.isNotEmpty()) from.removeAnnotations(annotations)
    }
}

/**
 * Renders a single GeoJSON document on an MKMapView.
 *
 * @param geoJson A UTF‑8 encoded GeoJSON string.
 * @return A handle to the rendered layer, or null if decoding fails.
 */
@OptIn(ExperimentalForeignApi::class)
public fun MKMapView.renderGeoJson(
    geoJson: String,
    clusterSettings: ClusterSettings,
): MKGeoJsonRenderedLayer? {
    val data = (geoJson as NSString).dataUsingEncoding(NSUTF8StringEncoding) ?: return null
    val decoder = MKGeoJSONDecoder()
    val objects = decoder.geoJSONObjectsWithData(data, error = null) ?: return null

    val annotations = mutableListOf<MKAnnotationProtocol>()
    val overlays = mutableListOf<MKOverlayProtocol>()
    val extractedMarkers = mutableListOf<Marker>()

    val polylineStyles = mutableMapOf<MKPolyline, AppleMapsGeoJsonLineStyle>()
    val polygonStyles = mutableMapOf<MKPolygon, AppleMapsGeoJsonPolygonStyle>()
    val pointStyles = mutableMapOf<MKPointAnnotation, AppleMapsGeoJsonPointStyle>()

    objects.forEach { obj ->
        collectAndAdd(
            obj = obj,
            mapView = this,
            overlays = overlays,
            annotations = annotations,
            extractedMarkers = extractedMarkers,
            polylineStyles = polylineStyles,
            polygonStyles = polygonStyles,
            pointStyles = pointStyles,
            defaults = null,
            featureProps = null,
            clusterSettings = clusterSettings,
        )
    }

    return MKGeoJsonRenderedLayer(
        overlays = overlays,
        annotations = annotations,
        extractedMarkers = extractedMarkers,
        polylineStyles = polylineStyles,
        polygonStyles = polygonStyles,
        pointStyles = pointStyles,
    )
}

/**
 * Recursively collects MapKit objects produced from a GeoJSON object and adds them to the provided
 * lists/maps, applying defaults and per‑feature overrides.
 *
 * @param obj GeoJSON object (feature, geometry, or array of geometries).
 * @param mapView Target map view (passed for API parity if needed).
 * @param overlays Destination list for overlays.
 * @param annotations Destination list for annotations.
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
    annotations: MutableList<MKAnnotationProtocol>,
    extractedMarkers: MutableList<Marker>,
    polylineStyles: MutableMap<MKPolyline, AppleMapsGeoJsonLineStyle>,
    polygonStyles: MutableMap<MKPolygon, AppleMapsGeoJsonPolygonStyle>,
    pointStyles: MutableMap<MKPointAnnotation, AppleMapsGeoJsonPointStyle>,
    defaults: GeoJsonLayer?,
    featureProps: Map<String, Any?>?,
    clusterSettings: ClusterSettings,
) {
    when (obj) {
        is MKGeoJSONFeature -> {
            val props = obj.readProperties()
            obj.geometry.forEach { g ->
                collectAndAdd(
                    g,
                    mapView,
                    overlays,
                    annotations,
                    extractedMarkers,
                    polylineStyles,
                    polygonStyles,
                    pointStyles,
                    defaults,
                    props,
                    clusterSettings,
                )
            }
        }
        is MKPolygon -> {
            overlays += obj
            polygonStyles[obj] = buildPolygonStyle(defaults, featureProps)
        }
        is MKPolyline -> {
            overlays += obj
            polylineStyles[obj] = buildLineStyle(defaults, featureProps)
        }
        is MKMultiPolygon,
        is MKMultiPolyline -> {
            overlays += obj
        }
        is MKPointAnnotation -> {
            if (clusterSettings.enabled) {
                val coordinates = obj.coordinate.useContents { Coordinates(latitude, longitude) }
                val title = featureProps?.string("title")

                val marker = Marker(coordinates = coordinates, title = title)
                extractedMarkers.add(marker)
            } else {
                val title =
                    featureProps?.string("title")
                        ?: featureProps?.string("name")
                        ?: defaults?.pointStyle?.pointTitle
                val subtitle =
                    featureProps?.string("snippet")
                        ?: featureProps?.string("description")
                        ?: defaults?.pointStyle?.snippet
                if (title != null) obj.setTitle(title)
                if (subtitle != null) obj.setSubtitle(subtitle)

                val style = buildPointStyle(defaults, featureProps)
                pointStyles[obj] = style

                annotations += obj
            }
        }
        is NSArray -> {
            val n = obj.count.toInt()
            for (i in 0 until n) {
                val any = obj.objectAtIndex(i.toULong())
                collectAndAdd(
                    any,
                    mapView,
                    overlays,
                    annotations,
                    extractedMarkers,
                    polylineStyles,
                    polygonStyles,
                    pointStyles,
                    defaults,
                    featureProps,
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
    val defaultStroke =
        defaults?.polygonStyle?.strokeColor?.toAppleMapsColor() ?: UIColor.blackColor
    val defaultStrokeWidth =
        (defaults?.polygonStyle?.strokeWidth ?: DEFAULT_STROKE_WIDTH_FALLBACK).toDouble()
    val defaultFill = defaults?.polygonStyle?.fillColor?.toAppleMapsColor()

    val strokeHex = props?.string("stroke")
    val strokeWidth = props?.double("stroke-width")
    val fillHex = props?.string("fill")
    val fillOpacity = props?.double("fill-opacity")?.coerceIn(0.0, 1.0)

    val strokeColor = strokeHex?.toUIColor() ?: defaultStroke
    val fillBase = fillHex?.toUIColor() ?: defaultFill
    val fillColor =
        if (fillOpacity != null && fillBase != null) {
            fillBase.colorWithAlphaComponent(fillOpacity)
        } else {
            fillBase
        }

    return AppleMapsGeoJsonPolygonStyle(
        strokeColor = strokeColor,
        strokeWidth = strokeWidth ?: defaultStrokeWidth,
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
