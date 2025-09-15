package com.swmansion.kmpmaps

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitInteropProperties
import androidx.compose.ui.viewinterop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import platform.MapKit.MKMapView
import platform.UIKit.UIView

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun Map(
    region: MapRegion?,
    mapType: MapType,
    annotations: List<MapAnnotation>,
    showUserLocation: Boolean,
    onRegionChange: (MapRegion) -> Unit,
    onAnnotationPress: (MapAnnotation) -> Unit,
    modifier: Modifier,
) {
    var mapView by remember { mutableStateOf<MKMapView?>(null) }

    UIKitView(
        factory = {
            val view = UIView()
            val mkMapView = MKMapView()

            view.addSubview(mkMapView)

            mkMapView.translatesAutoresizingMaskIntoConstraints = false
            mkMapView.setupMapConstraints(view)
            mkMapView.showsUserLocation = showUserLocation
            mkMapView.mapType = mapType.toMKMapType()

            region?.let { reg ->
                mkMapView.setRegion(reg.toMKCoordinateRegion(), animated = false)
            }

            mkMapView.updateAnnotations(annotations)
            mapView = mkMapView
            view
        },
        modifier = modifier,
        update = { view ->
            mapView?.let { mkMapView ->
                mkMapView.mapType = mapType.toMKMapType()
                mkMapView.showsUserLocation = showUserLocation
                mkMapView.updateAnnotations(annotations)
            }
        },
        properties =
            UIKitInteropProperties(isInteractive = true, isNativeAccessibilityEnabled = true),
    )
}
