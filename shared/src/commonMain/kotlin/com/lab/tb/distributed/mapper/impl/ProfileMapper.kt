package com.lab.tb.distributed.mapper.impl

import com.lab.tb.distributed.base.JsonUtils
import com.lab.tb.distributed.database.models.ProfileDb
import com.lab.tb.distributed.mapper.contracts.IProfileMapper
import com.lab.tb.distributed.model.ChatPresence
import com.lab.tb.distributed.model.Profile

class ProfileMapper : IProfileMapper {
    override fun fromDbToEntity(db: ProfileDb?): Profile? {
        if (db == null) return Profile()
        try {
            val presence = JsonUtils.decodeFromString<ChatPresence>(db.presence ?:"")
            return Profile(presence).apply {
                this.id = db.id
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    override fun fromEntityToDb(entity: Profile?): ProfileDb? {
        if (entity == null) return null
        try {
            val presence = JsonUtils.encodeToString(entity.presence)
            return ProfileDb(
                id = entity.id,
                presence = presence
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}