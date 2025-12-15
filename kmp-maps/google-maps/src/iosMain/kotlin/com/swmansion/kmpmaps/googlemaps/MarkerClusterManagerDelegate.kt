package com.swmansion.kmpmaps.googlemaps

import androidx.compose.runtime.Composable
import cocoapods.GoogleMaps.GMSCameraUpdate
import cocoapods.GoogleMaps.GMSMapView
import cocoapods.GoogleMaps.animateWithCameraUpdate
import cocoapods.Google_Maps_iOS_Utils.GMSMapView as UtilsGMSMapView
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
import com.swmansion.kmpmaps.core.DefaultCluster
import com.swmansion.kmpmaps.core.DefaultPin
import com.swmansion.kmpmaps.core.Marker
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class)
internal class MarkerClusterManagerDelegate(
    private val mapView: UtilsGMSMapView,
    private val clusterSettings: ClusterSettings,
    private val onMarkerClick: ((Marker) -> Unit)?,
    private val customMarkerContent: Map<String, @Composable (Marker) -> Unit>,
) : NSObject(), GMUClusterManagerDelegateProtocol, GMUClusterRendererDelegateProtocol {

    override fun renderer(renderer: GMUClusterRendererProtocol, willRenderMarker: GMSMarker) {
        val userData = willRenderMarker.userData()

        when (userData) {
            is MarkerClusterItem -> {
                val marker = userData.marker
                val iconView =
                    willRenderMarker.iconView() as? CustomMarkers ?: CustomMarkers(willRenderMarker)
                val content = customMarkerContent[marker.contentId]

                iconView.setContent { content?.invoke(marker) ?: DefaultPin(marker) }
                willRenderMarker.setIconView(iconView)
                willRenderMarker.setTracksViewChanges(true)
                willRenderMarker.setTitle(marker.title)
                willRenderMarker.setZIndex(marker.androidMarkerOptions.zIndex?.toInt() ?: 0)
            }
            is GMUClusterProtocol -> {
                val itemsList = userData.items
                val markers = itemsList.mapNotNull { item -> (item as? MarkerClusterItem)?.marker }
                val count = userData.count.toInt()
                val iconView =
                    willRenderMarker.iconView() as? CustomMarkers ?: CustomMarkers(willRenderMarker)

                if (clusterSettings.clusterContent != null) {

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

                    clusterSettings.clusterContent?.let { iconView.setContent { it(kmpCluster) } }
                } else {
                    iconView.setContent { DefaultCluster(size = count) }
                }
                willRenderMarker.setIconView(iconView)
                willRenderMarker.setTracksViewChanges(true)
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
            (mapView as GMSMapView).animateWithCameraUpdate(update)
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
