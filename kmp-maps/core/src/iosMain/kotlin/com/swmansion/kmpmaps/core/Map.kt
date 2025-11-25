package com.swmansion.kmpmaps.core

import androidx.compose.foundation.isSystemInDarkTheme
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
import platform.Foundation.NSSelectorFromString
import platform.MapKit.MKCircle
import platform.MapKit.MKMapView
import platform.MapKit.MKPointAnnotation
import platform.MapKit.MKPolygon
import platform.MapKit.MKPolyline
import platform.UIKit.UILongPressGestureRecognizer
import platform.UIKit.UITapGestureRecognizer

/** iOS implementation of the Map composable using Apple Maps or Google Maps. */
@OptIn(ExperimentalForeignApi::class)
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
    customMarkerContent: Map<String, @Composable () -> Unit>,
) {
    var mapView by remember { mutableStateOf<MKMapView?>(null) }
    var mapDelegate by remember { mutableStateOf<MapDelegate?>(null) }

    var tapGesture by remember { mutableStateOf<UITapGestureRecognizer?>(null) }
    var longPressGesture by remember { mutableStateOf<UILongPressGestureRecognizer?>(null) }

    val locationPermissionHandler = remember { LocationPermissionHandler() }
    var hasLocationPermission by remember {
        mutableStateOf(locationPermissionHandler.checkPermission())
    }
    var renderedGeoJsonLayers by remember {
        mutableStateOf<Map<Int, MKGeoJsonRenderedLayer>>(emptyMap())
    }

    val circleStyles = remember { mutableMapOf<MKCircle, Circle>() }
    val polygonStyles = remember { mutableMapOf<MKPolygon, Polygon>() }
    val polylineStyles = remember { mutableMapOf<MKPolyline, Polyline>() }
    val markerMapping = remember { mutableMapOf<MKPointAnnotation, Marker>() }

    val geoJsonPolygonStyles = remember { mutableMapOf<MKPolygon, AppleMapsGeoJsonPolygonStyle>() }
    val geoJsonPolylineStyles = remember { mutableMapOf<MKPolyline, AppleMapsGeoJsonLineStyle>() }
    val geoJsonPointStyles = remember {
        mutableMapOf<MKPointAnnotation, AppleMapsGeoJsonPointStyle>()
    }

    val lastCameraPosition = remember { mutableStateOf(cameraPosition) }

    val isDarkModeEnabled =
        if (properties.mapTheme == MapTheme.SYSTEM) {
            isSystemInDarkTheme()
        } else {
            properties.mapTheme == MapTheme.DARK
        }

    LaunchedEffect(Unit) {
        locationPermissionHandler.setOnPermissionChanged {
            hasLocationPermission = locationPermissionHandler.checkPermission()
        }
    }

    LaunchedEffect(properties.isMyLocationEnabled) {
        if (properties.isMyLocationEnabled && !hasLocationPermission) {
            locationPermissionHandler.requestPermission()
        }
    }

    LaunchedEffect(mapView, geoJsonLayers) {
        val view = mapView ?: return@LaunchedEffect
        renderedGeoJsonLayers =
            view.updateRenderedGeoJsonLayers(
                geoJsonLayers = geoJsonLayers,
                currentRendered = renderedGeoJsonLayers,
                geoJsonPolygonStyles = geoJsonPolygonStyles,
                geoJsonPolylineStyles = geoJsonPolylineStyles,
                geoJsonPointStyles = geoJsonPointStyles,
                polylineStyles = polylineStyles,
            )
    }

    UIKitView(
        factory = {
            val mkMapView = MKMapView()

            mkMapView.mapType = properties.mapType.toAppleMapsMapType()

            mkMapView.switchTheme(isDarkModeEnabled)

            mkMapView.showsUserLocation = properties.isMyLocationEnabled && hasLocationPermission
            mkMapView.showsTraffic = properties.isTrafficEnabled
            mkMapView.showsBuildings = properties.isBuildingEnabled
            mkMapView.showsPointsOfInterest = properties.iosMapProperties.showPOI
            properties.iosMapProperties.pointsOfInterest?.let { poiCategories ->
                val poiFilter = poiCategories.toMKPointOfInterestFilter()
                mkMapView.pointOfInterestFilter = poiFilter
            }

            mkMapView.showsCompass = uiSettings.compassEnabled
            mkMapView.zoomEnabled = uiSettings.zoomEnabled
            mkMapView.scrollEnabled = uiSettings.scrollEnabled
            mkMapView.rotateEnabled = uiSettings.rotateEnabled
            mkMapView.pitchEnabled = uiSettings.togglePitchEnabled

            cameraPosition?.let { pos ->
                mkMapView.setRegion(pos.toMKCoordinateRegion(), animated = false)
            }

            val delegate =
                MapDelegate(
                    properties = properties,
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
                    geoJsonPolygonStyles = geoJsonPolygonStyles,
                    geoJsonPolylineStyles = geoJsonPolylineStyles,
                    geoJsonPointStyles = geoJsonPointStyles,
                    customMarkerContent = customMarkerContent,
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
                target = delegate,
                action = NSSelectorFromString("handleMapTap:"),
            )
            mkMapView.addGestureRecognizer(tapGestureRecognizer)
            tapGesture = tapGestureRecognizer

            val longPressGestureRecognizer = UILongPressGestureRecognizer()
            longPressGestureRecognizer.addTarget(
                target = delegate,
                action = NSSelectorFromString("handleMapLongPress:"),
            )
            mkMapView.addGestureRecognizer(longPressGestureRecognizer)
            longPressGesture = longPressGestureRecognizer

            mapView = mkMapView
            mkMapView
        },
        modifier = modifier.fillMaxSize(),
        update = { mkMapView ->
            mkMapView.mapType = properties.mapType.toAppleMapsMapType()

            mkMapView.switchTheme(isDarkModeEnabled)

            mkMapView.showsUserLocation = properties.isMyLocationEnabled && hasLocationPermission
            mkMapView.showsTraffic = properties.isTrafficEnabled
            mkMapView.showsBuildings = properties.isBuildingEnabled

            properties.iosMapProperties.pointsOfInterest?.let { poiCategories ->
                val poiFilter = poiCategories.toMKPointOfInterestFilter()
                mkMapView.pointOfInterestFilter = poiFilter
            }

            mkMapView.showsCompass = uiSettings.compassEnabled
            mkMapView.zoomEnabled = uiSettings.zoomEnabled
            mkMapView.scrollEnabled = uiSettings.scrollEnabled
            mkMapView.rotateEnabled = uiSettings.rotateEnabled
            mkMapView.pitchEnabled = uiSettings.togglePitchEnabled
            mkMapView.delegate = mapDelegate

            if (cameraPosition != lastCameraPosition.value) {
                cameraPosition?.let { pos ->
                    mkMapView.setRegion(pos.toMKCoordinateRegion(), animated = false)
                }
                lastCameraPosition.value = cameraPosition
            }

            tapGesture?.let { gesture ->
                mkMapView.removeGestureRecognizer(gesture)
                gesture.addTarget(
                    mapDelegate as Any,
                    action = NSSelectorFromString("handleMapTap:"),
                )
                mkMapView.addGestureRecognizer(gesture)
            }

            longPressGesture?.let { gesture ->
                mkMapView.removeGestureRecognizer(gesture)
                gesture.addTarget(
                    mapDelegate as Any,
                    action = NSSelectorFromString("handleMapLongPress:"),
                )
                mkMapView.addGestureRecognizer(gesture)
            }

            markerMapping.clear()
            markerMapping.putAll(mkMapView.updateAppleMapsMarkers(markers))
            mkMapView.updateAppleMapsCircles(circles, circleStyles)
            mkMapView.updateAppleMapsPolygons(polygons, polygonStyles)
            mkMapView.updateAppleMapsPolylines(polylines, polylineStyles)
        },
        properties =
            UIKitInteropProperties(isInteractive = true, isNativeAccessibilityEnabled = true),
    )

    LaunchedEffect(mapView) { mapView?.let { mkMapView -> onMapLoaded?.invoke() } }
}
