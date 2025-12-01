package com.swmansion.kmpmaps.googlemaps

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import com.swmansion.kmpmaps.core.CameraPosition
import com.swmansion.kmpmaps.core.Circle
import com.swmansion.kmpmaps.core.Coordinates
import com.swmansion.kmpmaps.core.GeoJsonLayer
import com.swmansion.kmpmaps.core.MapProperties
import com.swmansion.kmpmaps.core.MapUISettings
import com.swmansion.kmpmaps.core.Marker
import com.swmansion.kmpmaps.core.Polygon
import com.swmansion.kmpmaps.core.Polyline
import javafx.embed.swing.JFXPanel
import javafx.scene.Scene
import javafx.scene.web.WebEngine
import javafx.scene.web.WebView
import netscape.javascript.JSObject
import javax.swing.SwingUtilities
import java.util.concurrent.atomic.AtomicBoolean
import javafx.application.Platform
import javafx.scene.layout.StackPane

@Composable
public actual fun Map(
    modifier: Modifier,
    cameraPosition: CameraPosition?,
    properties: MapProperties,
    uiSettings: MapUISettings,
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
) {
    val engineRef = remember { mutableStateOf<WebEngine?>(null) }
    val javafxStarted = remember { AtomicBoolean(false) }
    val markersSnapshot = remember { mutableStateOf<List<Marker>>(emptyList()) }

    LaunchedEffect(markers) { markersSnapshot.value = markers }

    SwingPanel(
        modifier = modifier.fillMaxSize(),
        factory = {
            if (!javafxStarted.getAndSet(true)) {
                try { Platform.startup { } } catch (_: IllegalStateException) { }
            }

            val jfxPanel = JFXPanel()

            Platform.runLater {
                val webView = WebView()
                val engine = webView.engine
                engineRef.value = engine

                engine.userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36"

                val bridge = object {
                    @Suppress("unused")
                    fun onMapClick(lat: Double, lng: Double) {
                        SwingUtilities.invokeLater { onMapClick?.invoke(Coordinates(lat, lng)) }
                    }

                    @Suppress("unused")
                    fun onMapLoaded() {
                        SwingUtilities.invokeLater { onMapLoaded?.invoke() }
                    }

                    @Suppress("unused")
                    fun log(message: Any?) {
                        println("JS LOG: $message")
                    }
                }

                engine.loadWorker.stateProperty().addListener { _, _, newState ->
                    if (newState === javafx.concurrent.Worker.State.SUCCEEDED) {
                        val window = engine.executeScript("window") as JSObject
                        window.setMember("kotlinBridge", bridge)
                        engine.executeScript("console.error = (msg) => kotlinBridge.log('ERROR: ' + msg);")
                    } else if (newState === javafx.concurrent.Worker.State.FAILED) {
                        println("JS LOG: WebView load failed")
                        engine.loadWorker.exception?.printStackTrace()
                    }
                }

                val url = object {}.javaClass.getResource("/web/index.html")
                    ?: error("Cannot find /web/index.html resource in google-maps module")
                engine.load(url.toExternalForm())

                val stackPane = StackPane()
                stackPane.children.add(webView)
                val scene = Scene(stackPane)

                jfxPanel.scene = scene
            }

            jfxPanel
        },
        update = {
            val engine = engineRef.value
            if (engine != null) {
                cameraPosition?.let { cam ->
                    Platform.runLater {
                        val lat = cam.coordinates.latitude
                        val lng = cam.coordinates.longitude
                        val zoom = cam.zoom
                        try { engine.executeScript("setCamera($lat, $lng, $zoom);") } catch (_: Exception) { }
                    }
                }

                val snapshot = markersSnapshot.value
                if (snapshot.isNotEmpty()) {
                    Platform.runLater {
                        try {
                            engine.executeScript("clearMarkers && clearMarkers();")
                            snapshot.forEachIndexed { idx, m ->
                                val script = "addMarker('m$idx', ${m.coordinates.latitude}, ${m.coordinates.longitude});"
                                try { engine.executeScript(script) } catch (_: Exception) { }
                            }
                        } catch (_: Exception) { }
                    }
                }
            }
        },
    )
}
