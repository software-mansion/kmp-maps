package com.swmansion.kmpmaps.applemaps

/**
 * Configuration object for the Apple Maps module.
 *
 * Before using the Apple Maps [Map] composable on Desktop (JVM), you must provide a valid
 * MapKit JS JWT token via [initialize]. This token is used to authenticate with Apple's
 * MapKit JS API in the embedded WebView.
 *
 * On Android and iOS, this configuration is not required as those platforms delegate to the
 * core module's native map implementations.
 *
 * Usage:
 * ```kotlin
 * AppleMapConfiguration.initialize(token = "your-mapkit-js-jwt-token")
 * ```
 *
 * @see <a href="https://developer.apple.com/documentation/mapkitjs/creating_a_maps_token">Creating a Maps Token</a>
 */
public object AppleMapConfiguration {
    private var _token: String? = null

    public val token: String
        get() =
            checkNotNull(_token) {
                "Apple MapKit JS token not found. Provide it via " +
                    "`AppleMapConfiguration.initialize(token = \"...\")`."
            }

    public fun initialize(token: String) {
        _token = token
    }
}
