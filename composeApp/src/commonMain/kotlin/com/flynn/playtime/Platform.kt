package com.flynn.playtime

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform