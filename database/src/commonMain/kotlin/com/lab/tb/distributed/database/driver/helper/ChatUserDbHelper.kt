package com.lab.tb.distributed.database.driver.helper

import com.lab.tb.distributed.database.driver.BaseDatabase
import com.lab.tb.distributed.database.models.ChatUserDb
import com.lab.tb.distributed.database.models.ChatUserQueries

class ChatUserDbHelper: BaseDatabase<ChatUserDb, ChatUserQueries>() {
    override fun query(): ChatUserQueries {
        return db.chatUserQueries
    }

    override fun get(id: String): ChatUserDb? {
        return selectOne { selectById(id) }
    }

    override fun set(obj: ChatUserDb): Boolean {
        return doQuery { insert(obj) }
    }

    override fun delete(id: String): Boolean {
        return doQuery { removeById(id) }
    }

    override fun deleteAll(): Boolean {
        return doQuery { removeAll() }
    }

    override fun getAll(): List<ChatUserDb> {
        return selectList { selectAll() }
    }
}