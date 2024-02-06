
package com.lab.tb.distributed.base

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.native.concurrent.ThreadLocal

open class BaseService : CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default

    @ThreadLocal
    companion object {
//        var onExceptionHandle: ((ErrorDetailV2) -> Unit)? = null
//        val errorHandledList = listOf(
//            ErrorCodeV2.DeviceAlreadyDisconnected
//        )
    }

    private fun isErrorHandled(ex: Exception): Boolean {
        return false
//        return if (ex is ErrorDetailV2) {
//            errorHandledList.contains(ex.errorCode)
//        } else {
//            return false
//        }
    }

    protected suspend fun <R> serviceHandlerAsync(
        action: suspend () -> R
    ): R {
        return try {
            action.invoke()
        } catch (ex: Exception) {
            if (!isErrorHandled(ex)) {
                println("serviceHandlerAsync exception: ${ex.stackTraceToString()}")
//                Log.e(this, "serviceHandlerAsync exception: ${ex.stackTraceToString()}")
            }
            throw ex
        }
    }

    protected fun <R> serviceHandle(action: () -> R): R {
        try {
            return action.invoke()
        } catch (ex: Exception) {
            if (!isErrorHandled(ex)) {
                println("serviceHandle exception: ${ex.stackTraceToString()}")
//                Log.e(this, "serviceHandle exception: ${ex.stackTraceToString()}")
            }
            throw ex
        }
    }

    fun startCoroutine(instance: Any? = null, action: suspend () -> Unit): Job {
        return launch {
            try {
                action.invoke()
            } catch (ex: Exception) {
//                val errorDetail = ErrorDetailV2()
//                errorDetail.tag = if (instance == null) "" else instance::class.qualifiedName?.trimPackageName()
//                onExceptionHandle?.invoke(errorDetail)
            }
        }
    }

    protected suspend fun initInstanceOnBothThread(action: () -> Unit) {
        withContext(Dispatchers.Default) {
            action.invoke()
        }
        withContext(Dispatchers.Main) {
            action.invoke()
        }
    }

    protected fun startCoroutineNewScope(
        dispatcher: CoroutineDispatcher,
        instance: Any? = null,
        action: suspend () -> Unit
    ): Job {
        return CoroutineScope(dispatcher).launch {
            try {
                action.invoke()
            } catch (ex: Exception) {
//                val errorDetail = ErrorDetailV2()
//                errorDetail.tag = if (instance == null) "" else instance::class.qualifiedName?.trimPackageName()
//                onExceptionHandle?.invoke(errorDetail)
            }
        }
    }
}
