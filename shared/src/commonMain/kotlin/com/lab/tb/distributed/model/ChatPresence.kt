package com.lab.tb.distributed.model

import com.lab.tb.distributed.entities.BaseEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatPresence(
    @SerialName("user")
    var user: ChatUser,
    @SerialName("status")
    var status: ChatStatus = ChatStatus.Online,
    @SerialName("info")
    var info: String = "",
    @SerialName("id")
    override var id: String = user.id,
): BaseEntity() {
    override fun toString(): String {
        return "ChatPresence(user=$user, status=$status, info='$info', id='$id')"
    }
}