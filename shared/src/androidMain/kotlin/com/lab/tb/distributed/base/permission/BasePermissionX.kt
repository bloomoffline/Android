package com.lab.tb.distributed.base.permission

import android.os.Bundle
import androidx.fragment.app.Fragment
import kotlinx.coroutines.CompletableDeferred

internal class BasePermissionX : Fragment(), IPermissionController {

    private lateinit var mTask: CompletableDeferred<PermissionResult>
    override var permissionTask: CompletableDeferred<PermissionResult>
        get() = mTask
        set(value) {
            mTask = value
        }

    override fun getFragment(): BasePermissionX {
        return this
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        println("onRequestPermissionsResult 222")
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
