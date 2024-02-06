package com.lab.tb.distributed.crypto.entity

import org.whispersystems.libsignal.IdentityKey
import org.whispersystems.libsignal.SignalProtocolAddress
import org.whispersystems.libsignal.ecc.Curve
import org.whispersystems.libsignal.ecc.ECPublicKey

class EncryptedRemoteUser(
    registrationId: Int,
    name: String?,
    deviceId: Int,
    val preKeyId: Int,
    preKeyPublicKey: ByteArray?,
    val signedPreKeyId: Int,
    signedPreKeyPublicKey: ByteArray?,
    val signedPreKeySignature: ByteArray,
    identityKeyPairPublicKey: ByteArray?
) :
    BaseEncryptedEntity(registrationId, SignalProtocolAddress(name, deviceId)) {
    val preKeyPublicKey: ECPublicKey
    val signedPreKeyPublicKey: ECPublicKey
    val identityKeyPairPublicKey: IdentityKey

    init {
        this.preKeyPublicKey = Curve.decodePoint(preKeyPublicKey, 0)
        this.signedPreKeyPublicKey = Curve.decodePoint(signedPreKeyPublicKey, 0)
        this.identityKeyPairPublicKey = IdentityKey(identityKeyPairPublicKey, 0)
    }
}