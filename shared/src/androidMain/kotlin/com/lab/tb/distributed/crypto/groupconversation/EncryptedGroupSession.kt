package com.lab.tb.distributed.crypto.groupconversation

import android.util.Pair
import com.lab.tb.distributed.crypto.entity.EncryptedLocalUser
import com.lab.tb.distributed.crypto.helper.Helper
import com.lab.tb.distributed.crypto.registration.RegistrationManager
import org.whispersystems.libsignal.DuplicateMessageException
import org.whispersystems.libsignal.InvalidMessageException
import org.whispersystems.libsignal.LegacyMessageException
import org.whispersystems.libsignal.NoSessionException
import org.whispersystems.libsignal.SignalProtocolAddress
import org.whispersystems.libsignal.groups.GroupCipher
import org.whispersystems.libsignal.groups.GroupSessionBuilder
import org.whispersystems.libsignal.groups.state.SenderKeyRecord
import org.whispersystems.libsignal.protocol.SenderKeyDistributionMessage
import org.whispersystems.libsignal.state.impl.InMemorySignalProtocolStore
import java.nio.charset.StandardCharsets
import java.util.UUID

//class EncryptedGroupSession(
//    private val localUser: EncryptedLocalUser,
//    keyRecord: String?,
//    groupName: String?
//) {
//    private val distributionId: UUID
//    private val encryptGroupCipher: GroupCipher
//    private val protocolStore: InMemorySignalProtocolStore
//    private val decryptGroupCiphers: MutableMap<String, GroupCipher>
//    private val senderKeyDistributionStore: MutableMap<String, String>
//    private val sessionBuilder: GroupSessionBuilder
//
//    init {
//        distributionId = UUID.fromString(groupName)
//        decryptGroupCiphers = HashMap()
//        protocolStore =
//            InMemorySignalProtocolStore(localUser.identityKeyPair, localUser.registrationId)
//        sessionBuilder = GroupSessionBuilder(protocolStore)
//        protocolStore.storeSignedPreKey(localUser.signedPreKey.id, localUser.signedPreKey)
//        for (record in localUser.preKeys) {
//            protocolStore.storePreKey(record.id, record)
//        }
//        if (keyRecord != null) protocolStore.storeSenderKey(
//            localUser.signalProtocolAddress, distributionId, SenderKeyRecord(
//                Helper.decodeToByteArray(keyRecord)
//            )
//        )
//        encryptGroupCipher = GroupCipher(protocolStore, localUser.signalProtocolAddress)
//        senderKeyDistributionStore = HashMap()
//    }
//
//    val keyRecord: String
//        get() {
//            val keyRecord =
//                protocolStore.loadSenderKey(localUser.signalProtocolAddress, distributionId)
//            return Helper.encodeToBase64(keyRecord.serialize())
//        }
//
//    @Throws(InvalidMessageException::class, LegacyMessageException::class)
//    private fun addDecryptGroupCiphers(keys: List<SenderKeyDistributionModel>) {
//        for (item in keys) {
//            val serializedSenderDistributionKey =
//                Helper.decodeToByteArray(item.senderKeyDistributionMessage)
//            val senderKeyDistributionMessage =
//                SenderKeyDistributionMessage(serializedSenderDistributionKey)
//            val senderAddress =
//                SignalProtocolAddress(item.id, RegistrationManager.DEFAULT_DEVICE_ID)
//            if (!decryptGroupCiphers.containsKey(item.id) || senderKeyDistributionStore[item.id] != item.senderKeyDistributionMessage) {
//                senderKeyDistributionStore.remove(item.id)
//                decryptGroupCiphers.remove(item.id)
//
//                //Start the session for reading group originator
//                sessionBuilder.process(senderAddress, senderKeyDistributionMessage)
//
//                //Save the decryption group cipher for that contact
//                senderKeyDistributionStore[item.id] = item.senderKeyDistributionMessage
//                decryptGroupCiphers[item.id] =
//                    GroupCipher(protocolStore, senderAddress)
//            }
//        }
//    }
//
//    @Throws(LegacyMessageException::class, InvalidMessageException::class)
//    fun createSession(list: List<SenderKeyDistributionModel>) {
//        addDecryptGroupCiphers(list)
//    }
//
//    @Throws(NoSessionException::class)
//    fun encryptMessage(message: String): String {
//        val encryptedMessage = encryptGroupCipher.encrypt(
//            distributionId, message.toByteArray(
//                StandardCharsets.UTF_8
//            )
//        )
//        return Helper.encodeToBase64(encryptedMessage.serialize())
//    }
//
//    @Throws(
//        NoSessionException::class,
//        InvalidMessageException::class,
//        DuplicateMessageException::class,
//        LegacyMessageException::class
//    )
//    fun decryptMessage(encryptedMessage: String?, userId: String): String {
//        val decryptGroupCipher = decryptGroupCiphers[userId]
//        val text = decryptGroupCipher?.decrypt(Helper.decodeToByteArray(encryptedMessage))
//        return String(text!!, StandardCharsets.UTF_8)
//    }
//
//    @Throws(
//        InvalidMessageException::class,
//        LegacyMessageException::class,
//        NoSessionException::class,
//        DuplicateMessageException::class
//    )
//    fun decryptMessage(encryptedMessage: String?, userId: String, senderKey: String?): String {
//        val mList: MutableList<SenderKeyDistributionModel> = ArrayList()
//        mList.add(SenderKeyDistributionModel(userId, senderKey!!))
//        addDecryptGroupCiphers(mList)
//        return decryptMessage(encryptedMessage, userId)
//    }
//
//    companion object {
//        fun createGroup(localUser: EncryptedLocalUser, uuid: String?): Pair<String, String> {
//            return createDistributionKey(localUser, uuid)
//        }
//
//        fun createDistributionKey(
//            localUser: EncryptedLocalUser,
//            uuid: String?
//        ): Pair<String, String> {
//            val address = localUser.signalProtocolAddress
//            val store =
//                InMemorySignalProtocolStore(localUser.identityKeyPair, localUser.registrationId)
//            store.storeSignedPreKey(localUser.signedPreKey.id, localUser.signedPreKey)
//            for (record in localUser.preKeys) {
//                store.storePreKey(record.id, record)
//            }
//            store.storeSenderKey(
//                localUser.signalProtocolAddress,
//                UUID.fromString(uuid),
//                SenderKeyRecord()
//            )
//            val sessionBuilder = GroupSessionBuilder(store)
//            val senderDistributionMessage = sessionBuilder.create(address, UUID.fromString(uuid))
//            val senderKeyRecord =
//                store.loadSenderKey(localUser.signalProtocolAddress, UUID.fromString(uuid))
//            return Pair(
//                Helper.encodeToBase64(senderDistributionMessage.serialize()),
//                Helper.encodeToBase64(senderKeyRecord.serialize())
//            )
//        }
//    }
//}