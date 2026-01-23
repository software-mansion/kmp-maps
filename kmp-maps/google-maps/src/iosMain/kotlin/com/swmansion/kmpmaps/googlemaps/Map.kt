package com.swmansion.kmpmaps.googlemaps

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import cocoapods.GoogleMaps.GMSCircle
import cocoapods.GoogleMaps.GMSPolygon
import cocoapods.GoogleMaps.GMSPolyline
import cocoapods.Google_Maps_iOS_Utils.GMSMapView as UtilsGMSMapView
import cocoapods.Google_Maps_iOS_Utils.GMSMarker
import cocoapods.Google_Maps_iOS_Utils.GMUClusterManager
import cocoapods.Google_Maps_iOS_Utils.GMUDefaultClusterRenderer
import com.swmansion.kmpmaps.core.CameraPosition
import com.swmansion.kmpmaps.core.Circle
import com.swmansion.kmpmaps.core.ClusterSettings
import com.swmansion.kmpmaps.core.Coordinates
import com.swmansion.kmpmaps.core.DefaultPin
import com.swmansion.kmpmaps.core.GeoJsonLayer
import com.swmansion.kmpmaps.core.MapProperties
import com.swmansion.kmpmaps.core.MapTheme
import com.swmansion.kmpmaps.core.MapUISettings
import com.swmansion.kmpmaps.core.Marker
import com.swmansion.kmpmaps.core.MarkerSnapshotter
import com.swmansion.kmpmaps.core.Polygon
import com.swmansion.kmpmaps.core.Polyline
import com.swmansion.kmpmaps.core.toUIImage
import com.swmansion.kmpmaps.googlemaps.GoogleMapsInitializer.ensureInitialized
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi

