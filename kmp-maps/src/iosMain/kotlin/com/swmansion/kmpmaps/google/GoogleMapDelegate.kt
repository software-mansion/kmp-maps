package com.swmansion.kmpmaps.google

import cocoapods.GoogleMaps.GMSCameraPosition
import cocoapods.GoogleMaps.GMSCircle
import cocoapods.GoogleMaps.GMSMapView
import cocoapods.GoogleMaps.GMSMapViewDelegateProtocol
import cocoapods.GoogleMaps.GMSMarker
import cocoapods.GoogleMaps.GMSPolygon
import cocoapods.GoogleMaps.GMSPolyline
import com.swmansion.kmpmaps.CameraPosition
import com.swmansion.kmpmaps.Circle
import com.swmansion.kmpmaps.Coordinates
import com.swmansion.kmpmaps.Marker
import com.swmansion.kmpmaps.Polygon
import com.swmansion.kmpmaps.Polyline
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCSignatureOverride
import kotlinx.cinterop.useContents
import platform.CoreLocation.CLLocationCoordinate2D
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class)
internal class GoogleMapDelegate(
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

    override fun mapView(mapView: GMSMapView, didChangeCameraPosition: GMSCameraPosition): Unit =
        didChangeCameraPosition.target.useContents {
            val cameraPosition =
                CameraPosition(
                    coordinates = Coordinates(latitude = latitude, longitude = longitude),
                    zoom = didChangeCameraPosition.zoom,
                )
            onCameraMove?.invoke(cameraPosition)
        }

    override fun mapView(mapView: GMSMapView, didTapMarker: GMSMarker): Boolean {
        val marker = markerMapping[didTapMarker]
        return if (marker != null) {
            onMarkerClick?.invoke(marker)
            true
        } else {
            false
        }
    }

    @ObjCSignatureOverride
    override fun mapView(mapView: GMSMapView, didTapAtCoordinate: CValue<CLLocationCoordinate2D>) {
        didTapAtCoordinate.useContents {
            val coordinates = Coordinates(latitude = latitude, longitude = longitude)
            onMapClick?.invoke(coordinates)
        }
    }

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
}
