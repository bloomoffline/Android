package com.lab.tb.distributed.mapper.impl

import com.lab.tb.distributed.database.models.PublicPartDb
import com.lab.tb.distributed.mapper.contracts.IPublicPartMapper
import com.lab.tb.distributed.model.PublicPart

class PublicPartMapper : IPublicPartMapper {
    override fun fromDbToEntity(db: PublicPartDb?): PublicPart? {
        if (db == null) return null
        return PublicPart(
            id = db.id,
            name = db.name,
            deviceId = db.deviceId?.toInt(),
            registrationId = db.registrationId?.toInt(),
            preKeyId = db.preKeyId?.toInt(),
            preKeyPublicKey = db.preKeyPublicKey,
            signedPreKeyId = db.signedPreKeyId?.toInt(),
            signedPreKeyPublicKey = db.signedPreKeyPublicKey,
            signedPreKeySignature = db.signedPreKeySignature,
            identityKeyPairPublicKey = db.identityKeyPairPublicKey
        )
    }

    override fun fromEntityToDb(entity: PublicPart?): PublicPartDb? {
        if (entity == null) return null
        return PublicPartDb(
            id = entity.id,
            name = entity.name,
            deviceId = entity.deviceId?.toLong(),
            registrationId = entity.registrationId?.toLong(),
            preKeyId = entity.preKeyId?.toLong(),
            preKeyPublicKey = entity.preKeyPublicKey,
            signedPreKeyId = entity.signedPreKeyId?.toLong(),
            signedPreKeyPublicKey = entity.signedPreKeyPublicKey,
            signedPreKeySignature = entity.signedPreKeySignature,
            identityKeyPairPublicKey = entity.identityKeyPairPublicKey
        )
    }
}
