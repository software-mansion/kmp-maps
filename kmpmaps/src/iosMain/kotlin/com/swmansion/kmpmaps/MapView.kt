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
public fun AppleMap(
    annotations: List<AppleMapsAnnotations> = emptyList(),
    cameraPosition: CameraPosition? = null,
    circles: List<AppleMapsCircle> = emptyList(),
    polygons: List<AppleMapsPolygon> = emptyList(),
    polylines: List<AppleMapsPolyline> = emptyList(),
    onCameraMove: ((CameraPosition) -> Unit)? = null,
    onMarkerClick: ((AppleMapsMarker) -> Unit)? = null,
    onCircleClick: ((AppleMapsCircle) -> Unit)? = null,
    onPolygonClick: ((AppleMapsPolygon) -> Unit)? = null,
    onPolylineClick: ((AppleMapsPolyline) -> Unit)? = null,
    onMapClick: ((Coordinates) -> Unit)? = null,
    onMapLongClick: ((Coordinates) -> Unit)? = null,
    onPOIClick: ((Coordinates) -> Unit)? = null,
    onMapLoaded: (() -> Unit)? = null,
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

            // Set initial properties
            mkMapView.mapType = properties.mapType.toMKMapType()
            mkMapView.showsUserLocation =
                properties.isMyLocationEnabled && locationPermissionHandler.hasPermission()
            mkMapView.showsTraffic = properties.isTrafficEnabled
            mkMapView.showsBuildings = properties.isBuildingsEnabled
            mkMapView.showsPointsOfInterest = true

            // Set UI settings
            mkMapView.showsCompass = uiSettings.compassEnabled
            mkMapView.showsUserLocation = uiSettings.myLocationButtonEnabled
            mkMapView.isZoomEnabled = uiSettings.zoomGesturesEnabled
            mkMapView.isScrollEnabled = uiSettings.scrollGesturesEnabled
            mkMapView.isRotateEnabled = uiSettings.rotateGesturesEnabled
            mkMapView.isPitchEnabled = uiSettings.tiltGesturesEnabled

            // Set initial camera position
            cameraPosition?.let { pos ->
                mkMapView.setRegion(pos.toMKCoordinateRegion(), animated = false)
            }

            // Add markers
            mkMapView.updateAppleMapsAnnotations(annotations)

            // Add circles, polygons, and polylines
            mkMapView.updateAppleMapsCircles(circles)
            mkMapView.updateAppleMapsPolygons(polygons)
            mkMapView.updateAppleMapsPolylines(polylines)

            mapView = mkMapView
            onMapLoaded?.invoke()
            view
        },
        modifier = modifier.fillMaxSize(),
        update = { view ->
            mapView?.let { mkMapView ->
                // Update properties
                mkMapView.mapType = properties.mapType.toMKMapType()
                mkMapView.showsUserLocation =
                    properties.isMyLocationEnabled && locationPermissionHandler.hasPermission()
                mkMapView.showsTraffic = properties.isTrafficEnabled
                mkMapView.showsBuildings = properties.isBuildingsEnabled

                // Update UI settings
                mkMapView.showsCompass = uiSettings.compassEnabled
                mkMapView.isZoomEnabled = uiSettings.zoomGesturesEnabled
                mkMapView.isScrollEnabled = uiSettings.scrollGesturesEnabled
                mkMapView.isRotateEnabled = uiSettings.rotateGesturesEnabled
                mkMapView.isPitchEnabled = uiSettings.tiltGesturesEnabled

                // Update circles, polygons, and polylines
                mkMapView.updateAppleMapsCircles(circles)
                mkMapView.updateAppleMapsPolygons(polygons)
                mkMapView.updateAppleMapsPolylines(polylines)
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
