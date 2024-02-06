

package com.lab.tb.distributed.database.driver.helper

import com.lab.tb.distributed.database.driver.BaseDatabase
import com.lab.tb.distributed.database.models.PublicPartDb
import com.lab.tb.distributed.database.models.PublicPartQueries

class PublicPartDbHelper : BaseDatabase<PublicPartDb, PublicPartQueries>() {
    override fun query(): PublicPartQueries {
        return db.publicPartQueries
    }

    override fun get(id: String): PublicPartDb? {
        return selectOne { selectById(id) }
    }

    override fun set(obj: PublicPartDb): Boolean {
        return doQuery { insert(obj) }
    }

    override fun delete(id: String): Boolean {
        return doQuery { removeById(id) }
    }

    override fun deleteAll(): Boolean {
        return doQuery { removeAll() }
    }

    override fun getAll(): List<PublicPartDb> {
        return selectList { selectAll() }
    }
}