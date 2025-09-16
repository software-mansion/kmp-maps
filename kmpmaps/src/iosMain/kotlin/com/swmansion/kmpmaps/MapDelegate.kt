package com.swmansion.kmpmaps

import kotlinx.cinterop.ExperimentalForeignApi
import platform.MapKit.MKCircle
import platform.MapKit.MKCircleRenderer
import platform.MapKit.MKMapView
import platform.MapKit.MKMapViewDelegateProtocol
import platform.MapKit.MKOverlayProtocol
import platform.MapKit.MKOverlayRenderer
import platform.MapKit.MKPolygon
import platform.MapKit.MKPolygonRenderer
import platform.MapKit.MKPolyline
import platform.MapKit.MKPolylineRenderer
import platform.UIKit.UIColor
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class)
public class SimpleMapDelegate(
    private val circleStyles: MutableMap<MKCircle, AppleMapsCircle>
) : NSObject(), MKMapViewDelegateProtocol {

    override fun mapView(
        mapView: MKMapView,
        rendererForOverlay: MKOverlayProtocol
    ): MKOverlayRenderer {
        return when (rendererForOverlay) {
            is MKCircle -> {
                val circleStyle = circleStyles[rendererForOverlay]
                val renderer = MKCircleRenderer(rendererForOverlay)
                renderer.strokeColor = circleStyle?.lineColor ?: UIColor.redColor
                renderer.lineWidth = (circleStyle?.lineWidth ?: 1).toDouble()
                renderer.fillColor = circleStyle?.color ?: UIColor.greenColor
                renderer
            }

            is MKPolygon -> MKPolygonRenderer(rendererForOverlay)
            is MKPolyline -> MKPolylineRenderer(rendererForOverlay)
            else -> MKCircleRenderer(rendererForOverlay)
        }
    }
}
