package com.lab.tb.distributed.database.driver.helper

import com.lab.tb.distributed.database.driver.BaseDatabase
import com.lab.tb.distributed.database.models.ChatPresenceDb
import com.lab.tb.distributed.database.models.ChatPresenceQueries

class ChatPresenceDbHelper : BaseDatabase<ChatPresenceDb, ChatPresenceQueries>()    {
    override fun query(): ChatPresenceQueries {
        return db.chatPresenceQueries
    }

    override fun get(id: String): ChatPresenceDb? {
        return selectOne { selectById(id) }
    }

    override fun set(obj: ChatPresenceDb): Boolean {
        return doQuery { insert(obj) }
    }

    override fun delete(id: String): Boolean {
        return doQuery { removeById(id) }
    }

    override fun deleteAll(): Boolean {
        return doQuery { removeAll() }
    }

    override fun getAll(): List<ChatPresenceDb> {
        return selectList { selectAll() }
    }
}