package com.swmansion.kmpmaps.core

import androidx.compose.ui.ExperimentalComposeUiApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGPointMake
import platform.MapKit.MKAnnotationProtocol
import platform.MapKit.MKAnnotationView
import platform.UIKit.UIImage

/** Custom [MKAnnotationView] that displays a [UIImage]. */
@OptIn(ExperimentalForeignApi::class)
internal class CustomMarkers(annotation: MKAnnotationProtocol, reuseIdentifier: String?) :
    MKAnnotationView(annotation = annotation, reuseIdentifier = reuseIdentifier) {

    /**
     * Sets [uiImage] as the annotation icon and adjusts [centerOffset] so the pin base aligns with
     * the coordinate.
     */
    @OptIn(ExperimentalComposeUiApi::class)
    fun setMarkerImage(uiImage: UIImage?) {
        this.image = uiImage
        if (uiImage != null) {
            val h = uiImage.size.useContents { height }
            this.centerOffset = CGPointMake(0.0, -h / 2.0)
        }
    }
}
