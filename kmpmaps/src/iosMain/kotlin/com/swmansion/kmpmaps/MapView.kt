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
import platform.MapKit.MKCircle
import platform.MapKit.MKCircleRenderer
import platform.MapKit.MKMapView
import platform.MapKit.MKMapViewDelegateProtocol
import platform.MapKit.MKOverlayProtocol
import platform.MapKit.MKOverlayRenderer
import platform.MapKit.MKPolygon
import platform.MapKit.MKPolygonRenderer
import platform.MapKit.MKPolyline
import platform.MapKit.MKPolylineRenderer
import platform.UIKit.UIColor
import platform.UIKit.UIView
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class)
public class SimpleMapDelegate : NSObject(), MKMapViewDelegateProtocol {
    override fun mapView(
        mapView: MKMapView,
        rendererForOverlay: MKOverlayProtocol
    ): MKOverlayRenderer {
        return when (rendererForOverlay) {
            is MKCircle -> {
                val renderer = MKCircleRenderer(rendererForOverlay)
                renderer.strokeColor = UIColor.redColor
                renderer.fillColor = UIColor.redColor.colorWithAlphaComponent(0.3)
                renderer.lineWidth = 3.0
                renderer
            }

            is MKPolygon -> MKPolygonRenderer(rendererForOverlay)
            is MKPolyline -> MKPolylineRenderer(rendererForOverlay)
            else -> MKCircleRenderer(rendererForOverlay)
        }
    }
}

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

            properties.pointsOfInterest?.let { poiCategories ->
                val poiFilter = poiCategories.toMKPointOfInterestFilter()
                mkMapView.pointOfInterestFilter = poiFilter
            }

            mkMapView.showsCompass = uiSettings.compassEnabled
            mkMapView.zoomEnabled = uiSettings.zoomGesturesEnabled
            mkMapView.scrollEnabled = uiSettings.scrollGesturesEnabled
            mkMapView.rotateEnabled = uiSettings.rotateGesturesEnabled
            mkMapView.pitchEnabled = uiSettings.tiltGesturesEnabled

            cameraPosition?.let { pos ->
                mkMapView.setRegion(pos.toMKCoordinateRegion(), animated = false)
            }

            mkMapView.delegate = SimpleMapDelegate()

            mkMapView.updateAppleMapsAnnotations(annotations)
            mkMapView.updateAppleMapsMarkers(markers)

            mkMapView.updateAppleMapsCircles(circles)

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

                properties.pointsOfInterest?.let { poiCategories ->
                    val poiFilter = poiCategories.toMKPointOfInterestFilter()
                    mkMapView.pointOfInterestFilter = poiFilter
                }

                mkMapView.showsCompass = uiSettings.compassEnabled
                mkMapView.zoomEnabled = uiSettings.zoomGesturesEnabled
                mkMapView.scrollEnabled = uiSettings.scrollGesturesEnabled
                mkMapView.rotateEnabled = uiSettings.rotateGesturesEnabled
                mkMapView.pitchEnabled = uiSettings.tiltGesturesEnabled

                mkMapView.delegate = SimpleMapDelegate()

                mkMapView.updateAppleMapsAnnotations(annotations)
                mkMapView.updateAppleMapsMarkers(markers)

                mkMapView.updateAppleMapsCircles(circles)
            }
        },
        properties = UIKitInteropProperties(
            isInteractive = true,
            isNativeAccessibilityEnabled = true,
        ),
    )

    LaunchedEffect(mapView) {
        mapView?.let { mkMapView ->
            // todo
        }
    }
}
