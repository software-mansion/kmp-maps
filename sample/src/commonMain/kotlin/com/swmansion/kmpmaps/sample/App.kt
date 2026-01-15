package com.swmansion.kmpmaps.sample

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.swmansion.kmpmaps.core.GeoJsonLayer
import com.swmansion.kmpmaps.core.MapConfiguration
import com.swmansion.kmpmaps.core.PointStyle
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    var options by remember { mutableStateOf(MapOptions()) }

    LaunchedEffect(Unit) {
        MapConfiguration.initialize(googleMapsApiKey = BuildKonfig.MAPS_API_KEY)
    }

    MaterialTheme(
        if (isSystemInDarkTheme() && !isJvm()) darkColorScheme() else lightColorScheme()
    ) {
        val geoJsonLayers =
            remember(
                options.showPointGeoJson,
                options.showPolygonGeoJson,
                options.showLineGeoJson,
            ) {
                buildList {
                    if (options.showPointGeoJson) {
                        add(
                            GeoJsonLayer(
                                geoJson = EXAMPLE_POINT_GEO_JSON,
                                pointStyle = PointStyle(snippet = "Recommended food places"),
                            )
                        )
                    }
                    if (options.showPolygonGeoJson) {
                        add(GeoJsonLayer(geoJson = EXAMPLE_POLYGON_GEO_JSON))
                    }
                    if (options.showLineGeoJson) {
                        add(GeoJsonLayer(geoJson = EXAMPLE_LINE_GEO_JSON))
                    }
                }
            }

        MapsScreen(
            map = { MapWrapper(modifier = it, options = options, geoJsonLayers = geoJsonLayers) },
            controls = { MapSettingsControls(options) { options = options.it() } },
        )
    }
}
