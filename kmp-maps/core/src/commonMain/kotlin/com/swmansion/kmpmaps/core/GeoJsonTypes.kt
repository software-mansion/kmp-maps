package com.swmansion.kmpmaps.core

import androidx.compose.ui.graphics.Color

/**
 * Represents a layer of GeoJSON data to be displayed on the map.
 *
 * @param geoJson Raw GeoJSON document (UTF-8).
 * @param visible Whether the layer is visible.
 * @param zIndex Drawing order relative to other layers (higher draws on top).
 * @param opacity Global opacity multiplier (0f..1f) applied to shapes.
 * @param lineWidth Stroke width for line/polygon features.
 * @param lineColor Stroke color for line/polygon features.
 * @param fillColor Fill color for polygon features.
 * @param lineCap Stroke cap style for lines.
 * @param lineJoin Stroke join style for lines/polygons.
 * @param lineDashPattern Dash pattern for stroke (sequence of on/off lengths in px).
 * @param geometryTypes If set, only geometries of these types will be rendered.
 * @param minZoom Minimum zoom at which the layer is visible.
 * @param maxZoom Maximum zoom at which the layer is visible.
 * @param fitToLayerBounds If true, camera will be adjusted to fit the layer on first render.
 * @param pointIconName Optional asset name for point features (markers).
 * @param pointColor Marker color for point features.
 * @param pointScale Scale for marker icon/symbol for point features (1.0 = original).
 * @param labelPropertyKey GeoJSON property key used as title/label for points.
 * @param androidOptions Android-specific options.
 * @param iosOptions iOS-specific options.
 */
public data class GeoJsonLayer(
    val geoJson: String,
    val visible: Boolean = true,
    val zIndex: Float = 0f,
    val opacity: Float? = null,
    val lineWidth: Float? = null,
    val lineColor: Color? = null,
    val fillColor: Color? = null,
    val lineCap: LineCap? = null,
    val lineJoin: LineJoin? = null,
    val lineDashPattern: List<Float>? = null,
    val geometryTypes: Set<GeoJsonGeometryType>? = null,
    val minZoom: Float? = null,
    val maxZoom: Float? = null,
    val fitToLayerBounds: Boolean = false,
    val pointIconName: String? = null,
    val pointColor: Color? = null,
    val pointScale: Float? = null,
    val labelPropertyKey: String? = null,
    val androidOptions: AndroidGeoJsonOptions = AndroidGeoJsonOptions(),
    val iosOptions: IosGeoJsonOptions = IosGeoJsonOptions(),
)

/** Geometry types present in GeoJSON. */
public enum class GeoJsonGeometryType {
    Point,
    MultiPoint,
    LineString,
    MultiLineString,
    Polygon,
    MultiPolygon,
    GeometryCollection,
    Feature,
    FeatureCollection,
}

/** Line cap style for strokes. */
public enum class LineCap {
    Butt,
    Round,
    Square,
}

/** Line join style for strokes. */
public enum class LineJoin {
    Miter,
    Round,
    Bevel,
}

/**
 * Android-specific options for GeoJSON layer styling/behavior.
 *
 * @param pointMarkerHue Marker hue for default Google marker (0..360).
 * @param clustering Enable clustering for point features (if supported by integration).
 */
public data class AndroidGeoJsonOptions(
    val pointMarkerHue: Float? = null,
    val clustering: Boolean = false,
)

/**
 * iOS-specific options for GeoJSON layer styling/behavior.
 *
 * @param overlayLevel Target overlay level (e.g., above roads) if supported.
 * @param lineDashPhase Starting offset for dashed lines.
 */
public data class IosGeoJsonOptions(val overlayLevel: Int? = null, val lineDashPhase: Float? = null)
