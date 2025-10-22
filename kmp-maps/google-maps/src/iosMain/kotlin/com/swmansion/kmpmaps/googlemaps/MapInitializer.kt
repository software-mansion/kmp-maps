package com.swmansion.kmpmaps.googlemaps

import cocoapods.GoogleMaps.GMSServices
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSBundle

/** Handles initialization of Google Maps SDK for iOS. */
internal object GoogleMapsInitializer {

    /**
     * Initializes Google Maps lazily. Returns true if initialization was successful, false if
     * failed.
     */
    @OptIn(ExperimentalForeignApi::class)
    private val initializationResult: Boolean by lazy {
        val apiKey = findApiKey()
        if (apiKey != null && apiKey.isNotEmpty()) {
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
     * @return true if initialization was successful or already completed, false if failed
     */
    fun initializeIfNeeded(): Boolean = initializationResult

    /**
     * Attempts to find the Google Maps API key from Info.plist.
     *
     * @return API key if found, null otherwise
     */
    private fun findApiKey() =
        (NSBundle.mainBundle.objectForInfoDictionaryKey("GoogleMapsAPIKey") as? String)
            .takeUnless { key -> key.isNullOrEmpty() }

    /** Checks if Google Maps has been successfully initialized. */
    fun isInitialized(): Boolean = initializationResult
}
