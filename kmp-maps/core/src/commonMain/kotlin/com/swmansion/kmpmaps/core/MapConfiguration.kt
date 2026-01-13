package com.swmansion.kmpmaps.core

public object MapConfiguration {
    private var _googleMapsApiKey: String? = null

    public val googleMapsApiKey: String
        get() =
            checkNotNull(_googleMapsApiKey) {
                "Google Maps API key not found. Provide it via " +
                    "`MapConfiguration.initialize(googleMapsApiKey = \"...\")`."
            }

    public fun initialize(googleMapsApiKey: String? = null) {
        _googleMapsApiKey = googleMapsApiKey
    }
}
