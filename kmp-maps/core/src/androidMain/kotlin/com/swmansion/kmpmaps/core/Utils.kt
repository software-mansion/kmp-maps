package com.swmansion.kmpmaps.core

import kotlin.math.PI
import kotlin.math.ln
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

/**
 * Calculates the zoom level required to fit [bounds] within the given viewport using the Mercator
 * projection formula. Returns a value clamped to [0, 21].
 *
 * The zoom is computed independently for latitude and longitude, then the minimum of the two is
 * taken so that the entire bounds are visible. Latitude uses the Mercator (non-linear) projection,
 * longitude is linear.
 *
 * @param viewportWidthPx Viewport width in pixels
 * @param viewportHeightPx Viewport height in pixels
 * @param bounds Geographic bounds to fit
 * @return Zoom level in [0, 21] that fits the bounds within the viewport
 */
internal fun calculateZoomFromViewport(
    viewportWidthPx: Int,
    viewportHeightPx: Int,
    bounds: MapBounds,
): Float {
    val latFraction = (latRad(bounds.northeast.latitude) - latRad(bounds.southwest.latitude)) / PI
    val lngDiff = bounds.northeast.longitude - bounds.southwest.longitude
    val lngFraction = if (lngDiff < 0) (lngDiff + 360.0) / 360.0 else lngDiff / 360.0
    val latZoom =
        if (latFraction > 0.0) ln(viewportHeightPx / 256.0 / latFraction) / ln(2.0) else 21.0
    val lngZoom =
        if (lngFraction > 0.0) ln(viewportWidthPx / 256.0 / lngFraction) / ln(2.0) else 21.0
    return min(latZoom, lngZoom).toFloat().coerceIn(0f, 21f)
}

/**
 * Converts a latitude in decimal degrees to its Mercator projection radian value, clamped to the
 * valid Mercator range [-π/2, π/2].
 *
 * @param lat Latitude in decimal degrees
 * @return Mercator radian value
 */
private fun latRad(lat: Double): Double {
    val s = sin(lat * PI / 180.0)
    val r = ln((1.0 + s) / (1.0 - s)) / 2.0
    return max(min(r, PI), -PI) / 2.0
}
