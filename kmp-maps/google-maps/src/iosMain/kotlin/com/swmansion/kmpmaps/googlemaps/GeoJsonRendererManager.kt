package com.swmansion.kmpmaps.googlemaps

import cocoapods.Google_Maps_iOS_Utils.GMSMapView as UtilsGMSMapView
import cocoapods.Google_Maps_iOS_Utils.GMUFeature
import cocoapods.Google_Maps_iOS_Utils.GMUGeoJSONParser
import cocoapods.Google_Maps_iOS_Utils.GMUGeometryRenderer
import cocoapods.Google_Maps_iOS_Utils.GMULineString
import cocoapods.Google_Maps_iOS_Utils.GMUPoint
import cocoapods.Google_Maps_iOS_Utils.GMUPolygon
import cocoapods.Google_Maps_iOS_Utils.GMUStyle
import com.swmansion.kmpmaps.core.ClusterSettings
import com.swmansion.kmpmaps.core.Coordinates
import com.swmansion.kmpmaps.core.GeoJsonLayer
import com.swmansion.kmpmaps.core.Marker
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGPointMake
import platform.Foundation.NSData
import platform.Foundation.NSDictionary
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create
import platform.Foundation.dataUsingEncoding
import platform.UIKit.UIColor

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
internal class GeoJsonRendererManager {
    private var mapView: UtilsGMSMapView? = null
    private var renderers: Map<Int, GMUGeometryRenderer> = emptyMap()

    fun attach(view: UtilsGMSMapView) {
        mapView = view
    }

    fun clear() {
        renderers.values.forEach(GMUGeometryRenderer::clear)
        renderers = emptyMap()
    }

    fun render(layers: List<GeoJsonLayer>, clusterSettings: ClusterSettings): List<Marker> {
        val view = mapView ?: return emptyList()
        val allExtractedMarkers = mutableListOf<Marker>()

        val desired = layers.indices.toSet()
        val toRemove = renderers.keys - desired
        toRemove.forEach { idx -> renderers[idx]?.clear() }
        renderers = renderers.filterKeys(desired::contains)

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

            val lineStrokeColor = layer.lineStringStyle?.lineColor.toUIColor(UIColor.blackColor)
            val polygonStrokeColor = layer.polygonStyle?.strokeColor.toUIColor(UIColor.blackColor)
            val fillColor = layer.polygonStyle?.fillColor.toUIColor(UIColor.clearColor)

            val lineWidth = (layer.lineStringStyle?.lineWidth ?: 2f).toDouble()
            val strokeWidth = (layer.polygonStyle?.strokeWidth ?: 2f).toDouble()

            val anchorU = layer.pointStyle?.anchorU?.toDouble()
            val anchorV = layer.pointStyle?.anchorV?.toDouble()
            val rotation = layer.pointStyle?.rotation?.toDouble()
            val pointTitle = layer.pointStyle?.pointTitle

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
                    hasFill = layer.polygonStyle?.fillColor != null,
                    hasStroke = true,
                )

            val pointStyle =
                GMUStyle(
                    styleID = "point_$index",
                    strokeColor = UIColor.clearColor,
                    fillColor = UIColor.clearColor,
                    width = 1.0,
                    scale = 1.0,
                    heading = rotation ?: 0.0,
                    anchor = CGPointMake(anchorU ?: 0.5, anchorV ?: 0.5),
                    iconUrl = null,
                    title = pointTitle,
                    hasFill = false,
                    hasStroke = false,
                )

            var featureIdx = 0
            val featuresToRender = mutableListOf<GMUFeature>()
            val features = parser.features as? List<GMUFeature> ?: emptyList()

