package com.swmansion.kmpmaps.core

import kotlinx.serialization.Serializable

/**
 * Configuration for Apple Maps points of interest categories.
 *
 * @property excluding List of POI categories to exclude from the map
 * @property including List of POI categories to include on the map (if specified, only these will
 *   be shown)
 */
public data class AppleMapsPointOfInterestCategories(
    val excluding: List<AppleMapsPointOfInterestCategory>? = emptyList(),
    val including: List<AppleMapsPointOfInterestCategory>? = emptyList(),
)

/**
 * Anchor point configuration for Google Maps markers.
 *
 * @property x The horizontal anchor point (0.0 = left edge, 1.0 = right edge)
 * @property y The vertical anchor point (0.0 = top edge, 1.0 = bottom edge)
 */
@Serializable public data class GoogleMapsAnchor(val x: Float, val y: Float)

/**
 * Custom map styling options for Google Maps.
 *
 * @property json The JSON string containing the map style configuration
 */
public data class GoogleMapsMapStyleOptions(val json: String?)
