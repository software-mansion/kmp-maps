package com.swmansion.kmpmaps.sample

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.swmansion.kmpmaps.core.MapConfiguration

fun main() = application {
    Window(title = "KMP Maps - Desktop", onCloseRequest = ::exitApplication) {
        MapConfiguration.initialize(googleMapsApiKey = "YOUR_API_KEY")
        App()
    }
}
