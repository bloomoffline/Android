package com.lab.tb.distributed.chat.android.receiver

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

class BluetoothConnectionReceiver(
    private val context: Context,
    private val onConnectionStateChanged: (isEnabled: Boolean) -> Unit
) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
            when (intent?.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)) {
                BluetoothAdapter.ERROR,
                BluetoothAdapter.STATE_OFF,
                BluetoothAdapter.STATE_TURNING_OFF -> {
                    onConnectionStateChanged(false)
                }
                BluetoothAdapter.STATE_ON -> {
                    onConnectionStateChanged(true)
                }
            }
        }
        onConnectionStateChanged
    }

    fun register() {
        val intentFilter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        context.registerReceiver(this, intentFilter)
    }

    fun unregister() {
        context.unregisterReceiver(this)
    }
}