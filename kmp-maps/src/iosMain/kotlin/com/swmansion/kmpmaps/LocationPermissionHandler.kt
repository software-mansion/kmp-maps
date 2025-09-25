package com.swmansion.kmpmaps

import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedWhenInUse
import platform.CoreLocation.kCLAuthorizationStatusNotDetermined
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class)
internal class LocationPermissionHandler : NSObject(), CLLocationManagerDelegateProtocol {
    private val locationManager = CLLocationManager()

    init {
        locationManager.delegate = this
    }

    fun checkPermission() =
        locationManager.authorizationStatus == kCLAuthorizationStatusAuthorizedWhenInUse

    fun requestPermission() {
        if (locationManager.authorizationStatus == kCLAuthorizationStatusNotDetermined) {
            locationManager.requestWhenInUseAuthorization()
        }
    }
}
