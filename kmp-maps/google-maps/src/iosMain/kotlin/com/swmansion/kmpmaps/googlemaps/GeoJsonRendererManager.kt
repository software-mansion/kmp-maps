package com.swmansion.kmpmaps.googlemaps

import androidx.compose.ui.graphics.Color
import cocoapods.GoogleMaps.GMSMapView
import cocoapods.Google_Maps_iOS_Utils.GMSMapView as UtilsGMSMapView
import cocoapods.Google_Maps_iOS_Utils.GMUFeature
import cocoapods.Google_Maps_iOS_Utils.GMUGeoJSONParser
import cocoapods.Google_Maps_iOS_Utils.GMUGeometryRenderer
import cocoapods.Google_Maps_iOS_Utils.GMULineString
import cocoapods.Google_Maps_iOS_Utils.GMUPoint
import cocoapods.Google_Maps_iOS_Utils.GMUPolygon
import cocoapods.Google_Maps_iOS_Utils.GMUStyle
import com.swmansion.kmpmaps.core.GeoJsonLayer
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreGraphics.CGPointMake
import platform.Foundation.NSData
import platform.Foundation.NSDictionary
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create
import platform.Foundation.dataUsingEncoding
import platform.Foundation.valueForKey
import platform.UIKit.UIColor

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
internal class GeoJsonRendererManager {
    private var mapView: UtilsGMSMapView? = null
    private var renderers: Map<Int, GMUGeometryRenderer> = emptyMap()

    fun attach(view: GMSMapView) {
        mapView = view as UtilsGMSMapView
    }

    fun clear() {
        renderers.values.forEach { it.clear() }
        renderers = emptyMap()
    }

    fun render(layers: List<GeoJsonLayer>) {
        val view = mapView ?: return

        val desired = layers.indices.toSet()
        val toRemove = renderers.keys - desired
        toRemove.forEach { idx -> renderers[idx]?.clear() }
        renderers = renderers.filterKeys { it in desired }

        layers.forEachIndexed { index, layer ->
            renderers[index]?.clear()
            if (layer.visible != true) {
                renderers = renderers - index
                return@forEachIndexed
            }

            val data: NSData =
                NSString.create(string = layer.geoJson).dataUsingEncoding(NSUTF8StringEncoding)!!
            val parser = GMUGeoJSONParser(data = data)
            parser.parse()

            val lineStrokeColor = layer.lineColor.toUIColor(UIColor.blackColor)
            val polygonStrokeColor = layer.strokeColor.toUIColor(UIColor.blackColor)
            val fillColor = layer.fillColor.toUIColor(UIColor.clearColor)

            val lineWidth = (layer.lineWidth ?: 2f).toDouble()
            val strokeWidth = (layer.strokeWidth ?: 2f).toDouble()

            val anchorU = layer.anchorU.toDouble()
            val anchorV = layer.anchorV.toDouble()
            val rotation = layer.rotation.toDouble()
            val pointTitle = layer.pointTitle

            val lineStyle =
                GMUStyle(
                    styleID = "line_$index",
                    strokeColor = lineStrokeColor,
                    fillColor = UIColor.clearColor,
                    width = lineWidth,
                    scale = 1.0,
                    heading = 0.0,
                    anchor = CGPointMake(0.5, 1.0),
                    iconUrl = null,
                    title = null,
                    hasFill = false,
                    hasStroke = true,
                )

            val polygonStyle =
                GMUStyle(
                    styleID = "polygon_$index",
                    strokeColor = polygonStrokeColor,
                    fillColor = fillColor,
                    width = strokeWidth,
                    scale = 1.0,
                    heading = 0.0,
                    anchor = CGPointMake(0.5, 1.0),
                    iconUrl = null,
                    title = null,
                    hasFill = layer.fillColor != null,
                    hasStroke = true,
                )

            val pointStyle =
                GMUStyle(
                    styleID = "point_$index",
                    strokeColor = UIColor.clearColor,
                    fillColor = UIColor.clearColor,
                    width = 1.0,
                    scale = 1.0,
                    heading = rotation,
                    anchor = CGPointMake(anchorU, anchorV),
                    iconUrl = null,
                    title = pointTitle,
                    hasFill = false,
                    hasStroke = false,
                )

            var featureIdx = 0
            parser.features.forEach { feature ->
                val f = feature as? GMUFeature ?: return@forEach
                when (f.geometry) {
                    is GMULineString -> f.style = lineStyle
                    is GMUPolygon -> f.style = polygonStyle
                    is GMUPoint -> {
                        val dict = f.properties as? NSDictionary
                        val titleFromJson =
                            (dict?.valueForKey("title") as? String)
                                ?: (dict?.valueForKey("name") as? String)
                                ?: pointTitle

                        val featurePointStyle =
                            GMUStyle(
                                styleID = "point_${index}_$featureIdx",
                                strokeColor = UIColor.clearColor,
                                fillColor = UIColor.clearColor,
                                width = 1.0,
                                scale = 1.0,
                                heading = rotation,
                                anchor = CGPointMake(anchorU, anchorV),
                                iconUrl = null,
                                title = titleFromJson,
                                hasFill = false,
                                hasStroke = false,
                            )
                        f.style = featurePointStyle
                        featureIdx += 1
                    }
                    else -> f.style = polygonStyle
                }
            }

            val renderer =
                GMUGeometryRenderer(
                    map = view,
                    geometries = parser.features,
                    styles = listOf(lineStyle, polygonStyle, pointStyle),
                )
            renderer.render()

            renderers = renderers + (index to renderer)
        }
    }

    private fun Color?.toUIColor(fallback: UIColor): UIColor =
        if (this == null) fallback
        else
            UIColor(
                red = this.red.toDouble(),
                green = this.green.toDouble(),
                blue = this.blue.toDouble(),
                alpha = this.alpha.toDouble(),
            )
}
