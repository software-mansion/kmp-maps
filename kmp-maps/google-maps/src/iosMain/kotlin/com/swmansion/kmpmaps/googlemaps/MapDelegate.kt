package com.swmansion.kmpmaps.googlemaps

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import cocoapods.GoogleMaps.GMSCircle
import cocoapods.GoogleMaps.GMSPolygon
import cocoapods.GoogleMaps.GMSPolyline
import cocoapods.Google_Maps_iOS_Utils.GMSCameraPosition
import cocoapods.Google_Maps_iOS_Utils.GMSMapView
import cocoapods.Google_Maps_iOS_Utils.GMSMapViewDelegateProtocol
import cocoapods.Google_Maps_iOS_Utils.GMSMarker
import cocoapods.Google_Maps_iOS_Utils.GMSOverlay
import cocoapods.Google_Maps_iOS_Utils.GMUClusterManager
import com.swmansion.kmpmaps.core.CameraPosition
import com.swmansion.kmpmaps.core.Circle
import com.swmansion.kmpmaps.core.Coordinates
import com.swmansion.kmpmaps.core.Marker
import com.swmansion.kmpmaps.core.Polygon
import com.swmansion.kmpmaps.core.Polyline
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCSignatureOverride
import kotlinx.cinterop.useContents
import platform.CoreLocation.CLLocationCoordinate2D
import platform.UIKit.UIImage
import platform.darwin.NSObject
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

/** iOS map delegate for handling Google Maps interactions and rendering. */
@OptIn(ExperimentalForeignApi::class)
internal class MapDelegate(
    var clusterManager: GMUClusterManager? = null,
    private var imageCache: MutableMap<String, UIImage> = mutableMapOf(),
    val renderingQueue: SnapshotStateMap<String, @Composable () -> Unit> = mutableStateMapOf(),
    private var onCameraMove: ((CameraPosition) -> Unit)?,
    private var onMarkerClick: ((Marker) -> Unit)?,
    private var onCircleClick: ((Circle) -> Unit)?,
    private var onPolygonClick: ((Polygon) -> Unit)?,
    private var onPolylineClick: ((Polyline) -> Unit)?,
    private var onMapClick: ((Coordinates) -> Unit)?,
    private var onMapLongClick: ((Coordinates) -> Unit)?,
    private var onPOIClick: ((Coordinates) -> Unit)?,
    private val markerMapping: MutableMap<GMSMarker, Marker>,
    private val circleMapping: MutableMap<GMSCircle, Circle>,
    private val polygonMapping: MutableMap<GMSPolygon, Polygon>,
    private val polylineMapping: MutableMap<GMSPolyline, Polyline>,
) : NSObject(), GMSMapViewDelegateProtocol {
    /**
     * Handles camera movement events when the map region changes.
     *
     * @param mapView The map view whose region changed
     * @param didChangeCameraPosition The new camera position
     */
    override fun mapView(mapView: GMSMapView, didChangeCameraPosition: GMSCameraPosition) {
        onCameraMove?.let {
            didChangeCameraPosition.target().useContents {
                val cameraPosition =
                    CameraPosition(
                        coordinates = Coordinates(latitude = latitude, longitude = longitude),
                        zoom = didChangeCameraPosition.zoom(),
                    )
                it(cameraPosition)
            }
        }
    }

    /**
     * Handles marker selection events when user taps on annotations.
     *
     * @param mapView The map view containing the annotation
     * @param didTapMarker The annotation that was selected
     * @return Whether the event was handled
     */
    override fun mapView(mapView: GMSMapView, didTapMarker: GMSMarker): Boolean {
        return onMarkerClick?.let {
            val marker = markerMapping[didTapMarker]
            if (marker != null) {
                mapView.setSelectedMarker(didTapMarker)
                it(marker)
                true
            } else {
                false
            }
        } ?: false
    }

    /**
     * Handles tap gestures on the map to detect clicks on overlays and map.
     *
     * @param mapView The map view containing the annotation
     * @param didTapAtCoordinate The coordinate that was tapped
     */
    @ObjCSignatureOverride
    override fun mapView(mapView: GMSMapView, didTapAtCoordinate: CValue<CLLocationCoordinate2D>) {
        onMapClick?.let {
            didTapAtCoordinate.useContents {
                val coordinates = Coordinates(latitude = latitude, longitude = longitude)
                it(coordinates)
            }
        }
    }

    /**
     * Handles long press gestures on the map.
     *
     * @param mapView The map view containing the annotation
     * @param didLongPressAtCoordinate The coordinate that was long pressed
     */
    @ObjCSignatureOverride
    override fun mapView(
        mapView: GMSMapView,
        didLongPressAtCoordinate: CValue<CLLocationCoordinate2D>,
    ) {
        onMapLongClick?.let {
            didLongPressAtCoordinate.useContents {
                val coordinates = Coordinates(latitude = latitude, longitude = longitude)
                it(coordinates)
            }
        }
    }

    /**
     * Handles POI tap events on the map.
     *
     * @param mapView The map view containing the annotation
     * @param didTapPOIWithPlaceID The place ID of the POI that was tapped
     * @param name The name of the POI
     * @param location The location of the POI
     */
    override fun mapView(
        mapView: GMSMapView,
        didTapPOIWithPlaceID: String,
        name: String,
        location: CValue<CLLocationCoordinate2D>,
    ) {
        onPOIClick?.let {
            location.useContents {
                val coordinates = Coordinates(latitude = latitude, longitude = longitude)
                it(coordinates)
            }
        }
    }

    /**
     * Handles overlay tap events on the map.
     *
     * @param mapView The map view containing the annotation
     * @param didTapOverlay The overlay that was tapped
     */
    override fun mapView(mapView: GMSMapView, didTapOverlay: GMSOverlay) {
        when (didTapOverlay) {
            is GMSCircle -> circleMapping[didTapOverlay]?.let { onCircleClick?.invoke(it) }
            is GMSPolygon -> polygonMapping[didTapOverlay]?.let { onPolygonClick?.invoke(it) }
            is GMSPolyline -> polylineMapping[didTapOverlay]?.let { onPolylineClick?.invoke(it) }
        }
    }

    fun onBitmapReady(id: String, image: UIImage) {
        dispatch_async(dispatch_get_main_queue()) {
            imageCache[id] = image
            renderingQueue.remove(id)

            val gmsMarker = markerMapping.entries.find { it.value.id == id }?.key
            gmsMarker?.let {
                it.setIcon(image)
                it.setTracksViewChanges(false)
            }

            if (id.startsWith("cluster_")) {
                clusterManager?.cluster()
            }
        }
    }

    fun getCachedImage(id: String): UIImage? = imageCache[id]

    fun pruneCache(activeIds: Set<String>) {
        val keysToRemove = imageCache.keys.filter { it !in activeIds }
        keysToRemove.forEach { imageCache.remove(it) }
    }
}
