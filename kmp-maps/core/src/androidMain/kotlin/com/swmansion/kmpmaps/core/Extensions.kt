package com.swmansion.kmpmaps.core

import android.util.Log
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
import org.json.JSONObject

/**
 * Converts [MapBounds] to Google Maps [LatLngBounds].
 *
 * @return [LatLngBounds] with the original southwest and northeast corners intact
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
 * @param viewportWidthPx Map viewport width in pixels (used to compute zoom for bounds)
 * @param viewportHeightPx Map viewport height in pixels (used to compute zoom for bounds)
 * @return [GoogleCameraPosition] with coordinates, zoom, bearing, and tilt
 */
internal fun CameraPosition.toGoogleMapsCameraPosition(
    viewportWidthPx: Int = 0,
    viewportHeightPx: Int = 0,
): GoogleCameraPosition {
    val target =
        bounds?.toLatLngBounds()?.center ?: LatLng(coordinates.latitude, coordinates.longitude)
    val computedZoom =
        if (bounds != null && viewportWidthPx > 0 && viewportHeightPx > 0) {
            calculateZoomFromViewport(viewportWidthPx, viewportHeightPx, bounds)
        } else {
            zoom
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
 * @param latLngBounds Optional visible region bounds to include in the result
 * @return CameraPosition with coordinates, zoom, bearing, tilt, and optional bounds
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
 * @return LatLng with latitude and longitude
 */
internal fun Coordinates.toGoogleMapsLatLng() = LatLng(latitude, longitude)

/**
 * Converts Google Maps LatLng back to [Coordinates].
 *
 * @return Coordinates with latitude and longitude
 */
internal fun LatLng.toCoordinates() = Coordinates(latitude, longitude)

/**
 * Converts MapTheme to native ComposeMapColorScheme.
 *
 * @return ComposeMapColorScheme corresponding to the enum value
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
 * @return GoogleMapProperties with map configuration
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
 * @return GoogleMapUiSettings with UI configuration
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
 * @return Google Maps MapType corresponding to the enum value
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
 * @return MapStyleOptions from JSON string, or null if no JSON provided
 */
internal fun GoogleMapsMapStyleOptions?.toNativeStyleOptions() = this?.json?.let(::MapStyleOptions)

/**
 * Converts GoogleMapsAnchor to Compose Offset.
 *
 * @return Offset with x and y coordinates (defaults to 0.5f, 1.0f if null)
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
 * @return Cluster with coordinates, size, and list of markers
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
 * Renders a GeoJSON layer onto the GoogleMap.
 *
 * If clustering is enabled, points are extracted from the GeoJSON, converted to [Marker] objects
 * for the cluster manager, and hidden on the original layer to prevent duplication.
 *
 * @param layerData The GeoJSON layer configuration and data.
 * @param clusterSettings Settings determining if clustering logic should be applied.
 * @return A [RenderedGeoJson] containing the native layer and extracted markers, or null if JSON
 *   parsing fails.
 */
internal fun GoogleMap.renderGeoJsonLayer(
    layerData: GeoJsonLayer,
    clusterSettings: ClusterSettings,
    onMarkerClick: ((Marker) -> Unit)?,
): RenderedGeoJson? {
    val json =
        runCatching { JSONObject(layerData.geoJson) }
            .getOrElse {
                Log.e("KMPMaps", "Invalid GeoJSON JSON", it)
                return null
            }

    val layer = GoogleGeoJsonLayer(this, json)
    layer.applyStylesFrom(layerData)

    val extractedMarkers = mutableListOf<Marker>()

    if (clusterSettings.enabled) {
        for (feature in layer.features) {
            if (feature.geometry is GeoJsonPoint) {
                val point = feature.geometry as GeoJsonPoint
                val marker = point.toMarker(feature)

                extractedMarkers.add(marker)

                val hiddenStyle = GeoJsonPointStyle()
                hiddenStyle.isVisible = false
                feature.pointStyle = hiddenStyle
            }
        }
    } else {
        layer.setOnFeatureClickListener { feature ->
            if (feature.geometry is GeoJsonPoint) {
                val point = feature.geometry as GeoJsonPoint
                val marker = point.toMarker(feature as GeoJsonFeature)
                onMarkerClick?.invoke(marker)
            }
        }
    }
    layer.addLayerToMap()

    return RenderedGeoJson(layer, extractedMarkers)
}

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

private fun GoogleGeoJsonLayer.applyStylesFrom(geo: GeoJsonLayer) {
    defaultLineStringStyle.pattern = geo.lineStringStyle?.pattern?.toGooglePattern()
    defaultLineStringStyle.isClickable = geo.isClickable == true
    defaultLineStringStyle.color =
        geo.lineStringStyle?.lineColor?.toArgb() ?: DEFAULT_STROKE_COLOR.toColorInt()
    defaultLineStringStyle.width = geo.lineStringStyle?.lineWidth ?: DEFAULT_STROKE_WIDTH
    defaultLineStringStyle.zIndex = geo.zIndex
    defaultLineStringStyle.isVisible = geo.visible != false
    defaultLineStringStyle.isGeodesic = geo.isGeodesic == true

    defaultPolygonStyle.fillColor =
        geo.polygonStyle?.fillColor?.toArgb() ?: DEFAULT_FILL_COLOR.toColorInt()
    defaultPolygonStyle.strokeColor =
        geo.polygonStyle?.strokeColor?.toArgb() ?: DEFAULT_STROKE_COLOR.toColorInt()
    defaultPolygonStyle.strokeWidth = geo.polygonStyle?.strokeWidth ?: DEFAULT_STROKE_WIDTH
    defaultPolygonStyle.zIndex = geo.zIndex
    defaultPolygonStyle.isGeodesic = geo.isGeodesic == true
    defaultPolygonStyle.isClickable = geo.isClickable == true
    defaultPolygonStyle.isVisible = geo.visible != false

    defaultPointStyle.alpha = geo.pointStyle?.alpha ?: 1f
    defaultPointStyle.isDraggable = geo.pointStyle?.isDraggable ?: true
    defaultPointStyle.isFlat = geo.pointStyle?.isFlat ?: false
    defaultPointStyle.rotation = geo.pointStyle?.rotation ?: 0f
    defaultPointStyle.title = geo.pointStyle?.pointTitle
    defaultPointStyle.snippet = geo.pointStyle?.snippet
    defaultPointStyle.isVisible = geo.visible != false
    defaultPointStyle.zIndex = geo.zIndex
    defaultPointStyle.setInfoWindowAnchor(
        geo.pointStyle?.infoWindowAnchorU ?: 0.5f,
        geo.pointStyle?.infoWindowAnchorV ?: 0.5f,
    )
    defaultPointStyle.setAnchor(geo.pointStyle?.anchorU ?: 0.5f, geo.pointStyle?.anchorV ?: 0.5f)

    features.forEach { feature ->
        val jsonStroke = feature.getProperty("stroke")
        val jsonFill = feature.getProperty("fill")
        val jsonFillOpacity = feature.getProperty("fill-opacity")
        val jsonWidth = feature.getProperty("stroke-width")?.toFloatOrNull()
        val width = jsonWidth ?: DEFAULT_STROKE_WIDTH

        when (feature.geometry) {
            is GeoJsonLineString,
            is GeoJsonMultiLineString -> {
                val strokeColor =
                    jsonStroke?.toColorInt()
                        ?: geo.lineStringStyle?.lineColor?.toArgb()
                        ?: DEFAULT_STROKE_COLOR.toColorInt()

                feature.setLineStringStyle(
                    GeoJsonLineStringStyle().apply {
                        color = strokeColor
                        this.width = width
                        isClickable = geo.isClickable == true
                        isVisible = geo.visible != false
                        zIndex = geo.zIndex
                        isGeodesic = geo.isGeodesic == true
                        pattern = geo.lineStringStyle?.pattern?.toGooglePattern()
                    }
                )
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

                feature.setPolygonStyle(
                    GeoJsonPolygonStyle().apply {
                        this.strokeColor = strokeColor
                        this.strokeWidth = strokeWidth
                        this.fillColor = fillColor
                        isClickable = geo.isClickable == true
                        isVisible = geo.visible != false
                        zIndex = geo.zIndex
                        isGeodesic = geo.isGeodesic == true
                    }
                )
            }
            is GeoJsonPoint -> {
                val titleFromJson =
                    feature.getProperty("title")
                        ?: feature.getProperty("name")
                        ?: geo.pointStyle?.pointTitle
                val snippetFromJson =
                    feature.getProperty("snippet")
                        ?: feature.getProperty("description")
                        ?: geo.pointStyle?.snippet

                feature.setPointStyle(
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
                )
            }
            else -> Unit
        }
    }
}

private fun applyAlpha(color: Int, opacity: Float?) =
    if (opacity != null) {
        ColorUtils.setAlphaComponent(color, (opacity.coerceIn(0f, 1f) * 255f).toInt())
    } else {
        color
    }
