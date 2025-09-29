package com.swmansion.kmpmaps

import android.content.Context
import android.content.res.Configuration

/**
 * Detects the current system theme setting on Android.
 *
 * @param context Android context to access system configuration
 * @return true if dark theme is enabled, false otherwise
 */
internal fun isSystemDarkTheme(context: Context): Boolean {
    val nightModeFlags = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
    return nightModeFlags == Configuration.UI_MODE_NIGHT_YES
}
