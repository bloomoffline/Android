package com.lab.tb.distributed.crypto.registration

import com.lab.tb.distributed.crypto.helper.Helper
import org.whispersystems.libsignal.IdentityKeyPair
import org.whispersystems.libsignal.state.PreKeyRecord
import org.whispersystems.libsignal.state.SignedPreKeyRecord

class RegistrationItem {
    var identityKeyPair: IdentityKeyPair
    var registrationId: Int
    var preKeys: List<PreKeyRecord>
    var signedPreKeyRecord: SignedPreKeyRecord

    constructor(
        identityKeyPair: IdentityKeyPair,
        registrationId: Int,
        preKeys: List<PreKeyRecord>,
        signedPreKeyRecord: SignedPreKeyRecord
    ) {
        this.identityKeyPair = identityKeyPair
        this.registrationId = registrationId
        this.preKeys = preKeys
        this.signedPreKeyRecord = signedPreKeyRecord
    }

    constructor(
        identityKeyPair: String?,
        registrationId: Int,
        preKeys: Array<String?>,
        signedPreKeyRecord: String?
    ) {
        this.identityKeyPair = IdentityKeyPair(Helper.decodeToByteArray(identityKeyPair))
        this.registrationId = registrationId
        val preKeyRecords: MutableList<PreKeyRecord> = ArrayList()
        for (item in preKeys) {
            preKeyRecords.add(PreKeyRecord(Helper.decodeToByteArray(item)))
        }
        this.preKeys = preKeyRecords
        this.signedPreKeyRecord = SignedPreKeyRecord(Helper.decodeToByteArray(signedPreKeyRecord))
    }

    val identityKeyPairString: String
        get() = Helper.encodeToBase64(identityKeyPair.serialize())
    val identityKeyPublicString: String
        get() = Helper.encodeToBase64(identityKeyPair.publicKey.serialize())
    val preKeysBytes: List<ByteArray>
        get() {
            val result: MutableList<ByteArray> = ArrayList()
            for (preKey in preKeys) {
                result.add(preKey.serialize())
            }
            return result
        }

    fun signedPreKeyRecord(): ByteArray {
        return signedPreKeyRecord.serialize()
    }

    val signedPreKeyRecordString: String
        get() = Helper.encodeToBase64(signedPreKeyRecord.serialize())
    val publicIdentityKey: String
        get() = Helper.encodeToBase64(identityKeyPair.publicKey.serialize())
    val signedPreKeyPublicKey: String
        get() = Helper.encodeToBase64(signedPreKeyRecord.keyPair.publicKey.serialize())
    val signedPreKeyId: Int
        get() = signedPreKeyRecord.id
    val signedPreKeyRecordSignature: String
        get() = Helper.encodeToBase64(signedPreKeyRecord.signature)
}