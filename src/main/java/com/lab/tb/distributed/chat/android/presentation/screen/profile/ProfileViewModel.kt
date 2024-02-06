package com.lab.tb.distributed.chat.android.presentation.screen.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lab.tb.distributed.chat.android.data.profile.ProfileRepository
import com.lab.tb.distributed.chat.android.model.BloomSettingType
import com.lab.tb.distributed.model.ChatStatus
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfileViewModel(private val profileRepository: ProfileRepository) : ViewModel() {
    val profileUiState: StateFlow<ProfileUiState> = combine(
        profileRepository.getBloomSettings(),
        profileRepository.getUserProfile()
    ) { bloomSettingsMap, userProfile ->
        val username = userProfile.me.name ?: "Your name"
        val bio = userProfile.presence.info
        val chatStatus = userProfile.presence.status
        ProfileUiState.Success(Pair(username, bio), chatStatus, bloomSettingsMap)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ProfileUiState.Empty
    )

    fun updateBloomSettings(type: BloomSettingType, value: Int) = viewModelScope.launch {
        profileRepository.updateBloomSettings(type, value)
    }

    fun updateName(name: String) = viewModelScope.launch {
        profileRepository.updateUsername(name)
    }

    fun updateActivityStatus(chatStatus: ChatStatus) = viewModelScope.launch {
        profileRepository.updateActivityStatus(chatStatus)
    }

    fun updateUserBio(content: String) = viewModelScope.launch {
        profileRepository.updateUserBio(content)
    }
}