package com.lab.tb.distributed.model

import com.lab.tb.distributed.base.UUID

data class NearbyUser(
    val peripheralIdentifier: String,
    val peripheralName: String?,
    val chatUser: ChatUser?,
    val rssi: Int? = null,
    var isVisible: Boolean = false,

    var id: String = peripheralIdentifier,
    var displayName: String? = if (chatUser?.displayName?.isEmpty() == true) peripheralName else peripheralIdentifier.substring(0, 5),
)
