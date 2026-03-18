package com.swmansion.kmpmaps.googlemaps

import cocoapods.Google_Maps_iOS_Utils.GMSMapView as UtilsGMSMapView
import cocoapods.Google_Maps_iOS_Utils.GMSOverlay
import cocoapods.Google_Maps_iOS_Utils.GMUFeature
import cocoapods.Google_Maps_iOS_Utils.GMUGeoJSONParser
import cocoapods.Google_Maps_iOS_Utils.GMUGeometryCollection
import cocoapods.Google_Maps_iOS_Utils.GMUGeometryRenderer
import cocoapods.Google_Maps_iOS_Utils.GMULineString
import cocoapods.Google_Maps_iOS_Utils.GMUPoint
import cocoapods.Google_Maps_iOS_Utils.GMUPolygon
import cocoapods.Google_Maps_iOS_Utils.GMUStyle
import cocoapods.Google_Maps_iOS_Utils.mapOverlays
import com.swmansion.kmpmaps.core.Coordinates
import com.swmansion.kmpmaps.core.GeoJsonLayer
import com.swmansion.kmpmaps.core.Marker
import kotlin.collections.emptyList
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

/**
 * Manages lifecycle and rendering of GeoJSON layers on an iOS Google Maps view.
 *
 * Maintains a map of [GMUGeometryRenderer] instances keyed by layer index so that individual layers
 * can be updated or removed without re-rendering unaffected layers.
 */
@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
internal class GeoJsonRendererManager {
    private var mapView: UtilsGMSMapView? = null
    private var renderers: Map<Int, List<GMUGeometryRenderer>> = emptyMap()

    /** Attaches this manager to [view] so subsequent [render] calls draw on it. */
    fun attach(view: UtilsGMSMapView) {
        mapView = view
    }

    /** Clears all active renderers and removes their overlays from the map. */
    fun clear() {
        renderers.values.forEach { list -> list.forEach(GMUGeometryRenderer::clear) }
        renderers = emptyMap()
    }

    /**
     * Renders [layers] onto the attached map view, applying per-layer styles.
     *
     * Point features are extracted and returned as [Marker] objects instead of being rendered
     * directly. Layers with `visible != true` are skipped and their existing renderers cleared.
     *
     * @param layers GeoJSON layers to render.
     * @return List of [Marker] objects extracted from Point features across all layers.
     */
    fun render(layers: List<GeoJsonLayer>): List<Marker> {
        val view = mapView ?: return emptyList()
        val allExtractedMarkers = mutableListOf<Marker>()

        val desired = layers.indices.toSet()
        val toRemove = renderers.keys - desired
        toRemove.forEach { idx -> renderers[idx]?.forEach(GMUGeometryRenderer::clear) }
        renderers = renderers.filterKeys(desired::contains)

        layers.forEachIndexed { index, layer ->
            renderers[index]?.forEach(GMUGeometryRenderer::clear)
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
            val rotation = layer.pointStyle?.rotation?.toDouble() ?: 0.0
            val anchorU = layer.pointStyle?.anchorU?.toDouble() ?: 0.5
            val anchorV = layer.pointStyle?.anchorV?.toDouble() ?: 0.5

            var featureIdx = 0
            val featuresToRender = mutableListOf<GMUFeature>()
            val features = parser.features as? List<GMUFeature> ?: emptyList()

            features.forEach { feature ->
                val dict = feature.properties as? NSDictionary
                val geometry = feature.geometry

                if (geometry is GMUPoint) {
                    val coordinates =
                        geometry.coordinate.useContents { Coordinates(latitude, longitude) }
                    val title = getString(dict, "title")
                    allExtractedMarkers.add(Marker(coordinates = coordinates, title = title))
                    return@forEach
                }

                val strokeHex = getString(dict, "stroke")
                val strokeOpacity = getDouble(dict, "stroke-opacity")?.coerceIn(0.0, 1.0)
                val strokeWidthJson = getDouble(dict, "stroke-width")
                val fillHex = getString(dict, "fill")
                val fillOpacity = getDouble(dict, "fill-opacity")?.coerceIn(0.0, 1.0)
                val title = getString(dict, "title")

                val isLine =
                    geometry is GMULineString ||
                        (geometry is GMUGeometryCollection &&
                            geometry.geometries.firstOrNull() is GMULineString)

                val isPolygon =
                    geometry is GMUPolygon ||
                        (geometry is GMUGeometryCollection &&
                            geometry.geometries.firstOrNull() is GMUPolygon)

                when {
                    isLine -> {
                        val strokeUIColor =
                            (parseHexToUIColor(strokeHex) ?: lineStrokeColor).let { c ->
                                if (strokeOpacity != null) {
                                    c.colorWithAlphaComponent(strokeOpacity)
                                } else {
                                    c
                                }
                            }
                        feature.style =
                            GMUStyle(
                                styleID = "line_${index}_$featureIdx",
                                strokeColor = strokeUIColor,
                                fillColor = UIColor.clearColor,
                                width = strokeWidthJson ?: lineWidth,
                                scale = 1.0,
                                heading = 0.0,
                                anchor = CGPointMake(0.5, 1.0),
                                iconUrl = null,
                                title = null,
                                hasFill = false,
                                hasStroke = true,
                            )
                    }
                    isPolygon -> {
                        val strokeUIColor =
                            (parseHexToUIColor(strokeHex) ?: polygonStrokeColor).let { c ->
                                if (strokeOpacity != null) {
                                    c.colorWithAlphaComponent(strokeOpacity)
                                } else {
                                    c
                                }
                            }
                        val finalFill =
                            (parseHexToUIColor(fillHex) ?: fillColor).let { c ->
                                if (fillOpacity != null) {
                                    c.colorWithAlphaComponent(fillOpacity)
                                } else {
                                    c
                                }
                            }
                        feature.style =
                            GMUStyle(
                                styleID = "polygon_${index}_$featureIdx",
                                strokeColor = strokeUIColor,
                                fillColor = finalFill,
                                width = strokeWidthJson ?: strokeWidth,
                                scale = 1.0,
                                heading = 0.0,
                                anchor = CGPointMake(0.5, 1.0),
                                iconUrl = null,
                                title = null,
                                hasFill = true,
                                hasStroke = true,
                            )
                    }
                    geometry is GMUPoint -> {
                        feature.style =
                            GMUStyle(
                                styleID = "point_${index}_$featureIdx",
                                strokeColor = UIColor.clearColor,
                                fillColor = UIColor.clearColor,
                                width = 1.0,
                                scale = 1.0,
                                heading = rotation,
                                anchor = CGPointMake(anchorU, anchorV),
                                iconUrl = null,
                                title = title,
                                hasFill = false,
                                hasStroke = false,
                            )
                    }
                }
                featuresToRender.add(feature)
                featureIdx++
            }

            val layerRenderers = mutableListOf<GMUGeometryRenderer>()
            featuresToRender.forEach { feature ->
                val renderer =
                    GMUGeometryRenderer(
                        map = view,
                        geometries = listOf(feature),
                        styles = emptyList<GMUStyle>(),
                    )
                renderer.render()
                renderer.mapOverlays()?.forEach { overlay ->
                    val gmsOverlay = overlay as? GMSOverlay ?: return@forEach
                    gmsOverlay.setTappable(layer.isClickable == true)
                    gmsOverlay.setUserData(feature)
                }
                layerRenderers.add(renderer)
            }

            renderers = renderers + (index to layerRenderers)
        }
        return allExtractedMarkers
    }
}
