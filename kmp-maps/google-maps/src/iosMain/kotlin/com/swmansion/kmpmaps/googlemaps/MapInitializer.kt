package com.swmansion.kmpmaps.googlemaps

import com.swmansion.kmpmaps.core.MapConfiguration
import swiftPMImport.com.swmansion.kmpmaps.kmp.maps.google.maps.GMSServices

/** Handles initialization of Google Maps SDK for iOS. */
internal object GoogleMapsInitializer {

    /**
     * Initializes Google Maps lazily. Returns true if initialization was successful, false if
     * failed.
     */
    private val initializationResult: Boolean by lazy {
        val apiKey = findApiKey()
        if (apiKey.isNotEmpty()) {
            GMSServices.provideAPIKey(apiKey)
            true
        } else {
            GMSServices.provideAPIKey("BAD API KEY")
            false
        }
    }

    /**
     * Initializes Google Maps if not already initialized.
     *
     * @return true if initialization was successful or already completed, throw error if failed
     */
    internal fun ensureInitialized(): Boolean {
        return initializationResult.takeIf { it } ?: throw Exception("Initialization failed")
    }

    /**
     * Attempts to find the Google Maps API key from [MapConfiguration]
     *
     * @return API key if found, null otherwise
     */
    private fun findApiKey() = MapConfiguration.googleMapsApiKey
}
