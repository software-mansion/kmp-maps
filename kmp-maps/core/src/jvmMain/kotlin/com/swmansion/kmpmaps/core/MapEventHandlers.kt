package com.swmansion.kmpmaps.core

import com.multiplatform.webview.jsbridge.IJsMessageHandler
import com.multiplatform.webview.jsbridge.JsMessage
import com.multiplatform.webview.jsbridge.WebViewJsBridge
import com.multiplatform.webview.web.WebViewNavigator
import java.io.BufferedReader
import kotlinx.serialization.json.Json

internal fun registerMapEvents(
    jsBridge: WebViewJsBridge,
    markers: List<Marker>,
    circles: List<Circle>,
    polygons: List<Polygon>,
    polylines: List<Polyline>,
    clusterSettings: ClusterSettings,
    onCameraMove: ((CameraPosition) -> Unit)?,
    onMarkerClick: ((Marker) -> Unit)?,
    onCircleClick: ((Circle) -> Unit)?,
    onPolygonClick: ((Polygon) -> Unit)?,
    onPolylineClick: ((Polyline) -> Unit)?,
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

    jsBridge.registerHandler("onCircleClick") { id ->
        circles.find { it.id == id }?.let { onCircleClick?.invoke(it) }
    }

    jsBridge.registerHandler("onPolygonClick") { id ->
        polygons.find { it.id == id }?.let { onPolygonClick?.invoke(it) }
    }

    jsBridge.registerHandler("onPolylineClick") { id ->
        polylines.find { it.id == id }?.let { onPolylineClick?.invoke(it) }
    }

    jsBridge.registerHandler("onClusterClick") { params ->
        val cluster = Json.decodeFromString<Cluster>(params)
        clusterSettings.onClusterClick?.invoke(cluster)
    }
}

private fun WebViewJsBridge.registerHandler(methodName: String, handler: (String) -> Unit) {
    register(
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
