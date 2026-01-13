package com.swmansion.kmpmaps.sample

import androidx.compose.material.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import dev.datlag.kcef.KCEF
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

fun main() = application {
    Window(title = "KMP Maps - Desktop", onCloseRequest = ::exitApplication) {
        var initialized by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            withContext(Dispatchers.IO) {
                KCEF.init(
                    builder = {
                        installDir(File("kcef-bundle"))
                        progress { onInitialized { initialized = true } }
                        settings { noSandbox = true }
                    },
                    onError = { it?.printStackTrace() },
                )
            }
        }

        if (initialized) {
            App()
        } else {
            Text("Initializing Map Engine...")
        }
    }
}
