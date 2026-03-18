package com.swmansion.kmpmaps.core

import androidx.annotation.RestrictTo
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.toColorInt
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition as GoogleCameraPosition
import com.google.android.gms.maps.model.Dash
import com.google.android.gms.maps.model.Dot
import com.google.android.gms.maps.model.Gap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.PatternItem
import com.google.maps.android.clustering.Cluster as GoogleMapCluster
import com.google.maps.android.compose.ComposeMapColorScheme
import com.google.maps.android.compose.MapProperties as GoogleMapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings as GoogleMapUiSettings
import com.google.maps.android.data.Feature
import com.google.maps.android.data.geojson.GeoJsonFeature
import com.google.maps.android.data.geojson.GeoJsonLayer as GoogleGeoJsonLayer
import com.google.maps.android.data.geojson.GeoJsonLineString
import com.google.maps.android.data.geojson.GeoJsonLineStringStyle
import com.google.maps.android.data.geojson.GeoJsonMultiLineString
import com.google.maps.android.data.geojson.GeoJsonMultiPolygon
import com.google.maps.android.data.geojson.GeoJsonPoint
import com.google.maps.android.data.geojson.GeoJsonPointStyle
import com.google.maps.android.data.geojson.GeoJsonPolygon
import com.google.maps.android.data.geojson.GeoJsonPolygonStyle
import org.json.JSONArray
import org.json.JSONObject

/**
 * Converts [MapBounds] to Google Maps [LatLngBounds].
 *
 * @return [LatLngBounds] with the original southwest and northeast corners intact.
 */
internal fun MapBounds.toLatLngBounds() =
    LatLngBounds(
        LatLng(southwest.latitude, southwest.longitude),
        LatLng(northeast.latitude, northeast.longitude),
    )

/**
 * Converts [CameraPosition] to [GoogleCameraPosition].
 *
 * When [CameraPosition.bounds] is set and viewport dimensions are provided, computes the zoom level
 * to fit the bounds using the Mercator projection formula. Otherwise, uses [CameraPosition.zoom].
 *
 * @param viewportWidthPx Map viewport width in pixels (used to compute zoom for bounds).
 * @param viewportHeightPx Map viewport height in pixels (used to compute zoom for bounds).
 * @return [GoogleCameraPosition] with coordinates, zoom, bearing, and tilt.
 */
internal fun CameraPosition.toGoogleMapsCameraPosition(
    viewportWidthPx: Int = 0,
    viewportHeightPx: Int = 0,
): GoogleCameraPosition {
    val target =
        bounds?.toLatLngBounds()?.center
            ?: LatLng(coordinates?.latitude ?: 0.0, coordinates?.longitude ?: 0.0)
    val computedZoom =
        if (bounds != null && viewportWidthPx > 0 && viewportHeightPx > 0) {
            calculateZoomFromViewport(viewportWidthPx, viewportHeightPx, bounds)
        } else {
            zoom ?: 0f
        }
    return GoogleCameraPosition.Builder()
        .target(target)
        .zoom(computedZoom)
        .bearing(androidCameraPosition?.bearing ?: 0f)
        .tilt(androidCameraPosition?.tilt ?: 0f)
        .build()
}

/**
 * Fits bounds if available; otherwise uses coordinates and zoom.
 *
 * @param padding Bounds padding (px).
 */
internal fun CameraPosition.toCameraUpdate(padding: Int = 0) =
    if (bounds != null) {
        CameraUpdateFactory.newLatLngBounds(bounds.toLatLngBounds(), padding)
    } else {
        CameraUpdateFactory.newCameraPosition(toGoogleMapsCameraPosition())
    }

/**
 * Converts Google Maps CameraPosition back to CameraPosition.
 *
 * @param latLngBounds Optional visible region bounds to include in the result.
 * @return CameraPosition with coordinates, zoom, bearing, tilt, and optional bounds.
 */
internal fun GoogleCameraPosition.toCameraPosition(latLngBounds: LatLngBounds? = null) =
    CameraPosition(
        coordinates = Coordinates(latitude = target.latitude, longitude = target.longitude),
        zoom = zoom,
        bounds =
            latLngBounds?.let {
                MapBounds(
                    northeast = Coordinates(it.northeast.latitude, it.northeast.longitude),
                    southwest = Coordinates(it.southwest.latitude, it.southwest.longitude),
                )
            },
        androidCameraPosition = AndroidCameraPosition(bearing = bearing, tilt = tilt),
    )

