package com.lab.tb.distributed.base
import java.util.TimerTask
import java.util.Timer as JavaTimer

actual class Timer {

    private val timer = JavaTimer()

    actual fun schedule(
        delayMillis: Long,
        periodMillis: Long,
        callback: () -> Unit
    ) {
        val task = object : TimerTask() {
            override fun run() {
                callback()
            }
        }

        timer.scheduleAtFixedRate(task, delayMillis, periodMillis)
    }
}