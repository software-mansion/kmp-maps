package com.swmansion.kmpmaps

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import java.awt.Desktop

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
) {
    val htmlContent =
        remember(cameraPosition, properties, uiSettings, markers, circles, polygons, polylines) {
            generateGoogleMapsHTML(
                cameraPosition = cameraPosition,
                properties = properties,
                uiSettings = uiSettings,
                markers = markers,
                circles = circles,
                polygons = polygons,
                polylines = polylines,
            )
        }

    androidx.compose.foundation.layout.Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center,
    ) {
        androidx.compose.material3.Text(
            text = "Google Maps WebView\n",
            style = androidx.compose.material3.MaterialTheme.typography.bodyLarge,
        )
    }

    LaunchedEffect(htmlContent) {
        try {
            val tempFile = java.io.File.createTempFile("google_maps", ".html")
            tempFile.writeText(htmlContent)

            if (
                Desktop.isDesktopSupported() &&
                    Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)
            ) {
                Desktop.getDesktop().browse(tempFile.toURI())
            }
        } catch (_: Exception) {}
    }

    LaunchedEffect(Unit) { onMapLoaded?.invoke() }
}
