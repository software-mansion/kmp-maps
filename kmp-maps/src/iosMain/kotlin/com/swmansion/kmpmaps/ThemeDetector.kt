package com.swmansion.kmpmaps

import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UITraitCollection
import platform.UIKit.UIUserInterfaceStyle
import platform.UIKit.currentTraitCollection

/**
 * Detects the current system theme setting on iOS.
 *
 * @return true if dark theme is enabled, false otherwise
 */
@OptIn(ExperimentalForeignApi::class)
internal fun isSystemDarkTheme(): Boolean {
    val traitCollection = UITraitCollection.currentTraitCollection
    return traitCollection.userInterfaceStyle == UIUserInterfaceStyle.UIUserInterfaceStyleDark
}
