package com.swmansion.kmpmaps

data class AppleMapsPointOfInterestCategories(
    val excluding: List<AppleMapPointOfInterestCategory>? = emptyList(),
    val including: List<AppleMapPointOfInterestCategory>? = emptyList(),
)

data class GoogleMapsAnchor(
    var x: Float,
    var y: Float
)

data class GoogleMapsMapStyleOptions(
    val json: String?
)