/**
 * Converts Coordinates to Google Maps LatLng.
 *
 * @return LatLng with latitude and longitude.
 */
internal fun Coordinates.toGoogleMapsLatLng() = LatLng(latitude, longitude)

/**
 * Converts Google Maps LatLng back to [Coordinates].
 *
 * @return Coordinates with latitude and longitude.
 */
internal fun LatLng.toCoordinates() = Coordinates(latitude, longitude)

/**
 * Converts MapTheme to native ComposeMapColorScheme.
 *
 * @return ComposeMapColorScheme corresponding to the enum value.
 */
internal fun MapTheme.toGoogleMapsTheme() =
    when (this) {
        MapTheme.LIGHT -> ComposeMapColorScheme.LIGHT
        MapTheme.DARK -> ComposeMapColorScheme.DARK
        MapTheme.SYSTEM -> ComposeMapColorScheme.FOLLOW_SYSTEM
    }

/**
 * Converts MapProperties to Google Maps Properties.
 *
 * @return GoogleMapProperties with map configuration.
 */
@OptIn(ExperimentalPermissionsApi::class)
internal fun MapProperties.toGoogleMapsProperties(locationPermissionState: PermissionState) =
    GoogleMapProperties(
        mapType = mapType.toGoogleMapsMapType(),
        isMyLocationEnabled = isMyLocationEnabled && locationPermissionState.status.isGranted,
        isTrafficEnabled = isTrafficEnabled,
        isBuildingEnabled = isBuildingEnabled,
        isIndoorEnabled = androidMapProperties.isIndoorEnabled,
        minZoomPreference = androidMapProperties.minZoomPreference ?: 0f,
        maxZoomPreference = androidMapProperties.maxZoomPreference ?: 20f,
        mapStyleOptions = androidMapProperties.mapStyleOptions.toNativeStyleOptions(),
    )

/**
 * Converts MapUISettings to Google Maps UI Settings.
 *
 * @return GoogleMapUiSettings with UI configuration.
 */
internal fun MapUISettings.toGoogleMapsUiSettings() =
    GoogleMapUiSettings(
        compassEnabled = compassEnabled,
        myLocationButtonEnabled = myLocationButtonEnabled,
        indoorLevelPickerEnabled = androidUISettings.indoorLevelPickerEnabled,
        mapToolbarEnabled = androidUISettings.mapToolbarEnabled,
        rotationGesturesEnabled = rotateEnabled,
        scrollGesturesEnabled = scrollEnabled,
        scrollGesturesEnabledDuringRotateOrZoom =
            androidUISettings.scrollGesturesEnabledDuringRotateOrZoom,
        tiltGesturesEnabled = androidUISettings.tiltGesturesEnabled,
        zoomControlsEnabled = androidUISettings.zoomControlsEnabled,
        zoomGesturesEnabled = zoomEnabled,
    )

/**
 * Converts MapType enum to Google Maps MapType.
 *
 * @return Google Maps MapType corresponding to the enum value.
 */
internal fun com.swmansion.kmpmaps.core.MapType?.toGoogleMapsMapType() =
    when (this) {
        com.swmansion.kmpmaps.core.MapType.HYBRID -> MapType.HYBRID
        com.swmansion.kmpmaps.core.MapType.NORMAL -> MapType.NORMAL
        com.swmansion.kmpmaps.core.MapType.SATELLITE -> MapType.SATELLITE
        com.swmansion.kmpmaps.core.MapType.TERRAIN -> MapType.TERRAIN
        else -> MapType.NORMAL
    }

/**
 * Converts GoogleMapsMapStyleOptions to native MapStyleOptions.
 *
 * @return MapStyleOptions from JSON string, or null if no JSON provided.
 */
internal fun GoogleMapsMapStyleOptions?.toNativeStyleOptions() = this?.json?.let(::MapStyleOptions)

/**
 * Converts GoogleMapsAnchor to Compose Offset.
 *
 * @return Offset with x and y coordinates (defaults to 0.5f, 1.0f if null).
 */
internal fun GoogleMapsAnchor?.toOffset() = Offset(this?.x ?: 0.5f, this?.y ?: 1.0f)

