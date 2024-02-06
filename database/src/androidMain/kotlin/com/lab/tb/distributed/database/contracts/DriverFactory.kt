package com.lab.tb.distributed.database.contracts

import com.lab.tb.distributed.database.BloomDatabase
import com.lab.tb.distributed.entities.ApplicationContext
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import kotlin.random.Random

actual class DriverFactory actual constructor (private val context: ApplicationContext) {

    private val keyStore = "BloomCipher"
    actual fun mainDriver(): SqlDriver {
//        val kVault = KVault(context)
//        val value = kVault.string(keyStore)
//        if (value == null) {
//            kVault.set(keyStore, Random.nextBytes(32).toString())
//        }

        return AndroidSqliteDriver(BloomDatabase.Schema, context, "BloomDatabase.db")
    }
}