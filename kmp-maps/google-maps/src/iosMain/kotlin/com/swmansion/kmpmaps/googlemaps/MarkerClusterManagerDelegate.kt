package com.swmansion.kmpmaps.googlemaps

import androidx.compose.runtime.Composable
import cocoapods.GoogleMaps.GMSCameraUpdate
import cocoapods.GoogleMaps.GMSMapView
import cocoapods.GoogleMaps.animateWithCameraUpdate
import cocoapods.Google_Maps_iOS_Utils.GMSMarker
import cocoapods.Google_Maps_iOS_Utils.GMUClusterItemProtocol
import cocoapods.Google_Maps_iOS_Utils.GMUClusterManager
import cocoapods.Google_Maps_iOS_Utils.GMUClusterManagerDelegateProtocol
import cocoapods.Google_Maps_iOS_Utils.GMUClusterProtocol
import cocoapods.Google_Maps_iOS_Utils.GMUClusterRendererDelegateProtocol
import cocoapods.Google_Maps_iOS_Utils.GMUClusterRendererProtocol
import com.swmansion.kmpmaps.core.Cluster
import com.swmansion.kmpmaps.core.ClusterSettings
import com.swmansion.kmpmaps.core.Coordinates
import com.swmansion.kmpmaps.core.Marker
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class)
internal class MarkerClusterManagerDelegate(
    private val mapView: GMSMapView,
    private val clusterSettings: ClusterSettings,
    private val onMarkerClick: ((Marker) -> Unit)?,
    private val customMarkerContent: Map<String, @Composable () -> Unit>,
) : NSObject(), GMUClusterManagerDelegateProtocol, GMUClusterRendererDelegateProtocol {

    override fun renderer(renderer: GMUClusterRendererProtocol, willRenderMarker: GMSMarker) {
        val userData = willRenderMarker.userData()

        if (userData is MarkerClusterItem) {
            val marker = userData.marker
            val content = marker.contentId?.let { customMarkerContent[it] }

            if (content != null) {
                val iconView =
                    willRenderMarker.iconView() as? CustomMarkers
                        ?: CustomMarkers(willRenderMarker as cocoapods.GoogleMaps.GMSMarker)

                iconView.setContent(content)
                willRenderMarker.setIconView(iconView)
                willRenderMarker.setTracksViewChanges(true)
            } else {
                willRenderMarker.setIconView(null)
            }
            willRenderMarker.setTitle(marker.title)
            willRenderMarker.setZIndex(marker.androidMarkerOptions.zIndex?.toInt() ?: 0)
        } else if (userData is GMUClusterProtocol) {
            if (clusterSettings.clusterContent != null) {
                val itemsList = userData.items

                val markers = itemsList.mapNotNull { item -> (item as? MarkerClusterItem)?.marker }

                val count = userData.count.toInt()

                val kmpCluster =
                    Cluster(
                        coordinates =
                            Coordinates(
                                userData.position.useContents { latitude },
                                userData.position.useContents { longitude },
                            ),
                        size = count,
                        items = markers,
                    )

                val iconView =
                    willRenderMarker.iconView() as? CustomMarkers
                        ?: CustomMarkers(willRenderMarker as cocoapods.GoogleMaps.GMSMarker)

                iconView.setContent { clusterSettings.clusterContent!!.invoke(kmpCluster) }
                willRenderMarker.setIconView(iconView)
                willRenderMarker.setTracksViewChanges(true)
            }
        }
    }

    override fun clusterManager(
        clusterManager: GMUClusterManager,
        didTapCluster: GMUClusterProtocol,
    ): Boolean {
        val itemsList = didTapCluster.items

        val items = itemsList.mapNotNull { item -> (item as? MarkerClusterItem)?.marker }

        val count = didTapCluster.count.toInt()

        val kmpCluster =
            Cluster(
                coordinates =
                    Coordinates(
                        didTapCluster.position.useContents { latitude },
                        didTapCluster.position.useContents { longitude },
                    ),
                size = count,
                items = items,
            )

        val consumed = clusterSettings.onClusterClick?.invoke(kmpCluster) ?: false

        if (!consumed) {
            val newZoom = mapView.camera.zoom + 1
            val update = GMSCameraUpdate.setTarget(didTapCluster.position, newZoom)
            mapView.animateWithCameraUpdate(update)
        }
        return true
    }

    override fun clusterManager(
        clusterManager: GMUClusterManager,
        didTapClusterItem: GMUClusterItemProtocol,
    ): Boolean {
        if (didTapClusterItem is MarkerClusterItem) {
            onMarkerClick?.invoke(didTapClusterItem.marker)
        }
        return true
    }
}
