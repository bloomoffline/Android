package com.lab.tb.distributed.base.permission

import kotlinx.coroutines.CompletableDeferred

interface IPermissionController {
    var permissionTask: CompletableDeferred<PermissionResult>

    suspend fun requestPermissions(
        requestId: Int,
        vararg permissions: String
    ): PermissionResult {
        permissionTask = CompletableDeferred()
        PermissionHelper.requestPermissions(this, requestId, *permissions) {
            permissionTask.complete(it)
        }
        return permissionTask.await()
    }

    fun onResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        PermissionHelper.handleResult(
            this,
            requestCode,
            permissions,
            grantResults,
            onResult = { permissionTask.complete(it) }
        )
    }

    fun close() {
        if (permissionTask.isActive) {
            permissionTask.cancel()
        }
    }

    fun getFragment(): Any
}
