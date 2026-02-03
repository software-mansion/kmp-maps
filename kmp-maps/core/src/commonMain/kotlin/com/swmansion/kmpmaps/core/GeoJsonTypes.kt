package com.swmansion.kmpmaps.core

import androidx.compose.ui.graphics.Color

/**
 * Represents a layer of GeoJSON data to be displayed on the map.
 *
 * @property geoJson Raw GeoJSON document (UTF-8). Supports Geometry, Feature, and FeatureCollection
 * @property visible Whether the layer is visible. Default: true
 * @property zIndex Drawing order relative to other layers (higher draws on top)
 * @property isClickable If true, features from this layer can emit click events when supported
 * @property isGeodesic If true, lines/polygon edges are rendered as geodesics when supported
 * @property lineStringStyle Style for LineString features
 * @property polygonStyle Style for Polygon features
 * @property pointStyle Style for Point features
 */
public data class GeoJsonLayer(
    val geoJson: String,
    val visible: Boolean? = true,
    val zIndex: Float = 0f,
    val isClickable: Boolean? = false,
    val isGeodesic: Boolean? = false,
    val lineStringStyle: LineStringStyle? = null,
    val polygonStyle: PolygonStyle? = null,
    val pointStyle: PointStyle? = null,
)

internal const val DEFAULT_STROKE_COLOR = "#000000"
internal const val DEFAULT_FILL_COLOR = "#00FFFFFF"
internal const val DEFAULT_STROKE_WIDTH = 2f

/**
 * Represents a style of GeoJSON line.
 *
 * @property lineWidth Stroke width for LineString
 * @property lineColor Stroke color for LineString features
 * @property pattern Dash pattern for strokes. See StrokePatternItem for available pattern items
 *   (Android only)
 */
public data class LineStringStyle(
    val lineWidth: Float? = null,
    val lineColor: Color? = null,
    val pattern: List<StrokePatternItem>? = null,
)

/**
 * Represents a style of GeoJSON polygon.
 *
 * @property fillColor Fill color for polygon interiors
 * @property strokeColor Stroke color for polygon outlines
 * @property strokeWidth Stroke width for polygon outlines
 */
public data class PolygonStyle(
    val fillColor: Color? = null,
    val strokeColor: Color? = null,
    val strokeWidth: Float? = null,
)

/**
 * Represents a style of GeoJSON point.
 *
 * @property alpha Opacity for point symbols/markers in the range [0f, 1f]
 * @property isDraggable Whether point markers are draggable (when supported)
 * @property isFlat Whether the marker icon is flat against the map surface (when supported)
 * @property pointTitle Title used for marker info windows where supported
 * @property snippet Subtitle/description used for marker info windows
 * @property rotation Marker/icon rotation in degrees
 * @property infoWindowAnchorU Horizontal info window anchor relative to the marker (0..1)
 * @property infoWindowAnchorV Vertical info window anchor relative to the marker (0..1)
 * @property anchorU Horizontal marker icon anchor relative to the icon (0..1)
 * @property anchorV Vertical marker icon anchor relative to the icon (0..1)
 */
public data class PointStyle(
    val alpha: Float = 1f,
    val isDraggable: Boolean = false,
    val isFlat: Boolean = false,
    val pointTitle: String? = null,
    val snippet: String? = null,
    val rotation: Float = 0f,
    val infoWindowAnchorU: Float = 0f,
    val infoWindowAnchorV: Float = 0f,
    val anchorU: Float = 0f,
    val anchorV: Float = 0f,
)

/**
 * Cross‑platform description of a stroke pattern used to render dashed or dotted outlines for lines
 * and polygon borders.
 */
public sealed interface StrokePatternItem {
    public data object Dot : StrokePatternItem

    public data class Dash(val lengthPx: Float) : StrokePatternItem

    public data class Gap(val lengthPx: Float) : StrokePatternItem
}
