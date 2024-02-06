package com.lab.tb.distributed.base.permission

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

@Suppress("DEPRECATION")
object PermissionHelper {
    private val rationalRequest = mutableMapOf<Int, Boolean>()

    internal fun requestPermissions(
        controller: IPermissionController,
        requestId: Int,
        vararg permissions: String,
        onResult: (PermissionResult) -> Unit
    ) {

        rationalRequest[requestId]?.let {
            requestPermissionsCompat(controller, requestId, *permissions)
            rationalRequest.remove(requestId)
            return
        }

        val notGranted = permissionsNotGrantedCompat(controller, *permissions)

        when {
            notGranted.isEmpty() -> {
                println("granted")
                onResult(PermissionResult.PermissionGranted(requestId))
            }
            notGranted.any { requestPermissionRationaleCompat(controller, it) } -> {
                println("rationalRequest")
                rationalRequest[requestId] = true
                onResult(PermissionResult.ShowRational(requestId))
            }
            else -> {
                println("start request permission")
                requestPermissionsCompat(controller, requestId, *permissions)
            }
        }
    }

    fun handleResult(
        controller: IPermissionController,
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        onResult: (PermissionResult) -> Unit
    ) {
        println("handle result -> $grantResults")
        if (grantResults.isNotEmpty() &&
            grantResults.all { it == PackageManager.PERMISSION_GRANTED }
        ) {
            onResult(PermissionResult.PermissionGranted(requestCode))
        } else if (permissions.any { requestPermissionRationaleCompat(controller, it) }) {
            onResult(
                PermissionResult.PermissionDenied(
                    requestCode,
                    permissions.filterIndexed { index, _ ->
                        grantResults[index] == PackageManager.PERMISSION_DENIED
                    }
                )
            )
        } else {
            onResult(
                PermissionResult.PermissionDeniedPermanently(
                    requestCode,
                    permissions.filterIndexed { index, _ ->
                        grantResults[index] == PackageManager.PERMISSION_DENIED
                    }
                )
            )
        }
    }

    private fun requestPermissionsCompat(
        controller: IPermissionController,
        requestId: Int,
        vararg permissions: String
    ) {
        when (val fragment = controller.getFragment()) {
            is BasePermission -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                fragment.requestPermissions(permissions, requestId)
            }
            is BasePermissionX -> fragment.requestPermissions(permissions, requestId)
            else -> TODO("Not implemented yet")
        }
    }

    private fun requestPermissionRationaleCompat(
        controller: IPermissionController,
        permission: String
    ): Boolean {
        return when (val fragment = controller.getFragment()) {
            is BasePermission -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                fragment.shouldShowRequestPermissionRationale(permission)
            } else {
                TODO("VERSION.SDK_INT < M")
            }
            is BasePermissionX -> fragment.shouldShowRequestPermissionRationale(permission)
            else -> false
        }
    }

    fun permissionsNotGranted(context: Context, vararg permissions: String): Array<String> {
        return permissions.filter {
            ContextCompat.checkSelfPermission(
                context,
                it
            ) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()
    }

    private fun permissionsNotGrantedCompat(controller: IPermissionController, vararg permissions: String): Array<String> {
        val context = when (val fragment = controller.getFragment()) {
            is BasePermission -> {
                fragment.activity
            }
            is BasePermissionX -> {
                fragment.context
            }
            else -> error("BasePermission[$fragment] not implemented yet")
        }

        return permissionsNotGranted(context!!, *permissions)
    }
}
