

package com.lab.tb.distributed.model

import com.lab.tb.distributed.entities.BaseEntity

data class BleSettings (
    var advertisingEnabled: Boolean = true,
    var scanningEnabled: Boolean = true,
    var monitorSignalStrengthInterval: Int = 5,
    var enableNotification: Boolean = true,
    var showRoomAndMessagesPreviews: Boolean = true
): BaseEntity() {

}