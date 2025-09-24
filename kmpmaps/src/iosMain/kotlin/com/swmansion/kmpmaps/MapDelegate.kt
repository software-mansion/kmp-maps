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
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class)
internal class SimpleMapDelegate(
    private val circleStyles: MutableMap<MKCircle, MapCircle>,
    private val polygonStyles: MutableMap<MKPolygon, MapPolygon>
) : NSObject(), MKMapViewDelegateProtocol {

    override fun mapView(
        mapView: MKMapView,
        rendererForOverlay: MKOverlayProtocol
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
            is MKPolyline -> MKPolylineRenderer(rendererForOverlay)
            else -> MKCircleRenderer(rendererForOverlay)
        }
    }
}
