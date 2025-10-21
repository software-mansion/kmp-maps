package com.swmansion.kmpmaps.kmpgmaps

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
import cocoapods.GoogleMaps.GMSCameraPosition
import cocoapods.GoogleMaps.GMSCircle
import cocoapods.GoogleMaps.GMSMapView
import cocoapods.GoogleMaps.GMSMarker
import cocoapods.GoogleMaps.GMSPolygon
import cocoapods.GoogleMaps.GMSPolyline
import com.swmansion.kmpmaps.kmpmaps.CameraPosition
import com.swmansion.kmpmaps.kmpmaps.Circle
import com.swmansion.kmpmaps.kmpmaps.Coordinates
import com.swmansion.kmpmaps.kmpmaps.MapProperties
import com.swmansion.kmpmaps.kmpmaps.MapTheme
import com.swmansion.kmpmaps.kmpmaps.MapUISettings
import com.swmansion.kmpmaps.kmpmaps.Marker
import com.swmansion.kmpmaps.kmpmaps.Polygon
import com.swmansion.kmpmaps.kmpmaps.Polyline
import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreLocation.CLLocationCoordinate2DMake

@OptIn(ExperimentalForeignApi::class)
@Composable
public actual fun GMap(
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
) {
    var mapView by remember { mutableStateOf<GMSMapView?>(null) }
    var mapDelegate by remember { mutableStateOf<GMapDelegate?>(null) }

    val locationPermissionHandler = remember { LocationPermissionHandler() }
    var hasLocationPermission by remember {
        mutableStateOf(locationPermissionHandler.checkPermission())
    }

    val circleMapping = remember { mutableMapOf<GMSCircle, Circle>() }
    val polygonMapping = remember { mutableMapOf<GMSPolygon, Polygon>() }
    val polylineMapping = remember { mutableMapOf<GMSPolyline, Polyline>() }
    val markerMapping = remember { mutableMapOf<GMSMarker, Marker>() }

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

    UIKitView(
        factory = {
            val initialized = GoogleMapsInitializer.initializeIfNeeded()
            if (!initialized) {
                println(
                    "KMP Maps Warning: GoogleMaps not initialized! " +
                        "Please add GoogleMapsAPIKey to Info.plist."
                )
            }

            val gmsMapView = GMSMapView()

            gmsMapView.mapType = properties.mapType.toGoogleMapsMapType()

            gmsMapView.switchTheme(isDarkModeEnabled)

            gmsMapView.myLocationEnabled = properties.isMyLocationEnabled && hasLocationPermission
            gmsMapView.trafficEnabled = properties.isTrafficEnabled
            gmsMapView.buildingsEnabled = properties.isBuildingEnabled
            gmsMapView.indoorEnabled = properties.iosGmsIsIndoorEnabled
            gmsMapView.mapStyle = properties.iosGmsMapStyleOptions.toNativeStyleOptions()
            gmsMapView.setMinZoom(
                properties.iosGmsMinZoomPreference ?: 0f,
                properties.iosGmsMaxZoomPreference ?: 20f,
            )

            uiSettings.toGoogleMapsSettings(gmsMapView)

            cameraPosition?.let { pos ->
                val camera =
                    GMSCameraPosition.cameraWithTarget(
                        target =
                            CLLocationCoordinate2DMake(
                                pos.coordinates.latitude,
                                pos.coordinates.longitude,
                            ),
                        zoom = pos.zoom,
                        bearing = (pos.iosGmsBearing ?: 0f).toDouble(),
                        viewingAngle = (pos.iosGmsViewingAngle ?: 0f).toDouble(),
                    )
                gmsMapView.camera = camera
            }

            val delegate =
                GMapDelegate(
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
            gmsMapView.indoorEnabled = properties.iosGmsIsIndoorEnabled
            gmsMapView.mapStyle = properties.iosGmsMapStyleOptions.toNativeStyleOptions()
            gmsMapView.setMinZoom(
                properties.iosGmsMinZoomPreference ?: 0f,
                properties.iosGmsMaxZoomPreference ?: 20f,
            )

            uiSettings.toGoogleMapsSettings(gmsMapView)
            gmsMapView.delegate = mapDelegate

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
