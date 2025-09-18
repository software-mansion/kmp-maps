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
actual fun Map(
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

    val appleMapsProperties = when (properties) {
        is AppleMapsProperties -> properties
        else -> AppleMapsProperties(
            mapType = properties.mapType,
            isMyLocationEnabled = properties.isMyLocationEnabled,
            isTrafficEnabled = properties.isTrafficEnabled,
            showsBuildings = properties.showsBuildings
        )
    }

    val appleMapsUISettings = when (uiSettings) {
        is AppleMapsUISettings -> uiSettings
        else -> AppleMapsUISettings(
            compassEnabled = uiSettings.compassEnabled,
            myLocationButtonEnabled = uiSettings.myLocationButtonEnabled,
            scrollGesturesEnabled = uiSettings.scrollGesturesEnabled,
            zoomGesturesEnabled = uiSettings.zoomGesturesEnabled,
            tiltGesturesEnabled = uiSettings.tiltGesturesEnabled,
            rotateGesturesEnabled = uiSettings.rotateGesturesEnabled
        )
    }

    val appleMapsMarkers = markers.map { marker ->
        when (marker) {
            is AppleMapsMarker -> marker
            else -> AppleMapsMarker(
                coordinates = marker.coordinates,
                title = marker.title
            )
        }
    }

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

            mkMapView.mapType = appleMapsProperties.mapType.toAppleMapsMapType().toMKMapType()
            mkMapView.showsUserLocation =
                appleMapsProperties.isMyLocationEnabled && locationPermissionHandler.hasPermission()
            mkMapView.showsTraffic = appleMapsProperties.isTrafficEnabled
            mkMapView.showsBuildings = appleMapsProperties.showsBuildings
            mkMapView.showsPointsOfInterest = true

            appleMapsProperties.pointsOfInterest?.let { poiCategories ->
                val poiFilter = poiCategories.toMKPointOfInterestFilter()
                mkMapView.pointOfInterestFilter = poiFilter
            }

            mkMapView.showsCompass = appleMapsUISettings.compassEnabled
            mkMapView.zoomEnabled = appleMapsUISettings.zoomGesturesEnabled
            mkMapView.scrollEnabled = appleMapsUISettings.scrollGesturesEnabled
            mkMapView.rotateEnabled = appleMapsUISettings.rotateGesturesEnabled
            mkMapView.pitchEnabled = appleMapsUISettings.tiltGesturesEnabled

            cameraPosition?.let { pos ->
                mkMapView.setRegion(pos.toMKCoordinateRegion(), animated = false)
            }

            mkMapView.delegate = SimpleMapDelegate(circleStyles)

            mkMapView.updateAppleMapsMarkers(appleMapsMarkers)
            mkMapView.updateAppleMapsCircles(circles, circleStyles)

            mapView = mkMapView
            view
        },
        modifier = modifier.fillMaxSize(),
        update = { view ->
            mapView?.let { mkMapView ->
                mkMapView.mapType = appleMapsProperties.mapType.toAppleMapsMapType().toMKMapType()
                mkMapView.showsUserLocation =
                    appleMapsProperties.isMyLocationEnabled && locationPermissionHandler.hasPermission()
                mkMapView.showsTraffic = appleMapsProperties.isTrafficEnabled
                mkMapView.showsBuildings = appleMapsProperties.showsBuildings

                appleMapsProperties.pointsOfInterest?.let { poiCategories ->
                    val poiFilter = poiCategories.toMKPointOfInterestFilter()
                    mkMapView.pointOfInterestFilter = poiFilter
                }

                mkMapView.showsCompass = appleMapsUISettings.compassEnabled
                mkMapView.zoomEnabled = appleMapsUISettings.zoomGesturesEnabled
                mkMapView.scrollEnabled = appleMapsUISettings.scrollGesturesEnabled
                mkMapView.rotateEnabled = appleMapsUISettings.rotateGesturesEnabled
                mkMapView.pitchEnabled = appleMapsUISettings.tiltGesturesEnabled

                mkMapView.delegate = SimpleMapDelegate(circleStyles)

                mkMapView.updateAppleMapsMarkers(appleMapsMarkers)
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
