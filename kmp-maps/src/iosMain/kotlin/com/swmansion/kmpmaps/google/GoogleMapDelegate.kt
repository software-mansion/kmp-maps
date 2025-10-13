package com.swmansion.kmpmaps.google

import cocoapods.GoogleMaps.GMSCameraPosition
import cocoapods.GoogleMaps.GMSMapView
import cocoapods.GoogleMaps.GMSMapViewDelegateProtocol
import com.swmansion.kmpmaps.CameraPosition
import com.swmansion.kmpmaps.Coordinates
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class)
internal class GoogleMapDelegate(private var onCameraMove: ((CameraPosition) -> Unit)?) :
    NSObject(), GMSMapViewDelegateProtocol {

    override fun mapView(mapView: GMSMapView, didChangeCameraPosition: GMSCameraPosition): Unit =
        didChangeCameraPosition.target.useContents {
            val cameraPosition =
                CameraPosition(
                    coordinates = Coordinates(latitude = latitude, longitude = longitude),
                    zoom = didChangeCameraPosition.zoom,
                )
            onCameraMove?.invoke(cameraPosition)
        }
}
