package org.auctions.klaravik

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform