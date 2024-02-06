package com.lab.tb.distributed.database.contracts

import com.lab.tb.distributed.entities.ApplicationContext
import com.squareup.sqldelight.db.SqlDriver

expect class DriverFactory(context: ApplicationContext) {
    fun mainDriver(): SqlDriver
}