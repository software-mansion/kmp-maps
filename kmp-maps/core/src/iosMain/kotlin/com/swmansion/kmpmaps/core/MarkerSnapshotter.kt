package com.swmansion.kmpmaps.core

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import kotlinx.coroutines.launch

@Composable
public fun MarkerSnapshotter(
    content: @Composable () -> Unit,
    onSnapshotReady: (ImageBitmap) -> Unit,
) {
    val graphicsLayer = rememberGraphicsLayer()
    val scope = rememberCoroutineScope()

    Box(
        modifier =
            Modifier.wrapContentSize()
                .onGloballyPositioned { coordinates ->
                    val size = coordinates.size
                    if (size.width > 0 && size.height > 0) {
                        scope.launch {
                            withFrameNanos {}
                            onSnapshotReady(graphicsLayer.toImageBitmap())
                        }
                    }
                }
                .drawWithContent {
                    graphicsLayer.record { this@drawWithContent.drawContent() }
                    drawContent()
                }
    ) {
        content()
    }
}
