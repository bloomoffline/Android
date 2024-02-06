package com.lab.tb.distributed.crypto

import com.lab.tb.distributed.crypto.entity.EncryptedLocalUser
import com.lab.tb.distributed.crypto.entity.EncryptedRemoteUser
import com.lab.tb.distributed.crypto.registration.RegistrationItem
import com.lab.tb.distributed.crypto.registration.RegistrationManager
import com.lab.tb.distributed.crypto.singleconversation.EncryptedSingleSession
import com.lab.tb.distributed.model.PrivatePart
import com.lab.tb.distributed.model.PublicPart
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

//import java.util.Base64

actual class CryptoManager {
    actual companion object {
        val items: MutableMap<String, RegistrationItem> = mutableMapOf()
        val sessions: MutableMap<String, EncryptedSingleSession> = mutableMapOf()

        @OptIn(ExperimentalEncodingApi::class)
        actual fun generatePrivatePart(id: String): PrivatePart? {
            if (items[id] == null) {
                items[id] = RegistrationManager.generateKeys()
            }
            items[id]?.let { item ->
                 return PrivatePart(
                    id = id,
                    name = id,
                    deviceId = RegistrationManager.DEFAULT_DEVICE_ID,
                    registrationId = item.registrationId,
                    identityKeyPair = Base64.encode(item.identityKeyPair.serialize()),
                    preKeys = item.preKeysBytes.map { Base64.encode(it) },
                    signedPreKey = Base64.encode(item.signedPreKeyRecord())
                )
            }

            return null
        }

        @OptIn(ExperimentalEncodingApi::class)
        actual fun generatePublicPart(id: String): PublicPart? {
            if (items[id] == null) {
                items[id] = RegistrationManager.generateKeys()
            }

            items[id]?.let { item ->
                return PublicPart(
                    id = id,
                    name = id,
                    registrationId = item.registrationId,
                    preKeyId = item.preKeys[0].id,
                    deviceId = RegistrationManager.DEFAULT_DEVICE_ID,
                    preKeyPublicKey = Base64.encode(item.preKeys[0].keyPair.publicKey.serialize()),
                    signedPreKeyId = item.signedPreKeyId,
                    signedPreKeyPublicKey = Base64.encode(item.signedPreKeyRecord.keyPair.publicKey.serialize()),
                    signedPreKeySignature = Base64.encode(item.signedPreKeyRecord.signature),
                    identityKeyPairPublicKey = Base64.encode(item.identityKeyPair.publicKey.serialize())
                )
            }
            return null
        }

        @OptIn(ExperimentalEncodingApi::class)
        actual fun initSession(
            sessionId: String,
            privatePart: PrivatePart,
            publicPart: PublicPart
        ) {
            if (sessions[sessionId] != null) {
                println("Session already exists for $sessionId")
                return
            }
            val localUser = EncryptedLocalUser(
                identityKeyPair = Base64.decode(privatePart.identityKeyPair ?: ""),
                registrationId = privatePart.registrationId,
                name = privatePart.name,
                deviceId = privatePart.deviceId,
                preKeys = privatePart.preKeys.map { Base64.decode(it!!) },
                signedPreKey = Base64.decode(privatePart.signedPreKey!!)
            )

            val remoteUser = EncryptedRemoteUser(
                registrationId = publicPart.registrationId ?: 0,
                name = publicPart.name ?: "",
                deviceId = publicPart.deviceId ?: 0,
                preKeyId = publicPart.preKeyId ?: 0,
                preKeyPublicKey = Base64.decode(publicPart.preKeyPublicKey ?: ""),
                signedPreKeyId = publicPart.signedPreKeyId ?: 0,
                signedPreKeyPublicKey = Base64.decode(publicPart.signedPreKeyPublicKey ?: ""),
                signedPreKeySignature = Base64.decode(publicPart.signedPreKeySignature ?: ""),
                identityKeyPairPublicKey = Base64.decode(publicPart.identityKeyPairPublicKey ?: "")
            )

            val session = EncryptedSingleSession(
                localUser,
                remoteUser
            )
            sessions[sessionId] = session
        }

        actual fun encrypt(sessionId: String, message: String): String {
            val session = sessions[sessionId]
            return session?.encrypt(message) ?: ""
        }

        actual fun decrypt(sessionId: String, message: String): String {
            val session = sessions[sessionId]
            return session?.decrypt(message) ?: ""
        }
    }
}