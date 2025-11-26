package com.swmansion.kmpmaps.core

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import com.google.android.gms.maps.Projection
import com.google.android.gms.maps.model.LatLng
import kotlin.math.roundToInt

@Composable
internal fun CustomMarkers(
    markers: List<Marker>,
    customMarkerContent: Map<String, @Composable () -> Unit>,
    projection: Projection?,
    onMarkerClick: ((Marker) -> Unit)?,
) {
    val validMarkers = remember(markers, customMarkerContent) {
        markers.filter { it.contentId != null && customMarkerContent.containsKey(it.contentId) }
    }

    if (projection == null || validMarkers.isEmpty()) return

    Layout(
        modifier = Modifier.fillMaxSize(),
        content = {
            validMarkers.forEach { marker ->
                Box(
                    modifier = Modifier
                        .clickable { onMarkerClick?.invoke(marker) }
                ) {
                    customMarkerContent[marker.contentId]?.invoke()
                }
            }
        },
    ) { measurables, constraints ->

        val placeables = measurables.map {
            it.measure(constraints.copy(minWidth = 0, minHeight = 0))
        }

        layout(constraints.maxWidth, constraints.maxHeight) {
            placeables.forEachIndexed { index, placeable ->
                val marker = validMarkers[index]

                val latLng = LatLng(marker.coordinates.latitude, marker.coordinates.longitude)
                val screenPosition = projection.toScreenLocation(latLng)

                val anchorX = marker.androidMarkerOptions.anchor?.x ?: 0.5f
                val anchorY = marker.androidMarkerOptions.anchor?.y ?: 1.0f

                val x = screenPosition.x - (placeable.width * anchorX)
                val y = screenPosition.y - (placeable.height * anchorY)

                placeable.place(x.roundToInt(), y.roundToInt())
            }
        }
    }
}
