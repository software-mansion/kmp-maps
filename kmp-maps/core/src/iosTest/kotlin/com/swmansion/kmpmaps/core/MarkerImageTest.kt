package com.swmansion.kmpmaps.core

import androidx.compose.ui.graphics.ImageBitmap
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents

@OptIn(ExperimentalForeignApi::class)
class MarkerImageTest {
    @Test
    fun composeMarkerBitmapConvertsToNativeImage() {
        // Exercise the Skia encoder used by custom MapKit marker snapshots. Linking a
        // framework alone does not execute this path when partial linkage is enabled.
        val image = assertNotNull(ImageBitmap(8, 8).toUIImage())
        image.size.useContents {
            assertTrue(width > 0.0)
            assertTrue(height > 0.0)
        }
    }
}
