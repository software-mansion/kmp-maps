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
import com.multiplatform.webview.jsbridge.rememberWebViewJsBridge
import com.multiplatform.webview.web.LoadingState
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.rememberWebViewNavigator
import com.multiplatform.webview.web.rememberWebViewStateWithHTMLData

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

    LaunchedEffect(Unit) { htmlContent = loadHTMLContent(apiKey, cameraPosition, properties.webMapProperties) }

    if (htmlContent != null) {
        val state = rememberWebViewStateWithHTMLData(data = htmlContent!!)
        val loadingState = state.loadingState
        state.webSettings.isJavaScriptEnabled = true

        val navigator = rememberWebViewNavigator()
        val jsBridge = rememberWebViewJsBridge(navigator)

        LaunchedEffect(jsBridge, markers) {
            registerMapEvents(
                jsBridge = jsBridge,
                markers = markers,
                onCameraMove = onCameraMove,
                onMarkerClick = onMarkerClick,
                onMapClick = onMapClick,
                onPOIClick = onPOIClick,
                onMapLoaded = onMapLoaded,
            )
        }

        LaunchedEffect(markers, clusterSettings.enabled, loadingState) {
            if (loadingState is LoadingState.Finished) {
                val markersJson = markers.map { it.toJson() }.toJsonString()
                navigator.evaluateJavaScript(
                    "updateMarkers('$markersJson', ${clusterSettings.enabled})"
                )
            }
        }

        LaunchedEffect(properties, loadingState) {
            if (loadingState is LoadingState.Finished) {
                val json = properties.toJson().toString()
                navigator.evaluateJavaScript("updateMapProperties($json)")
            }
        }

        LaunchedEffect(uiSettings, loadingState) {
            if (loadingState is LoadingState.Finished) {
                val json = uiSettings.toJson().toString()
                navigator.evaluateJavaScript("updateMapUISettings($json)")
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
