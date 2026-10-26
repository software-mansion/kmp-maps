package com.swmansion.kmpmaps.googlemaps

import com.swmansion.kmpmaps.core.Marker
import kotlinx.cinterop.CValue
import platform.CoreLocation.CLLocationCoordinate2D
import platform.CoreLocation.CLLocationCoordinate2DMake
import platform.darwin.NSObject
import swiftPMImport.com.swmansion.kmpmaps.kmp.maps.google.maps.GMUClusterItemProtocol

internal class MarkerClusterItem(val marker: Marker) : NSObject(), GMUClusterItemProtocol {

    override fun position(): CValue<CLLocationCoordinate2D> {
        return CLLocationCoordinate2DMake(marker.coordinates.latitude, marker.coordinates.longitude)
    }

    override fun title(): String? = marker.title

    override fun snippet(): String? = marker.androidMarkerOptions.snippet
}
