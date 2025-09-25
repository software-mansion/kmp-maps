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

        if (
            ((piLat > pointLat) != (pjLat > pointLat)) &&
                (pointLon < (pjLon - piLon) * (pointLat - piLat) / (pjLat - piLat) + piLon)
        ) {
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

    val a =
        kotlin.math.sin(deltaLatRad / 2) * kotlin.math.sin(deltaLatRad / 2) +
            cos(lat1Rad) *
                cos(lat2Rad) *
                kotlin.math.sin(deltaLonRad / 2) *
                kotlin.math.sin(deltaLonRad / 2)
    val c = 2 * kotlin.math.atan2(kotlin.math.sqrt(a), kotlin.math.sqrt(1 - a))
    val distance = EARTH_RADIUS * c

    return distance <= radius
}

internal fun isPointNearPolyline(
    pointLat: Double,
    pointLon: Double,
    threshold: Float?,
    mapPolyline: MapPolyline,
): Boolean {
    val coordinates = mapPolyline.coordinates
    val localThreshold = threshold ?: (mapPolyline.width * 2.0f)

    for (i in 0 until coordinates.size - 1) {
        val start = coordinates[i]
        val end = coordinates[i + 1]

        val distanceToSegment =
            distanceToLineSegment(
                pointLat,
                pointLon,
                start.latitude,
                start.longitude,
                end.latitude,
                end.longitude,
            )

        if (distanceToSegment <= localThreshold) {
            return true
        }
    }

    return false
}

private fun distanceToLineSegment(
    pointLat: Double,
    pointLon: Double,
    lineStartLat: Double,
    lineStartLon: Double,
    lineEndLat: Double,
    lineEndLon: Double,
): Double {
    val a = pointLat - lineStartLat
    val b = pointLon - lineStartLon
    val c = lineEndLat - lineStartLat
    val d = lineEndLon - lineStartLon

    val dot = a * c + b * d
    val lenSq = c * c + d * d

    if (lenSq == 0.0) {
        return calculateDistance(pointLat, pointLon, lineStartLat, lineStartLon)
    }

    val param = dot / lenSq

    val xx: Double
    val yy: Double

    if (param < 0) {
        xx = lineStartLat
        yy = lineStartLon
    } else if (param > 1) {
        xx = lineEndLat
        yy = lineEndLon
    } else {
        xx = lineStartLat + param * c
        yy = lineStartLon + param * d
    }

    return calculateDistance(pointLat, pointLon, xx, yy)
}

private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val lat1Rad = lat1 * PI / 180.0
    val lat2Rad = lat2 * PI / 180.0
    val deltaLatRad = (lat2 - lat1) * PI / 180.0
    val deltaLonRad = (lon2 - lon1) * PI / 180.0

    val a =
        kotlin.math.sin(deltaLatRad / 2) * kotlin.math.sin(deltaLatRad / 2) +
            cos(lat1Rad) *
                cos(lat2Rad) *
                kotlin.math.sin(deltaLonRad / 2) *
                kotlin.math.sin(deltaLonRad / 2)
    val c = 2 * kotlin.math.atan2(kotlin.math.sqrt(a), kotlin.math.sqrt(1 - a))

    return EARTH_RADIUS * c
}
