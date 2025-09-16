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
                renderer.strokeColor = parseColor(circleStyle?.lineColor ?: "#1111111")
                renderer.lineWidth = (circleStyle?.lineWidth ?: 1).toDouble()
                renderer.fillColor = parseColor(circleStyle?.color ?: "#1111111")
                renderer
            }

            is MKPolygon -> MKPolygonRenderer(rendererForOverlay)
            is MKPolyline -> MKPolylineRenderer(rendererForOverlay)
            else -> MKCircleRenderer(rendererForOverlay)
        }
    }

    private fun parseColor(colorHex: String): UIColor {
        return when (colorHex.uppercase()) {
            "#FF0000", "#FF0000FF" -> UIColor.redColor
            "#00FF00", "#00FF00FF" -> UIColor.greenColor
            "#0000FF", "#0000FF00" -> UIColor.blueColor
            "#FFFF00", "#FFFF00FF" -> UIColor.yellowColor
            "#FF00FF", "#FF00FFFF" -> UIColor.magentaColor
            "#00FFFF", "#00FFFFFF" -> UIColor.cyanColor
            "#FFFFFF", "#FFFFFFFF" -> UIColor.whiteColor
            "#000000", "#000000FF" -> UIColor.blackColor
            else -> UIColor.redColor
        }
    }
}
