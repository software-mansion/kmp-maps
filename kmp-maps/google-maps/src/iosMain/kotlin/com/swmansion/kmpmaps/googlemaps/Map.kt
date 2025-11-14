package com.swmansion.kmpmaps.googlemaps

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitInteropProperties
import androidx.compose.ui.viewinterop.UIKitView
import cocoapods.GoogleMaps.GMSCircle
import cocoapods.GoogleMaps.GMSMapView
import cocoapods.GoogleMaps.GMSMarker
import cocoapods.GoogleMaps.GMSPolygon
import cocoapods.GoogleMaps.GMSPolyline
import com.swmansion.kmpmaps.core.CameraPosition
import com.swmansion.kmpmaps.core.Circle
import com.swmansion.kmpmaps.core.Coordinates
import com.swmansion.kmpmaps.core.GeoJsonLayer
import com.swmansion.kmpmaps.core.MapProperties
import com.swmansion.kmpmaps.core.MapTheme
import com.swmansion.kmpmaps.core.MapUISettings
import com.swmansion.kmpmaps.core.Marker
import com.swmansion.kmpmaps.core.Polygon
import com.swmansion.kmpmaps.core.Polyline
import com.swmansion.kmpmaps.googlemaps.GoogleMapsInitializer.ensureInitialized
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
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
) {
    var mapView by remember { mutableStateOf<GMSMapView?>(null) }
    var mapDelegate by remember { mutableStateOf<MapDelegate?>(null) }
    val geoJsonManager = remember { GeoJsonRendererManager() }

    val locationPermissionHandler = remember { LocationPermissionHandler() }
    var hasLocationPermission by remember {
        mutableStateOf(locationPermissionHandler.checkPermission())
    }

    val circleMapping = remember { mutableMapOf<GMSCircle, Circle>() }
    val polygonMapping = remember { mutableMapOf<GMSPolygon, Polygon>() }
    val polylineMapping = remember { mutableMapOf<GMSPolyline, Polyline>() }
    val markerMapping = remember { mutableMapOf<GMSMarker, Marker>() }

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
        geoJsonManager.render(geoJsonLayers)
    }

    UIKitView(
        factory = {
            ensureInitialized()

            val gmsMapView = GMSMapView()

            gmsMapView.mapType = properties.mapType.toGoogleMapsMapType()

            gmsMapView.switchTheme(isDarkModeEnabled)

            gmsMapView.myLocationEnabled = properties.isMyLocationEnabled && hasLocationPermission
            gmsMapView.trafficEnabled = properties.isTrafficEnabled
            gmsMapView.buildingsEnabled = properties.isBuildingEnabled
            gmsMapView.indoorEnabled = properties.iosMapProperties.gmsIsIndoorEnabled
            gmsMapView.mapStyle =
                properties.iosMapProperties.gmsMapStyleOptions.toNativeStyleOptions()
            gmsMapView.setMinZoom(
                properties.iosMapProperties.gmsMinZoomPreference ?: 0f,
                properties.iosMapProperties.gmsMaxZoomPreference ?: 20f,
            )

            uiSettings.toGoogleMapsSettings(gmsMapView)

            cameraPosition?.let { position -> gmsMapView.setUpGMSCameraPosition(position) }

            val delegate =
                MapDelegate(
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

            gmsMapView.delegate = delegate
            mapDelegate = delegate

            updateGoogleMapsMarkers(gmsMapView, markers, markerMapping)
            updateGoogleMapsCircles(gmsMapView, circles, circleMapping)
            updateGoogleMapsPolygons(gmsMapView, polygons, polygonMapping)
            updateGoogleMapsPolylines(gmsMapView, polylines, polylineMapping)

            mapView = gmsMapView
            gmsMapView
        },
        modifier = modifier.fillMaxSize(),
        update = { gmsMapView ->
            gmsMapView.mapType = properties.mapType.toGoogleMapsMapType()

            gmsMapView.switchTheme(isDarkModeEnabled)

            gmsMapView.myLocationEnabled = properties.isMyLocationEnabled && hasLocationPermission
            gmsMapView.trafficEnabled = properties.isTrafficEnabled
            gmsMapView.buildingsEnabled = properties.isBuildingEnabled
            gmsMapView.indoorEnabled = properties.iosMapProperties.gmsIsIndoorEnabled
            gmsMapView.mapStyle =
                properties.iosMapProperties.gmsMapStyleOptions.toNativeStyleOptions()
            gmsMapView.setMinZoom(
                properties.iosMapProperties.gmsMinZoomPreference ?: 0f,
                properties.iosMapProperties.gmsMaxZoomPreference ?: 20f,
            )

            uiSettings.toGoogleMapsSettings(gmsMapView)
            gmsMapView.delegate = mapDelegate

            if (cameraPosition != lastCameraPosition.value) {
                cameraPosition?.let { position -> gmsMapView.setUpGMSCameraPosition(position) }
                lastCameraPosition.value = cameraPosition
            }

            updateGoogleMapsMarkers(gmsMapView, markers, markerMapping)
            updateGoogleMapsCircles(gmsMapView, circles, circleMapping)
            updateGoogleMapsPolygons(gmsMapView, polygons, polygonMapping)
            updateGoogleMapsPolylines(gmsMapView, polylines, polylineMapping)
        },
        properties =
            UIKitInteropProperties(isInteractive = true, isNativeAccessibilityEnabled = true),
    )

    LaunchedEffect(mapView) { mapView?.let { gmsMapView -> onMapLoaded?.invoke() } }
}
