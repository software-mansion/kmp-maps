package com.swmansion.kmpmaps

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

private const val LOCATION_PERMISSION_REQUEST_CODE = 1001

/** Android location permission handler for Google Maps. */
internal class LocationPermissionHandler(private val context: Context) {

    private var hasLocationPermission = false

    init {
        checkPermission()
    }

    /**
     * Requests location permission from the user if not already granted.
     *
     * Only requests permission if the context is an Activity and permission is not granted.
     * Requests both FINE and COARSE location permissions.
     */
    fun requestPermission() {
        val granted = checkPermission()

        if (!granted && context is Activity) {
            ActivityCompat.requestPermissions(
                context,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                ),
                LOCATION_PERMISSION_REQUEST_CODE,
            )
        }
    }

    /**
     * Checks if location permission is currently granted.
     *
     * Checks for either FINE or COARSE location permission.
     *
     * @return true if either permission is granted, false otherwise
     */
    fun checkPermission(): Boolean {
        hasLocationPermission =
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                ) == PackageManager.PERMISSION_GRANTED
        return hasLocationPermission
    }
}
