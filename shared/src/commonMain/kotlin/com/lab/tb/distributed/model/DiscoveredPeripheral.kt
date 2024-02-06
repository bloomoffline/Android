package com.lab.tb.distributed.model

import com.lab.tb.distributed.base.BleGatt
import com.lab.tb.distributed.base.BleGattCharacteristic

data class DiscoveredPeripheral(
    var isConnected: Boolean = false,
    var isDistributedChat: Boolean = false,
    var rssi: Int = 0,

    var inboxCharacteristic: BleGattCharacteristic? = null,
    var userNameCharacteristic: BleGattCharacteristic? = null,
    var userIdCharacteristic: BleGattCharacteristic? = null,
    var gatt: BleGatt? = null,

    var userName: String? = null,
    var userId: String? = null,
    var isWriting: Boolean = false,
    var outGoingData: String? = null,

    ) {
    override fun toString(): String {
        return "DiscoveredPeripheral(isConnected=$isConnected, isDistributedChat=$isDistributedChat, rssi=$rssi, inboxCharacteristic=$inboxCharacteristic, userNameCharacteristic=$userNameCharacteristic, userIdCharacteristic=$userIdCharacteristic, userName=$userName, userId=$userId, isWriting=$isWriting, outGoingData=$outGoingData)"
    }
}
