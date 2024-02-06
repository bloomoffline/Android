package com.lab.tb.distributed.ble

import com.lab.tb.distributed.ChatTransport
import com.lab.tb.distributed.entities.ApplicationContext
import com.lab.tb.distributed.model.BleSettings
import com.lab.tb.distributed.model.Network
import com.lab.tb.distributed.model.Profile

expect class CoreBluetoothTransport(
    context: ApplicationContext
): ChatTransport {
    fun startServer()
    fun stopServer()

    fun startAdvertising()
    fun stopAdvertising()
    fun startScanning()
    fun stopScanning()
}
