package com.swmansion.kmpmaps

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCAction
import kotlinx.cinterop.useContents
import platform.MapKit.MKCircle
import platform.MapKit.MKCircleRenderer
import platform.MapKit.MKMapView
import platform.MapKit.MKMapViewDelegateProtocol
import platform.MapKit.MKOverlayProtocol
import platform.MapKit.MKOverlayRenderer
import platform.MapKit.MKPointAnnotation
import platform.MapKit.MKPolygon
import platform.MapKit.MKPolygonRenderer
import platform.MapKit.MKPolyline
import platform.MapKit.MKPolylineRenderer
import platform.darwin.NSObject
import platform.UIKit.UITapGestureRecognizer
import platform.UIKit.UILongPressGestureRecognizer
import platform.UIKit.UIGestureRecognizerStateBegan

@OptIn(ExperimentalForeignApi::class)
internal class MapDelegate(
    private val circleStyles: MutableMap<MKCircle, MapCircle>,
    private val polygonStyles: MutableMap<MKPolygon, MapPolygon>,
    private val polylineStyles: MutableMap<MKPolyline, MapPolyline>,
    private val markerMapping: MutableMap<MKPointAnnotation, MapMarker>,
    private var onMarkerClick: ((MapMarker) -> Unit)?,
    private var onCircleClick: ((MapCircle) -> Unit)?,
    private var onPolygonClick: ((MapPolygon) -> Unit)?,
    private var onPolylineClick: ((MapPolyline) -> Unit)?,
    private var onMapClick: ((Coordinates) -> Unit)?,
    private var onMapLongClick: ((Coordinates) -> Unit)?,
    private var onPOIClick: ((Coordinates) -> Unit)?,
    private var onCameraMove: ((CameraPosition) -> Unit)?,
) : NSObject(), MKMapViewDelegateProtocol {

    override fun mapView(
        mapView: MKMapView,
        rendererForOverlay: MKOverlayProtocol,
    ): MKOverlayRenderer {
        return when (rendererForOverlay) {
            is MKCircle -> {
                val circleStyle = circleStyles[rendererForOverlay]
                val renderer = MKCircleRenderer(rendererForOverlay)
                renderer.strokeColor = circleStyle?.lineColor?.toUIColor()
                renderer.lineWidth = (circleStyle?.lineWidth ?: 1).toDouble()
                renderer.fillColor = circleStyle?.color?.toUIColor()
                renderer
            }
            is MKPolygon -> {
                val polygonStyle = polygonStyles[rendererForOverlay]
                val renderer = MKPolygonRenderer(rendererForOverlay)
                renderer.strokeColor = polygonStyle?.lineColor?.toUIColor()
                renderer.lineWidth = (polygonStyle?.lineWidth ?: 1).toDouble()
                renderer.fillColor = polygonStyle?.color?.toUIColor()
                renderer
            }
            is MKPolyline -> {
                val polylineStyle = polylineStyles[rendererForOverlay]
                val renderer = MKPolylineRenderer(rendererForOverlay)
                renderer.strokeColor = polylineStyle?.lineColor?.toUIColor()
                renderer.lineWidth = (polylineStyle?.width ?: 1).toDouble()
                renderer
            }
            else -> MKCircleRenderer(rendererForOverlay)
        }
    }

    override fun mapView(mapView: MKMapView, didSelectAnnotationView: platform.MapKit.MKAnnotationView) {
        val annotation = didSelectAnnotationView.annotation
        if (annotation is MKPointAnnotation) {
            markerMapping[annotation]?.let { marker ->
                onMarkerClick?.invoke(marker)
            }
        }
    }

    override fun mapView(mapView: MKMapView, regionDidChangeAnimated: Boolean) {
        val region = mapView.region
        val cameraPosition = region.toCameraPosition()
        onCameraMove?.invoke(cameraPosition)
    }

    fun updateCallbacks(
        onMarkerClick: ((MapMarker) -> Unit)?,
        onCircleClick: ((MapCircle) -> Unit)?,
        onPolygonClick: ((MapPolygon) -> Unit)?,
        onPolylineClick: ((MapPolyline) -> Unit)?,
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

    @ObjCAction
    fun handleMapTap(gestureRecognizer: UITapGestureRecognizer) {
        val mapView = gestureRecognizer.view as? MKMapView ?: return
        val tapPoint = gestureRecognizer.locationInView(mapView)
        val coordinate = mapView
            .convertPoint(tapPoint, toCoordinateFromView = mapView)

        coordinate.useContents {
            val tapLat = latitude
            val tapLon = longitude
            val tapCoord = Coordinates(tapLat, tapLon)
            
            for ((mkPolygon, mapPolygon) in polygonStyles) {
                if (isPointInPolygon(tapLat, tapLon, mapPolygon)) {
                    onPolygonClick?.invoke(mapPolygon)
                    return@useContents
                }
            }
            onMapClick?.invoke(tapCoord)
        }
    }

    @ObjCAction
    fun handleMapLongPress(gestureRecognizer: UILongPressGestureRecognizer) {
        if (gestureRecognizer.state == UIGestureRecognizerStateBegan) {
            val mapView = gestureRecognizer.view as? MKMapView ?: return
            val longPressPoint = gestureRecognizer.locationInView(mapView)
            val coordinate = mapView
                .convertPoint(longPressPoint, toCoordinateFromView = mapView)

            coordinate.useContents {
                val coordinates = Coordinates(latitude, longitude)
                onMapLongClick?.invoke(coordinates)
            }
        }
    }
}
