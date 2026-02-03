package com.swmansion.kmpmaps.core

import androidx.annotation.RestrictTo
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import kotlinx.coroutines.launch

/**
 * A helper component designed to convert Composable content into an [ImageBitmap].
 *
 * @param content The Composable content to be "photographed."
 * @param onSnapshotReady A callback triggered once the bitmap is generated.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
@Composable
public fun MarkerSnapshotter(
    content: @Composable () -> Unit,
    onSnapshotReady: (ImageBitmap) -> Unit,
) {
    val graphicsLayer = rememberGraphicsLayer()
    val scope = rememberCoroutineScope()
    var isReadyToCapture by remember { mutableStateOf(false) }

    Box(
        modifier =
            Modifier.wrapContentSize()
                .onGloballyPositioned { coordinates ->
                    if (coordinates.size.width > 0 && coordinates.size.height > 0) {
                        isReadyToCapture = true
                    }
                }
                .drawWithContent {
                    graphicsLayer.record { this@drawWithContent.drawContent() }
                    drawContent()
                    if (isReadyToCapture) {
                        isReadyToCapture = false
                        scope.launch {
                            val bitmap = graphicsLayer.toImageBitmap()
                            onSnapshotReady(bitmap)
                        }
                    }
                }
    ) {
        content()
    }
}
