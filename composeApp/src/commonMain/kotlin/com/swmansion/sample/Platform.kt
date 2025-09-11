package com.swmansion.sample

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