/**
 * Converts the multiplatform stroke pattern description into a list of Google Maps Android
 * [PatternItem]s.
 *
 * @return List of [PatternItem] suitable for Google Maps styling.
 */
internal fun List<StrokePatternItem>.toGooglePattern(): List<PatternItem> = map {
    when (it) {
        is StrokePatternItem.Dot -> Dot()
        is StrokePatternItem.Dash -> Dash(it.lengthPx)
        is StrokePatternItem.Gap -> Gap(it.lengthPx)
    }
}

/**
 * Converts Google Maps [GoogleMapCluster] to native [Cluster].
 *
 * @return Cluster with coordinates, size, and list of markers.
 */
internal fun GoogleMapCluster<MarkerClusterItem>.toNativeCluster() =
    Cluster(
        coordinates = Coordinates(position.latitude, position.longitude),
        size = size,
        items = items.map(MarkerClusterItem::marker),
    )

/** @suppress */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
internal data class RenderedGeoJson(
    val layer: GoogleGeoJsonLayer,
    val extractedMarkers: List<Marker>,
)

/**
 * Merges all [layers] into a single GeoJSON object and renders it on this [GoogleMap].
 *
 * @param layers GeoJSON layers to combine and render.
 * @param clusterSettings Clustering configuration applied to Point features.
 * @param onMarkerClick Callback invoked when a GeoJSON Point marker is clicked.
 * @param onGeoJsonFeatureClick Callback invoked when a non-Point GeoJSON feature is clicked.
 * @return A [RenderedGeoJson] handle for the rendered layer, or `null` if rendering was skipped.
 */
internal fun GoogleMap.renderCombinedGeoJsonLayers(
    layers: List<GeoJsonLayer>,
    clusterSettings: ClusterSettings,
    onMarkerClick: ((Marker) -> Unit)?,
    onGeoJsonFeatureClick: ((GeoJsonFeatureClicked) -> Unit)?,
): RenderedGeoJson? {
    val combinedJson = buildCombinedGeoJson(layers) ?: return null

    return renderGeoJsonLayer(
        combinedJson = combinedJson,
        originalLayers = layers,
        clusterSettings = clusterSettings,
        onMarkerClick = onMarkerClick,
        onGeoJsonFeatureClick = onGeoJsonFeatureClick,
    )
}

/**
 * Merges all visible [layers] into a single GeoJSON FeatureCollection [JSONObject].
 *
 * @param layers Source layers to merge.
 * @return A FeatureCollection [JSONObject] containing all extracted features, or `null` if no
 *   visible features were found.
 */
private fun buildCombinedGeoJson(layers: List<GeoJsonLayer>): JSONObject? {
    val combinedFeaturesArray = JSONArray()

    layers.forEachIndexed { index, layer ->
        if (layer.visible != false) {
            val json =
                runCatching { JSONObject(layer.geoJson) }.getOrNull() ?: return@forEachIndexed

            val geoJsonType = GeoJsonType.fromString(json.optString("type"))
            val extractedFeatures = mutableListOf<JSONObject>()

            when (geoJsonType) {
                GeoJsonType.FEATURE_COLLECTION -> {
                    json.optJSONArray("features")?.let { jsonArray ->
                        val features =
                            (0 until jsonArray.length()).map { i -> jsonArray.getJSONObject(i) }
                        extractedFeatures.addAll(features)
                    }
                }
                GeoJsonType.FEATURE -> {
                    extractedFeatures.add(json)
                }
                GeoJsonType.GEOMETRY -> {
                    val feature =
                        JSONObject().apply {
                            put("type", "Feature")
                            put("geometry", json)
                        }
                    extractedFeatures.add(feature)
                }
            }

            extractedFeatures.forEach { f ->
                val props =
                    f.optJSONObject("properties") ?: JSONObject().also { f.put("properties", it) }

                props.put(DEFAULT_LAYER_ID, index)
                combinedFeaturesArray.put(f)
            }
        }
    }

    if (combinedFeaturesArray.length() == 0) return null

    return JSONObject().apply {
        put("type", GeoJsonType.FEATURE_COLLECTION.value)
        put("features", combinedFeaturesArray)
    }
}

