package com.lab.tb.distributed.chat.android.data.profile

import com.lab.tb.distributed.BloomService
import com.lab.tb.distributed.ChatController
import com.lab.tb.distributed.chat.android.model.BloomSettingType
import com.lab.tb.distributed.model.BleSettings
import com.lab.tb.distributed.model.ChatStatus
import com.lab.tb.distributed.model.Profile
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileRepositoryImpl(dispatcher: CoroutineDispatcher): ProfileRepository {
    private val bloomSettingsMapStateFlow = MutableStateFlow(emptyMap<BloomSettingType, Int>())
    private val userProfileStateFlow = MutableStateFlow(Profile())

    override fun getUserProfile(): Flow<Profile> {
        val profile = ChatController.getInstance().profile
        userProfileStateFlow.update { profile }
        return userProfileStateFlow.asStateFlow()
    }

    override fun getBloomSettings(): Flow<Map<BloomSettingType, Int>> {
        val settings = ChatController.getInstance().database.get<BleSettings>("ID") ?: BleSettings()
        bloomSettingsMapStateFlow.update {
            return@update mutableMapOf<BloomSettingType, Int>(
                BloomSettingType.NOTIFICATIONS to settings.enableNotification.toInt(),
                BloomSettingType.BE_DISCOVERABLE_BY_NEARBY_USERS to settings.advertisingEnabled.toInt(),
                BloomSettingType.SCAN_TO_DISCOVER_NEARBY_USERS to settings.scanningEnabled.toInt(),
                BloomSettingType.SHOW_ROOM_AND_MESSAGES_PREVIEWS to settings.showRoomAndMessagesPreviews.toInt(),
                BloomSettingType.BLOOM_MONITORING_INTERVAL to settings.monitorSignalStrengthInterval
            )
        }
        return bloomSettingsMapStateFlow.asStateFlow()
    }

    override fun updateBloomSettings(type: BloomSettingType, value: Int) {
        val settings = ChatController.getInstance().database.get<BleSettings>("ID") ?: BleSettings()
        when (type) {
            BloomSettingType.NOTIFICATIONS -> {
                settings.enableNotification = value == 1
            }
            BloomSettingType.BE_DISCOVERABLE_BY_NEARBY_USERS -> {
                settings.advertisingEnabled = value == 1
                if (value == 1) {
                    CoroutineScope(Dispatchers.Main).launch {
                        BloomService.getInstance().startAdvertising()
                    }
                } else {
                    CoroutineScope(Dispatchers.Main).launch {
                        BloomService.getInstance().stopAdvertising()
                    }
                }
            }
            BloomSettingType.SCAN_TO_DISCOVER_NEARBY_USERS -> {
                settings.scanningEnabled = value == 1
                if (value == 1) {
                    CoroutineScope(Dispatchers.Main).launch {
                        BloomService.getInstance().startScanning()
                    }
                } else {
                    CoroutineScope(Dispatchers.Main).launch {
                        BloomService.getInstance().stopScanning()
                    }
                }
            }
            BloomSettingType.SHOW_ROOM_AND_MESSAGES_PREVIEWS -> {
                settings.showRoomAndMessagesPreviews = value == 1
            }

            BloomSettingType.BLOOM_MONITORING_INTERVAL -> {
                settings.monitorSignalStrengthInterval = value
            }
        }
        val isUpdateSuccess = ChatController.getInstance().database.set(settings)
        if (isUpdateSuccess) {
            bloomSettingsMapStateFlow.update {
                return@update mutableMapOf<BloomSettingType, Int>(
                    BloomSettingType.NOTIFICATIONS to settings.enableNotification.toInt(),
                    BloomSettingType.BE_DISCOVERABLE_BY_NEARBY_USERS to settings.advertisingEnabled.toInt(),
                    BloomSettingType.SCAN_TO_DISCOVER_NEARBY_USERS to settings.scanningEnabled.toInt(),
                    BloomSettingType.SHOW_ROOM_AND_MESSAGES_PREVIEWS to settings.showRoomAndMessagesPreviews.toInt(),
                    BloomSettingType.BLOOM_MONITORING_INTERVAL to settings.monitorSignalStrengthInterval
                )
            }
        }
    }

    override fun updateUsername(name: String) {
        val profile = ChatController.getInstance().database.get<Profile>("ID") ?: return
        profile.me.name = name
        val isUpdateSuccess = ChatController.getInstance().database.set(profile)

        if (isUpdateSuccess) {
            userProfileStateFlow.update { profile }
        }
    }

    override fun updateUserBio(content: String) {
        val profile = ChatController.getInstance().database.get<Profile>("ID") ?: return
        profile.presence.info = content
        val isUpdateSuccess = ChatController.getInstance().database.set(profile)

        if (isUpdateSuccess) {
            userProfileStateFlow.update { profile }
        }
    }

    override fun updateActivityStatus(chatStatus: ChatStatus) {
        val profile = ChatController.getInstance().database.get<Profile>("ID") ?: return
        profile.presence.status = chatStatus
        val isUpdateSuccess = ChatController.getInstance().database.set(profile)

        if (isUpdateSuccess) {
            userProfileStateFlow.update { profile }
        }
    }

    private fun Boolean.toInt() = if (this) 1 else 0
}