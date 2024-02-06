package com.lab.tb.distributed.model

import com.lab.tb.distributed.base.UUID
import com.lab.tb.distributed.entities.BaseEntity
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Transient
import kotlin.js.JsExport

@Serializable
data class Message(
    @SerialName("id")
    override var id: String = UUID.randomUUID(),
    @SerialName("timestamp")
    var timestamp: Double? = null,
    @SerialName("sourceUserId")
    var sourceUserId: String? = null,
    @SerialName("destinationUserId")
    var destinationUserId: String? = null,
    @SerialName("addedChatMessages")
    var addedChatMessages: ArrayList<ChatMessage>? = null,
    @SerialName("updatedPresences")
    var updatedPresences: ArrayList<ChatPresence>? = null,
    // todo: implement
//    deletedChatMessages: [ChatDeletion]? = nil,
//    messageRequest: MessageRequest? = nil,
    @SerialName("logicalClock")
    var logicalClock: Int? = 0, // 22

    @Transient
    var isPin: Boolean? = false,
    @Transient
    var isNewNotification: Boolean? = false,
    @Transient
    var chatChannel: ChatChannelEnum? = null,
): BaseEntity() {

}

@Serializable
data class ChatMessage(
    @SerialName("id")
    var id: String?,
    @SerialName("timestamp")
    var timestamp: Double?, // 1702394258.2353492
    @SerialName("author")
    var author: ChatUser?,
    @SerialName("content")
    var content: ChatMessageContent?,
    @SerialName("signalEncrypted")
    var signalEncrypted: String? = null, // true
    @SerialName("channel")
    var channelValue: ChatChannelSerialized?,
    @SerialName("attachments")
    var attachments: ArrayList<ChatAttachment>?,
    @SerialName("repliedToMessageId")
    var repliedToMessageId: String?
)

@Serializable
data class ChatAttachment(
    @SerialName("compression")
    var compression: Int?, // 0
    @SerialName("content")
    var content: Content?,
    @SerialName("id")
    var id: String?, // 6418EC24-EAE1-405D-A487-C263C98C3183
    @SerialName("name")
    var name: String?, // voiceNote.m4a
    @SerialName("type")
    var type: String? // VoiceNote
)

@Serializable
data class Content(
    @SerialName("data")
    var data: String?, //
    @SerialName("type")
    var type: String? // data
)

@Serializable
data class ChatMessageContent(
    @SerialName("data")
    var data: String?, //  Hello
    @SerialName("type")
    var type: String? // text
)

@Serializable
data class ChatChannelSerialized(
    @SerialName("type")
    var type: String?, // dm
    @SerialName("data")
    var data: String? // 0EE8E648-5D87-41C7-9B2B-FC9A7ABD0D88
)