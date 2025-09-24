package com.swmansion.kmpmaps

import kotlin.math.PI
import kotlin.math.cos
import platform.posix.pow

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
