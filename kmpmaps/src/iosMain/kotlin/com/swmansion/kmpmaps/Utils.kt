package com.swmansion.kmpmaps

import kotlin.math.PI
import kotlin.math.cos
import platform.posix.pow

fun calculateLatitudeDelta(zoom: Float): Double {
    return 360.0 / pow(2.0, zoom.toDouble())
}

fun calculateLongitudeDelta(zoom: Float, latitude: Double): Double {
    val latRad = latitude / 180.0 * PI
    val lngDelta = 360.0 / pow(2.0, zoom.toDouble())
    return lngDelta / cos(latRad)
}
