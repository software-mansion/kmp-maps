package com.swmansion.kmpmaps.core

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonObjectBuilder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject

/**
 * Converts a list of [JsonObject]s to a single JSON array string.
 *
 * @return A string representation of the JSON array.
 */
internal fun List<JsonObject>.toJsonString() = JsonArray(this).toString()

/**
 * Converts a list of [Marker]s to a JSON array.
 *
 * @param webCustomMarkerContent Map of content IDs to HTML rendering functions.
 * @return A [JsonArray] containing serialized markers with optional injected HTML.
 */
internal fun List<Marker>.toJson(webCustomMarkerContent: Map<String, (Marker) -> String>) =
    buildJsonArray {
        this@toJson.forEach { marker ->
            val baseJson = marker.toJson().toMutableMap()

            marker.contentId?.let { id ->
                webCustomMarkerContent[id]?.invoke(marker)?.let {
                    baseJson["renderedHtml"] = JsonPrimitive(it.trimIndent())
                }
            }

            add(JsonObject(baseJson))
        }
    }

/**
 * Serializes a [Marker] object to a JsonObject.
 *
 * @return [JsonObject] containing marker ID, position, title, and metadata.
 */
internal fun Marker.toJson() = buildJsonObject {
    put("id", getId())
    put("position", coordinates.toJson())
    put("title", title)
    put("opacity", 1.0f)
    contentId?.let { put("contentId", it) }
}

/**
 * Serializes geographic [Coordinates] to a [JsonObject].
 *
 * @return [JsonObject] with "lat" and "lng" keys.
 */
internal fun Coordinates.toJson() = buildJsonObject {
    put("lat", latitude)
    put("lng", longitude)
}

/**
 * Serializes [MapProperties] to a [JsonObject].
 *
 * @return [JsonObject] including traffic status, map type, and web-specific properties.
 */
internal fun MapProperties.toJson() = buildJsonObject {
    put("isTrafficEnabled", isTrafficEnabled)
    put("mapType", mapType.name)
    put("web", webMapProperties.toJson())
}

/**
 * Serializes [WebMapProperties] to a [JsonObject].
 *
 * @return [JsonObject] containing map ID, gesture handling, and styling options.
 */
internal fun WebMapProperties.toJson() = buildJsonObject {
    put("mapId", mapId)
    put("gestureHandling", gestureHandling.name.lowercase())
    put("disableDoubleClickZoom", disableDoubleClickZoom)
    put("keyboardShortcuts", keyboardShortcuts)
    put("minZoom", minZoom)
    put("maxZoom", maxZoom)
    put("clickableIcons", clickableIcons)
    put("backgroundColor", backgroundColor?.toHex())
    restriction?.let { put("restriction", it.toJson()) }
    putStyles(styles)
}

private fun JsonObjectBuilder.putStyles(options: GoogleMapsMapStyleOptions?) {
    val jsonString = options?.json
    if (jsonString.isNullOrBlank()) return

    val element = Json.parseToJsonElement(jsonString)
    put("styles", element)
}

/**
 * Serializes [WebMapRestriction] to a [JsonObject].
 *
 * @return [JsonObject] defining strict bounds and coordinate ranges.
 */
internal fun WebMapRestriction.toJson() = buildJsonObject {
    put("strictBounds", strictBounds)
    putJsonObject("latLngBounds") {
        put("north", north)
        put("south", south)
        put("east", east)
        put("west", west)
    }
}

/**
 * Serializes [MapUISettings] to a [JsonObject].
 *
 * @return [JsonObject] containing zoom/scroll toggles and web UI control positions.
 */
internal fun MapUISettings.toJson() = buildJsonObject {
    put("zoomEnabled", zoomEnabled)
    put("scrollEnabled", scrollEnabled)
    put("web", webUISettings.toJson())
}

/**
 * Serializes [WebUISettings] to a [JsonObject].
 *
 * @return [JsonObject] for JS API control visibility and placement.
 */
