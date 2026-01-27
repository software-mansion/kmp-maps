package com.swmansion.kmpmaps.core

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.viewinterop.UIKitInteropProperties
import androidx.compose.ui.viewinterop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSSelectorFromString
import platform.MapKit.MKAnnotationProtocol
import platform.MapKit.MKCircle
import platform.MapKit.MKMapView
import platform.MapKit.MKOverlayProtocol
import platform.MapKit.MKPointAnnotation
import platform.MapKit.MKPolygon
import platform.MapKit.MKPolyline
import platform.UIKit.UILongPressGestureRecognizer
import platform.UIKit.UITapGestureRecognizer

/** iOS implementation of the Map composable using Apple Maps. */
@OptIn(ExperimentalForeignApi::class)
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
    webCustomMarkerContent: Map<String, (Marker) -> String>,
) {
    var mapView by remember { mutableStateOf<MKMapView?>(null) }
    var mapDelegate by remember { mutableStateOf<MapDelegate?>(null) }

    val locationPermissionHandler = remember { LocationPermissionHandler() }
    var hasLocationPermission by remember {
        mutableStateOf(locationPermissionHandler.checkPermission())
    }
    var renderedGeoJsonLayers by remember {
        mutableStateOf<Map<Int, MKGeoJsonRenderedLayer>>(emptyMap())
    }
    var geoJsonExtractedMarkers by remember { mutableStateOf<List<Marker>>(emptyList()) }
    val allMarkers =
        remember(markers, geoJsonExtractedMarkers) { markers + geoJsonExtractedMarkers }

    val circleStyles = remember { mutableMapOf<MKCircle, Circle>() }
    val polygonStyles = remember { mutableMapOf<MKPolygon, Polygon>() }
    val polylineStyles = remember { mutableMapOf<MKPolyline, Polyline>() }
    val markerMapping = remember { mutableMapOf<MKAnnotationProtocol, Marker>() }

    val geoJsonPolygonStyles = remember {
        mutableMapOf<MKOverlayProtocol, AppleMapsGeoJsonPolygonStyle>()
    }
    val geoJsonPolylineStyles = remember {
        mutableMapOf<MKOverlayProtocol, AppleMapsGeoJsonLineStyle>()
    }
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

    LaunchedEffect(uiSettings, properties, hasLocationPermission, isDarkModeEnabled) {
        val view = mapView ?: return@LaunchedEffect
        view.mapType = properties.mapType.toAppleMapsMapType()
        view.switchTheme(isDarkModeEnabled)
        view.showsUserLocation = properties.isMyLocationEnabled && hasLocationPermission
        view.showsTraffic = properties.isTrafficEnabled
        view.showsBuildings = properties.isBuildingEnabled
        view.showsPointsOfInterest = properties.iosMapProperties.showPOI

        properties.iosMapProperties.pointsOfInterest?.let { poiCategories ->
            val poiFilter = poiCategories.toMKPointOfInterestFilter()
            view.pointOfInterestFilter = poiFilter
        }

        view.showsCompass = uiSettings.compassEnabled
        view.zoomEnabled = uiSettings.zoomEnabled
        view.scrollEnabled = uiSettings.scrollEnabled
        view.rotateEnabled = uiSettings.rotateEnabled
        view.pitchEnabled = uiSettings.togglePitchEnabled
    }

    LaunchedEffect(cameraPosition) {
        val view = mapView ?: return@LaunchedEffect
        val newPos = cameraPosition ?: return@LaunchedEffect

        if (newPos != lastCameraPosition.value) {
            view.setRegion(newPos.toMKCoordinateRegion(), animated = false)
            lastCameraPosition.value = newPos
        }
    }

    LaunchedEffect(mapView, geoJsonLayers, clusterSettings.enabled) {
        val view = mapView ?: return@LaunchedEffect
        val result =
            view.updateRenderedGeoJsonLayers(
                geoJsonLayers = geoJsonLayers,
                currentRendered = renderedGeoJsonLayers,
                geoJsonPolygonStyles = geoJsonPolygonStyles,
                geoJsonPolylineStyles = geoJsonPolylineStyles,
                geoJsonPointStyles = geoJsonPointStyles,
                polylineStyles = polylineStyles,
                clusterSettings = clusterSettings,
            )
        renderedGeoJsonLayers = result.first
        geoJsonExtractedMarkers = result.second
    }

    LaunchedEffect(allMarkers, circles, polygons, polylines, mapDelegate) {
        val view = mapView ?: return@LaunchedEffect

        val activeIds = allMarkers.map { it.id }.toSet()
        mapDelegate?.pruneCache(activeIds)

        markerMapping.clear()
        markerMapping.putAll(view.updateAppleMapsMarkers(allMarkers))
        view.updateAppleMapsCircles(circles, circleStyles)
        view.updateAppleMapsPolygons(polygons, polygonStyles)
        view.updateAppleMapsPolylines(polylines, polylineStyles)
    }

    UIKitView(
        factory = {
            val mkMapView = MKMapView()

            cameraPosition?.let { pos ->
                mkMapView.setRegion(pos.toMKCoordinateRegion(), animated = false)
            }

            val delegate =
                MapDelegate(
                    mapView = mkMapView,
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
                    clusterSettings = clusterSettings,
                )
            mkMapView.delegate = delegate
            mapDelegate = delegate

            val tapGestureRecognizer = UITapGestureRecognizer()
            tapGestureRecognizer.addTarget(
                target = delegate,
                action = NSSelectorFromString("handleMapTap:"),
            )
            mkMapView.addGestureRecognizer(tapGestureRecognizer)

            val longPressGestureRecognizer = UILongPressGestureRecognizer()
            longPressGestureRecognizer.addTarget(
                target = delegate,
                action = NSSelectorFromString("handleMapLongPress:"),
            )
            mkMapView.addGestureRecognizer(longPressGestureRecognizer)

            mapView = mkMapView
            mkMapView
        },
        modifier = modifier.fillMaxSize(),
        update = { _ -> },
        properties =
            UIKitInteropProperties(isInteractive = true, isNativeAccessibilityEnabled = true),
    )
    Box(modifier = Modifier.alpha(0f)) {
        allMarkers.forEach { marker ->
            val content = customMarkerContent[marker.contentId]
            if (content != null && mapDelegate?.getCachedImage(marker.id) == null) {
                key(marker.id, marker.contentId) {
                    MarkerSnapshotter(
                        content = { content(marker) },
                        onSnapshotReady = { bitmap ->
                            val uiImage = bitmap.toUIImage()
                            if (uiImage != null) mapDelegate?.onBitmapReady(marker.id, uiImage)
                        },
                    )
                }
            }
        }
        mapDelegate?.clustersToRender?.forEach { (clusterId, cluster) ->
            if (mapDelegate?.getCachedImage(clusterId) == null) {
                key(clusterId) {
                    MarkerSnapshotter(
                        content = { clusterSettings.clusterContent?.invoke(cluster) },
                        onSnapshotReady = { bitmap ->
                            val uiImage = bitmap.toUIImage()
                            if (uiImage != null) mapDelegate?.onBitmapReady(clusterId, uiImage)
                        },
                    )
                }
            }
        }
    }

    LaunchedEffect(mapView) { mapView?.let { mkMapView -> onMapLoaded?.invoke() } }
}
