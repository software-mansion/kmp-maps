package com.swmansion.kmpmaps.core

internal fun loadHTMLContent(
    apiKey: String,
    cameraPosition: CameraPosition?,
    webMapProperties: WebMapProperties?,
): String {
    val html = readResource("web/google_map.html")
    val js =
        readResource("web/google_map.js")
            .replace("{{INITIAL_MAP_ID}}", webMapProperties?.mapId ?: "DEMO_MAP_ID")
            .replace("{{INITIAL_LAT}}", cameraPosition?.coordinates?.latitude.toString())
            .replace("{{INITIAL_LNG}}", cameraPosition?.coordinates?.longitude.toString())
            .replace("{{INITIAL_ZOOM}}", cameraPosition?.zoom.toString())

    return html.replace("{{API_KEY}}", apiKey).replace("{{LOCAL_JS_CONTENT}}", js)
}

private fun readResource(path: String): String {
    val stream =
        object {}.javaClass.getResourceAsStream("/$path")
            ?: object {}.javaClass.getResourceAsStream(path)
            ?: Thread.currentThread().contextClassLoader.getResourceAsStream(path)
            ?: throw IllegalArgumentException("Resource not found: $path")
    return stream.bufferedReader().use { it.readText() }
}
