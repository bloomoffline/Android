package com.lab.tb.distributed.database.driver.helper

import com.lab.tb.distributed.database.driver.BaseDatabase
import com.lab.tb.distributed.database.models.BleSettingsDb
import com.lab.tb.distributed.database.models.BleSettingsQueries

class BleSettingsDbHelper : BaseDatabase<BleSettingsDb, BleSettingsQueries>()    {
    override fun query(): BleSettingsQueries {
        return db.bleSettingsQueries
    }

    override fun get(id: String): BleSettingsDb? {
        return selectOne { selectById(id) }
    }

    override fun set(obj: BleSettingsDb): Boolean {
        return doQuery { insert(obj) }
    }

    override fun delete(id: String): Boolean {
        return doQuery { removeById(id) }
    }

    override fun deleteAll(): Boolean {
        return doQuery { removeAll() }
    }

    override fun getAll(): List<BleSettingsDb> {
        return selectList { selectAll() }
    }
}