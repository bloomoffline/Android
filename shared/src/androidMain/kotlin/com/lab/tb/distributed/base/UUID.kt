package com.lab.tb.distributed.base

import java.util.UUID

actual class UUID  {
    actual companion object {
        actual fun randomUUID(): String = UUID.randomUUID().toString().uppercase()
    }
}