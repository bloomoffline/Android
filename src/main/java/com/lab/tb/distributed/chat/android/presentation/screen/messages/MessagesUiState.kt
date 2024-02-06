package com.lab.tb.distributed.chat.android.presentation.screen.messages

import com.lab.tb.distributed.model.MessageView

sealed interface MessagesUiState {
    data object Empty : MessagesUiState
    data class Success(
        val messageList: List<MessageView>,
    ) : MessagesUiState
}