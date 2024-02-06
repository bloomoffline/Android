
package com.lab.tb.distributed.database

import com.lab.tb.distributed.database.contracts.IBaseQuery
import com.lab.tb.distributed.entities.BaseEntity
import com.lab.tb.distributed.mapper.MapperManager
import kotlin.reflect.KClass

object DbUtils {
    inline fun <reified T> getBaseQuery(): IBaseQuery<Any>? {
        return getBaseQuery(T::class)
    }

    fun getBaseQuery(tableName: KClass<*>): IBaseQuery<Any>? {
        val dbClazz = DbConst.mapper[tableName] ?: return null
        return DatabaseHelper.instance.getDatabase().getBaseQuery(dbClazz)
    }

    inline fun <reified E : BaseEntity> toEntityList(db: List<Any>): List<E>? {
        return MapperManager.getEntityToDbMapper<E>().fromDbToEntityList(db)
    }

    inline fun <reified E : BaseEntity> toDb(entity: E): Any? {
        return MapperManager.getEntityToDbMapper<E>().fromEntityToDb(entity)
    }

    inline fun <reified E : BaseEntity> toEntity(db: Any): E? {
        return MapperManager.getEntityToDbMapper<E>().fromDbToEntity(db)
    }
}