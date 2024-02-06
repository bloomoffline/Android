package com.lab.tb.distributed.model

data class MessageView(
    val name: String,
    val bio: String,
    val chatChannel: ChatChannelEnum,
    val isNewNotification: Boolean = false,
    val isPinned: Boolean = false,
    val message: Message? = null
)