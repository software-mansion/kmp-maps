package com.swmansion.kmpmaps.core

import androidx.compose.ui.graphics.Color

public data class WebMapProperties(
    val mapId: String? = null,
    val gestureHandling: WebMapGesture = WebMapGesture.AUTO,
    val disableDoubleClickZoom: Boolean = false,
    val keyboardShortcuts: Boolean = true,
    val minZoom: Float? = null,
    val maxZoom: Float? = null,
    val clickableIcons: Boolean = true,
    val restriction: WebMapRestriction? = null,
    val styles: GoogleMapsMapStyleOptions? = null,
    val backgroundColor: Color? = null,
)

public data class WebUISettings(
    val zoomControl: Boolean = true,
    val mapTypeControl: Boolean = false,
    val scaleControl: Boolean = false,
    val streetViewControl: Boolean = false,
    val rotateControl: Boolean = false,
    val fullscreenControl: Boolean = false,
    val disableDefaultUI: Boolean = false,
)

public data class WebMapRestriction(
    val north: Double,
    val south: Double,
    val east: Double,
    val west: Double,
    val strictBounds: Boolean = false,
)

public enum class WebMapGesture {
    COOPERATIVE,
    AUTO,
    GREEDY,
    NONE,
}
