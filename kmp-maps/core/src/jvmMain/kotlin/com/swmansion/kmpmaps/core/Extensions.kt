package com.swmansion.kmpmaps.core

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

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

internal fun List<JsonObject>.toJsonString(): String {
    return JsonArray(this).toString()
}
