
package com.lab.tb.distributed.model

import com.lab.tb.distributed.entities.BaseEntity
data class Profile(
    val presence: ChatPresence = ChatPresence(ChatUser()),
): BaseEntity() {
    var me = presence.user
}