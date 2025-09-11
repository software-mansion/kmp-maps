package com.swmansion.kmpmaps.sample

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
