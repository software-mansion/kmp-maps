package com.swmansion.kmpmaps

import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedWhenInUse
import platform.CoreLocation.kCLAuthorizationStatusNotDetermined
import platform.darwin.NSObject

/** iOS location permission handler for Apple Maps. */
@OptIn(ExperimentalForeignApi::class)
internal class LocationPermissionHandler : NSObject(), CLLocationManagerDelegateProtocol {
    private val locationManager = CLLocationManager()

    init {
        locationManager.delegate = this
    }

    /**
     * Checks if location permission is currently granted for "when in use" access.
     *
     * @return true if permission is granted, false otherwise
     */
    fun checkPermission() =
        locationManager.authorizationStatus == kCLAuthorizationStatusAuthorizedWhenInUse

    /**
     * Requests location permission from the user if not yet determined.
     *
     * Only requests permission if the current status is "not determined". If permission was
     * previously denied or granted, this method does nothing.
     */
    fun requestPermission() {
        if (locationManager.authorizationStatus == kCLAuthorizationStatusNotDetermined) {
            locationManager.requestWhenInUseAuthorization()
        }
    }
}
