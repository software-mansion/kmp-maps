package com.swmansion.kmpmaps.core

import androidx.compose.ui.graphics.Color

/**
 * Represents a layer of GeoJSON data to be displayed on the map.
 *
 * @param geoJson Raw GeoJSON document (UTF-8). Supports Geometry, Feature, and FeatureCollection
 * @param visible Whether the layer is visible. Default: true
 * @param zIndex Drawing order relative to other layers (higher draws on top)
 * @param isClickable: If true, features from this layer can emit click events when supported
 * @param isGeodesic: If true, lines/polygon edges are rendered as geodesics when supported
 * @param lineStringStyle: Style for LineString features
 * @param polygonStyle: Style for Polygon features
 * @param pointStyle: Style for Point features
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

internal val DEFAULT_STROKE_COLOR = Color.Black
internal val DEFAULT_FILL_COLOR = Color.Transparent
internal const val DEFAULT_STROKE_WIDTH = 2f

/**
 * Represents a style of GeoJSON line.
 *
 * @param lineWidth: Stroke width for LineString
 * @param lineColor: Stroke color for LineString features
 * @param pattern: Dash pattern for strokes. See StrokePatternItem for available pattern items
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
 * @param fillColor: Fill color for polygon interiors
 * @param strokeColor: Stroke color for polygon outlines
 * @param strokeWidth: Stroke width for polygon outlines
 */
public data class PolygonStyle(
    val fillColor: Color? = null,
    val strokeColor: Color? = null,
    val strokeWidth: Float? = null,
)

/**
 * Represents a style of GeoJSON point.
 *
 * @param alpha: Opacity for point symbols/markers in the range [0f, 1f]
 * @param isDraggable: Whether point markers are draggable (when supported)
 * @param isFlat: Whether the marker icon is flat against the map surface (when supported)
 * @param pointTitle: Title used for marker info windows where supported
 * @param snippet: Subtitle/description used for marker info windows
 * @param rotation: Marker/icon rotation in degrees
 * @param infoWindowAnchorU Horizontal info window anchor relative to the marker (0..1)
 * @param infoWindowAnchorV Vertical info window anchor relative to the marker (0..1)
 * @param anchorU Horizontal marker icon anchor relative to the icon (0..1)
 * @param anchorV Vertical marker icon anchor relative to the icon (0..1)
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
