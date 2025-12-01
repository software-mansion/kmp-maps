package com.swmansion.kmpmaps.core

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.ComposeUIViewController
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGPointMake
import platform.CoreGraphics.CGRectMake
import platform.MapKit.MKAnnotationProtocol
import platform.MapKit.MKAnnotationView
import platform.UIKit.UIColor
import platform.UIKit.UIViewController

private const val MAX_DIM = 500.0

@OptIn(ExperimentalForeignApi::class)
internal class CustomMarkers(annotation: MKAnnotationProtocol, reuseIdentifier: String?) :
    MKAnnotationView(annotation = annotation, reuseIdentifier = reuseIdentifier) {
    private var viewController: UIViewController? = null

    fun updateContent(content: @Composable () -> Unit) {
        viewController?.view?.removeFromSuperview()

        val vc = ComposeUIViewController { AutoSizeBox(::updateFrameSize) { content() } }

        vc.view.backgroundColor = UIColor.clearColor
        vc.view.opaque = false

        addSubview(vc.view)
        viewController = vc

        setFrame(CGRectMake(0.0, 0.0, MAX_DIM, MAX_DIM))
        vc.view.setFrame(bounds)
    }

    override fun prepareForReuse() {
        super.prepareForReuse()
        viewController?.view?.removeFromSuperview()
        viewController = null
    }

    private fun updateFrameSize(width: Double, height: Double) {
        val currentOrigin = frame.useContents { origin }
        setFrame(CGRectMake(currentOrigin.x, currentOrigin.y, width, height))
        viewController?.view?.setFrame(bounds)
        centerOffset = CGPointMake(0.0, -height / 2.0)
    }
}