            features.forEach { feature ->
                val dict = feature.properties as? NSDictionary

                if (feature.geometry is GMUPoint && clusterSettings.enabled) {
                    val point = feature.geometry as GMUPoint

                    val coordinates =
                        point.coordinate.useContents { Coordinates(latitude, longitude) }
                    val title = getString(dict, "title")

                    val marker = Marker(coordinates = coordinates, title = title)
                    allExtractedMarkers.add(marker)
                    return@forEach
                }

                val strokeHex = getString(dict, "stroke")
                val strokeOpacity = getDouble(dict, "stroke-opacity")?.coerceIn(0.0, 1.0)
                val strokeWidthJson = getDouble(dict, "stroke-width")

                val fillHex = getString(dict, "fill")
                val fillOpacity = getDouble(dict, "fill-opacity")?.coerceIn(0.0, 1.0)

                when (feature.geometry) {
                    is GMULineString -> {
                        val strokeUIColor =
                            (parseHexToUIColor(strokeHex) ?: lineStrokeColor).let { c ->
                                if (strokeOpacity != null) {
                                    c.colorWithAlphaComponent(strokeOpacity)
                                } else {
                                    c
                                }
                            }
                        val width = strokeWidthJson ?: lineWidth

                        val featureLineStyle =
                            GMUStyle(
                                styleID = "line_${index}_$featureIdx",
                                strokeColor = strokeUIColor,
                                fillColor = UIColor.clearColor,
                                width = width,
                                scale = 1.0,
                                heading = 0.0,
                                anchor = CGPointMake(0.5, 1.0),
                                iconUrl = null,
                                title = null,
                                hasFill = false,
                                hasStroke = true,
                            )
                        feature.style = featureLineStyle
                        featureIdx += 1
                    }
                    is GMUPolygon -> {
                        val strokeUIColor =
                            (parseHexToUIColor(strokeHex) ?: polygonStrokeColor).let { c ->
                                if (strokeOpacity != null) {
                                    c.colorWithAlphaComponent(strokeOpacity)
                                } else {
                                    c
                                }
                            }
                        val width = strokeWidthJson ?: strokeWidth

                        val finalFill =
                            (parseHexToUIColor(fillHex) ?: fillColor).let { c ->
                                if (fillOpacity != null) {
                                    c.colorWithAlphaComponent(fillOpacity)
                                } else {
                                    c
                                }
                            }
                        val hasFill = fillHex != null || layer.polygonStyle?.fillColor != null

                        val featurePolygonStyle =
                            GMUStyle(
                                styleID = "polygon_${index}_$featureIdx",
                                strokeColor = strokeUIColor,
                                fillColor = finalFill,
                                width = width,
                                scale = 1.0,
                                heading = 0.0,
                                anchor = CGPointMake(0.5, 1.0),
                                iconUrl = null,
                                title = null,
                                hasFill = hasFill,
                                hasStroke = true,
                            )
                        feature.style = featurePolygonStyle
                        featureIdx += 1
                    }
                    is GMUPoint -> {
                        val titleFromJson = getString(dict, "title") ?: getString(dict, "name")

                        val featurePointStyle =
                            GMUStyle(
                                styleID = "point_${index}_$featureIdx",
                                strokeColor = UIColor.clearColor,
                                fillColor = UIColor.clearColor,
                                width = 1.0,
                                scale = 1.0,
                                heading = rotation ?: 0.0,
                                anchor = CGPointMake(anchorU ?: 0.5, anchorV ?: 0.5),
                                iconUrl = null,
                                title = titleFromJson,
                                hasFill = false,
                                hasStroke = false,
                            )
                        feature.style = featurePointStyle
                        featureIdx += 1
                    }
                    else -> {
                        feature.style = polygonStyle
                    }
                }
                featuresToRender.add(feature)
            }

            val renderer =
                GMUGeometryRenderer(
                    map = view,
                    geometries = featuresToRender,
                    styles = listOf(lineStyle, polygonStyle, pointStyle),
                )
            renderer.render()

            renderers = renderers + (index to renderer)
        }
        return allExtractedMarkers
    }
}
