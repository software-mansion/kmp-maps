package com.swmansion.kmpmaps.core

import java.io.BufferedReader

/**
 * Injects configuration data into the HTML and JavaScript templates for the WebView.
 *
 * This function performs template substitution, replacing placeholders with the provided API key,
 * initial camera coordinates, and map properties.
 *
 * @param apiKey The Google Maps API key to be used in the JS SDK script tag.
 * @param cameraPosition The camera position to be set in the map.
 * @param properties The configuration properties for the map.
 * @return A complete HTML string with embedded JavaScript, ready to be loaded into a WebView.
 */
internal fun loadHTMLContent(
    apiKey: String,
    cameraPosition: CameraPosition?,
    properties: MapProperties?,
): String {
    val html = readResource("web/google_map.html")
    val bounds = cameraPosition?.bounds
    val fitBoundsCall =
        if (bounds != null) {
            "map.fitBounds(new google.maps.LatLngBounds(" +
                "{lat: ${bounds.southwest.latitude}, lng: ${bounds.southwest.longitude}}, " +
                "{lat: ${bounds.northeast.latitude}, lng: ${bounds.northeast.longitude}}));"
        } else {
            ""
        }
    val js =
        readResource("web/google_map.js")
            .replace("{{INITIAL_MAP_ID}}", properties?.webMapProperties?.mapId ?: "DEMO_MAP_ID")
            .replace("{{INITIAL_COLOR_SCHEME}}", properties?.mapTheme?.name ?: MapTheme.SYSTEM.name)
            .replace("{{INITIAL_LAT}}", (cameraPosition?.coordinates?.latitude ?: 0f).toString())
            .replace("{{INITIAL_LNG}}", (cameraPosition?.coordinates?.longitude ?: 0f).toString())
            .replace("{{INITIAL_ZOOM}}", (cameraPosition?.zoom ?: 0f).toString())
            .replace("{{FIT_BOUNDS_CALL}}", fitBoundsCall)

    return html.replace("{{API_KEY}}", apiKey).replace("{{LOCAL_JS_CONTENT}}", js)
}

/**
 * Reads a text resource from the application assets or classpath. It attempts to locate the
 * resource using multiple class loader strategies to ensure compatibility across different platform
 * environments.
 *
 * @param path The relative path to the resource (e.g., "web/google_map.html").
 * @return The content of the resource as a string.
 */
public fun readResource(path: String): String {
    val stream =
        object {}.javaClass.getResourceAsStream("/$path")
            ?: object {}.javaClass.getResourceAsStream(path)
            ?: Thread.currentThread().contextClassLoader.getResourceAsStream(path)
            ?: throw IllegalArgumentException("Resource not found: $path")
    return stream.bufferedReader().use(BufferedReader::readText)
}