/**
 * Renders a pre-built combined GeoJSON [JSONObject] onto this [GoogleMap].
 *
 * @param combinedJson A GeoJSON FeatureCollection produced by [buildCombinedGeoJson].
 * @param originalLayers The original layers list used to look up per-layer style configuration.
 * @param clusterSettings Clustering configuration.
 * @param onMarkerClick Callback invoked when a Point feature is clicked and clustering is disabled.
 * @param onGeoJsonFeatureClick Callback invoked when a non-Point GeoJSON feature is clicked.
 * @return A [RenderedGeoJson] containing the active layer and any extracted cluster markers.
 */
private fun GoogleMap.renderGeoJsonLayer(
    combinedJson: JSONObject,
    originalLayers: List<GeoJsonLayer>,
    clusterSettings: ClusterSettings,
    onMarkerClick: ((Marker) -> Unit)?,
    onGeoJsonFeatureClick: ((GeoJsonFeatureClicked) -> Unit)?,
): RenderedGeoJson {
    val googleLayer = GoogleGeoJsonLayer(this, combinedJson)
    val extractedMarkers = mutableListOf<Marker>()

    googleLayer.features.forEach { feature ->
        val layerIndexStr = feature.getProperty(DEFAULT_LAYER_ID)
        val layerIndex = layerIndexStr?.toIntOrNull() ?: return@forEach
        val layerData = originalLayers[layerIndex]

        feature.applyExplicitStyle(layerData)

        if (clusterSettings.enabled && feature.geometry is GeoJsonPoint) {
            val marker = (feature.geometry as GeoJsonPoint).toMarker(feature as GeoJsonFeature)
            extractedMarkers.add(marker)

            val hiddenStyle = GeoJsonPointStyle().apply { isVisible = false }
            feature.pointStyle = hiddenStyle
        }
    }

    googleLayer.setOnFeatureClickListener { feature ->
        val geoJsonFeature = feature as GeoJsonFeature
        val properties = geoJsonFeature.getPropertiesMap().filterKeys { it != DEFAULT_LAYER_ID }

        val featureClicked =
            GeoJsonFeatureClicked(
                id = geoJsonFeature.id,
                geometryType = geoJsonFeature.geometry?.geometryType ?: "Unknown",
                properties = properties,
            )

        when (geoJsonFeature.geometry) {
            is GeoJsonPoint -> {
                if (!clusterSettings.enabled) {
                    val marker = (geoJsonFeature.geometry as GeoJsonPoint).toMarker(geoJsonFeature)
                    onMarkerClick?.invoke(marker)
                }
            }
            else -> {
                onGeoJsonFeatureClick?.invoke(featureClicked)
            }
        }
    }

    googleLayer.addLayerToMap()
    return RenderedGeoJson(googleLayer, extractedMarkers)
}

/**
 * Returns all properties of this [Feature] as a [Map] of String keys to String values.
 *
 * @return A map of property key-value pairs.
 */
private fun Feature.getPropertiesMap(): Map<String, String> {
    val map = mutableMapOf<String, String>()
    this.propertyKeys?.forEach { key -> this.getProperty(key)?.let { value -> map[key] = value } }
    return map
}

/**
 * Applies the visual style from a [GeoJsonLayer] configuration to this [Feature].
 *
 * @param geo The layer configuration providing fallback style values.
 */
