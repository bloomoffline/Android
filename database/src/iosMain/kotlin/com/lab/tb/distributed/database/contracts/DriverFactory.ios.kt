package com.lab.tb.distributed.database.contracts

import com.lab.tb.distributed.entities.ApplicationContext
import com.squareup.sqldelight.db.SqlDriver

actual class DriverFactory actual constructor(val context: ApplicationContext) {

    actual fun mainDriver(): SqlDriver {
        TODO()
    }

}