package com.swmansion.kmpmaps.core

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSArray
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.dataUsingEncoding
import platform.MapKit.*

/**
 * Handle for a rendered GeoJSON layer on MKMapView. Keep this instance and call clear(from:) to
 * remove it later.
 */
public class MKGeoJsonRenderedLayer(
    internal val overlays: List<MKOverlayProtocol>,
    internal val annotations: List<MKAnnotationProtocol>,
) {
    @OptIn(ExperimentalForeignApi::class)
    public fun clear(from: MKMapView) {
        if (overlays.isNotEmpty()) from.removeOverlays(overlays)
        if (annotations.isNotEmpty()) from.removeAnnotations(annotations)
    }
}

/**
 * Renders a single GeoJSON document on an MKMapView.
 *
 * @param geoJson A UTF‑8 encoded GeoJSON string.
 * @return A handle to the rendered layer, or null if decoding fails.
 */
@OptIn(ExperimentalForeignApi::class)
public fun MKMapView.renderGeoJson(geoJson: String): MKGeoJsonRenderedLayer? {
    val data = (geoJson as NSString).dataUsingEncoding(NSUTF8StringEncoding) ?: return null
    val decoder = MKGeoJSONDecoder()
    val objects = decoder.geoJSONObjectsWithData(data, error = null) ?: return null

    val annotations = mutableListOf<MKAnnotationProtocol>()
    val overlays = mutableListOf<MKOverlayProtocol>()

    objects.forEach { obj -> collectAndAdd(obj, this, overlays, annotations) }

    return MKGeoJsonRenderedLayer(overlays = overlays, annotations = annotations)
}

@OptIn(ExperimentalForeignApi::class)
private fun collectAndAdd(
    obj: Any?,
    mapView: MKMapView,
    overlays: MutableList<MKOverlayProtocol>,
    annotations: MutableList<MKAnnotationProtocol>,
) {
    when (obj) {
        is MKGeoJSONFeature ->
            obj.geometry?.forEach { g -> collectAndAdd(g, mapView, overlays, annotations) }
        is MKPolygon -> {
            mapView.addOverlay(obj)
            overlays += obj
        }
        is MKPolyline -> {
            mapView.addOverlay(obj)
            overlays += obj
        }
        is MKMultiPolygon -> {
            mapView.addOverlay(obj)
            overlays += obj
        }
        is MKMultiPolyline -> {
            mapView.addOverlay(obj)
            overlays += obj
        }
        is MKPointAnnotation -> {
            mapView.addAnnotation(obj)
            annotations += obj
        }
        is NSArray -> {
            val arr = obj as NSArray
            val n = arr.count.toInt()
            for (i in 0 until n) {
                val any = arr.objectAtIndex(i.toULong())
                collectAndAdd(any, mapView, overlays, annotations)
            }
        }
        else -> Unit
    }
}
