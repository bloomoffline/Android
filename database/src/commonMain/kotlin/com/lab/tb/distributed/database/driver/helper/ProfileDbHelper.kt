package com.lab.tb.distributed.database.driver.helper

import com.lab.tb.distributed.database.driver.BaseDatabase
import com.lab.tb.distributed.database.models.ProfileDb
import com.lab.tb.distributed.database.models.ProfileQueries

class ProfileDbHelper : BaseDatabase<ProfileDb, ProfileQueries>()    {
    override fun query(): ProfileQueries {
        return db.profileQueries
    }

    override fun get(id: String): ProfileDb? {
        return selectOne { selectById(id) }
    }

    override fun set(obj: ProfileDb): Boolean {
        return doQuery { insert(obj) }
    }

    override fun delete(id: String): Boolean {
        return doQuery { removeById(id) }
    }

    override fun deleteAll(): Boolean {
        return doQuery { removeAll() }
    }

    override fun getAll(): List<ProfileDb> {
        return selectList { selectAll() }
    }
}