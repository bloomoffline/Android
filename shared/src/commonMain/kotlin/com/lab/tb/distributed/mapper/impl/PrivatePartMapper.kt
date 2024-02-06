package com.lab.tb.distributed.mapper.impl

import com.lab.tb.distributed.database.models.PrivatePartDb
import com.lab.tb.distributed.mapper.contracts.IPrivatePartMapper
import com.lab.tb.distributed.model.PrivatePart
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class PrivatePartMapper : IPrivatePartMapper {
    override fun fromDbToEntity(db: PrivatePartDb?): PrivatePart? {
        if (db == null) return null
        return PrivatePart(
            id = db.id,
            name = db.name ?: "",
            deviceId = db.deviceId?.toInt() ?: 0,
            registrationId = db.registrationId?.toInt() ?: 0,
            identityKeyPair = db.identityKeyPair,
            preKeys = db.preKeys?.split(",")?.map { it } ?: listOf(),
            signedPreKey = db.signedPreKey
        )
    }

    override fun fromEntityToDb(entity: PrivatePart?): PrivatePartDb? {
        if (entity == null) return null

        return PrivatePartDb(
            id = entity.id,
            name = entity.name,
            deviceId = entity.deviceId.toLong(),
            registrationId = entity.registrationId.toLong(),
            identityKeyPair = entity.identityKeyPair,
            preKeys = entity.preKeys.joinToString(separator = ","),
            signedPreKey = entity.signedPreKey
        )
    }
}