internal fun WebUISettings.toJson() = buildJsonObject {
    put("zoomControl", zoomControl)
    put("mapTypeControl", mapTypeControl)
    put("streetViewControl", streetViewControl)
    put("rotateControl", rotateControl)
    put("disableDefaultUI", disableDefaultUI)

    // Fullscreen control is not supported in the web implementation and is always disabled.
    put("fullscreenControl", false)

    zoomControlPosition?.let { put("zoomControlPosition", it.name) }
    mapTypeControlPosition?.let { put("mapTypeControlPosition", it.name) }
    streetViewControlPosition?.let { put("streetViewControlPosition", it.name) }
    rotateControlPosition?.let { put("rotateControlPosition", it.name) }
}

/**
 * Converts a [Color] object to a hex string.
 *
 * @return A string in "#RRGGBB" format.
 */
internal fun Color.toHex() = "#%06X".format(0xFFFFFF and toArgb())

/**
 * Serializes a [Circle] object to a [JsonObject].
 *
 * @return [JsonObject] with center, radius, and stroke/fill styles.
 */
internal fun Circle.toJson() = buildJsonObject {
    put("id", getId())
    put("center", center.toJson())
    put("radius", radius)

    lineColor?.let {
        put("strokeColor", it.toHex())
        put("strokeOpacity", it.alpha)
    }
    lineWidth?.let { put("strokeWeight", it) }

    color?.let {
        put("fillColor", it.toHex())
        put("fillOpacity", it.alpha)
    }
}

/**
 * Serializes a [Polygon] object to a [JsonObject].
 *
 * @return [JsonObject] containing the list of paths and styling properties.
 */
internal fun Polygon.toJson() = buildJsonObject {
    put("id", getId())
    put("paths", JsonArray(coordinates.map(Coordinates::toJson)))
    put("strokeWeight", lineWidth)

    lineColor?.let {
        put("strokeColor", it.toHex())
        put("strokeOpacity", it.alpha)
    }
    color?.let {
        put("fillColor", it.toHex())
        put("fillOpacity", it.alpha)
    }
}

/**
 * Serializes a [Polyline] object to a [JsonObject].
 *
 * @return [JsonObject] containing the path coordinates and line styles.
 */
internal fun Polyline.toJson() = buildJsonObject {
    put("id", getId())
    put("path", JsonArray(coordinates.map(Coordinates::toJson)))
    put("strokeWeight", width)

    lineColor?.let {
        put("strokeColor", it.toHex())
        put("strokeOpacity", it.alpha)
    }
}

/**
 * Serializes a [GeoJsonLayer] to a [JsonObject].
 *
 * @return [JsonObject] containing raw GeoJSON string and associated feature styles.
 */
internal fun GeoJsonLayer.toJson() = buildJsonObject {
    put("geoJson", geoJson)
    put("visible", visible ?: true)
    put("zIndex", zIndex)
    put("isClickable", isClickable ?: false)
    put("isGeodesic", isGeodesic ?: false)

    lineStringStyle?.let { put("lineStringStyle", it.toJson()) }
    polygonStyle?.let { put("polygonStyle", it.toJson()) }
    pointStyle?.let { put("pointStyle", it.toJson()) }
}

/**
 * Serializes [LineStringStyle] to a [JsonObject].
 *
 * @return [JsonObject] including strokeWeight, strokeColor, and dashArray patterns.
 */
internal fun LineStringStyle.toJson() = buildJsonObject {
    lineWidth?.let { put("strokeWeight", it) }
    lineColor?.let { put("strokeColor", it.toHex()) }
}

/**
 * Serializes [PolygonStyle] to a [JsonObject].
 *
 * @return [JsonObject] with fill and stroke color/opacity/weight.
 */
internal fun PolygonStyle.toJson() = buildJsonObject {
    fillColor?.let {
        put("fillColor", it.toHex())
        put("fillOpacity", it.alpha)
    }
    strokeColor?.let { put("strokeColor", it.toHex()) }
    strokeWidth?.let { put("strokeWeight", it) }
}

/**
 * Serializes [PointStyle] to a [JsonObject].
 *
 * @return [JsonObject] containing marker opacity, rotation, and anchor offsets.
 */
internal fun PointStyle.toJson() = buildJsonObject {
    put("opacity", alpha)
    put("draggable", isDraggable)
    pointTitle?.let { put("title", it) }
}
