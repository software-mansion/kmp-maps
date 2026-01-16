package com.swmansion.kmpmaps.sample

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MapsScreen(map: @Composable (Modifier) -> Unit, controls: @Composable () -> Unit) {
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }

    if (isJvm()) {
        Row(Modifier.fillMaxSize()) {
            map(Modifier.weight(1f).fillMaxHeight())
            Column(
                Modifier.width(370.dp)
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                Arrangement.spacedBy(8.dp),
                Alignment.CenterHorizontally,
            ) {
                Text("Settings", style = MaterialTheme.typography.headlineSmall)
                controls()
            }
        }
    } else {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = { showBottomSheet = true }) {
                    Icon(imageVector = Icons.Filled.Settings, contentDescription = "Settings")
                }
            }
        ) { padding ->
            map(Modifier.fillMaxSize().padding(padding))
        }
        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = bottomSheetState,
            ) {
                Column(
                    Modifier.fillMaxWidth()
                        .windowInsetsPadding(
                            WindowInsets.safeDrawing.exclude(WindowInsets.statusBars)
                        )
                        .padding(vertical = 16.dp),
                    Arrangement.spacedBy(8.dp),
                    Alignment.CenterHorizontally,
                ) {
                    controls()
                }
            }
        }
    }
}
