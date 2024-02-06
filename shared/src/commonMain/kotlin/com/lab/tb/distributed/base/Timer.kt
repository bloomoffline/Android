package com.lab.tb.distributed.base

expect class Timer() {
    fun schedule(delayMillis: Long, periodMillis: Long, callback: () -> Unit)
}