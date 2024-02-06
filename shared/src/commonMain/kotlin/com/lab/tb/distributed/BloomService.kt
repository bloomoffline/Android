package com.lab.tb.distributed

import com.lab.tb.distributed.ble.CoreBluetoothTransport
import com.lab.tb.distributed.entities.ApplicationContext

class BloomService {

    private var bleTransport: CoreBluetoothTransport? = null

    companion object {
        private val instance = BloomService()
        fun getInstance(): BloomService {
            return instance
        }
    }

    fun getBleTransport(): ChatTransport? {
        return bleTransport
    }

    fun init(context: ApplicationContext) {
        bleTransport = CoreBluetoothTransport(
            context
        )
    }

    fun stopServer() {
        bleTransport?.stopServer()
    }

    fun startAdvertising() {
        bleTransport?.startServer()
    }

    fun startScanning() {
        bleTransport?.startScanning()
    }

    fun stopAdvertising() {
        bleTransport?.stopAdvertising()
    }

    fun stopScanning() {
        bleTransport?.stopScanning()
    }
}