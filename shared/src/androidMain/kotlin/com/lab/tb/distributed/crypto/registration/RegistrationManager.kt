package com.lab.tb.distributed.crypto.registration

import org.whispersystems.libsignal.IdentityKey
import org.whispersystems.libsignal.IdentityKeyPair
import org.whispersystems.libsignal.InvalidKeyException
import org.whispersystems.libsignal.ecc.Curve
import org.whispersystems.libsignal.state.PreKeyRecord
import org.whispersystems.libsignal.state.SignedPreKeyRecord
import org.whispersystems.libsignal.util.KeyHelper
import org.whispersystems.libsignal.util.Medium
import java.io.IOException
import java.util.LinkedList
import java.util.Random

object RegistrationManager {
    const val DEFAULT_DEVICE_ID = 2
    private const val BATCH_SIZE = 100
    fun generateIdentityKeyPair(): IdentityKeyPair {
        val djbKeyPair = Curve.generateKeyPair()
        val djbIdentityKey = IdentityKey(djbKeyPair.publicKey)
        val djbPrivateKey = djbKeyPair.privateKey
        return IdentityKeyPair(djbIdentityKey, djbPrivateKey)
    }

    @Throws(InvalidKeyException::class)
    fun generateSignedPreKey(
        identityKeyPair: IdentityKeyPair,
        signedPreKeyId: Int
    ): SignedPreKeyRecord {
        val keyPair = Curve.generateKeyPair()
        val signature =
            Curve.calculateSignature(identityKeyPair.privateKey, keyPair.publicKey.serialize())
        return SignedPreKeyRecord(signedPreKeyId, System.currentTimeMillis(), keyPair, signature)
    }

    fun generatePreKeys(): List<PreKeyRecord> {
        val records: MutableList<PreKeyRecord> = LinkedList()
        val preKeyIdOffset = Random().nextInt(Medium.MAX_VALUE - 101)
        for (i in 0 until BATCH_SIZE) {
            val preKeyId = (preKeyIdOffset + i) % Medium.MAX_VALUE
            val keyPair = Curve.generateKeyPair()
            val record = PreKeyRecord(preKeyId, keyPair)
            records.add(record)
        }
        return records
    }

    @Throws(InvalidKeyException::class, IOException::class)
    fun generateKeys(): RegistrationItem {
        val identityKeyPair = generateIdentityKeyPair()
        val registrationId = KeyHelper.generateRegistrationId(false)
        val signedPreKey =
            generateSignedPreKey(identityKeyPair, Random().nextInt(Medium.MAX_VALUE - 1))
        val preKeys = generatePreKeys()
        return RegistrationItem(
            identityKeyPair,
            registrationId,
            preKeys,
            signedPreKey
        )
    }
}