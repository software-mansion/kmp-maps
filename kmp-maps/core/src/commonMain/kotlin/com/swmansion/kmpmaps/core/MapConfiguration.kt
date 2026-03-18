package com.swmansion.kmpmaps.core

/**
 * Global configuration holder for KMP Maps.
 *
 * Call [initialize] once — typically in your app entry point — before using any map composable.
 */
public object MapConfiguration {
    private var _googleMapsApiKey: String? = null

    /** The Google Maps API key set via [initialize]. Throws if [initialize] has not been called. */
    public val googleMapsApiKey: String
        get() =
            checkNotNull(_googleMapsApiKey) {
                "Google Maps API key not found. Provide it via " +
                    "`MapConfiguration.initialize(googleMapsApiKey = \"...\")`."
            }

    /** Stores the [googleMapsApiKey] required by Google Maps platforms. */
    public fun initialize(googleMapsApiKey: String) {
        _googleMapsApiKey = googleMapsApiKey
    }
}
