package com.swmansion.kmpmaps.core

internal fun loadHTMLContent(apiKey: String): String {
    val resourcePath = "web/google_map.html"
    val inputStream =
        Thread.currentThread().contextClassLoader.getResourceAsStream(resourcePath)
            ?: object {}.javaClass.classLoader.getResourceAsStream(resourcePath)

    return inputStream.let {
        val template = inputStream.bufferedReader().use { it.readText() }
        template.replace("{{API_KEY}}", apiKey)
    }
}
