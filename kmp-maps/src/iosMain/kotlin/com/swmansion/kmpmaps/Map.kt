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
import platform.MapKit.MKPointAnnotation
import platform.MapKit.MKPolygon
import platform.MapKit.MKPolyline
import platform.UIKit.UILongPressGestureRecognizer
import platform.UIKit.UITapGestureRecognizer
import platform.UIKit.UIView

@OptIn(ExperimentalForeignApi::class)
@Composable
public actual fun Map(
    modifier: Modifier,
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
) {
    var mapView by remember { mutableStateOf<MKMapView?>(null) }
    var mapDelegate by remember { mutableStateOf<MapDelegate?>(null) }
    var tapGesture by remember { mutableStateOf<UITapGestureRecognizer?>(null) }
    var longPressGesture by remember { mutableStateOf<UILongPressGestureRecognizer?>(null) }
    val locationPermissionHandler = remember { LocationPermissionHandler() }
    val circleStyles = remember { mutableMapOf<MKCircle, MapCircle>() }
    val polygonStyles = remember { mutableMapOf<MKPolygon, MapPolygon>() }
    val polylineStyles = remember { mutableMapOf<MKPolyline, MapPolyline>() }
    val markerMapping = remember { mutableMapOf<MKPointAnnotation, MapMarker>() }

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

            val delegate =
                MapDelegate(
                    circleStyles = circleStyles,
                    polygonStyles = polygonStyles,
                    polylineStyles = polylineStyles,
                    markerMapping = markerMapping,
                    onMarkerClick = onMarkerClick,
                    onCircleClick = onCircleClick,
                    onPolygonClick = onPolygonClick,
                    onPolylineClick = onPolylineClick,
                    onMapClick = onMapClick,
                    onMapLongClick = onMapLongClick,
                    onPOIClick = onPOIClick,
                    onCameraMove = onCameraMove,
                )
            mkMapView.delegate = delegate
            mapDelegate = delegate

            markerMapping.clear()
            markerMapping.putAll(mkMapView.updateAppleMapsMarkers(markers))
            mkMapView.updateAppleMapsCircles(circles, circleStyles)
            mkMapView.updateAppleMapsPolygons(polygons, polygonStyles)
            mkMapView.updateAppleMapsPolylines(polylines, polylineStyles)

            val tapGestureRecognizer = UITapGestureRecognizer()
            tapGestureRecognizer.addTarget(
                target = delegate as Any,
                action = platform.Foundation.NSSelectorFromString("handleMapTap:"),
            )
            mkMapView.addGestureRecognizer(tapGestureRecognizer)
            tapGesture = tapGestureRecognizer

            val longPressGestureRecognizer = UILongPressGestureRecognizer()
            longPressGestureRecognizer.addTarget(
                target = delegate as Any,
                action = platform.Foundation.NSSelectorFromString("handleMapLongPress:"),
            )
            mkMapView.addGestureRecognizer(longPressGestureRecognizer)
            longPressGesture = longPressGestureRecognizer

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

                mapDelegate?.updateCallbacks(
                    onMarkerClick = onMarkerClick,
                    onCircleClick = onCircleClick,
                    onPolygonClick = onPolygonClick,
                    onPolylineClick = onPolylineClick,
                    onMapClick = onMapClick,
                    onMapLongClick = onMapLongClick,
                    onPOIClick = onPOIClick,
                    onCameraMove = onCameraMove,
                )

                tapGesture?.let { gesture ->
                    mkMapView.removeGestureRecognizer(gesture)
                    gesture.addTarget(
                        mapDelegate as Any,
                        action = platform.Foundation.NSSelectorFromString("handleMapTap:"),
                    )
                    mkMapView.addGestureRecognizer(gesture)
                }

                longPressGesture?.let { gesture ->
                    mkMapView.removeGestureRecognizer(gesture)
                    gesture.addTarget(
                        mapDelegate as Any,
                        action = platform.Foundation.NSSelectorFromString("handleMapLongPress:"),
                    )
                    mkMapView.addGestureRecognizer(gesture)
                }

                markerMapping.clear()
                markerMapping.putAll(mkMapView.updateAppleMapsMarkers(markers))
                mkMapView.updateAppleMapsCircles(circles, circleStyles)
                mkMapView.updateAppleMapsPolygons(polygons, polygonStyles)
                mkMapView.updateAppleMapsPolylines(polylines, polylineStyles)
            }
        },
        properties =
            UIKitInteropProperties(isInteractive = true, isNativeAccessibilityEnabled = true),
    )

    LaunchedEffect(mapView) { mapView?.let { mkMapView -> onMapLoaded?.invoke() } }
}
