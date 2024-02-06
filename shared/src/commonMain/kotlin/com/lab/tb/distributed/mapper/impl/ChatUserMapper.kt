package com.lab.tb.distributed.mapper.impl

import com.lab.tb.distributed.database.models.ChatUserDb
import com.lab.tb.distributed.mapper.contracts.IChatUserMapper
import com.lab.tb.distributed.model.ChatUser

class ChatUserMapper: IChatUserMapper {
    override fun fromDbToEntity(db: ChatUserDb?): ChatUser? {
        if (db == null) return null
        return ChatUser(
            id = db.id,
            name = db.name ?: "",
            publicKey = db.publicKey,
            logicalClock = db.logicalClock?.toInt() ?: 0,
            displayName = db.displayName,
        )
    }

    override fun fromEntityToDb(entity: ChatUser?): ChatUserDb? {
        if (entity == null) return null
        return ChatUserDb(
            id = entity.id,
            name = entity.name,
            publicKey = entity.publicKey,
            logicalClock = entity.logicalClock.toLong(),
            displayName = entity.displayName,
        )
    }
}