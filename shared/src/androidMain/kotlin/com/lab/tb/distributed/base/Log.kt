package com.lab.tb.distributed.base
import android.graphics.drawable.LevelListDrawable
import java.lang.Exception
import android.util.Log as Logger

open class Log {
    companion object {
        fun d(instance: Any, message: String) = doLog(LogLevel.DEBUG, getTag(instance), message)

        fun d(tag: String, message: String) = doLog(LogLevel.DEBUG, tag, message)

        fun e(instance: Any, message: String, exception: Exception) = doLog(LogLevel.ERROR, getTag(instance), message + " " + exception.stackTraceToString())

        fun e(tag: String, message: String, exception: Exception) = doLog(LogLevel.ERROR, tag, message + " " + exception.stackTraceToString())

        fun v(instance: Any, message: String) = doLog(LogLevel.TRACE, getTag(instance), message)

        fun v(tag: String, message: String) = doLog(LogLevel.TRACE, tag, message)

        fun i(instance: Any, message: String) = Logger.i("${instance::class.qualifiedName}", message)

        fun i(tag: String, message: String)= doLog(LogLevel.INFO, tag, message)

        private fun doLog(level: LogLevel, tag: String, message: String) {
            val logMessage = "|$level| $tag: $message"
            when (level) {
                LogLevel.TRACE -> Logger.v(tag, logMessage)
                LogLevel.DEBUG -> Logger.d(tag, logMessage)
                LogLevel.INFO -> Logger.i(tag, logMessage)
                LogLevel.WARNING -> Logger.w(tag, logMessage)
                LogLevel.ERROR -> Logger.e(tag, logMessage)
            }
        }

        private fun getTag(instance: Any): String {
            var tag = instance::class.qualifiedName
            if (tag == null || instance::class == String::class) {
                tag = instance.toString()
            }
            return tag
        }
    }
}

enum class LogLevel {
    TRACE,
    DEBUG,
    INFO,
    WARNING,
    ERROR;
}