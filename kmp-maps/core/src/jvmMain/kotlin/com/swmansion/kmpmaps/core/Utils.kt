package com.swmansion.kmpmaps.core

import com.multiplatform.webview.jsbridge.IJsMessageHandler
import com.multiplatform.webview.jsbridge.JsMessage
import com.multiplatform.webview.jsbridge.WebViewJsBridge
import com.multiplatform.webview.web.WebViewNavigator
import kotlinx.serialization.json.Json

internal fun loadHTMLContent(apiKey: String, cameraPosition: CameraPosition?, webMapProperties: WebMapProperties?): String {
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

internal fun registerMapEvents(
    jsBridge: WebViewJsBridge,
    markers: List<Marker>,
    onCameraMove: ((CameraPosition) -> Unit)?,
    onMarkerClick: ((Marker) -> Unit)?,
    onMapClick: ((Coordinates) -> Unit)?,
    onPOIClick: ((Coordinates) -> Unit)?,
    onMapLoaded: (() -> Unit)?,
) {
    jsBridge.registerHandler("onCameraMove") { params ->
        val position = Json.decodeFromString<CameraPosition>(params)
        onCameraMove?.invoke(position)
    }

    jsBridge.registerHandler("onMarkerClick") { params ->
        val markerId = params
        val clickedMarker = markers.find { marker -> marker.id == markerId }
        clickedMarker?.let { onMarkerClick?.invoke(it) }
    }

    jsBridge.registerHandler("onMapClick") { params ->
        val coords = Json.decodeFromString<Coordinates>(params)
        onMapClick?.invoke(coords)
    }

    jsBridge.registerHandler("onPOIClick") { params ->
        val coords = Json.decodeFromString<Coordinates>(params)
        onPOIClick?.invoke(coords)
    }

    jsBridge.registerHandler("onMapLoaded") { onMapLoaded?.invoke() }
}

private fun WebViewJsBridge.registerHandler(methodName: String, handler: (String) -> Unit) {
    this.register(
        object : IJsMessageHandler {
            override fun methodName() = methodName

            override fun handle(
                message: JsMessage,
                navigator: WebViewNavigator?,
                callback: (String) -> Unit,
            ) {
                try {
                    handler(message.params)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    )
}
