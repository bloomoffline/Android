package com.lab.tb.distributed.chat.android.data.channel

import com.lab.tb.distributed.model.MessageView
import kotlinx.coroutines.flow.Flow

interface ChannelRepository {
    fun getChannelList(): Flow<List<MessageView>>
    fun deleteChannel(id: String)
    fun setPinChannel(id: String, isPinned: Boolean)
}