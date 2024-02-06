package com.lab.tb.distributed.base

actual class Timer {
    actual fun schedule(
        delayMillis: Long,
        periodMillis: Long,
        callback: () -> Unit
    ) {
    }
}