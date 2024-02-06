package com.lab.tb.distributed.database.driver

import com.lab.tb.distributed.database.contracts.IBaseQuery
import com.lab.tb.distributed.database.contracts.IDatabase
import com.squareup.sqldelight.Query
import com.squareup.sqldelight.Transacter

abstract class BaseDatabase<T : Any, out Q : Transacter> : IBaseQuery<T> {
    protected val db = IDatabase.getInstance()!!.getDatabase()

    //    private inline fun <reified Q> query(): Q {
//        when(typeOf<Q>()){
//            is AccessReportInfoDbQueries -> db.accessReportDbQueries
//        }
//
//    }
    abstract fun query(): Q

//    abstract fun get(id: String = DbConst.DEFAULT_ID): T?
//    abstract fun set(obj: T): Boolean

    open fun update(obj: T): Boolean {
        return false
    }

    override fun getAll(): List<T> {
        return emptyList()
    }

    override fun getAllByParent(parentId: String): List<T> {
        return emptyList()
    }

    override fun delete(id: String): Boolean {
        return false
    }

    override fun deleteAll(): Boolean {
        return false
    }

    override fun deleteAllByParent(parentId: String): Boolean {
        return false
    }


    // protect functions
    protected fun selectOne(query: Q.() -> Query<T>): T? {
        return query.invoke(query()).executeAsOneOrNull()
    }

    protected fun <R : Any> selectList(query: Q.() -> Query<R>): List<R> {
        return query.invoke(query()).executeAsList()
    }

    protected fun doQuery(query: Q.() -> Unit): Boolean {
        return runCatching {
            query.invoke(query())
        }.isSuccess
    }
}