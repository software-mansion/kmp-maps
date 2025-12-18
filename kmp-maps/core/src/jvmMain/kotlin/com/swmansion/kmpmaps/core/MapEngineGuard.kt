package com.swmansion.kmpmaps.core

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import dev.datlag.kcef.KCEF
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
internal fun MapEngineGuard(
    loadingContent: @Composable () -> Unit = { Text("Initializing Map Engine...") },
    content: @Composable () -> Unit,
) {
    var isReady by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            KCEF.init(
                builder = {
                    installDir(File("kcef-bundle"))
                    progress { onInitialized { isReady = true } }
                    settings { noSandbox = true }
                },
                onError = { it?.printStackTrace() },
            )
        }
    }

    if (isReady) {
        content()
    } else {
        loadingContent()
    }
}
