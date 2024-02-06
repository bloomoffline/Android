package com.lab.tb.distributed.chat.android.navigation

import com.lab.tb.distributed.chat.android.R

/**
 * Type for the top level destinations in the application. Each of these destinations
 * can contain one or more screens (based on the window size). Navigation from one screen to the
 * next within a single destination will be handled directly in composables.
 */
enum class TopLevelDestination(
    val iconId: Int,
    val textId: Int,
) {
    NETWORK(
        iconId = R.drawable.ic_network,
        textId = R.string.network,
    ),
    MESSAGES(
        iconId = R.drawable.ic_messages,
        textId = R.string.messages,
    ),
    PROFILE(
        iconId = R.drawable.ic_profile,
        textId = R.string.profile,
    )
}