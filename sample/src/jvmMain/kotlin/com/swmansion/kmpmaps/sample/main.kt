package com.swmansion.kmpmaps.sample

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(title = "KMP Maps - Desktop", onCloseRequest = ::exitApplication) { App() }
}
