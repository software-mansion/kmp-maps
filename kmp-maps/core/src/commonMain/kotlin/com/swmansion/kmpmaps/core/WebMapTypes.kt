package com.swmansion.kmpmaps.core

import androidx.compose.ui.graphics.Color

/**
 * Web-only map properties passed to the Google Maps JavaScript API.
 *
 * @property mapId Optional Map ID for cloud-based styling.
 * @property gestureHandling Controls how touch/scroll gestures interact with the map.
 * @property disableDoubleClickZoom Disables zoom on double-click when `true`.
 * @property keyboardShortcuts Enables keyboard shortcuts when `true`.
 * @property minZoom Minimum zoom level the user can zoom out to.
 * @property maxZoom Maximum zoom level the user can zoom in to.
 * @property clickableIcons Makes default POI icons clickable when `true`.
 * @property restriction Restricts the viewport to a lat/lng bounding box.
 * @property styles Custom map styles applied via the Google Maps JavaScript API.
 * @property backgroundColor Background color shown while tiles are loading.
 */
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

/**
 * Web-only UI control visibility settings for the Google Maps JavaScript API.
 *
 * @property zoomControl Shows the zoom in/out control when `true`.
 * @property mapTypeControl Shows the map type switcher control when `true`.
 * @property streetViewControl Shows the Street View control when `true`.
 * @property rotateControl Shows the rotate control when `true`.
 * @property disableDefaultUI Hides all default UI controls when `true`.
 * @property zoomControlPosition Position of the zoom control on the map.
 * @property mapTypeControlPosition Position of the map type control on the map.
 * @property streetViewControlPosition Position of the Street View control on the map.
 * @property rotateControlPosition Position of the rotate control on the map.
 */
public data class WebUISettings(
    val zoomControl: Boolean = true,
    val mapTypeControl: Boolean = false,
    val streetViewControl: Boolean = false,
    val rotateControl: Boolean = false,
    val disableDefaultUI: Boolean = false,
    val zoomControlPosition: WebControlPosition? = WebControlPosition.LEFT_TOP,
    val mapTypeControlPosition: WebControlPosition? = null,
    val streetViewControlPosition: WebControlPosition? = null,
    val rotateControlPosition: WebControlPosition? = null,
)

/**
 * Restricts the map viewport to the given lat/lng bounding box.
 *
 * @property north Northern boundary latitude.
 * @property south Southern boundary latitude.
 * @property east Eastern boundary longitude.
 * @property west Western boundary longitude.
 * @property strictBounds When `true`, prevents the user from panning outside the restriction.
 */
public data class WebMapRestriction(
    val north: Double,
    val south: Double,
    val east: Double,
    val west: Double,
    val strictBounds: Boolean = false,
)

/** Controls how the map handles touch/scroll gestures on web. */
public enum class WebMapGesture {
    COOPERATIVE,
    AUTO,
    GREEDY,
    NONE,
}

/** Position of a UI control on the web map. */
public enum class WebControlPosition {
    TOP_LEFT,
    TOP_CENTER,
    TOP_RIGHT,
    LEFT_TOP,
    LEFT_CENTER,
    LEFT_BOTTOM,
    RIGHT_TOP,
    RIGHT_CENTER,
    RIGHT_BOTTOM,
    BOTTOM_LEFT,
    BOTTOM_CENTER,
    BOTTOM_RIGHT,
}
