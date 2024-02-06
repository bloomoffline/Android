package com.lab.tb.distributed.ble

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothStatusCodes
import android.os.Build
import androidx.annotation.RequiresPermission
import com.lab.tb.distributed.base.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import java.util.LinkedList
import java.util.Queue


enum class OperationType {
    READ,
    WRITE,
    RSSI,
    MTU,
    DISCOVER_SERVICES,
}

data class BluetoothQueueOperation(
    val characteristic: BluetoothGattCharacteristic? = null,
    val operationType: OperationType,
    val message: ByteArray? = null,
    val gatt: BluetoothGatt
)

@Suppress("DEPRECATION")
class GattOperationManager() {

    private val characteristicQueue: Queue<BluetoothQueueOperation> = LinkedList()
    private var isReading = false

    var timerJob: Job? = null
    val scope = CoroutineScope(Dispatchers.Default)


    @RequiresPermission(value = "android.permission.BLUETOOTH_CONNECT")
    fun enqueueBleGatt(operation: BluetoothQueueOperation) {
        characteristicQueue.offer(operation)
        if (!isReading) {
            onPerform()
        }
    }

    @RequiresPermission(value = "android.permission.BLUETOOTH_CONNECT")
    private fun onPerform() {
        val queue = characteristicQueue.peek()
        if (queue != null) {
            isReading = true
            println("GattOperationManager onPerform: ${queue.operationType}")
            when (queue.operationType) {
                OperationType.MTU -> {
                    queue.gatt.requestMtu(517)
                }

                OperationType.DISCOVER_SERVICES -> {
                    var result = false
                    try {
                        result = queue.gatt.discoverServices()
                    } finally {
                        if (!result) {
                            onPerformDone()
                        }
                    }
                }

                OperationType.WRITE -> {
                    var result = false
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            println("GattOperationManager onPerform: 13  ${queue.message?.decodeToString() }")
                            result = queue.gatt.writeCharacteristic(
                                queue.characteristic!!,
                                queue.message ?: byteArrayOf(),
                                BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
                            ) == BluetoothStatusCodes.SUCCESS
                            println("GattOperationManager onPerform: $result")
                        } else {
                            println("GattOperationManager onPerform: ${queue.message?.decodeToString() }")
                            queue.characteristic!!.value = queue.message
                            queue.characteristic.writeType =
                                BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
                            result = queue.gatt.writeCharacteristic(queue.characteristic)
                            println("GattOperationManager onPerform: $result")
                        }
                    } catch (ex: Exception) {
                        Log.e(this, "GattOperationManager onPerform: ${ex.message}", ex)
                        result = false
                    } finally {
                        if (!result) {
                            onPerformDone()
                        }
                    }
                }

                OperationType.RSSI -> {
                    var result = false
                    try {
                        result = queue.gatt.readRemoteRssi()
                    } finally {
                        if (!result) {
                            onPerformDone()
                        }
                    }
                }
                OperationType.READ -> {
                    var result = false
                   try {
                       result = queue.gatt.readCharacteristic(queue.characteristic)
                   } finally {
                       if (!result) {
                           onPerformDone()
                       }
                   }
                }
            }
        } else {
            isReading = false
        }
    }

    @RequiresPermission(value = "android.permission.BLUETOOTH_CONNECT")
    fun onPerformDone() {
        val hello = characteristicQueue.poll()
        println("GattOperationManager onPerformDone: ${hello?.operationType}")
        isReading = false
        onPerform()
    }
}