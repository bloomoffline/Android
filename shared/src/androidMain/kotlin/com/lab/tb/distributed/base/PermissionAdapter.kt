package com.lab.tb.distributed.base

import android.Manifest
import com.lab.tb.distributed.base.permission.PermissionController
import com.lab.tb.distributed.base.permission.PermissionResult
import com.lab.tb.distributed.entities.ApplicationContext

actual class PermissionAdapter actual constructor(private val context: ApplicationContext?) {
    actual fun isIOS(): Boolean = false

    actual suspend fun requestInUseLocationPermission(): Boolean {
        val activity = AndroidComponents.getCurrentActivity()
        if (activity == null) {
            Log.d(this, "Activity is null")
            return false
        }

        val result = PermissionController.requestPermissions(
            activity,
            9999,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        return isGranted(result)
    }

    actual suspend fun requestAlwaysLocationPermission(): Boolean = requestInUseLocationPermission()

    actual fun isAlwaysLocationPermissionGranted(): Boolean {
        return PermissionController.isPermissionsGranted(
            context?.applicationContext!!,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

    actual fun isLocationPermissionGranted() = isAlwaysLocationPermissionGranted()

    private fun isGranted(result: PermissionResult) = when (result) {
        is PermissionResult.PermissionGranted -> true
        is PermissionResult.PermissionDenied -> false
        is PermissionResult.ShowRational -> false
        is PermissionResult.PermissionDeniedPermanently -> false
    }
}
