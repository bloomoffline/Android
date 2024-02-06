package com.lab.tb.distributed.ble

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattServer
import android.bluetooth.BluetoothGattServerCallback
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.BluetoothLeAdvertiser
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.ParcelUuid
import androidx.annotation.RequiresPermission
import com.lab.tb.distributed.ChatTransport
import com.lab.tb.distributed.base.Log
import com.lab.tb.distributed.entities.ApplicationContext
import com.lab.tb.distributed.ext.ByteArrayExt.toUtf8String
import com.lab.tb.distributed.helper.DiscoveryUserHelper
import com.lab.tb.distributed.model.ChatUser
import com.lab.tb.distributed.model.DiscoveredPeripheral
import com.lab.tb.distributed.model.DiscoveryCentral
import com.lab.tb.distributed.model.NearbyUser
import java.util.UUID


actual class CoreBluetoothTransport actual constructor(
    private val context: ApplicationContext,
): ChatTransport {
    private val mainHandler = Handler(Looper.getMainLooper())

    companion object {
        // Custom UUID specifically for the 'Bloom Chat' service
        private val BLOOM_SERVICE_UUID = UUID.fromString("59553ceb-2ffa-4018-8a6c-453a5292044d")

        // Custom UUID for the (write-only) message inbox characteristic
        private val INBOX_CHARACTERISTIC_UUID =
            UUID.fromString("440a594c-3cc2-494a-a08a-be8dd23549ff")

        // Custom UUID for the user name characteristic (used to display 'nearby' users)
        private val USERNAME_CHARACTERISTIC_UUID =
            UUID.fromString("b2234f40-2c0b-401b-8145-c612b9a7bae1")

        // Custom UUID for the user ID characteristic (user to display 'nearby' users)
        private var USER_ID_CHARACTERISTIC_UUID =
            UUID.fromString("13a4d26e-0a75-4fde-9340-4974e3da3100")
    }

    var mGattOperationManager: GattOperationManager = GattOperationManager()

    var nearbyPeripherals: MutableMap<BluetoothDevice, DiscoveredPeripheral> = mutableMapOf()
    var nearbyCentrals: MutableMap<BluetoothDevice, DiscoveryCentral> = mutableMapOf()
    var nearbyUser: MutableMap<BluetoothDevice, NearbyUser> = mutableMapOf()

    private val mBluetoothManager: BluetoothManager =
        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private var mAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    private var mScanner: BluetoothLeScanner? = null
    private var mBluetoothScanCallBack: DeviceScanCallback? = null

    private var mBluetoothGattServer: BluetoothGattServer? = null
    private var mGattServerCallback: BluetoothGattServerCallback? = null

    private var mAdvertiser: BluetoothLeAdvertiser? = null
    private var mAdvertiseCallback: AdvertiseCallback? = null

    private var mOnReceive: ((String) -> Unit)? = null

    @RequiresPermission(allOf = ["android.permission.BLUETOOTH_CONNECT", "android.permission.BLUETOOTH_ADVERTISE"])
    actual fun startServer() {
        mAdapter = mBluetoothManager.adapter
        startAdvertising()
        setupGattServer()
    }

    @RequiresPermission(allOf = ["android.permission.BLUETOOTH_CONNECT", "android.permission.BLUETOOTH_ADVERTISE", "android.permission.BLUETOOTH_SCAN"])
    actual fun stopServer() {
        stopAdvertising()
        stopScanning()
        closeServer()
    }

    @RequiresPermission(allOf = ["android.permission.BLUETOOTH_CONNECT", "android.permission.BLUETOOTH_ADVERTISE"])
    actual fun startAdvertising() {
        mAdvertiser = mBluetoothManager.adapter.bluetoothLeAdvertiser
        if (mAdvertiseCallback == null) {
            mAdvertiseCallback = DeviceAdvertiseCallback()
            val advertiseSettings: AdvertiseSettings = buildAdvertiseSettings()
            val scanResponse: AdvertiseData = buildScanResponseData()
            mAdvertiser?.startAdvertising(advertiseSettings, scanResponse, mAdvertiseCallback)
        }
    }

    @RequiresPermission(allOf = ["android.permission.BLUETOOTH_CONNECT"])
    private fun setupGattServer() {
        try {
            mGattServerCallback = GattServerCallback()
            mBluetoothGattServer = mBluetoothManager.openGattServer(context, mGattServerCallback).apply {
                addService(createGattService())
            }
        } catch (ex: Exception) {
            Log.e(this, "setupGattServer: ${ex.message}", ex)
        }
    }

    private fun createGattService(): BluetoothGattService {
        return BluetoothGattService(
            BLOOM_SERVICE_UUID, BluetoothGattService.SERVICE_TYPE_PRIMARY
        ).apply {
            listOf(
                BluetoothGattCharacteristic(
                    INBOX_CHARACTERISTIC_UUID,
                    BluetoothGattCharacteristic.PROPERTY_WRITE,
                    BluetoothGattCharacteristic.PERMISSION_WRITE
                ),
                BluetoothGattCharacteristic(
                    USERNAME_CHARACTERISTIC_UUID,
                    BluetoothGattCharacteristic.PROPERTY_READ,
                    BluetoothGattCharacteristic.PERMISSION_READ
                ),
                BluetoothGattCharacteristic(
                    USER_ID_CHARACTERISTIC_UUID,
                    BluetoothGattCharacteristic.PROPERTY_READ,
                    BluetoothGattCharacteristic.PERMISSION_READ
                )
            ).forEach { characteristic ->
                addCharacteristic(characteristic)
            }
        }
    }

    private fun buildAdvertiseData(): AdvertiseData {
        return AdvertiseData.Builder().build()
    }

    private fun buildScanResponseData(): AdvertiseData {
        return AdvertiseData.Builder()
            .setIncludeTxPowerLevel(false)
            .addServiceUuid(ParcelUuid(BLOOM_SERVICE_UUID))
            .build()
    }

    private fun buildAdvertiseSettings(): AdvertiseSettings {
        return AdvertiseSettings.Builder()
            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
            .setConnectable(true)
            .setTimeout(0)
            .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_ULTRA_LOW)
            .build()
    }

    private fun buildScanFilters(): List<ScanFilter> {
        val filter = ScanFilter.Builder().setServiceUuid(ParcelUuid(BLOOM_SERVICE_UUID)).build()
        return listOf(filter)
    }

    private fun buildScanSettings(): ScanSettings {
        return ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
            .build()
    }

    @RequiresPermission(value = "android.permission.BLUETOOTH_CONNECT")
    private fun closeServer() {
        Log.d(this, "closeServer..........")
        val connectedDevices = mBluetoothManager.getConnectedDevices(BluetoothProfile.GATT_SERVER)
        if (!connectedDevices.isNullOrEmpty()) {
            for (device in connectedDevices) {
                Log.d(this, "closeServer..........cancel connection")
                mBluetoothGattServer?.cancelConnection(device)
            }
        }
        Log.d(this, "closeServer..........clear service")
        mBluetoothGattServer?.clearServices()
        Log.d(this, "closeServer..........close")
        mBluetoothGattServer?.close()

        Log.d(this, "closeServer..........completed")
    }

    @RequiresPermission(value = "android.permission.BLUETOOTH_SCAN")
    actual fun startScanning() {
        Log.d("CoreBluetoothTransport", "startScanning")
        if (mBluetoothScanCallBack == null) {
            val scanFilters = buildScanFilters()
            val scanSettings = buildScanSettings()
            mScanner = mBluetoothManager.adapter.bluetoothLeScanner
            mBluetoothScanCallBack = DeviceScanCallback()
            mScanner?.startScan(scanFilters, scanSettings, mBluetoothScanCallBack)
        }
        Log.d(this, "startScan done")
    }

    inner class DeviceScanCallback : ScanCallback() {
        @RequiresPermission(value = "android.permission.BLUETOOTH_CONNECT")
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            result?.let {
                val nativeDevice = mBluetoothManager.adapter.getRemoteDevice(it.device.address)

                val localDevice = nearbyPeripherals[nativeDevice]
                if (localDevice != null) {
                    val nearbyUser = nearbyUser[nativeDevice] ?: NearbyUser(
                        peripheralIdentifier = nativeDevice.address,
                        peripheralName = nativeDevice.name ?: "",
                        chatUser = localDevice.userId?.let { userId->
                            ChatUser(
                                name = localDevice.userName,
                                id = userId
                            )
                        },
                        isVisible = true,
                        rssi = localDevice.rssi,
                    )
                    if (!localDevice.isConnected) {
                        localDevice.gatt = nativeDevice.connectGatt(context, false, GattClientCallback(), BluetoothDevice.TRANSPORT_LE)
                    }
                    DiscoveryUserHelper.notifyOnlineSensor(nearbyUser)
                    return@let
                } else {
                    nearbyUser[nativeDevice]?.let{ near ->
                        near.isVisible = false
                        DiscoveryUserHelper.notifyOnlineSensor(near)
                    }
                }

                Log.d(this, "mainHandler.post onScanResult ")
                nearbyPeripherals[nativeDevice] = DiscoveredPeripheral()
                nearbyPeripherals[nativeDevice]?.isConnected = false

                mainHandler.post {
                    Log.d(this, "mainHandler.post onScanResult ${nativeDevice.address}")
                    val gatt = nativeDevice.connectGatt(context, false, GattClientCallback(), BluetoothDevice.TRANSPORT_LE)
                    nearbyPeripherals[nativeDevice]?.gatt = gatt
                }
            }
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
        }
    }

    inner class GattClientCallback : BluetoothGattCallback() {
        @RequiresPermission(value = "android.permission.BLUETOOTH_CONNECT")
        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            super.onServicesDiscovered(gatt, status)
            Log.d("GattClientCallback", "onServicesDiscovered..........")
            if (status == BluetoothGatt.GATT_SUCCESS) {
                mGattOperationManager.onPerformDone()
                gatt.device?.let { bluetoothDevice ->
                    val localDevice = nearbyPeripherals[bluetoothDevice] ?: return@let
                    gatt.services.let { services ->
                        services.forEach { service ->
                            if (service.uuid == BLOOM_SERVICE_UUID) {
                                service.characteristics.forEach { characteristic ->
                                    when (characteristic.uuid) {
                                        INBOX_CHARACTERISTIC_UUID -> {
                                            localDevice.inboxCharacteristic = characteristic
                                        }


                                        /* USERNAME_CHARACTERISTIC_UUID -> {
                                             localDevice.userNameCharacteristic = characteristic
                                             mGattOperationManager?.enqueueBleGatt(BluetoothGattOperation(
                                                 characteristic = characteristic,
                                                 operationType = OperationType.READ
                                             ))
                                         }

                                         USER_ID_CHARACTERISTIC_UUID -> {
                                             localDevice.userIdCharacteristic = characteristic
                                             mGattOperationManager?.enqueueBleGatt(BluetoothGattOperation(
                                                 characteristic = characteristic,
                                                 operationType = OperationType.READ
                                             ))
                                         }*/
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }

        @RequiresPermission(value = "android.permission.BLUETOOTH_CONNECT")
        override fun onMtuChanged(gatt: BluetoothGatt?, mtu: Int, status: Int) {
            super.onMtuChanged(gatt, mtu, status)
            Log.d("GattClientCallback", "onMtuChanged.......... {$mtu} {$status}")
            mGattOperationManager.onPerformDone()
        }

        @RequiresPermission(value = "android.permission.BLUETOOTH_CONNECT")
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            val localDevice = nearbyPeripherals[gatt.device] ?: return
            println("onConnectionStateChange: $status $newState")
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    localDevice.isConnected = true
                    mainHandler.postDelayed({
                        mGattOperationManager.enqueueBleGatt(BluetoothQueueOperation(
                            operationType = OperationType.DISCOVER_SERVICES,
                            gatt = gatt
                        ))

                        mGattOperationManager.enqueueBleGatt(BluetoothQueueOperation(
                            operationType = OperationType.MTU,
                            gatt = gatt
                        ))
                    }, 0)
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    mainHandler.post {
                        gatt.close()
                        nearbyPeripherals.remove(gatt.device)
                    }
                }
            } else {
                mainHandler.post {
                    gatt.close()
                    nearbyPeripherals.remove(gatt.device)
                }
            }
        }

        @RequiresPermission(value = "android.permission.BLUETOOTH_CONNECT")
        override fun onCharacteristicWrite(
            gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int
        ) {
            super.onCharacteristicWrite(gatt, characteristic, status)
            characteristic?.let {
                Log.d("onCharacteristicWrite", "characteristic:${it.uuid} status:$status value: ${it .value?.toUtf8String()}")
            }
            mGattOperationManager.onPerformDone()
        }

        @RequiresPermission(value = "android.permission.BLUETOOTH_CONNECT")
        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray,
            status: Int
        ) {
            super.onCharacteristicRead(gatt, characteristic, value, status)
            onHandleCharacteristicRead(gatt, characteristic, value, status)
        }

        @RequiresPermission(value = "android.permission.BLUETOOTH_CONNECT")
        override fun onCharacteristicRead(
            gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int
        ) {
            super.onCharacteristicRead(gatt, characteristic, status)
            onHandleCharacteristicRead(gatt, characteristic, characteristic?.value, status)
        }

        @RequiresPermission(value = "android.permission.BLUETOOTH_CONNECT")
        private fun onHandleCharacteristicRead(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            value: ByteArray?,
            status: Int
        ) {
            characteristic?.let {
                val local = nearbyPeripherals[gatt?.device]
                when (it.uuid) {
                    USERNAME_CHARACTERISTIC_UUID -> {
                        Log.d(
                            "onCharacteristicRead",
                            "mUserNameCharacteristicUUID status:$status value: ${value?.toUtf8String()}"
                        )
                        value?.toUtf8String()?.let { userName ->
                            local?.userName = userName
                        }
                        mGattOperationManager.onPerformDone()
                    }

                    USER_ID_CHARACTERISTIC_UUID -> {
                        value?.toUtf8String()?.let { userId ->
                            local?.userId = userId
                        }
                        Log.d(
                            "onCharacteristicRead", "mUserIDCharacteristicUUID status:$status value: ${value?.toUtf8String()}"
                        )
                        mGattOperationManager.onPerformDone()
                    }

                    else -> {}
                }
            }
        }
    }

    @RequiresPermission(allOf = ["android.permission.BLUETOOTH_ADVERTISE"])
    actual fun stopAdvertising() {
        if (mAdvertiser != null && mAdvertiseCallback != null) {
            mAdvertiser?.stopAdvertising(mAdvertiseCallback)
            mAdvertiseCallback = null
        }
    }

    @RequiresPermission(value = "android.permission.BLUETOOTH_SCAN")
    actual fun stopScanning() {
        mScanner?.stopScan(mBluetoothScanCallBack)
        mBluetoothScanCallBack = null
    }

    @RequiresPermission(value = "android.permission.BLUETOOTH_CONNECT")
    override fun broadcast(raw: String) {
        Log.d(this, "broadcast.......... ${nearbyPeripherals.size}")
        nearbyPeripherals.forEach { (device, peripheral) ->
            if (peripheral.isConnected && peripheral.inboxCharacteristic != null) {
                val maxValueSize = 512
                val dataBytes = raw.toByteArray()
                var offset = 0
                while (offset < dataBytes.size) {
                    val endOffset = if (offset + maxValueSize > dataBytes.size) dataBytes.size else offset + maxValueSize
                    val chunk = dataBytes.copyOfRange(offset, endOffset)
                    mGattOperationManager.enqueueBleGatt(
                        BluetoothQueueOperation(
                            characteristic = peripheral.inboxCharacteristic!!,
                            operationType = OperationType.WRITE,
                            message = chunk,
                            gatt = peripheral.gatt!!
                        )
                    )
                    offset += maxValueSize
                }
            } else {
                Log.d(this, "broadcast..........not connected")
            }
        }
    }

    override fun onReceive(handler: (String) -> Unit) {
        mOnReceive = handler
    }

    private inner class GattServerCallback: BluetoothGattServerCallback() {
        @RequiresPermission(value = "android.permission.BLUETOOTH_CONNECT")
        override fun onConnectionStateChange(
            device: BluetoothDevice, status: Int, newState: Int
        ) {
            super.onConnectionStateChange(device, status, newState)
            val isSuccess = status == BluetoothGatt.GATT_SUCCESS
            val isConnected = newState == BluetoothProfile.STATE_CONNECTED
            Log.d(this, "onConnectionStateChange.......... $isSuccess $isConnected")
        }

        @RequiresPermission(value = "android.permission.BLUETOOTH_CONNECT")
        override fun onCharacteristicReadRequest(
            device: BluetoothDevice?,
            requestId: Int,
            offset: Int,
            characteristic: BluetoothGattCharacteristic?
        ) {
            super.onCharacteristicReadRequest(device, requestId, offset, characteristic)
            when (characteristic?.uuid) {
                USERNAME_CHARACTERISTIC_UUID -> {
                    mBluetoothGattServer?.sendResponse(
                        device,
                        requestId,
                        BluetoothGatt.GATT_SUCCESS,
                        offset,
                        "".toByteArray()
                    )
                }

                USER_ID_CHARACTERISTIC_UUID -> {
                    mBluetoothGattServer?.sendResponse(
                        device,
                        requestId,
                        BluetoothGatt.GATT_SUCCESS,
                        offset,
                        "".toByteArray()
                    )
                }

                else -> {
                    mBluetoothGattServer?.sendResponse(
                        device,
                        requestId,
                        BluetoothGatt.GATT_FAILURE,
                        offset,
                        null
                    )
                }
            }
        }

        @RequiresPermission(value = "android.permission.BLUETOOTH_CONNECT")
        override fun onCharacteristicWriteRequest(
            device: BluetoothDevice?,
            requestId: Int,
            characteristic: BluetoothGattCharacteristic?,
            preparedWrite: Boolean,
            responseNeeded: Boolean,
            offset: Int,
            value: ByteArray?
        ) {
            Log.d("GattServerCallback", "onCharacteristicWriteRequest.......... for ${characteristic?.uuid} ${value?.toUtf8String()}")
            super.onCharacteristicWriteRequest(
                device,
                requestId,
                characteristic,
                preparedWrite,
                responseNeeded,
                offset,
                value
            )

            if (characteristic?.uuid == INBOX_CHARACTERISTIC_UUID) {
                value?.let {
                    if (device == null) return@let
                    val state = nearbyCentrals[device] ?: DiscoveryCentral()
                    nearbyCentrals[device] = state
                    if (responseNeeded) {
                        println("onCharacteristicWriteRequest: send response")
                        mBluetoothGattServer?.sendResponse(
                            device,
                            requestId,
                            BluetoothGatt.GATT_SUCCESS,
                            offset,
                            value
                        )
                    }
                    state.incomingData.addAll(it.asList())
                    state.dequeueLine()?.let { line ->
                        println("onCharacteristicWriteRequest: receive ${line.decodeToString()}")
                        mOnReceive?.invoke(line.decodeToString())
                    }

                    if (state.incomingData.isEmpty()) {
                        nearbyCentrals.remove(device)
                    }
                }
            }
        }
    }

    private inner class DeviceAdvertiseCallback: AdvertiseCallback() {
        @RequiresPermission(value = "android.permission.BLUETOOTH_CONNECT")
        override fun onStartSuccess(settingsInEffect: AdvertiseSettings) {
            Log.i(this, "LE Advertise Started.")
        }

        @RequiresPermission(value = "android.permission.BLUETOOTH_CONNECT")
        override fun onStartFailure(errorCode: Int) {
            Log.d(this, "LE Advertise Failed: $errorCode")
        }
    }
}