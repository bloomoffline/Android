package com.lab.tb.distributed.chat.android.presentation.screen.messages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lab.tb.distributed.chat.android.data.channel.ChannelRepository
import com.lab.tb.distributed.chat.android.data.message.MessageRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MessagesViewModel(private val channelRepository: ChannelRepository,
) : ViewModel() {
    val messagesUiState: StateFlow<MessagesUiState> = channelRepository.getChannelList().map { channelList ->
        MessagesUiState.Success(channelList.sortedBy { !it.isPinned })
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MessagesUiState.Empty
    )

    fun deleteChannel(id: String) = viewModelScope.launch {
        channelRepository.deleteChannel(id)
    }

    fun setPinChannel(id: String, isPinned: Boolean) = viewModelScope.launch {
        channelRepository.setPinChannel(id, isPinned)
    }
}