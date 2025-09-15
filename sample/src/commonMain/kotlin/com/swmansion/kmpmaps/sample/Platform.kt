package com.swmansion.kmpmaps.sample

public interface Platform {
    public val name: String
}

public expect fun getPlatform(): Platform
