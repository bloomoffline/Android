package com.lab.tb.distributed.base.permission

import android.app.Fragment
import android.os.Bundle
import kotlinx.coroutines.CompletableDeferred

@Suppress("DEPRECATION")
internal class BasePermission : Fragment(), IPermissionController {

    private lateinit var mTask: CompletableDeferred<PermissionResult>
    override var permissionTask: CompletableDeferred<PermissionResult>
        get() = mTask
        set(value) {
            mTask = value
        }

    override fun getFragment(): BasePermission {
        return this
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        println("onRequestPermissionsResult")

        onResult(requestCode, permissions, grantResults)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onDestroy() {
        super.onDestroy()
        close()
    }
}
