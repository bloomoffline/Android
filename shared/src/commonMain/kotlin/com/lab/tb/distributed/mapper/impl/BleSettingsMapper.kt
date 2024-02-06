
package com.lab.tb.distributed.mapper.impl

import com.lab.tb.distributed.database.models.BleSettingsDb
import com.lab.tb.distributed.mapper.contracts.IBleSettingsMapper
import com.lab.tb.distributed.model.BleSettings

class BleSettingsMapper: IBleSettingsMapper {
    override fun fromDbToEntity(db: BleSettingsDb?): BleSettings? {
        if (db == null) return BleSettings()
        return BleSettings(
            advertisingEnabled = db.advertisingEnabled ?: true,
            scanningEnabled = db.scanningEnabled ?: true,
            monitorSignalStrengthInterval = db.monitorSignalStrengthInterval?.toInt() ?: 5,
            enableNotification = db.enableNotification ?: true,
            showRoomAndMessagesPreviews = db.showRoomAndMessagesPreviews ?: true
        ).apply {
            id = db.id
        }
    }

    override fun fromEntityToDb(entity: BleSettings?): BleSettingsDb? {
        if (entity == null) return null
        return BleSettingsDb(
            id = entity.id,
            advertisingEnabled = entity.advertisingEnabled,
            scanningEnabled = entity.scanningEnabled,
            monitorSignalStrengthInterval = entity.monitorSignalStrengthInterval.toLong(),
            enableNotification = entity.enableNotification,
            showRoomAndMessagesPreviews = entity.showRoomAndMessagesPreviews
        )
    }
}