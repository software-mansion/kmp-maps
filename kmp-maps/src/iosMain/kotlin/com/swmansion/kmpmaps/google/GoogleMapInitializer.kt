package com.swmansion.kmpmaps.google

import cocoapods.GoogleMaps.GMSServices
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSBundle

/** Handles initialization of Google Maps SDK for iOS. */
internal object GoogleMapsInitializer {
    private var isInitialized = false
    private var initializationAttempted = false

    /**
     * Initializes Google Maps if not already initialized.
     *
     * @return true if initialization was successful or already completed, false if failed
     */
    @OptIn(ExperimentalForeignApi::class)
    fun initializeIfNeeded(): Boolean {
        if (isInitialized) return true
        if (initializationAttempted) return false

        initializationAttempted = true

        val apiKey = findApiKey()
        if (apiKey != null && apiKey.isNotEmpty()) {
            GMSServices.provideAPIKey(apiKey)
            isInitialized = true
            return true
        }
        GMSServices.provideAPIKey("BAD API KEY")
        return false
    }

    /**
     * Attempts to find the Google Maps API key from Info.plist.
     *
     * @return API key if found, null otherwise
     */
    private fun findApiKey(): String? {
        val infoPlistKey =
            NSBundle.mainBundle.objectForInfoDictionaryKey("GoogleMapsAPIKey") as? String
        if (!infoPlistKey.isNullOrEmpty()) return infoPlistKey
        return null
    }

    /** Checks if Google Maps has been successfully initialized. */
    fun isInitialized(): Boolean = isInitialized
}
