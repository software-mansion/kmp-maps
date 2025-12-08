package com.swmansion.kmpmaps.googlemaps

import cocoapods.Google_Maps_iOS_Utils.GMUClusterItemProtocol
import com.swmansion.kmpmaps.core.Marker
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreLocation.CLLocationCoordinate2D
import platform.CoreLocation.CLLocationCoordinate2DMake
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class)
internal class MarkerClusterItem(val marker: Marker) : NSObject(), GMUClusterItemProtocol {

    override fun position(): CValue<CLLocationCoordinate2D> {
        return CLLocationCoordinate2DMake(marker.coordinates.latitude, marker.coordinates.longitude)
    }

    override fun title(): String? = marker.title

    override fun snippet(): String? = marker.androidMarkerOptions.snippet
}
