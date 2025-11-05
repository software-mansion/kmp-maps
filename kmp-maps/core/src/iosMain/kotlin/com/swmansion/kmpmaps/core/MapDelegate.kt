package com.swmansion.kmpmaps.core

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCAction
import kotlinx.cinterop.useContents
import platform.MapKit.MKAnnotationProtocol
import platform.MapKit.MKAnnotationView
import platform.MapKit.MKCircle
import platform.MapKit.MKCircleRenderer
import platform.MapKit.MKMapView
import platform.MapKit.MKMapViewDelegateProtocol
import platform.MapKit.MKMarkerAnnotationView
import platform.MapKit.MKOverlayProtocol
import platform.MapKit.MKPointAnnotation
import platform.MapKit.MKPolygon
import platform.MapKit.MKPolygonRenderer
import platform.MapKit.MKPolyline
import platform.MapKit.MKPolylineRenderer
import platform.MapKit.MKUserLocation
import platform.UIKit.UIColor
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
    private val geoJsonPolygonStyles: MutableMap<MKPolygon, AppleGeoJsonPolygonStyle>,
    private val geoJsonPolylineStyles: MutableMap<MKPolyline, AppleGeoJsonLineStyle>,
    private val geoJsonPointStyles: MutableMap<MKPointAnnotation, AppleGeoJsonPointStyle>,
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
                renderer.strokeColor =
                    circleStyle?.lineColor?.toAppleMapsColor() ?: UIColor.blackColor
                renderer.lineWidth = (circleStyle?.lineWidth ?: DEFAULT_STROKE_WIDTH).toDouble()
                renderer.fillColor = circleStyle?.color?.toAppleMapsColor()
                renderer
            }
            is MKPolygon -> {
                val core = polygonStyles[rendererForOverlay]
                val renderer = MKPolygonRenderer(rendererForOverlay)
                if (core != null) {
                    renderer.strokeColor = core.lineColor?.toAppleMapsColor() ?: UIColor.blackColor
                    renderer.lineWidth = core.lineWidth.toDouble()
                    renderer.fillColor = core.color?.toAppleMapsColor()
                } else {
                    val gj = geoJsonPolygonStyles[rendererForOverlay]
                    if (gj != null) {
                        renderer.strokeColor = gj.strokeColor
                        renderer.lineWidth = gj.strokeWidth
                        renderer.fillColor = gj.fillColor
                    } else {
                        renderer.strokeColor = UIColor.blackColor
                        renderer.lineWidth = DEFAULT_STROKE_WIDTH.toDouble()
                        renderer.fillColor = null
                    }
                }
                renderer
            }
            is MKPolyline -> {
                val core = polylineStyles[rendererForOverlay]
                val renderer = MKPolylineRenderer(rendererForOverlay)
                if (core != null) {
                    renderer.strokeColor = core.lineColor?.toAppleMapsColor() ?: UIColor.blackColor
                    renderer.lineWidth = core.width.toDouble()
                } else {
                    val gj = geoJsonPolylineStyles[rendererForOverlay]
                    if (gj != null) {
                        renderer.strokeColor = gj.color
                        renderer.lineWidth = gj.width
                    } else {
                        renderer.strokeColor = UIColor.blackColor
                        renderer.lineWidth = DEFAULT_STROKE_WIDTH.toDouble()
                    }
                }
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
    override fun mapView(mapView: MKMapView, didSelectAnnotationView: MKAnnotationView) {
        val annotation = didSelectAnnotationView.annotation
        if (annotation is MKPointAnnotation) {
            markerMapping[annotation]?.let { marker -> onMarkerClick?.invoke(marker) }
        }
    }

    override fun mapView(
        mapView: MKMapView,
        viewForAnnotation: MKAnnotationProtocol,
    ): MKAnnotationView? {
        if (viewForAnnotation is MKUserLocation) return null
        val point = viewForAnnotation as? MKPointAnnotation ?: return null

        val style = geoJsonPointStyles[point] ?: return null
        val reuseId = "kmp_geojson_marker"

        val view =
            mapView.dequeueReusableAnnotationViewWithIdentifier(reuseId) as? MKMarkerAnnotationView
                ?: MKMarkerAnnotationView(annotation = viewForAnnotation, reuseIdentifier = reuseId)

        view.annotation = viewForAnnotation
        view.canShowCallout = true
        view.hidden = !style.visible

        return view
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
     * Handles tap gestures on the map to detect clicks on overlays and map.
     *
     * @param gestureRecognizer The tap gesture recognizer
     */
    @ObjCAction
    @OptIn(BetaInteropApi::class)
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
                        properties.iosMapProperties.polylineTapThreshold,
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
    @OptIn(BetaInteropApi::class)
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
