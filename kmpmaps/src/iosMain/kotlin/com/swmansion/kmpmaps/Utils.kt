package com.swmansion.kmpmaps

import platform.posix.pow
import kotlin.math.PI
import kotlin.math.cos

fun calculateLatitudeDelta(zoom: Float): Double {
    return 360.0 / pow(2.0, zoom.toDouble())
}

fun calculateLongitudeDelta(zoom: Float, latitude: Double): Double {
    val latRad = latitude / 180.0 * PI
    val lngDelta = 360.0 / pow(2.0, zoom.toDouble())
    return lngDelta / cos(latRad)
}