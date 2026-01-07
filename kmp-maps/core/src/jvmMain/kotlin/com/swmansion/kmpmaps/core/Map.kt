package com.swmansion.kmpmaps.core

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.multiplatform.webview.jsbridge.IJsMessageHandler
import com.multiplatform.webview.jsbridge.JsMessage
import com.multiplatform.webview.jsbridge.rememberWebViewJsBridge
import com.multiplatform.webview.web.LoadingState
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.WebViewNavigator
import com.multiplatform.webview.web.rememberWebViewNavigator
import com.multiplatform.webview.web.rememberWebViewStateWithHTMLData
import kotlinx.serialization.json.Json

@Composable
public actual fun Map(
    modifier: Modifier,
    cameraPosition: CameraPosition?,
    properties: MapProperties,
    uiSettings: MapUISettings,
    clusterSettings: ClusterSettings,
    markers: List<Marker>,
    circles: List<Circle>,
    polygons: List<Polygon>,
    polylines: List<Polyline>,
    onCameraMove: ((CameraPosition) -> Unit)?,
    onMarkerClick: ((Marker) -> Unit)?,
    onCircleClick: ((Circle) -> Unit)?,
    onPolygonClick: ((Polygon) -> Unit)?,
    onPolylineClick: ((Polyline) -> Unit)?,
    onMapClick: ((Coordinates) -> Unit)?,
    onMapLongClick: ((Coordinates) -> Unit)?,
    onPOIClick: ((Coordinates) -> Unit)?,
    onMapLoaded: (() -> Unit)?,
    geoJsonLayers: List<GeoJsonLayer>,
    customMarkerContent: Map<String, @Composable (Marker) -> Unit>,
) {
    var htmlContent by remember { mutableStateOf<String?>(null) }
    val apiKey = remember { MapConfiguration.googleMapsApiKey }

    LaunchedEffect(Unit) { htmlContent = loadHTMLContent(apiKey, cameraPosition) }

    if (htmlContent != null) {
        val state = rememberWebViewStateWithHTMLData(data = htmlContent!!)
        val loadingState = state.loadingState
        state.webSettings.isJavaScriptEnabled = true

        val navigator = rememberWebViewNavigator()
        val jsBridge = rememberWebViewJsBridge(navigator)

        LaunchedEffect(jsBridge) {
            jsBridge.register(
                object : IJsMessageHandler {
                    override fun methodName() = "onMapClick"

                    override fun handle(
                        message: JsMessage,
                        navigator: WebViewNavigator?,
                        callback: (String) -> Unit,
                    ) {
                        try {
                            val coords = Json.decodeFromString<Coordinates>(message.params)
                            onMapClick?.invoke(Coordinates(coords.latitude, coords.longitude))
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            )
            jsBridge.register(
                object : IJsMessageHandler {
                    override fun methodName() = "onMarkerClick"

                    override fun handle(
                        message: JsMessage,
                        navigator: WebViewNavigator?,
                        callback: (String) -> Unit,
                    ) {
                        try {
                            val markerId = message.params
                            val clickedMarker = markers.find { marker -> marker.id == markerId }
                            clickedMarker?.let { onMarkerClick?.invoke(it) }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            )
            jsBridge.register(
                object : IJsMessageHandler {
                    override fun methodName() = "onCameraMove"

                    override fun handle(
                        message: JsMessage,
                        navigator: WebViewNavigator?,
                        callback: (String) -> Unit,
                    ) {
                        try {
                            val currentCameraPosition =
                                Json.decodeFromString<CameraPosition>(message.params)
                            onCameraMove?.invoke(currentCameraPosition)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            )
        }

        LaunchedEffect(markers, clusterSettings.enabled, loadingState) {
            val json = markers.map { it.toJson() }.toJsonString()

            if (loadingState is LoadingState.Finished) {
                navigator.evaluateJavaScript("updateMarkers('$json', ${clusterSettings.enabled})")
            }
        }

        WebView(
            modifier = Modifier.fillMaxSize(),
            state = state,
            navigator = navigator,
            webViewJsBridge = jsBridge,
            onCreated = { _ -> },
        )
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}
