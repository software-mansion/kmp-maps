package com.swmansion.kmpmaps.core

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

/**
 * Implementation of ClusterItem for marker clustering on Android.
 *
 * @property marker The Marker object to cluster
 */
internal data class MarkerClusterItem(val marker: Marker) : ClusterItem {
    override fun getPosition(): LatLng =
        LatLng(marker.coordinates.latitude, marker.coordinates.longitude)

    override fun getTitle(): String? = marker.title

    override fun getSnippet(): String? = marker.androidMarkerOptions.snippet

    override fun getZIndex(): Float? = marker.androidMarkerOptions.zIndex
}

@Composable
internal fun DefaultCluster(size: Int) {
    Box(
        modifier =
            Modifier.size(40.dp)
                .background(Color.Blue, CircleShape)
                .border(2.dp, Color.White, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = size.toString(),
            color = Color.White,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            fontSize = 14.sp,
        )
    }
}
