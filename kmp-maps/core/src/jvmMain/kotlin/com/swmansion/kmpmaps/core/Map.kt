package com.swmansion.kmpmaps.core

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
    val htmlContent =
        """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="utf-8" />
                <style>html, body, #map { height: 100%; margin: 0; padding: 0; }</style>
            </head>
            <body>
                <div id="map"></div>
                <script>
                    function initMap() {
                        try {
                            new google.maps.Map(document.getElementById("map"), {
                                center: { lat: 52.2297, lng: 21.0122 },
                                zoom: 12,
                                renderingType: google.maps.RenderingType.RASTER,
                                mapTypeId: google.maps.MapTypeId.ROADMAP
                            });
                        } catch (e) {
                            document.body.innerHTML = "ERR: " + e;
                        }
                    }
                </script>
                <script 
                    src="https://maps.googleapis.com/maps/api/js?key=API_KEY&callback=initMap&loading=async" 
                    async defer>
                </script>
            </body>
            </html>
        """
            .trimIndent()

    val state = rememberWebViewStateWithHTMLData(data = htmlContent, baseUrl = "https://localhost/")

    val navigator = rememberWebViewNavigator()

    MapEngineGuard {
        WebView(state = state, modifier = modifier, navigator = navigator, onCreated = { _ -> })
    }
}
