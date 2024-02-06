package com.lab.tb.distributed.crypto.entity

import org.whispersystems.libsignal.IdentityKeyPair
import org.whispersystems.libsignal.SignalProtocolAddress
import org.whispersystems.libsignal.state.PreKeyRecord
import org.whispersystems.libsignal.state.SignedPreKeyRecord
import java.io.IOException

class EncryptedLocalUser(
    identityKeyPair: ByteArray?,
    registrationId: Int,
    name: String?,
    deviceId: Int,
    preKeys: List<ByteArray?>,
    signedPreKey: ByteArray?
) :
    BaseEncryptedEntity(registrationId, SignalProtocolAddress(name, deviceId)) {
    val identityKeyPair: IdentityKeyPair
    private val preKey: MutableList<PreKeyRecord>
    val signedPreKey: SignedPreKeyRecord

    init {
        this.identityKeyPair = IdentityKeyPair(identityKeyPair)
        preKey = ArrayList()
        for (item in preKeys) {
            preKey.add(PreKeyRecord(item))
        }
        this.signedPreKey = SignedPreKeyRecord(signedPreKey)
    }

    val preKeys: List<PreKeyRecord>
        get() = preKey

    companion object {
        @Throws(IOException::class)
        fun toPreKeyRecord(bytes: ByteArray?): PreKeyRecord {
            return PreKeyRecord(bytes)
        }
    }
}