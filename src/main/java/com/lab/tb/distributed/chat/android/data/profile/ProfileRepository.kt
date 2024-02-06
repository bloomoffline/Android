package com.lab.tb.distributed.chat.android.data.profile

import com.lab.tb.distributed.chat.android.model.BloomSettingType
import com.lab.tb.distributed.model.ChatStatus
import com.lab.tb.distributed.model.Profile
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun getUserProfile() : Flow<Profile>
    fun getBloomSettings(): Flow<Map<BloomSettingType, Int>>
    fun updateBloomSettings(type: BloomSettingType, value: Int)
    fun updateUsername(name: String)
    fun updateUserBio(content: String)
    fun updateActivityStatus(chatStatus: ChatStatus)
}