/** iOS implementation of the Map composable using Google Maps. */
@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
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
    var mapView by remember { mutableStateOf<UtilsGMSMapView?>(null) }
    var mapDelegate by remember { mutableStateOf<MapDelegate?>(null) }
    val geoJsonManager = remember { GeoJsonRendererManager() }
    var geoJsonExtractedMarkers by remember { mutableStateOf<List<Marker>>(emptyList()) }
    val allMarkers =
        remember(markers, geoJsonExtractedMarkers) { markers + geoJsonExtractedMarkers }

    val locationPermissionHandler = remember { LocationPermissionHandler() }
    var hasLocationPermission by remember {
        mutableStateOf(locationPermissionHandler.checkPermission())
    }

    val circleMapping = remember { mutableMapOf<GMSCircle, Circle>() }
    val polygonMapping = remember { mutableMapOf<GMSPolygon, Polygon>() }
    val polylineMapping = remember { mutableMapOf<GMSPolyline, Polyline>() }
    val markerMapping = remember { mutableMapOf<GMSMarker, Marker>() }

    var clusterManager by remember { mutableStateOf<GMUClusterManager?>(null) }
    var clusterRenderer by remember { mutableStateOf<GMUDefaultClusterRenderer?>(null) }
    val clusteringDelegate =
        remember(mapView, clusterSettings, onMarkerClick, customMarkerContent) {
            mapView?.let { map ->
                MarkerClusterManagerDelegate(
                    mapView = map,
                    mapDelegate = mapDelegate,
                    clusterSettings = clusterSettings,
                    onMarkerClick = onMarkerClick,
                    customMarkerContent = customMarkerContent,
                )
            }
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

    DisposableEffect(mapView) {
        mapView?.let(geoJsonManager::attach)
        onDispose(geoJsonManager::clear)
    }

    LaunchedEffect(mapView, geoJsonLayers) {
        if (mapView == null) return@LaunchedEffect
        geoJsonExtractedMarkers = geoJsonManager.render(geoJsonLayers, clusterSettings)
    }

    UIKitView(
        factory = {
            ensureInitialized()

            val utilsMapView = UtilsGMSMapView()

            val delegate =
                MapDelegate(
                    clusterManager = clusterManager,
                    customMarkerContent = customMarkerContent,
                    onCameraMove = onCameraMove,
                    onMarkerClick = onMarkerClick,
                    onCircleClick = onCircleClick,
                    onPolygonClick = onPolygonClick,
                    onPolylineClick = onPolylineClick,
                    onMapClick = onMapClick,
                    onMapLongClick = onMapLongClick,
                    onPOIClick = onPOIClick,
                    markerMapping = markerMapping,
                    circleMapping = circleMapping,
                    polygonMapping = polygonMapping,
                    polylineMapping = polylineMapping,
                )

            mapDelegate = delegate

            val clusteringComponents =
                initializeClustering(
                    mapView = utilsMapView,
                    mapDelegate = delegate,
                    clusteringDelegate = clusteringDelegate,
                )

            clusterManager = clusteringComponents.manager
            clusterRenderer = clusteringComponents.renderer

            utilsMapView.setMapType(properties.mapType.toGoogleMapsMapType())
            utilsMapView.switchTheme(isDarkModeEnabled)
            utilsMapView.setMyLocationEnabled(
                properties.isMyLocationEnabled && hasLocationPermission
            )
            utilsMapView.setTrafficEnabled(properties.isTrafficEnabled)
            utilsMapView.setBuildingsEnabled(properties.isBuildingEnabled)
            utilsMapView.setIndoorEnabled(properties.iosMapProperties.gmsIsIndoorEnabled)
            utilsMapView.setMapStyle(
                properties.iosMapProperties.gmsMapStyleOptions.toNativeStyleOptions()
            )
            utilsMapView.setMinZoom(
                properties.iosMapProperties.gmsMinZoomPreference ?: 0f,
                properties.iosMapProperties.gmsMaxZoomPreference ?: 20f,
            )

            uiSettings.toGoogleMapsSettings(utilsMapView)

            cameraPosition?.let { position -> utilsMapView.setUpGMSCameraPosition(position) }

            utilsMapView.setDelegate(delegate)

            mapView = utilsMapView
            utilsMapView
        },
        modifier = modifier.fillMaxSize(),
        update = { utilsMapView ->
            val manager = clusterManager
            val renderer = clusterRenderer

            utilsMapView.setMapType(properties.mapType.toGoogleMapsMapType())
            utilsMapView.switchTheme(isDarkModeEnabled)
            utilsMapView.setMyLocationEnabled(
                properties.isMyLocationEnabled && hasLocationPermission
            )
            utilsMapView.setTrafficEnabled(properties.isTrafficEnabled)
            utilsMapView.setBuildingsEnabled(properties.isBuildingEnabled)
            utilsMapView.setIndoorEnabled(properties.iosMapProperties.gmsIsIndoorEnabled)
            utilsMapView.setMapStyle(
                properties.iosMapProperties.gmsMapStyleOptions.toNativeStyleOptions()
            )
            utilsMapView.setMinZoom(
                properties.iosMapProperties.gmsMinZoomPreference ?: 0f,
                properties.iosMapProperties.gmsMaxZoomPreference ?: 20f,
            )

            uiSettings.toGoogleMapsSettings(utilsMapView)
            utilsMapView.setDelegate(mapDelegate)

            if (cameraPosition != lastCameraPosition.value) {
                cameraPosition?.let { position -> utilsMapView.setUpGMSCameraPosition(position) }
                lastCameraPosition.value = cameraPosition
            }

            if (
                manager != null &&
                    renderer != null &&
                    clusterSettings.enabled &&
                    clusteringDelegate != null
            ) {
                updateClusteringMarkers(
                    manager = manager,
                    renderer = renderer,
                    mapDelegate = mapDelegate,
                    clusteringDelegate = clusteringDelegate,
                    markers = allMarkers,
                    markerMapping = markerMapping,
                )
            } else {
                disableClusteringAndUpdateMarkers(
                    manager = manager,
                    mapView = utilsMapView,
                    mapDelegate = mapDelegate,
                    markers = allMarkers,
                    markerMapping = markerMapping,
                    customMarkerContent = customMarkerContent,
                )
            }
            updateGoogleMapsCircles(utilsMapView, circles, circleMapping)
            updateGoogleMapsPolygons(utilsMapView, polygons, polygonMapping)
            updateGoogleMapsPolylines(utilsMapView, polylines, polylineMapping)
        },
        properties =
            UIKitInteropProperties(isInteractive = true, isNativeAccessibilityEnabled = true),
    )

    Box(modifier = Modifier.alpha(0f)) {
        allMarkers.forEach { marker ->
            key(marker.id) {
                MarkerSnapshotter(
                    content = {
                        val content = customMarkerContent[marker.contentId]
                        if (content != null) content(marker) else DefaultPin(marker)
                    },
                    onSnapshotReady = { bitmap ->
                        bitmap.toUIImage()?.let { mapDelegate?.onBitmapReady(marker.id, it) }
                    },
                )
            }
        }
        mapDelegate?.renderingQueue?.forEach { (id, content) ->
            key(id) {
                MarkerSnapshotter(
                    content = content,
                    onSnapshotReady = { bitmap ->
                        bitmap.toUIImage()?.let { mapDelegate?.onBitmapReady(id, it) }
                    },
                )
            }
        }
    }

    LaunchedEffect(mapView) { if (mapView != null) onMapLoaded?.invoke() }
}
