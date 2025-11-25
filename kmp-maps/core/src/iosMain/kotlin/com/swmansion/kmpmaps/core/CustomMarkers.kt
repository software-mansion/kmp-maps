package com.swmansion.kmpmaps.core

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.ComposeUIViewController
import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreGraphics.CGPointMake
import platform.CoreGraphics.CGRectMake
import platform.MapKit.MKAnnotationProtocol
import platform.MapKit.MKAnnotationView
import platform.UIKit.NSLayoutConstraint
import platform.UIKit.UIView
import platform.UIKit.UIViewController
import platform.UIKit.addChildViewController
import platform.UIKit.didMoveToParentViewController
import platform.UIKit.removeFromParentViewController
import platform.UIKit.willMoveToParentViewController

@OptIn(ExperimentalForeignApi::class)
internal class CustomMarkers(annotation: MKAnnotationProtocol, reuseIdentifier: String?) :
    MKAnnotationView(annotation = annotation, reuseIdentifier = reuseIdentifier) {
    private var composeViewController: UIViewController? = null

    fun updateContent(content: @Composable () -> Unit) {
        composeViewController?.view?.removeFromSuperview()
        composeViewController?.removeFromParentViewController()

        val viewController = ComposeUIViewController { content() }
        composeViewController = viewController

        val composeView = viewController.view
        composeView.translatesAutoresizingMaskIntoConstraints = false
        addSubview(composeView)

        NSLayoutConstraint.activateConstraints(
            listOf(
                composeView.leadingAnchor.constraintEqualToAnchor(leadingAnchor),
                composeView.trailingAnchor.constraintEqualToAnchor(trailingAnchor),
                composeView.topAnchor.constraintEqualToAnchor(topAnchor),
                composeView.bottomAnchor.constraintEqualToAnchor(bottomAnchor),
            )
        )

        setFrame(CGRectMake(0.0, 0.0, 50.0, 50.0))
        centerOffset = CGPointMake(0.0, 0.0)

        var parentVC: UIViewController? = null
        var currentView: UIView? = superview

        while (currentView != null && parentVC == null) {
            val responder = currentView.nextResponder
            parentVC = responder as? UIViewController
            currentView = currentView.superview
        }

        parentVC?.addChildViewController(viewController)
        viewController.didMoveToParentViewController(parentVC)
    }

    override fun prepareForReuse() {
        super.prepareForReuse()
        composeViewController?.let { vc ->
            vc.view.removeFromSuperview()
            vc.willMoveToParentViewController(null)
            vc.removeFromParentViewController()
        }
        composeViewController = null
    }
}
