package com.swmansion.kmpmaps.kmpmaps

import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import platform.posix.pow

private const val EARTH_RADIUS = 6371000.0

/**
 * Calculates latitude delta for a given zoom level.
 *
 * @param zoom The zoom level
 * @return Latitude delta in degrees
 */
internal fun calculateLatitudeDelta(zoom: Float): Double {
    return 360.0 / pow(2.0, zoom.toDouble())
}

/**
 * Calculates longitude delta for a given zoom level and latitude.
 *
 * @param zoom The zoom level
 * @param latitude The latitude coordinate
 * @return Longitude delta in degrees
 */
internal fun calculateLongitudeDelta(zoom: Float, latitude: Double): Double {
    val latRad = latitude / 180.0 * PI
    val lngDelta = 360.0 / pow(2.0, zoom.toDouble())
    return lngDelta / cos(latRad)
}

/**
 * Checks if a point is inside a polygon using ray casting algorithm.
 *
 * @param pointLat The latitude of the point to test
 * @param pointLon The longitude of the point to test
 * @param polygon The polygon to test against
 * @return true if the point is inside the polygon, false otherwise
 */
internal fun isPointInPolygon(pointLat: Double, pointLon: Double, polygon: Polygon): Boolean {
    val coordinates = polygon.coordinates
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

/**
 * Checks if a point is inside a circle using Haversine distance calculation.
 *
 * @param pointLat The latitude of the point to test
 * @param pointLon The longitude of the point to test
 * @param circle The circle to test against
 * @return true if the point is inside the circle, false otherwise
 */
internal fun isPointInCircle(pointLat: Double, pointLon: Double, circle: Circle): Boolean {
    val centerLat = circle.center.latitude
    val centerLon = circle.center.longitude
    val radius = circle.radius

    val lat1Rad = centerLat * PI / 180.0
    val lat2Rad = pointLat * PI / 180.0
    val deltaLatRad = (pointLat - centerLat) * PI / 180.0
    val deltaLonRad = (pointLon - centerLon) * PI / 180.0

    val a =
        sin(deltaLatRad / 2) * sin(deltaLatRad / 2) +
            cos(lat1Rad) * cos(lat2Rad) * sin(deltaLonRad / 2) * sin(deltaLonRad / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    val distance = EARTH_RADIUS * c

    return distance <= radius
}

/**
 * Checks if a point is near a polyline within a given threshold.
 *
 * @param pointLat The latitude of the point to test
 * @param pointLon The longitude of the point to test
 * @param threshold The distance threshold in meters (if null, uses polyline width * 2)
 * @param polyline The polyline to test against
 * @return true if the point is near the polyline, false otherwise
 */
internal fun isPointNearPolyline(
    pointLat: Double,
    pointLon: Double,
    threshold: Float?,
    polyline: Polyline,
): Boolean {
    val coordinates = polyline.coordinates
    val localThreshold = threshold ?: (polyline.width * 2.0f)

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

/**
 * Calculates the distance from a point to a line segment.
 *
 * @param pointLat The latitude of the point
 * @param pointLon The longitude of the point
 * @param lineStartLat The latitude of the line start
 * @param lineStartLon The longitude of the line start
 * @param lineEndLat The latitude of the line end
 * @param lineEndLon The longitude of the line end
 * @return Distance in meters
 */
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

/**
 * Calculates the distance between two geographical points using Haversine formula.
 *
 * @param lat1 The latitude of the first point
 * @param lon1 The longitude of the first point
 * @param lat2 The latitude of the second point
 * @param lon2 The longitude of the second point
 * @return Distance in meters
 */
private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val lat1Rad = lat1 * PI / 180.0
    val lat2Rad = lat2 * PI / 180.0
    val deltaLatRad = (lat2 - lat1) * PI / 180.0
    val deltaLonRad = (lon2 - lon1) * PI / 180.0

    val a =
        sin(deltaLatRad / 2) * sin(deltaLatRad / 2) +
            cos(lat1Rad) * cos(lat2Rad) * sin(deltaLonRad / 2) * sin(deltaLonRad / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))

    return EARTH_RADIUS * c
}
