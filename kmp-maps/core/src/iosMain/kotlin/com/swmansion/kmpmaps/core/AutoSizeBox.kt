package com.swmansion.kmpmaps.core

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity

@Composable
internal fun AutoSizeBox(
    onSizeChanged: (width: Double, height: Double) -> Unit,
    content: @Composable () -> Unit,
) {
    val density = LocalDensity.current
    Box(
        modifier =
            Modifier.wrapContentSize().onGloballyPositioned { coords ->
                with(density) {
                    onSizeChanged(
                        coords.size.width.toDp().value.toDouble(),
                        coords.size.height.toDp().value.toDouble(),
                    )
                }
            }
    ) {
        content()
    }
}
