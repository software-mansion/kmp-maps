package com.swmansion.kmpmaps.googlemaps

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeUIViewController
import cocoapods.GoogleMaps.GMSMarker
import com.swmansion.kmpmaps.core.AutoSizeBox
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGRectMake
import platform.UIKit.UIView
import platform.UIKit.UIViewController

private const val MAX_DIM = 500.0

@OptIn(ExperimentalForeignApi::class)
internal class CustomMarkers(private val gmsMarker: GMSMarker) :
    UIView(frame = CGRectMake(0.0, 0.0, MAX_DIM, MAX_DIM)) {

    private var controller: UIViewController? = null

    @OptIn(ExperimentalComposeUiApi::class)
    fun setContent(content: @Composable () -> Unit) {
        controller?.view?.removeFromSuperview()

        val vc =
            ComposeUIViewController(configure = { opaque = false }) {
                AutoSizeBox(onSizeChanged = ::updateFrameSize) { content() }
            }

        addSubview(vc.view)
        controller = vc

        vc.view.setFrame(bounds)
    }

    private fun updateFrameSize(width: Double, height: Double) {
        val currentOrigin = frame.useContents { origin }
        setFrame(CGRectMake(currentOrigin.x, currentOrigin.y, width, height))
        controller?.view?.setFrame(bounds)
        gmsMarker.groundAnchor = platform.CoreGraphics.CGPointMake(0.5, 1.0)
    }
}
