
package com.lab.tb.distributed.database

import com.lab.tb.distributed.database.contracts.IDatabase
import com.lab.tb.distributed.entities.ApplicationContext
import com.lab.tb.distributed.entities.BaseEntity

class DatabaseHelper private constructor() {

    companion object {
        val instance = DatabaseHelper()
    }

    inline fun <reified T: BaseEntity> set(entity: T): Boolean {
        val db = DbUtils.toDb(entity) ?: return false
        return DbUtils.getBaseQuery<T>()?.set(db) ?: false
    }

    inline fun <reified T: BaseEntity> get(id: String): T? {
        val db = DbUtils.getBaseQuery<T>()?.get(id) ?: return null
        return DbUtils.toEntity(db) ?: null
    }

    inline fun <reified T: BaseEntity> getAll(): List<T> {
        val db = DbUtils.getBaseQuery<T>()?.getAll() ?: return emptyList()
        return DbUtils.toEntityList(db) ?: emptyList()
    }

    inline fun <reified T: BaseEntity> delete(id: String): Boolean {
        return DbUtils.getBaseQuery<T>()?.delete(id) ?: false
    }

    inline fun <reified T: BaseEntity> deleteAll(): Boolean {
        return DbUtils.getBaseQuery<T>()?.deleteAll() ?: false
    }

    fun init(context: ApplicationContext) {
        IDatabase.init(context = context)
    }

    fun getDatabase(): IDatabase {
        return IDatabase.getInstance()
            ?: throw Exception("Database is not initialized yet")
    }
}