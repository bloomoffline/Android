

package com.lab.tb.distributed.database.driver

import com.lab.tb.distributed.database.BloomDatabase
import com.lab.tb.distributed.database.contracts.DriverFactory
import com.lab.tb.distributed.database.contracts.IBaseQuery
import com.lab.tb.distributed.database.contracts.IDatabase
import com.lab.tb.distributed.database.driver.helper.BleSettingsDbHelper
import com.lab.tb.distributed.database.driver.helper.ChatPresenceDbHelper
import com.lab.tb.distributed.database.driver.helper.ChatUserDbHelper
import com.lab.tb.distributed.database.driver.helper.MessageDbHelper
import com.lab.tb.distributed.database.driver.helper.PrivatePartDbHelper
import com.lab.tb.distributed.database.driver.helper.ProfileDbHelper
import com.lab.tb.distributed.database.driver.helper.PublicPartDbHelper
import com.lab.tb.distributed.database.models.BleSettingsDb
import com.lab.tb.distributed.database.models.ChatPresenceDb
import com.lab.tb.distributed.database.models.ChatUserDb
import com.lab.tb.distributed.database.models.MessageDb
import com.lab.tb.distributed.database.models.PrivatePartDb
import com.lab.tb.distributed.database.models.ProfileDb
import com.lab.tb.distributed.database.models.PublicPartDb
import com.lab.tb.distributed.entities.ApplicationContext
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.internal.Atomic
import kotlin.reflect.KClass

class Database constructor(context: ApplicationContext) : IDatabase {

    private var mainDriver: SqlDriver? = null
    private var bloomDatabase: BloomDatabase? = null

    private var factory: DriverFactory = DriverFactory(context)

    companion object {
        val ref: Atomic<Database?> = Atomic(null)
    }

    override fun getDatabase(): BloomDatabase {
        if (mainDriver == null || bloomDatabase == null) {
            mainDriver = factory.mainDriver()
            bloomDatabase = BloomDatabase(mainDriver!!)
        }
        return bloomDatabase!!
    }

    override fun close() {
        mainDriver?.close()
        mainDriver = null
        bloomDatabase = null
    }
    @Suppress("UNCHECKED_CAST")
    override fun getBaseQuery(clazz: KClass<*>): IBaseQuery<Any> {
        return when (clazz) {
            BleSettingsDb::class -> BleSettingsDbHelper()
            ChatPresenceDb::class -> ChatPresenceDbHelper()
            MessageDb::class -> MessageDbHelper()
            ChatUserDb::class -> ChatUserDbHelper()
            ProfileDb::class -> ProfileDbHelper()
            PrivatePartDb::class -> PrivatePartDbHelper()
            PublicPartDb::class -> PublicPartDbHelper()
            else -> throw IllegalArgumentException("Unknown class $clazz")
        } as IBaseQuery<Any>
    }
}

