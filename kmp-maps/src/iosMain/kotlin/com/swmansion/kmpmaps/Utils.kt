package com.swmansion.kmpmaps

import kotlin.math.PI
import kotlin.math.cos
import platform.posix.pow

private const val EARTH_RADIUS = 6371000.0

internal fun calculateLatitudeDelta(zoom: Float): Double {
    return 360.0 / pow(2.0, zoom.toDouble())
}

internal fun calculateLongitudeDelta(zoom: Float, latitude: Double): Double {
    val latRad = latitude / 180.0 * PI
    val lngDelta = 360.0 / pow(2.0, zoom.toDouble())
    return lngDelta / cos(latRad)
}

internal fun isPointInPolygon(pointLat: Double, pointLon: Double, mapPolygon: MapPolygon): Boolean {
    val coordinates = mapPolygon.coordinates
    var inside = false

    var j = coordinates.size - 1
    for (i in coordinates.indices) {
        val pi = coordinates[i]
        val pj = coordinates[j]

        val piLat = pi.latitude
        val piLon = pi.longitude
        val pjLat = pj.latitude
        val pjLon = pj.longitude

        if (((piLat > pointLat) != (pjLat > pointLat)) &&
            (pointLon < (pjLon - piLon) * (pointLat - piLat) / (pjLat - piLat) + piLon)) {
            inside = !inside
        }
        j = i
    }

    return inside
}

internal fun isPointInCircle(pointLat: Double, pointLon: Double, mapCircle: MapCircle): Boolean {
    val centerLat = mapCircle.center.latitude
    val centerLon = mapCircle.center.longitude
    val radius = mapCircle.radius

    val lat1Rad = centerLat * PI / 180.0
    val lat2Rad = pointLat * PI / 180.0
    val deltaLatRad = (pointLat - centerLat) * PI / 180.0
    val deltaLonRad = (pointLon - centerLon) * PI / 180.0
    
    val a = kotlin.math.sin(deltaLatRad / 2) * kotlin.math.sin(deltaLatRad / 2) +
            cos(lat1Rad) * cos(lat2Rad) *
            kotlin.math.sin(deltaLonRad / 2) * kotlin.math.sin(deltaLonRad / 2)
    val c = 2 * kotlin.math.atan2(kotlin.math.sqrt(a), kotlin.math.sqrt(1 - a))
    val distance = EARTH_RADIUS * c
    
    return distance <= radius
}
