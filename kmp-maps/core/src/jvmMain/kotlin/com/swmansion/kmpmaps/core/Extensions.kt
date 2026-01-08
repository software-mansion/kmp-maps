package com.swmansion.kmpmaps.core

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonObjectBuilder
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject

internal fun List<JsonObject>.toJsonString() = JsonArray(this).toString()

internal fun Marker.toJson(): JsonObject = buildJsonObject {
    put("id", id)
    put("position", coordinates.toJson())
    put("title", title)
    put("opacity", 1.0f)
}

internal fun Coordinates.toJson(): JsonObject = buildJsonObject {
    put("lat", latitude)
    put("lng", longitude)
}

internal fun MapProperties.toJson(): JsonObject = buildJsonObject {
    put("isTrafficEnabled", isTrafficEnabled)
    put("mapType", mapType.name)
    put("web", webMapProperties.toJson())
}

internal fun WebMapProperties.toJson(): JsonObject = buildJsonObject {
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

internal fun WebMapRestriction.toJson(): JsonObject = buildJsonObject {
    put("strictBounds", strictBounds)
    putJsonObject("latLngBounds") {
        put("north", north)
        put("south", south)
        put("east", east)
        put("west", west)
    }
}

internal fun MapUISettings.toJson(): JsonObject = buildJsonObject {
    put("zoomEnabled", zoomEnabled)
    put("scrollEnabled", scrollEnabled)
    put("web", webUISettings.toJson())
}

internal fun WebUISettings.toJson(): JsonObject = buildJsonObject {
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

internal fun Color.toHex() = "#%06X".format(0xFFFFFF and toArgb())
internal fun Circle.toJson(): JsonObject = buildJsonObject {
    put("id", id)
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

internal fun Polygon.toJson(): JsonObject = buildJsonObject {
    put("id", id)
    put("paths", JsonArray(coordinates.map { it.toJson() }))
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

internal fun Polyline.toJson(): JsonObject = buildJsonObject {
    put("id", id)
    put("path", JsonArray(coordinates.map { it.toJson() }))
    put("strokeWeight", width)

    lineColor?.let {
        put("strokeColor", it.toHex())
        put("strokeOpacity", it.alpha)
    }
}


