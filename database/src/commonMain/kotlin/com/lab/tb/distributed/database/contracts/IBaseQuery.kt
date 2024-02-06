
package com.lab.tb.distributed.database.contracts

interface IBaseQuery<T> {
    fun get(id: String = "ID"): T?

    fun getAll(): List<T>

    fun getAllByParent(parentId: String): List<T>

    fun set(obj: T): Boolean

    fun delete(id: String): Boolean

    fun deleteAll(): Boolean

    fun deleteAllByParent(parentId: String): Boolean
}