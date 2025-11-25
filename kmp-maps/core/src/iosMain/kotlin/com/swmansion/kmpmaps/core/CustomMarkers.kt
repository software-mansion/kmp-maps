package com.swmansion.kmpmaps.core

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.MapKit.MKMapView

@OptIn(ExperimentalForeignApi::class)
@Composable
internal fun CustomMarkers(
    markers: List<Marker>,
    customMarkerContent: Map<String, @Composable () -> Unit>,
    mapView: MKMapView?,
    onMarkerClick: ((Marker) -> Unit)?,
) {
    markers.forEach { marker ->
        customMarkerContent[marker.contentId]?.let { content ->
            val screenLocation = mapView?.coordinateToScreenPoint(marker.coordinates)
            screenLocation?.useContents {
                println("Custom_markers_ios location: $x, $y")
                Box(
                    modifier =
                        Modifier.offset { IntOffset(x.toInt(), y.toInt()) }
                            .clickable { onMarkerClick?.invoke(marker) }
                ) {
                    content()
                }
            }
        }
    }
}
