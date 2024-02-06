package com.lab.tb.distributed.chat.android

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat

class PermissionsUtil(private val activity: Activity) {

    private val REQ_CODE = 9999

    private var permissionCallback: ((Boolean) -> Unit)? = null

    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode != REQ_CODE) return

        val isGrantedAll =
            permissions.size == grantResults.filter { it == PackageManager.PERMISSION_GRANTED }.size
        permissionCallback?.invoke(isGrantedAll)
    }

    fun requestLocationPermission(action: ((Boolean) -> Unit)) {
        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        if (isGrantedPermission(permission)) {
            action.invoke(true)
            return
        }

        permissionCallback = action
        ActivityCompat.requestPermissions(activity, arrayOf(permission), REQ_CODE)
    }

    fun requestBluetoothPermission(action: ((Boolean) -> Unit)) {
        permissionCallback = action
        var permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_ADVERTISE,
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        } else {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions += Manifest.permission.POST_NOTIFICATIONS
        }

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
            permissions += Manifest.permission.WRITE_EXTERNAL_STORAGE
            permissions += Manifest.permission.READ_EXTERNAL_STORAGE
        }

        permissions += Manifest.permission.READ_CONTACTS
        permissions += Manifest.permission.RECORD_AUDIO
        permissions += Manifest.permission.CAMERA

        var isGranted = true
        permissions.forEach {
            if (!isGrantedPermission(it)) {
                isGranted = false
            }
        }
        if (isGranted) {
            action.invoke(true)
            return
        }
        ActivityCompat.requestPermissions(activity, permissions, REQ_CODE)
    }

    fun requestBackgroundLocationPermission(action: ((Boolean) -> Unit)) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            action.invoke(false)
            return
        }

        val permission = Manifest.permission.ACCESS_BACKGROUND_LOCATION
        if (isGrantedPermission(permission)) {
            action.invoke(true)
            return
        }

        permissionCallback = action
        ActivityCompat.requestPermissions(activity, arrayOf(permission), REQ_CODE)
    }

    fun isGrantedPermission(permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(
            activity,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }


    fun isBleGrantedPermission(vararg permission: String): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val pers = arrayOf(
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_ADVERTISE,
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.ACCESS_FINE_LOCATION
            )

            pers.forEach {
                if (!isGrantedPermission(it)) {
                    return false
                }
            }
            return true
        } else {
            return isGrantedPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
}