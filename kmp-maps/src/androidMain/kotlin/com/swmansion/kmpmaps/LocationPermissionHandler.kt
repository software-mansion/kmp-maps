package com.swmansion.kmpmaps

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

private const val LOCATION_PERMISSION_REQUEST_CODE = 1001

internal class LocationPermissionHandler(private val context: Context) {

    private var hasLocationPermission = false

    init {
        checkPermission()
    }

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
