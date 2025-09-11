package com.swmansion.kmpmaps

interface MapController {
    fun setRegion(region: MapRegion, animated: Boolean = true)

    fun addAnnotation(annotation: MapAnnotation)

    fun removeAnnotation(annotationId: String)

    fun removeAllAnnotations()

    fun setMapType(mapType: MapType)

    fun showUserLocation(show: Boolean)
}
