package com.swmansion.kmpmaps.googlemaps

import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreLocation.CLAuthorizationStatus
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

    /**
     * Sets a callback to be invoked when the location permission changes.
     *
     * @param callback The callback function to be called when the permission changes
     */
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

    /**
     * Handles changes in location permission status.
     *
     * @param manager The location manager that generated the event
     * @param didChangeAuthorizationStatus The new authorization status
     */
    override fun locationManager(
        manager: CLLocationManager,
        didChangeAuthorizationStatus: CLAuthorizationStatus,
    ) {
        onPermissionChanged?.invoke()
    }
}
