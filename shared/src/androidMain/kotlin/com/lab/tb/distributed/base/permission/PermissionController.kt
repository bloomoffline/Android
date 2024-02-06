package com.lab.tb.distributed.base.permission

import android.Manifest
import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Suppress("DEPRECATION")
internal object PermissionController {

    private const val TAG = "PermissionController"

    suspend fun requestPermissions(
        activity: Activity,
        requestId: Int,
        vararg permissions: String
    ): PermissionResult = withContext(Dispatchers.Main) {

        val controller = if (activity is AppCompatActivity) {
            activity.supportFragmentManager.run {
                findFragmentByTag(TAG) as? IPermissionController
                    ?: BasePermissionX().apply {
                        beginTransaction().add(this, TAG).commitNow()
                    }
            }
        } else {
            activity.fragmentManager.run {
                findFragmentByTag(TAG) as? IPermissionController ?: BasePermission().apply {
                    beginTransaction().add(this, TAG).commit()
                }
            }
        }

        controller.requestPermissions(requestId, *permissions)
    }

    fun isPermissionsGranted(context: Context, vararg permissions: String): Boolean {
        return PermissionHelper.permissionsNotGranted(
            context.applicationContext!!,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ).isEmpty()
    }
}
