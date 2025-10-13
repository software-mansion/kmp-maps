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
    private var onPermissionChanged: (() -> Unit)? = null

    init {
        locationManager.delegate = this
    }

    fun setOnPermissionChanged(callback: () -> Unit) {
        onPermissionChanged = callback
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

    override fun locationManager(
        manager: CLLocationManager,
        didChangeAuthorizationStatus: platform.CoreLocation.CLAuthorizationStatus,
    ) {
        onPermissionChanged?.invoke()
    }
}
