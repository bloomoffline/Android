package com.lab.tb.distributed.chat

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform