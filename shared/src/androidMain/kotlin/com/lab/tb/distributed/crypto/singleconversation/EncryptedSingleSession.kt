package com.lab.tb.distributed.crypto.singleconversation

import com.lab.tb.distributed.crypto.entity.EncryptedLocalUser
import com.lab.tb.distributed.crypto.entity.EncryptedRemoteUser
import com.lab.tb.distributed.crypto.helper.Helper
import org.whispersystems.libsignal.DuplicateMessageException
import org.whispersystems.libsignal.InvalidKeyException
import org.whispersystems.libsignal.InvalidKeyIdException
import org.whispersystems.libsignal.InvalidMessageException
import org.whispersystems.libsignal.InvalidVersionException
import org.whispersystems.libsignal.LegacyMessageException
import org.whispersystems.libsignal.SessionBuilder
import org.whispersystems.libsignal.SessionCipher
import org.whispersystems.libsignal.SignalProtocolAddress
import org.whispersystems.libsignal.UntrustedIdentityException
import org.whispersystems.libsignal.protocol.PreKeySignalMessage
import org.whispersystems.libsignal.state.PreKeyBundle
import org.whispersystems.libsignal.state.impl.InMemorySignalProtocolStore
import java.nio.charset.StandardCharsets

class EncryptedSingleSession {
    private enum class Operation {
        ENCRYPT,
        DECRYPT
    }

    private var mSessionCipher: SessionCipher? = null
    private var lastOp: Operation? = null
    private val protocolAddress: SignalProtocolAddress
    private val localUser: EncryptedLocalUser
    private var remoteUser: EncryptedRemoteUser? = null

    constructor(localUser: EncryptedLocalUser, remoteUser: EncryptedRemoteUser) {
        protocolAddress = remoteUser.signalProtocolAddress
        this.localUser = localUser
        this.remoteUser = remoteUser
    }

    constructor(
        localUser: EncryptedLocalUser,
        remotePersonProtocolAddress: SignalProtocolAddress
    ) {
        protocolAddress = remotePersonProtocolAddress
        this.localUser = localUser
    }

    private fun initSession() {
        val protocolStore =
            InMemorySignalProtocolStore(localUser.identityKeyPair, localUser.registrationId)
        for (record in localUser.preKeys) {
            protocolStore.storePreKey(record.id, record)
        }
        protocolStore.storeSignedPreKey(localUser.signedPreKey.id, localUser.signedPreKey)
        mSessionCipher = SessionCipher(protocolStore, protocolAddress)
    }

    @Throws(UntrustedIdentityException::class, InvalidKeyException::class)
    private fun initSessionFromPreKey() {
        val protocolStore =
            InMemorySignalProtocolStore(localUser.identityKeyPair, localUser.registrationId)
        for (record in localUser.preKeys) {
            protocolStore.storePreKey(record.id, record)
        }
        protocolStore.storeSignedPreKey(localUser.signedPreKey.id, localUser.signedPreKey)

        //Session
        val sessionBuilder = SessionBuilder(protocolStore, remoteUser!!.signalProtocolAddress)
        val preKeyBundle = PreKeyBundle(
            remoteUser!!.registrationId,
            remoteUser!!.signalProtocolAddress.deviceId,
            remoteUser!!.preKeyId,
            remoteUser!!.preKeyPublicKey,
            remoteUser!!.signedPreKeyId,
            remoteUser!!.signedPreKeyPublicKey,
            remoteUser!!.signedPreKeySignature,
            remoteUser!!.identityKeyPairPublicKey
        )
        sessionBuilder.process(preKeyBundle)
        mSessionCipher = SessionCipher(protocolStore, protocolAddress)
    }

    @Throws(UntrustedIdentityException::class, InvalidKeyException::class)
    private fun createSession(operation: Operation) {
        if (operation == lastOp) {
            return
        }
        lastOp = operation
        if (remoteUser == null) {
            initSession()
        } else {
            initSessionFromPreKey()
        }
    }

    @Throws(
        UntrustedIdentityException::class,
        InvalidKeyException::class,
        InvalidMessageException::class,
        InvalidVersionException::class
    )
    fun encrypt(message: String): String {
        createSession(Operation.ENCRYPT)
        val ciphertextMessage = mSessionCipher!!.encrypt(message.toByteArray())
        val preKeySignalMessage = PreKeySignalMessage(ciphertextMessage.serialize())
        return Helper.encodeToBase64(preKeySignalMessage.serialize())
    }

    @Throws(
        UntrustedIdentityException::class,
        InvalidKeyException::class,
        InvalidMessageException::class,
        InvalidVersionException::class,
        DuplicateMessageException::class,
        InvalidKeyIdException::class,
        LegacyMessageException::class
    )
    fun decrypt(message: String?): String {
        createSession(Operation.DECRYPT)
        val bytes = Helper.decodeToByteArray(message)
        val decryptedMessage = mSessionCipher!!.decrypt(PreKeySignalMessage(bytes))
        return String(decryptedMessage, StandardCharsets.UTF_8)
    }
}