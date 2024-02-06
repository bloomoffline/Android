package com.lab.tb.distributed.model

enum class ChatStatus(val value: String) {
    Online("Online"),
    Busy("Busy"),
    Emergency("Emergency");

    companion object {
        fun from(value: String?): ChatStatus = when (value) {
            "Online" -> Online
            "Offline" -> Busy
            "Emergency" -> Emergency
            else -> Online
        }
    }
}