private fun Feature.applyExplicitStyle(geo: GeoJsonLayer) {
    val jsonStroke = this.getProperty("stroke")
    val jsonFill = this.getProperty("fill")
    val jsonFillOpacity = this.getProperty("fill-opacity")
    val jsonWidth = this.getProperty("stroke-width")?.toFloatOrNull()

    val width =
        jsonWidth
            ?: geo.lineStringStyle?.lineWidth
            ?: geo.polygonStyle?.strokeWidth
            ?: DEFAULT_STROKE_WIDTH

    when (this.geometry) {
        is GeoJsonLineString,
        is GeoJsonMultiLineString -> {
            val strokeColor =
                jsonStroke?.toColorInt()
                    ?: geo.lineStringStyle?.lineColor?.toArgb()
                    ?: DEFAULT_STROKE_COLOR.toColorInt()

            (this as GeoJsonFeature).lineStringStyle =
                GeoJsonLineStringStyle().apply {
                    color = strokeColor
                    this.width = width
                    isClickable = geo.isClickable == true
                    isVisible = geo.visible != false
                    zIndex = geo.zIndex
                    isGeodesic = geo.isGeodesic == true
                    pattern = geo.lineStringStyle?.pattern?.toGooglePattern()
                }
        }
        is GeoJsonPolygon,
        is GeoJsonMultiPolygon -> {
            val strokeColor =
                jsonStroke?.toColorInt()
                    ?: geo.polygonStyle?.strokeColor?.toArgb()
                    ?: DEFAULT_STROKE_COLOR.toColorInt()

            val fillColor =
                jsonFill?.toColorInt()?.let { c ->
                    val opacity = jsonFillOpacity?.toFloatOrNull()
                    if (opacity != null) applyAlpha(c, opacity) else c
                } ?: geo.polygonStyle?.fillColor?.toArgb() ?: DEFAULT_FILL_COLOR.toColorInt()

            (this as GeoJsonFeature).polygonStyle =
                GeoJsonPolygonStyle().apply {
                    this.strokeColor = strokeColor
                    this.strokeWidth = width
                    this.fillColor = fillColor
                    isClickable = geo.isClickable == true
                    isVisible = geo.visible != false
                    zIndex = geo.zIndex
                    isGeodesic = geo.isGeodesic == true
                }
        }
        is GeoJsonPoint -> {
            val titleFromJson =
                this.getProperty("title") ?: this.getProperty("name") ?: geo.pointStyle?.pointTitle
            val snippetFromJson =
                this.getProperty("snippet")
                    ?: this.getProperty("description")
                    ?: geo.pointStyle?.snippet

            (this as GeoJsonFeature).pointStyle =
                GeoJsonPointStyle().apply {
                    alpha = geo.pointStyle?.alpha ?: 1f
                    isDraggable = geo.pointStyle?.isDraggable ?: true
                    isFlat = geo.pointStyle?.isFlat ?: false
                    rotation = geo.pointStyle?.rotation ?: 0f
                    title = titleFromJson
                    snippet = snippetFromJson
                    isVisible = geo.visible != false
                    zIndex = geo.zIndex
                    setInfoWindowAnchor(
                        geo.pointStyle?.infoWindowAnchorU ?: 0.5f,
                        geo.pointStyle?.infoWindowAnchorV ?: 0.5f,
                    )
                    setAnchor(geo.pointStyle?.anchorU ?: 0.5f, geo.pointStyle?.anchorV ?: 0.5f)
                }
        }
    }
}

/**
 * Applies an [opacity] alpha component to a packed ARGB [color].
 *
 * @param color The source ARGB color integer.
 * @param opacity Opacity in the range `0.0` (transparent) to `1.0` (opaque), or `null` to leave
 *   alpha unchanged.
 * @return The color with the updated alpha component.
 */
private fun applyAlpha(color: Int, opacity: Float?) =
    if (opacity != null) {
        ColorUtils.setAlphaComponent(color, (opacity.coerceIn(0f, 1f) * 255f).toInt())
    } else {
        color
    }

/**
 * Converts this [GeoJsonPoint] to a KMP [Marker].
 *
 * @param feature The GeoJSON feature providing property values for the marker.
 * @return A [Marker] positioned at this point's coordinates.
 */
private fun GeoJsonPoint.toMarker(feature: GeoJsonFeature): Marker {
    val title = feature.getProperty("title")
    val snippet = feature.getProperty("snippet")

    val anchor = feature.parseGeoJsonAnchor()
    val draggable = feature.getProperty("draggable")?.toBoolean() == true
    val zIndex = feature.getProperty("zIndex")?.toFloatOrNull()

    return Marker(
        coordinates = Coordinates(coordinates.latitude, coordinates.longitude),
        title = title,
        androidMarkerOptions =
            AndroidMarkerOptions(
                snippet = snippet,
                anchor = anchor,
                draggable = draggable,
                zIndex = zIndex,
            ),
    )
}

/**
 * Parses a [GoogleMapsAnchor] from the `anchor` property of this [GeoJsonFeature].
 *
 * @return A [GoogleMapsAnchor] with the parsed U/V coordinates, or `null` if parsing fails.
 */
private fun GeoJsonFeature.parseGeoJsonAnchor(): GoogleMapsAnchor? {
    val anchorStr = getProperty("anchor")

    if (anchorStr != null) {
        val parts = anchorStr.split(",")
        if (parts.size == 2) {
            val x = parts[0].trim().toFloatOrNull()
            val y = parts[1].trim().toFloatOrNull()
            if (x != null && y != null) return GoogleMapsAnchor(x, y)
        }
    }

    return null
}
