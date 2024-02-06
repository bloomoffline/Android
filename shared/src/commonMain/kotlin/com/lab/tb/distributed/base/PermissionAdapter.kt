package com.lab.tb.distributed.base

import com.lab.tb.distributed.entities.ApplicationContext

expect class PermissionAdapter(context: ApplicationContext?) {

    fun isIOS(): Boolean

    suspend fun requestInUseLocationPermission(): Boolean

    suspend fun requestAlwaysLocationPermission(): Boolean

    fun isAlwaysLocationPermissionGranted(): Boolean

    fun isLocationPermissionGranted(): Boolean
}
