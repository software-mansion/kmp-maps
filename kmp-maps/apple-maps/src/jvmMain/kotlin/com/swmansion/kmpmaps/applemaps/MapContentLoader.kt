package com.swmansion.kmpmaps.applemaps

import com.swmansion.kmpmaps.core.readResource

internal fun loadAppleHTMLContent(
    token: String,
): String {
    val html = readResource("web/apple_map.html")
    val js =
        readResource("web/apple_map.js")
            .replace("{{API_KEY}}", token)

    return html.replace("{{LOCAL_JS_CONTENT}}", js)
}
