package com.swmansion.kmpmaps.core

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity

/**
 * Measures content size in dp and reports changes via [onSizeChanged].
 *
 * Content size is determined by [wrapContentSize], and the callback is invoked whenever the layout
 * size changes. Dimensions are provided in dp.
 *
 * @param onSizeChanged Callback invoked with width and height in dp on size changes.
 * @param content The composable content to measure.
 */
@Composable
public fun AutoSizeBox(
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
