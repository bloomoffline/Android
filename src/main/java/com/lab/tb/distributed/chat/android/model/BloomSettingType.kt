package com.lab.tb.distributed.chat.android.model

import com.lab.tb.distributed.chat.android.R

enum class BloomSettingType(
    val titleTextId: Int,
    val explanationTextId: Int? = null
) {
    NOTIFICATIONS(
        titleTextId = R.string.notifications,
    ),
    SHOW_ROOM_AND_MESSAGES_PREVIEWS(
        titleTextId = R.string.show_room_and_messages_previews,
    ),
    BE_DISCOVERABLE_BY_NEARBY_USERS(
        titleTextId = R.string.be_discoverable_by_nearby_users,
        explanationTextId = R.string.be_discoverable_by_nearby_users_explanation
    ),
    SCAN_TO_DISCOVER_NEARBY_USERS(
        titleTextId = R.string.scan_to_discover_nearby_users,
        explanationTextId = R.string.scan_to_discover_nearby_users_explanation
    ),
    BLOOM_MONITORING_INTERVAL(
        titleTextId = R.string.bloom_monitoring_interval,
        explanationTextId = R.string.bloom_monitoring_interval_explanation
    ),
}