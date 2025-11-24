package com.swmansion.kmpmaps.core

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import com.google.android.gms.maps.Projection
import com.google.android.gms.maps.model.LatLng

@Composable
internal fun CustomMarkers(
    markers: List<Marker>,
    customMarkerContent: Map<String, @Composable () -> Unit>,
    projection: Projection?,
    draggable: Boolean = false,
    onMarkerClick: ((Marker) -> Unit)?,
) {
    markers.forEach { marker ->
        customMarkerContent[marker.contentId]?.let {
            val screenLocation =
                projection?.toScreenLocation(
                    LatLng(marker.coordinates.latitude, marker.coordinates.longitude)
                )
            screenLocation?.let { point ->
                Box(
                    modifier =
                        Modifier.offset { IntOffset(point.x, point.y) }
                            .clickable { onMarkerClick?.invoke(marker) }
                ) {
                    it()
                }
            }
        }
    }
}
