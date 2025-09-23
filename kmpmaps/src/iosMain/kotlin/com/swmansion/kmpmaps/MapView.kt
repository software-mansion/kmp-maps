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
import platform.MapKit.MKMapView
import platform.UIKit.UIView

@OptIn(ExperimentalForeignApi::class)
@Composable
public actual fun Map(
    cameraPosition: CameraPosition?,
    properties: MapProperties,
    uiSettings: MapUISettings,
    markers: List<MapMarker>,
    circles: List<MapCircle>,
    polygons: List<MapPolygon>,
    polylines: List<MapPolyline>,
    onCameraMove: ((CameraPosition) -> Unit)?,
    onMarkerClick: ((MapMarker) -> Unit)?,
    onCircleClick: ((MapCircle) -> Unit)?,
    onPolygonClick: ((MapPolygon) -> Unit)?,
    onPolylineClick: ((MapPolyline) -> Unit)?,
    onMapClick: ((Coordinates) -> Unit)?,
    onMapLongClick: ((Coordinates) -> Unit)?,
    onPOIClick: ((Coordinates) -> Unit)?,
    onMapLoaded: (() -> Unit)?,
    modifier: Modifier,
) {
    var mapView by remember { mutableStateOf<MKMapView?>(null) }
    val locationPermissionHandler = remember { LocationPermissionHandler() }
    val circleStyles = remember { mutableMapOf<MKCircle, MapCircle>() }

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

            mkMapView.mapType = properties.mapType.toAppleMapsMapType()
            mkMapView.showsUserLocation =
                properties.isMyLocationEnabled && locationPermissionHandler.hasPermission()
            mkMapView.showsTraffic = properties.isTrafficEnabled
            mkMapView.showsBuildings = properties.isBuildingEnabled
            mkMapView.showsPointsOfInterest = properties.applePointsOfInterest != null
            properties.applePointsOfInterest?.let { poiCategories ->
                val poiFilter = poiCategories.toMKPointOfInterestFilter()
                mkMapView.pointOfInterestFilter = poiFilter
            }

            mkMapView.showsCompass = uiSettings.compassEnabled
            mkMapView.zoomEnabled = uiSettings.zoomEnabled
            mkMapView.scrollEnabled = uiSettings.scrollEnabled
            mkMapView.rotateEnabled = uiSettings.appleRotateGesturesEnabled
            mkMapView.pitchEnabled = uiSettings.togglePitchEnabled

            cameraPosition?.let { pos ->
                mkMapView.setRegion(pos.toMKCoordinateRegion(), animated = false)
            }

            mkMapView.delegate = SimpleMapDelegate(circleStyles)

            mkMapView.updateAppleMapsMarkers(markers)
            mkMapView.updateAppleMapsCircles(circles, circleStyles)

            mapView = mkMapView
            view
        },
        modifier = modifier.fillMaxSize(),
        update = { view ->
            mapView?.let { mkMapView ->
                mkMapView.mapType = properties.mapType.toAppleMapsMapType()
                mkMapView.showsUserLocation =
                    properties.isMyLocationEnabled && locationPermissionHandler.hasPermission()
                mkMapView.showsTraffic = properties.isTrafficEnabled
                mkMapView.showsBuildings = properties.isBuildingEnabled

                properties.applePointsOfInterest?.let { poiCategories ->
                    val poiFilter = poiCategories.toMKPointOfInterestFilter()
                    mkMapView.pointOfInterestFilter = poiFilter
                }

                mkMapView.showsCompass = uiSettings.compassEnabled
                mkMapView.zoomEnabled = uiSettings.zoomEnabled
                mkMapView.scrollEnabled = uiSettings.scrollEnabled
                mkMapView.rotateEnabled = uiSettings.appleRotateGesturesEnabled
                mkMapView.pitchEnabled = uiSettings.togglePitchEnabled
                mkMapView.delegate = SimpleMapDelegate(circleStyles)

                mkMapView.updateAppleMapsMarkers(markers)
                mkMapView.updateAppleMapsCircles(circles, circleStyles)
            }
        },
        properties = UIKitInteropProperties(
            isInteractive = true,
            isNativeAccessibilityEnabled = true,
        ),
    )

    LaunchedEffect(mapView) {
        mapView?.let { mkMapView ->
            onMapLoaded?.invoke()
        }
    }
}
