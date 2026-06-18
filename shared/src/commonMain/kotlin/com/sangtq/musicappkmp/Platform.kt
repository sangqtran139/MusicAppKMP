package com.sangtq.musicappkmp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform