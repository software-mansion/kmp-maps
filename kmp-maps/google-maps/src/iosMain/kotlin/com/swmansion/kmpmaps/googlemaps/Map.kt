package com.swmansion.kmpmaps.googlemaps

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.UIKitInteropProperties
import androidx.compose.ui.viewinterop.UIKitView
import cocoapods.GoogleMaps.GMSCameraPosition
import cocoapods.GoogleMaps.GMSCircle
import cocoapods.GoogleMaps.GMSMapView
import cocoapods.GoogleMaps.GMSMarker
import cocoapods.GoogleMaps.GMSPolygon
import cocoapods.GoogleMaps.GMSPolyline
import cocoapods.Google_Maps_iOS_Utils.GMSMapView as UtilsGMSMapView
import cocoapods.Google_Maps_iOS_Utils.GMUFeature
import cocoapods.Google_Maps_iOS_Utils.GMUGeoJSONParser
import cocoapods.Google_Maps_iOS_Utils.GMUGeometryRenderer
import cocoapods.Google_Maps_iOS_Utils.GMUStyle
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
import platform.CoreGraphics.CGPointMake
import platform.CoreLocation.CLLocationCoordinate2DMake
import platform.Foundation.NSData
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create
import platform.Foundation.dataUsingEncoding
import platform.UIKit.UIColor

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
    geoJsonLayer: GeoJsonLayer?,
) {
    var mapView by remember { mutableStateOf<GMSMapView?>(null) }
    var mapDelegate by remember { mutableStateOf<MapDelegate?>(null) }

    val locationPermissionHandler = remember { LocationPermissionHandler() }
    var hasLocationPermission by remember {
        mutableStateOf(locationPermissionHandler.checkPermission())
    }

    val circleMapping = remember { mutableMapOf<GMSCircle, Circle>() }
    val polygonMapping = remember { mutableMapOf<GMSPolygon, Polygon>() }
    val polylineMapping = remember { mutableMapOf<GMSPolyline, Polyline>() }
    val markerMapping = remember { mutableMapOf<GMSMarker, Marker>() }
    var geoJsonRenderer by remember { mutableStateOf<GMUGeometryRenderer?>(null) }

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

    LaunchedEffect(mapView, geoJsonLayer) {
        geoJsonRenderer?.clear()
        geoJsonRenderer = null

        val view = mapView ?: return@LaunchedEffect
        val layer = geoJsonLayer ?: return@LaunchedEffect
        geoJsonRenderer = view.renderGeoJson(layer.geoJson)
    }

    LaunchedEffect(mapView, geoJsonLayer) {
        geoJsonRenderer?.clear()
        geoJsonRenderer = null

        val view: GMSMapView = mapView ?: return@LaunchedEffect
        val layer = geoJsonLayer ?: return@LaunchedEffect

        if (layer.visible != true) return@LaunchedEffect

        val data: NSData =
            NSString.create(string = layer.geoJson).dataUsingEncoding(NSUTF8StringEncoding)!!
        val parser = GMUGeoJSONParser(data = data)
        parser.parse()

        fun uiColor(c: Color?, fallback: UIColor): UIColor {
            if (c == null) return fallback
            return UIColor(
                red = c.red.toDouble(),
                green = c.green.toDouble(),
                blue = c.blue.toDouble(),
                alpha = c.alpha.toDouble(),
            )
        }

        val lineStrokeColor = uiColor(layer.lineColor, UIColor.magentaColor)
        val polygonStrokeColor = uiColor(layer.strokeColor, UIColor.magentaColor)
        val fillColor = uiColor(layer.fillColor, UIColor.clearColor)

        val lineWidth = (layer.lineWidth ?: 5f).toDouble()
        val strokeWidth = (layer.strokeWidth ?: 5f).toDouble()

        val anchorU = layer.anchorU.toDouble()
        val anchorV = layer.anchorV.toDouble()
        val rotation = layer.rotation.toDouble()
        val pointTitle = layer.pointTitle
        

        val lineStyle =
            GMUStyle(
                styleID = "line",
                strokeColor = lineStrokeColor,
                fillColor = UIColor.clearColor,
                width = lineWidth,
                scale = 1.0,
                heading = 0.0,
                anchor = CGPointMake(0.5, 1.0),
                iconUrl = null,
                title = null,
                hasFill = false,
                hasStroke = true,
            )

        val polygonStyle =
            GMUStyle(
                styleID = "polygon",
                strokeColor = polygonStrokeColor,
                fillColor = fillColor,
                width = strokeWidth,
                scale = 1.0,
                heading = 0.0,
                anchor = CGPointMake(0.5, 1.0),
                iconUrl = null,
                title = null,
                hasFill = layer.fillColor != null,
                hasStroke = true,
            )

        val pointStyle =
            GMUStyle(
                styleID = "point",
                strokeColor = UIColor.clearColor,
                fillColor = UIColor.clearColor,
                width = 1.0,
                scale = 1.0,
                heading = rotation,
                anchor = CGPointMake(anchorU, anchorV),
                iconUrl = null,
                title = pointTitle,
                hasFill = false,
                hasStroke = false,
            )

        parser.features.forEach { feature ->
            val f = feature as? GMUFeature ?: return@forEach
            val geom = f.geometry
            when (geom) {
                is cocoapods.Google_Maps_iOS_Utils.GMULineString -> f.style = lineStyle
                is cocoapods.Google_Maps_iOS_Utils.GMUPolygon -> f.style = polygonStyle
                is cocoapods.Google_Maps_iOS_Utils.GMUPoint-> f.style = pointStyle
                else -> f.style = polygonStyle
            }
        }

        val renderer =
            GMUGeometryRenderer(
                map = view as UtilsGMSMapView,
                geometries = parser.features,
                styles = listOf(lineStyle, polygonStyle, pointStyle),
            )
        renderer.render()
        geoJsonRenderer = renderer
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

            cameraPosition?.let { pos ->
                val camera =
                    GMSCameraPosition.cameraWithTarget(
                        target =
                            CLLocationCoordinate2DMake(
                                pos.coordinates.latitude,
                                pos.coordinates.longitude,
                            ),
                        zoom = pos.zoom,
                        bearing = (pos.iosCameraPosition?.gmsBearing ?: 0f).toDouble(),
                        viewingAngle = (pos.iosCameraPosition?.gmsViewingAngle ?: 0f).toDouble(),
                    )
                gmsMapView.camera = camera
            }

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
