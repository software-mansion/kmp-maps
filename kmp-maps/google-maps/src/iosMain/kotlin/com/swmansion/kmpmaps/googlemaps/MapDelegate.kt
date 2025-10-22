package com.swmansion.kmpmaps.googlemaps

import cocoapods.GoogleMaps.GMSCameraPosition
import cocoapods.GoogleMaps.GMSCircle
import cocoapods.GoogleMaps.GMSMapView
import cocoapods.GoogleMaps.GMSMapViewDelegateProtocol
import cocoapods.GoogleMaps.GMSMarker
import cocoapods.GoogleMaps.GMSOverlay
import cocoapods.GoogleMaps.GMSPolygon
import cocoapods.GoogleMaps.GMSPolyline
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
import platform.darwin.NSObject

/** iOS map delegate for handling Google Maps interactions and rendering. */
@OptIn(ExperimentalForeignApi::class)
internal class MapDelegate(
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
        didChangeCameraPosition.target.useContents {
            val cameraPosition =
                CameraPosition(
                    coordinates = Coordinates(latitude = latitude, longitude = longitude),
                    zoom = didChangeCameraPosition.zoom,
                )
            onCameraMove?.invoke(cameraPosition)
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
        val marker = markerMapping[didTapMarker]
        return if (marker != null) {
            mapView.selectedMarker = didTapMarker
            onMarkerClick?.invoke(marker)
            true
        } else {
            false
        }
    }

    /**
     * Handles tap gestures on the map to detect clicks on overlays and map.
     *
     * @param mapView The map view containing the annotation
     * @param didTapAtCoordinate The coordinate that was tapped
     */
    @ObjCSignatureOverride
    override fun mapView(mapView: GMSMapView, didTapAtCoordinate: CValue<CLLocationCoordinate2D>) {
        didTapAtCoordinate.useContents {
            val coordinates = Coordinates(latitude = latitude, longitude = longitude)
            onMapClick?.invoke(coordinates)
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
        didLongPressAtCoordinate.useContents {
            val coordinates = Coordinates(latitude = latitude, longitude = longitude)
            onMapLongClick?.invoke(coordinates)
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
        location.useContents {
            val coordinates = Coordinates(latitude = latitude, longitude = longitude)
            onPOIClick?.invoke(coordinates)
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
}
