package com.lab.tb.distributed.chat.android.data.channel

import com.lab.tb.distributed.ChatController
import com.lab.tb.distributed.model.MessageView
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ChannelRepositoryImpl(dispatcher: CoroutineDispatcher): ChannelRepository {
    override fun getChannelList(): Flow<List<MessageView>> {
        return ChatController.getInstance().roomMessage.map {
            it.values.toList()
        }
    }

    override fun deleteChannel(id: String) {
       ChatController.getInstance().deleteChannel(id)
    }

    override fun setPinChannel(id: String, isPinned: Boolean) {
        ChatController.getInstance().setPinChannel(id, isPinned)
    }
}