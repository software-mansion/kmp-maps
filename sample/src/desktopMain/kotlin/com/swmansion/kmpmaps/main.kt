package com.swmansion.kmpmaps

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.swmansion.kmpmaps.sample.App

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "kmp-maps") { App() }
}
