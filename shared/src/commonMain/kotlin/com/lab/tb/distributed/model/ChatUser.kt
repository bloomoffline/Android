package com.lab.tb.distributed.model

import com.lab.tb.distributed.base.UUID
import com.lab.tb.distributed.entities.BaseEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient


@Serializable
data class ChatUser(
    @SerialName("id")
    override var id: String = UUID.randomUUID(),
    @SerialName("publicKey")
    var publicKey: String? = null, // todo:
    @SerialName("name")
    var name: String? = "",
    @SerialName("logicalClock")
    var logicalClock: Int = 0,
    @Transient
    var displayName: String? = if (name.isNullOrEmpty()) "User ${id.substring(0, 5)}" else name,
//    @Transient
//    var publicPart: PublicPart? = null,
//    @Transient
//    var privatePart: PrivatePart? = null
): BaseEntity() {
    override fun toString(): String {
        return "ChatUser(id='$id', publicKey='$publicKey', name='$name', logicalClock=$logicalClock, displayName=$displayName)"
    }
}

@Serializable
data class PublicPart(
    @Transient
    override var id: String = "ID",
    @SerialName("registrationId")
    val registrationId: Int? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("deviceId")
    val deviceId: Int? = null ,
    @SerialName("preKeyId")
    val preKeyId: Int? = null,
    @SerialName("preKeyPublicKey")
    val preKeyPublicKey: String? = null, // encode to ByteArray,
    @SerialName("signedPreKeyId")
    val signedPreKeyId: Int? = null,
    @SerialName("signedPreKeyPublicKey")
    val signedPreKeyPublicKey: String? = null, // encode to ByteArray,
    @SerialName("signedPreKeySignature")
    val signedPreKeySignature: String? = null, // encode to ByteArray,
    @SerialName("identityKeyPairPublicKey")
    val identityKeyPairPublicKey: String? = null // encode to ByteArray
): BaseEntity()

@Serializable
data class PrivatePart(
    @SerialName("id")
    override var id: String = "ID",
    @SerialName("identityKeyPair")
    var identityKeyPair: String?,
    @SerialName("registrationId")
    var registrationId: Int,
    @SerialName("name")
    var name: String?,
    @SerialName("deviceId")
    var deviceId: Int,
    @SerialName("preKeys")
    var preKeys: List<String?>,
    @SerialName("signedPreKey")
    var signedPreKey: String?
):BaseEntity()