package com.swmansion.kmpmaps.core

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

internal data class MarkerClusterItem(val marker: Marker) : ClusterItem {
    override val position: LatLng
        get() = LatLng(marker.coordinates.latitude, marker.coordinates.longitude)

    override val title: String?
        get() = marker.title

    override val snippet: String?
        get() = marker.androidMarkerOptions.snippet

    override val zIndex: Float?
        get() = marker.androidMarkerOptions.zIndex
}
