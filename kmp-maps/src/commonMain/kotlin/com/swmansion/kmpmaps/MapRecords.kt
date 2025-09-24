package com.swmansion.kmpmaps

public data class AppleMapsPointOfInterestCategories(
    val excluding: List<AppleMapPointOfInterestCategory>? = emptyList(),
    val including: List<AppleMapPointOfInterestCategory>? = emptyList(),
)

public data class GoogleMapsAnchor(var x: Float, var y: Float)

public data class GoogleMapsMapStyleOptions(val json: String?)
