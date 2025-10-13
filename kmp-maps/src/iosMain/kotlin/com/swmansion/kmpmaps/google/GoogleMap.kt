package com.swmansion.kmpmaps.google

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
import com.swmansion.kmpmaps.CameraPosition
import com.swmansion.kmpmaps.Circle
import com.swmansion.kmpmaps.Coordinates
import com.swmansion.kmpmaps.MapProperties
import com.swmansion.kmpmaps.MapTheme
import com.swmansion.kmpmaps.MapUISettings
import com.swmansion.kmpmaps.Marker
import com.swmansion.kmpmaps.Polygon
import com.swmansion.kmpmaps.Polyline
import com.swmansion.kmpmaps.switchTheme
import com.swmansion.kmpmaps.toGoogleMapsMapType
import com.swmansion.kmpmaps.updateGoogleMapsCircles
import com.swmansion.kmpmaps.updateGoogleMapsMarkers
import com.swmansion.kmpmaps.updateGoogleMapsPolygons
import com.swmansion.kmpmaps.updateGoogleMapsPolylines
import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(ExperimentalForeignApi::class)
@Composable
internal fun GoogleMap(
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

    UIKitView(
        factory = {
            val gmsMapView = GMSMapView()

            gmsMapView.mapType = properties.mapType.toGoogleMapsMapType()

            gmsMapView.switchTheme(isDarkModeEnabled)

            gmsMapView.trafficEnabled = properties.isTrafficEnabled
            gmsMapView.buildingsEnabled = properties.isBuildingEnabled

            gmsMapView.settings.compassButton = uiSettings.compassEnabled
            gmsMapView.settings.myLocationButton = uiSettings.myLocationButtonEnabled
            gmsMapView.settings.zoomGestures = uiSettings.zoomEnabled
            gmsMapView.settings.scrollGestures = uiSettings.scrollEnabled
            gmsMapView.settings.rotateGestures = uiSettings.iosRotateGesturesEnabled
            gmsMapView.settings.tiltGestures = uiSettings.togglePitchEnabled

            cameraPosition?.let { pos ->
                val camera =
                    GMSCameraPosition.cameraWithLatitude(
                        latitude = pos.coordinates.latitude,
                        longitude = pos.coordinates.longitude,
                        zoom = pos.zoom,
                    )
                gmsMapView.camera = camera
            }

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

            gmsMapView.trafficEnabled = properties.isTrafficEnabled
            gmsMapView.buildingsEnabled = properties.isBuildingEnabled

            gmsMapView.settings.compassButton = uiSettings.compassEnabled
            gmsMapView.settings.myLocationButton = uiSettings.myLocationButtonEnabled
            gmsMapView.settings.zoomGestures = uiSettings.zoomEnabled
            gmsMapView.settings.scrollGestures = uiSettings.scrollEnabled
            gmsMapView.settings.rotateGestures = uiSettings.iosRotateGesturesEnabled
            gmsMapView.settings.tiltGestures = uiSettings.togglePitchEnabled

            cameraPosition?.let { pos ->
                val camera =
                    GMSCameraPosition.cameraWithLatitude(
                        latitude = pos.coordinates.latitude,
                        longitude = pos.coordinates.longitude,
                        zoom = pos.zoom,
                    )
                gmsMapView.camera = camera
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
