package com.lab.tb.distributed.model

enum class ChatChannelEnum(val value: String) {
    DM("DM"),
    ROOM("ROOM");

    companion object {
        fun get(value: String): ChatChannelEnum =
            when (value) {
                "DM" -> DM
                "ROOM" -> ROOM
                else -> throw IllegalArgumentException()
            }
    }
}