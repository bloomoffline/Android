package com.lab.tb.distributed.database.contracts

import com.lab.tb.distributed.database.BloomDatabase
import com.lab.tb.distributed.database.driver.Database
import com.lab.tb.distributed.entities.ApplicationContext
import kotlin.reflect.KClass

interface IDatabase {

    companion object {
        fun init(context: ApplicationContext) {
            Database.ref.set(Database(context))
        }

        fun getInstance(): IDatabase? = Database.ref.get()

    }


    fun getDatabase(): BloomDatabase

    fun close()

    fun getBaseQuery(clazz: KClass<*>): IBaseQuery<Any>

}