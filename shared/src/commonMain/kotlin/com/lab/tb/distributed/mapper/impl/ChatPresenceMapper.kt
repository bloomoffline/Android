package com.lab.tb.distributed.mapper.impl

import com.lab.tb.distributed.base.JsonUtils
import com.lab.tb.distributed.database.models.ChatPresenceDb
import com.lab.tb.distributed.database.models.ChatUserDb
import com.lab.tb.distributed.mapper.contracts.IChatPresenceMapper
import com.lab.tb.distributed.model.ChatPresence
import com.lab.tb.distributed.model.ChatStatus
import com.lab.tb.distributed.model.ChatUser

class ChatPresenceMapper : IChatPresenceMapper {
    override fun fromDbToEntity(db: ChatPresenceDb?): ChatPresence? {
        if (db == null) return null
        try {
            val chatUser = JsonUtils.decodeFromString<ChatUser>(db.user ?: "")
            return ChatPresence(
                id = db.id,
                user = chatUser,
                status = ChatStatus.from(db.status),
                info = db.info ?: ""
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    override fun fromEntityToDb(entity: ChatPresence?): ChatPresenceDb? {
        if (entity == null) return null
        try {
            val user = JsonUtils.encodeToString(entity.user)
            return ChatPresenceDb(
                id = entity.id,
                user = user,
                status = entity.status.value,
                info = entity.info
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    override fun fromDbToEntityList(dbList: List<ChatPresenceDb>?): List<ChatPresence>? {
        if (dbList == null) return null
        val list = mutableListOf<ChatPresence>()
        for (db in dbList) {
            val entity = fromDbToEntity(db)
            if (entity != null) {
                list.add(entity)
            }
        }
        return list
    }
}