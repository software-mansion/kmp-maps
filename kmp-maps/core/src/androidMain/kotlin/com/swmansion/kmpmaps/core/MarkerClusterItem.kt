package com.swmansion.kmpmaps.core

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

internal data class MarkerClusterItem(val marker: Marker) : ClusterItem {
    override fun getPosition(): LatLng =
        LatLng(marker.coordinates.latitude, marker.coordinates.longitude)

    override fun getTitle(): String? = marker.title

    override fun getSnippet(): String? = marker.androidMarkerOptions.snippet

    override fun getZIndex(): Float? = marker.androidMarkerOptions.zIndex
}
