package com.swmansion.kmpmaps

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
public fun AppleMapsView(
    annotations: List<AppleMapsAnnotations> = emptyList(),
    cameraPosition: CameraPosition? = null,
    circles: List<AppleMapsCircle> = emptyList(),
    markers: List<AppleMapsMarker> = emptyList(),
    polygons: List<AppleMapsPolygon> = emptyList(),
    polylines: List<AppleMapsPolyline> = emptyList(),
    onCameraMove: ((CameraPosition) -> Unit)? = null,
    onCircleClick: ((AppleMapsCircle) -> Unit)? = null,
    onMapClick: ((Coordinates) -> Unit)? = null,
    onMarkerClick: ((AppleMapsMarker) -> Unit)? = null,
    onPolygonClick: ((AppleMapsPolygon) -> Unit)? = null,
    onPolylineClick: ((AppleMapsPolyline) -> Unit)? = null,
    properties: AppleMapsProperties = AppleMapsProperties(),
    uiSettings: AppleMapsUISettings = AppleMapsUISettings(),
    modifier: Modifier = Modifier,
) {
    var mapView by remember { mutableStateOf<MKMapView?>(null) }
    val locationPermissionHandler = remember { LocationPermissionHandler() }

    LaunchedEffect(properties.isMyLocationEnabled) {
        if (properties.isMyLocationEnabled) {
            if (!locationPermissionHandler.checkPermission()) {
                locationPermissionHandler.requestPermission()
            }
        }
    }

    UIKitView(
        factory = {
            val view = UIView()
            val mkMapView = MKMapView()

            view.addSubview(mkMapView)

            mkMapView.translatesAutoresizingMaskIntoConstraints = false
            mkMapView.setupMapConstraints(view)

            mkMapView.mapType = properties.mapType.toMKMapType()
            mkMapView.showsUserLocation =
                properties.isMyLocationEnabled && locationPermissionHandler.hasPermission()
            mkMapView.showsTraffic = properties.isTrafficEnabled
            mkMapView.showsBuildings = properties.showsBuildings
            mkMapView.showsPointsOfInterest = true

            mkMapView.showsCompass = uiSettings.compassEnabled
            mkMapView.zoomEnabled = uiSettings.zoomGesturesEnabled
            mkMapView.scrollEnabled = uiSettings.scrollGesturesEnabled
            mkMapView.rotateEnabled = uiSettings.rotateGesturesEnabled
            mkMapView.pitchEnabled = uiSettings.tiltGesturesEnabled

            cameraPosition?.let { pos ->
                mkMapView.setRegion(pos.toMKCoordinateRegion(), animated = false)
            }

            mkMapView.updateAppleMapsAnnotations(annotations)
            mkMapView.updateAppleMapsMarkers(markers)

            mapView = mkMapView
            view
        },
        modifier = modifier.fillMaxSize(),
        update = { view ->
            mapView?.let { mkMapView ->
                mkMapView.mapType = properties.mapType.toMKMapType()
                mkMapView.showsUserLocation =
                    properties.isMyLocationEnabled && locationPermissionHandler.hasPermission()
                mkMapView.showsTraffic = properties.isTrafficEnabled
                mkMapView.showsBuildings = properties.showsBuildings

                mkMapView.showsCompass = uiSettings.compassEnabled
                mkMapView.zoomEnabled = uiSettings.zoomGesturesEnabled
                mkMapView.scrollEnabled = uiSettings.scrollGesturesEnabled
                mkMapView.rotateEnabled = uiSettings.rotateGesturesEnabled
                mkMapView.pitchEnabled = uiSettings.tiltGesturesEnabled

                mkMapView.updateAppleMapsAnnotations(annotations)
                mkMapView.updateAppleMapsMarkers(markers)
            }
        },
        properties = UIKitInteropProperties(
            isInteractive = true,
            isNativeAccessibilityEnabled = true,
        ),
    )

    // Handle camera movement
    LaunchedEffect(mapView) {
        mapView?.let { mkMapView ->
            // Note: Camera movement detection would require implementing MKMapViewDelegate
            // This is a simplified version - in a full implementation, you'd need to
            // implement the delegate methods to detect camera changes
        }
    }
}
