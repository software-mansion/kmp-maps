package com.swmansion.kmpmaps.googlemaps

import androidx.compose.runtime.Composable
import com.swmansion.kmpmaps.core.Cluster
import com.swmansion.kmpmaps.core.ClusterSettings
import com.swmansion.kmpmaps.core.Coordinates
import com.swmansion.kmpmaps.core.DefaultCluster
import com.swmansion.kmpmaps.core.Marker
import com.swmansion.kmpmaps.core.generateClusterId
import com.swmansion.kmpmaps.core.getId
import kotlinx.cinterop.useContents
import platform.UIKit.UIImage
import platform.darwin.NSObject
import swiftPMImport.com.swmansion.kmpmaps.kmp.maps.google.maps.GMSCameraUpdate
import swiftPMImport.com.swmansion.kmpmaps.kmp.maps.google.maps.GMSMapView
import swiftPMImport.com.swmansion.kmpmaps.kmp.maps.google.maps.GMSMarker
import swiftPMImport.com.swmansion.kmpmaps.kmp.maps.google.maps.GMUClusterItemProtocol
import swiftPMImport.com.swmansion.kmpmaps.kmp.maps.google.maps.GMUClusterManager
import swiftPMImport.com.swmansion.kmpmaps.kmp.maps.google.maps.GMUClusterManagerDelegateProtocol
import swiftPMImport.com.swmansion.kmpmaps.kmp.maps.google.maps.GMUClusterProtocol
import swiftPMImport.com.swmansion.kmpmaps.kmp.maps.google.maps.GMUClusterRendererDelegateProtocol
import swiftPMImport.com.swmansion.kmpmaps.kmp.maps.google.maps.GMUClusterRendererProtocol
import swiftPMImport.com.swmansion.kmpmaps.kmp.maps.google.maps.animateWithCameraUpdate

internal class MarkerClusterManagerDelegate(
    private val mapView: GMSMapView,
    private val mapDelegate: MapDelegate?,
    private val clusterSettings: ClusterSettings,
    private val onMarkerClick: ((Marker) -> Unit)?,
    private val customMarkerContent: Map<String, @Composable (Marker) -> Unit>,
) : NSObject(), GMUClusterManagerDelegateProtocol, GMUClusterRendererDelegateProtocol {

    override fun renderer(renderer: GMUClusterRendererProtocol, willRenderMarker: GMSMarker) {
        val userData = willRenderMarker.userData()

        when (userData) {
            is MarkerClusterItem -> {
                val marker = userData.marker
                val cached = mapDelegate?.getCachedImage(marker.getId())

                if (cached != null) {
                    willRenderMarker.setIcon(cached)
                } else {
                    val content = customMarkerContent[marker.contentId]
                    if (content != null) {
                        mapDelegate?.renderingQueue[marker.getId()] = { content(marker) }
                        willRenderMarker.setIcon(UIImage())
                    }
                }
                willRenderMarker.setTitle(marker.title)
                willRenderMarker.setZIndex(marker.androidMarkerOptions.zIndex?.toInt() ?: 0)
            }
            is GMUClusterProtocol -> {
                val markers = userData.items.mapNotNull { (it as? MarkerClusterItem)?.marker }
                val count = userData.count.toInt()

                val clusterId = generateClusterId(markers)
                val cached = mapDelegate?.getCachedImage(clusterId)

                if (cached != null) {
                    willRenderMarker.setIcon(cached)
                } else {
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

                    mapDelegate?.renderingQueue[clusterId] =
                        @Composable {
                            if (clusterSettings.clusterContent != null) {
                                clusterSettings.clusterContent?.invoke(kmpCluster)
                            } else {
                                DefaultCluster(size = count)
                            }
                        }
                    willRenderMarker.setIcon(UIImage())
                }
            }
            else -> return
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
            val newZoom = mapView.camera().zoom() + 1
            val update = GMSCameraUpdate.setTarget(didTapCluster.position, newZoom)
            mapView.animateWithCameraUpdate(update)
        }
        return true
    }

    override fun clusterManager(
        clusterManager: GMUClusterManager,
        didTapClusterItem: GMUClusterItemProtocol,
    ): Boolean {
        if (didTapClusterItem is MarkerClusterItem) onMarkerClick?.invoke(didTapClusterItem.marker)
        return true
    }
}
