package com.lab.tb.distributed.chat.android.presentation.screen.profile

import com.lab.tb.distributed.chat.android.model.BloomSettingType
import com.lab.tb.distributed.model.ChatStatus

sealed interface ProfileUiState {
    data object Empty : ProfileUiState
    data class Success(
        val userInformation: Pair<String, String>,
        val chatStatus: ChatStatus,
        val bloomSettingsMap: Map<BloomSettingType, Int>,
    ) : ProfileUiState
}