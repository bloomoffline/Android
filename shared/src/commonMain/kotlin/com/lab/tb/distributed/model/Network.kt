package com.lab.tb.distributed.model

import com.lab.tb.distributed.base.UUID

class Network(
    /// The ID of our node.
    val myId: String = UUID.randomUUID(),
    val presences: List<ChatPresence> = emptyList(),
    val nearbyUsers: List<NearbyUser> = emptyList(),
    val message: Message? = null,
) {
//    var offlinePresences: [UUID: ChatPresence] {
//        Dictionary(uniqueKeysWithValues: messages.users
//                .filter { !presences.keys.contains($0.id) }
//            .map { ($0.id, ChatPresence(user: $0, status: .offline)) })
//    }

//    var offlinePresences: Map<String, ChatPresence>
//        get() = message?.users?.filter { !presencesMap.keys.contains(it.id) }?.associateBy { it.id } ?: emptyMap()

    var presencesMap: MutableMap<String, ChatPresence> = mutableMapOf()
        private set

    init {
        presencesMap = presences.associateBy { it.user.id }.toMutableMap()
    }

    fun register(presence: ChatPresence) {
        presencesMap[presence.id] = presence
    }
}