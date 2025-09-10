package com.example.kmpmaps

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform