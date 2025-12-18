package com.swmansion.kmpmaps.core

public object MapConfiguration {
    private var _googleMapsApiKey: String? = null

    public val googleMapsApiKey: String
        get() {
            _googleMapsApiKey?.let {
                return it
            }

            throw IllegalStateException(
                "Google Maps API Key not found. Provide it via KmpMaps.initialize(googleMapsApiKey = \"...\")"
            )
        }

    public fun initialize(googleMapsApiKey: String? = null) {
        if (googleMapsApiKey != null) {
            this._googleMapsApiKey = googleMapsApiKey
        }
    }
}
