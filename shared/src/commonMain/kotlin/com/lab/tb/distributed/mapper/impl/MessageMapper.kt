package com.lab.tb.distributed.mapper.impl

import com.lab.tb.distributed.base.JsonUtils
import com.lab.tb.distributed.database.models.MessageDb
import com.lab.tb.distributed.mapper.contracts.IMessageMapper
import com.lab.tb.distributed.model.ChatChannelEnum
import com.lab.tb.distributed.model.ChatMessage
import com.lab.tb.distributed.model.ChatPresence
import com.lab.tb.distributed.model.Message

class MessageMapper : IMessageMapper {
    override fun fromDbToEntity(db: MessageDb?): Message? {
        if (db == null) return null
        try {
            val chatMessages = if (db.addedChatMessages == null || db.addedChatMessages == "null") {
                null
            } else {
                JsonUtils.decodeFromString<ArrayList<ChatMessage>>(db.addedChatMessages!!)
            }
            val updatedPresences = if (db.updatedPresences == null || db.updatedPresences == "null") {
                null
            } else {
                JsonUtils.decodeFromString<ArrayList<ChatPresence>>(db.updatedPresences!!)
            }
            return Message(
                id = db.id,
                timestamp = db.timestamp?.toDouble(),
                sourceUserId = db.sourceUserId,
                destinationUserId = db.destinationUserId,
                addedChatMessages = chatMessages,
                updatedPresences = updatedPresences,
                logicalClock = db.logicalClock?.toInt(),
//                deletedChatMessages = db.deletedChatMessages,
//                messageRequest = db.messageRequest,
                isPin = db.isPin,
                isNewNotification = db.isNewNotification,
                chatChannel = ChatChannelEnum.get(db.chatChannel ?: "")
            )
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return null
    }

    override fun fromEntityToDb(entity: Message?): MessageDb? {
        return if (entity == null) null else MessageDb(
            id = entity.id ?: "",
            timestamp = entity.timestamp?.toLong(),
            sourceUserId = entity.sourceUserId,
            destinationUserId = entity.destinationUserId,
            addedChatMessages = JsonUtils.encodeToString(entity.addedChatMessages),
            updatedPresences = JsonUtils.encodeToString(entity.updatedPresences),
            deletedChatMessages = null,
            messageRequest = null,
            logicalClock = entity.logicalClock?.toLong(),
            isPin = entity.isPin,
            isNewNotification = entity.isNewNotification,
            chatChannel = entity.chatChannel?.value
        )
    }

    override fun fromDbToEntityList(dbList: List<MessageDb>?): List<Message>? {
        if (dbList == null) return null
        val list = mutableListOf<Message>()
        for (db in dbList) {
            val entity = fromDbToEntity(db)
            if (entity != null) {
                list.add(entity)
            }
        }
        return list
    }
}