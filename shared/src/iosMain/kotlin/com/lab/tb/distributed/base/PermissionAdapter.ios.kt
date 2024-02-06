package com.lab.tb.distributed.base

actual class PermissionAdapter actual constructor(context: ApplicationContext?) {
    actual fun isIOS(): Boolean {
        TODO("Not yet implemented")
    }

    actual suspend fun requestInUseLocationPermission(): Boolean {
        TODO("Not yet implemented")
    }

    actual suspend fun requestAlwaysLocationPermission(): Boolean {
        TODO("Not yet implemented")
    }

    actual fun isAlwaysLocationPermissionGranted(): Boolean {
        TODO("Not yet implemented")
    }

    actual fun isLocationPermissionGranted(): Boolean {
        TODO("Not yet implemented")
    }

}