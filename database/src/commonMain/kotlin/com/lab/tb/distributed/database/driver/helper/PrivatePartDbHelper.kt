

package com.lab.tb.distributed.database.driver.helper

import com.lab.tb.distributed.database.driver.BaseDatabase
import com.lab.tb.distributed.database.models.PrivatePartDb
import com.lab.tb.distributed.database.models.PrivatePartQueries

class PrivatePartDbHelper: BaseDatabase<PrivatePartDb, PrivatePartQueries>() {
    override fun query(): PrivatePartQueries {
        return db.privatePartQueries
    }

    override fun get(id: String): PrivatePartDb? {
        return selectOne { selectById(id) }
    }

    override fun set(obj: PrivatePartDb): Boolean {
        return doQuery { insert(obj) }
    }

    override fun delete(id: String): Boolean {
        return doQuery { removeById(id) }
    }

    override fun deleteAll(): Boolean {
        return doQuery { removeAll() }
    }

    override fun getAll(): List<PrivatePartDb> {
        return selectList { selectAll() }
    }
}