package com.swmansion.kmpmaps.core

import androidx.compose.foundation.layout.Box
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

/** JVM implementation of the Map composable using Google Maps. */
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
    webCustomMarkerContent: Map<String, (Marker) -> String>,
) {
    var htmlContent by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val apiKey = MapConfiguration.googleMapsApiKey
        htmlContent = loadHTMLContent(apiKey, cameraPosition, properties.webMapProperties)
    }

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
                circles = circles,
                polygons = polygons,
                polylines = polylines,
                clusterSettings = clusterSettings,
                onCameraMove = onCameraMove,
                onMarkerClick = onMarkerClick,
                onCircleClick = onCircleClick,
                onPolygonClick = onPolygonClick,
                onPolylineClick = onPolylineClick,
                onMapClick = onMapClick,
                onPOIClick = onPOIClick,
                onMapLoaded = onMapLoaded,
            )
        }

        LaunchedEffect(markers, webCustomMarkerContent, clusterSettings.enabled, loadingState) {
            if (loadingState is LoadingState.Finished) {
                val json = markers.toJson(webCustomMarkerContent).toString()
                val hasCustomCluster = clusterSettings.webClusterContent != null
                navigator.evaluateJavaScript(
                    "updateMarkers($json, ${clusterSettings.enabled}, $hasCustomCluster)"
                )
            }
        }

        LaunchedEffect(circles, loadingState) {
            if (loadingState is LoadingState.Finished) {
                val json = circles.map(Circle::toJson).toJsonString()
                navigator.evaluateJavaScript("updateCircles($json)")
            }
        }

        LaunchedEffect(polygons, loadingState) {
            if (loadingState is LoadingState.Finished) {
                val json = polygons.map(Polygon::toJson).toJsonString()
                navigator.evaluateJavaScript("updatePolygons($json)")
            }
        }

        LaunchedEffect(polylines, loadingState) {
            if (loadingState is LoadingState.Finished) {
                val json = polylines.map(Polyline::toJson).toJsonString()
                navigator.evaluateJavaScript("updatePolylines($json)")
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

        LaunchedEffect(geoJsonLayers, clusterSettings.enabled, loadingState) {
            if (loadingState is LoadingState.Finished) {
                val json = geoJsonLayers.map(GeoJsonLayer::toJson).toJsonString()
                navigator.evaluateJavaScript(
                    "updateGeoJsonLayers($json, ${clusterSettings.enabled})"
                )
            }
        }

        WebView(
            modifier = modifier,
            state = state,
            navigator = navigator,
            webViewJsBridge = jsBridge,
            onCreated = { _ -> },
        )
    } else {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}
