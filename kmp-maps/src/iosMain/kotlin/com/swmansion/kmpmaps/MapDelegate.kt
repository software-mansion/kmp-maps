package com.swmansion.kmpmaps

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCAction
import kotlinx.cinterop.useContents
import platform.MapKit.MKCircle
import platform.MapKit.MKCircleRenderer
import platform.MapKit.MKMapView
import platform.MapKit.MKMapViewDelegateProtocol
import platform.MapKit.MKOverlayProtocol
import platform.MapKit.MKPointAnnotation
import platform.MapKit.MKPolygon
import platform.MapKit.MKPolygonRenderer
import platform.MapKit.MKPolyline
import platform.MapKit.MKPolylineRenderer
import platform.UIKit.UIGestureRecognizerStateBegan
import platform.UIKit.UILongPressGestureRecognizer
import platform.UIKit.UITapGestureRecognizer
import platform.darwin.NSObject

/** iOS map delegate for handling Apple Maps interactions and rendering. */
@OptIn(ExperimentalForeignApi::class)
internal class MapDelegate(
    private val properties: MapProperties,
    private val circleStyles: MutableMap<MKCircle, Circle>,
    private val polygonStyles: MutableMap<MKPolygon, Polygon>,
    private val polylineStyles: MutableMap<MKPolyline, Polyline>,
    private val markerMapping: MutableMap<MKPointAnnotation, Marker>,
    private var onMarkerClick: ((Marker) -> Unit)?,
    private var onCircleClick: ((Circle) -> Unit)?,
    private var onPolygonClick: ((Polygon) -> Unit)?,
    private var onPolylineClick: ((Polyline) -> Unit)?,
    private var onMapClick: ((Coordinates) -> Unit)?,
    private var onMapLongClick: ((Coordinates) -> Unit)?,
    private var onPOIClick: ((Coordinates) -> Unit)?,
    private var onCameraMove: ((CameraPosition) -> Unit)?,
) : NSObject(), MKMapViewDelegateProtocol {

    /**
     * Provides renderers for map overlays (circles, polygons, polylines).
     *
     * @param mapView The map view requesting the renderer
     * @param rendererForOverlay The overlay that needs a renderer
     * @return Appropriate renderer with styling applied
     */
    override fun mapView(mapView: MKMapView, rendererForOverlay: MKOverlayProtocol) =
        when (rendererForOverlay) {
            is MKCircle -> {
                val circleStyle = circleStyles[rendererForOverlay]
                val renderer = MKCircleRenderer(rendererForOverlay)
                renderer.strokeColor = circleStyle?.lineColor?.toAppleMapsColor()
                renderer.lineWidth = (circleStyle?.lineWidth ?: 1).toDouble()
                renderer.fillColor = circleStyle?.color?.toAppleMapsColor()
                renderer
            }
            is MKPolygon -> {
                val polygonStyle = polygonStyles[rendererForOverlay]
                val renderer = MKPolygonRenderer(rendererForOverlay)
                renderer.strokeColor = polygonStyle?.lineColor?.toAppleMapsColor()
                renderer.lineWidth = (polygonStyle?.lineWidth ?: 1).toDouble()
                renderer.fillColor = polygonStyle?.color?.toAppleMapsColor()
                renderer
            }
            is MKPolyline -> {
                val polylineStyle = polylineStyles[rendererForOverlay]
                val renderer = MKPolylineRenderer(rendererForOverlay)
                renderer.strokeColor = polylineStyle?.lineColor?.toAppleMapsColor()
                renderer.lineWidth = (polylineStyle?.width ?: 1).toDouble()
                renderer
            }
            else -> MKCircleRenderer(rendererForOverlay)
        }

    /**
     * Handles marker selection events when user taps on annotations.
     *
     * @param mapView The map view containing the annotation
     * @param didSelectAnnotationView The annotation view that was selected
     */
    override fun mapView(
        mapView: MKMapView,
        didSelectAnnotationView: platform.MapKit.MKAnnotationView,
    ) {
        val annotation = didSelectAnnotationView.annotation
        if (annotation is MKPointAnnotation) {
            markerMapping[annotation]?.let { marker -> onMarkerClick?.invoke(marker) }
        }
    }

    /**
     * Handles camera movement events when the map region changes.
     *
     * @param mapView The map view whose region changed
     * @param regionDidChangeAnimated Whether the change was animated
     */
    override fun mapView(mapView: MKMapView, regionDidChangeAnimated: Boolean) {
        val region = mapView.region
        val cameraPosition = region.toCameraPosition()
        onCameraMove?.invoke(cameraPosition)
    }

    /**
     * Updates callback functions for map interactions.
     *
     * @param onMarkerClick Callback for marker clicks
     * @param onCircleClick Callback for circle clicks
     * @param onPolygonClick Callback for polygon clicks
     * @param onPolylineClick Callback for polyline clicks
     * @param onMapClick Callback for map clicks
     * @param onMapLongClick Callback for map long clicks
     * @param onPOIClick Callback for POI clicks
     * @param onCameraMove Callback for camera movement
     */
    fun updateCallbacks(
        onMarkerClick: ((Marker) -> Unit)?,
        onCircleClick: ((Circle) -> Unit)?,
        onPolygonClick: ((Polygon) -> Unit)?,
        onPolylineClick: ((Polyline) -> Unit)?,
        onMapClick: ((Coordinates) -> Unit)?,
        onMapLongClick: ((Coordinates) -> Unit)?,
        onPOIClick: ((Coordinates) -> Unit)?,
        onCameraMove: ((CameraPosition) -> Unit)?,
    ) {
        this.onMarkerClick = onMarkerClick
        this.onCircleClick = onCircleClick
        this.onPolygonClick = onPolygonClick
        this.onPolylineClick = onPolylineClick
        this.onMapClick = onMapClick
        this.onMapLongClick = onMapLongClick
        this.onPOIClick = onPOIClick
        this.onCameraMove = onCameraMove
    }

    /**
     * Handles tap gestures on the map to detect clicks on overlays and map.
     *
     * @param gestureRecognizer The tap gesture recognizer
     */
    @ObjCAction
    @OptIn(kotlinx.cinterop.BetaInteropApi::class)
    fun handleMapTap(gestureRecognizer: UITapGestureRecognizer) {
        val mapView = gestureRecognizer.view as? MKMapView ?: return
        val tapPoint = gestureRecognizer.locationInView(mapView)
        val coordinate = mapView.convertPoint(tapPoint, toCoordinateFromView = mapView)

        coordinate.useContents {
            val tapLat = latitude
            val tapLon = longitude
            val tapCoord = Coordinates(tapLat, tapLon)

            for (mapCircle in circleStyles.values) {
                if (isPointInCircle(tapLat, tapLon, mapCircle)) {
                    onCircleClick?.invoke(mapCircle)
                    return@useContents
                }
            }

            for (mapPolygon in polygonStyles.values) {
                if (isPointInPolygon(tapLat, tapLon, mapPolygon)) {
                    onPolygonClick?.invoke(mapPolygon)
                    return@useContents
                }
            }

            for (mapPolyline in polylineStyles.values) {
                if (
                    isPointNearPolyline(
                        tapLat,
                        tapLon,
                        properties.applePolylineTapThreshold,
                        mapPolyline,
                    )
                ) {
                    onPolylineClick?.invoke(mapPolyline)
                    return@useContents
                }
            }

            onMapClick?.invoke(tapCoord)
        }
    }

    /**
     * Handles long press gestures on the map.
     *
     * @param gestureRecognizer The long press gesture recognizer
     */
    @ObjCAction
    @OptIn(kotlinx.cinterop.BetaInteropApi::class)
    fun handleMapLongPress(gestureRecognizer: UILongPressGestureRecognizer) {
        if (gestureRecognizer.state == UIGestureRecognizerStateBegan) {
            val mapView = gestureRecognizer.view as? MKMapView ?: return
            val longPressPoint = gestureRecognizer.locationInView(mapView)
            val coordinate = mapView.convertPoint(longPressPoint, toCoordinateFromView = mapView)

            coordinate.useContents {
                val coordinates = Coordinates(latitude, longitude)
                onMapLongClick?.invoke(coordinates)
            }
        }
    }
}
