package com.lab.tb.distributed.chat.android.presentation.screen.chat

import com.lab.tb.distributed.chat.android.model.DraftAttachment
import com.lab.tb.distributed.model.ChatUser

sealed interface ChatUiState {
    data object Empty : ChatUiState
    data class Success(
        val roomName: String,
        val chatMessageBubbleList: List<MessageBubbleUiState>,
        val draftAttachmentList: List<DraftAttachment>,
        val chatUser: ChatUser
    ) : ChatUiState
}

data class MessageBubbleUiState(
    val id: String,
    val authorName: String,
    val authorId: String,
    val content: String,
    val messageContentList: List<MessageAttachmentUiState>,
    val isMe: Boolean,
)

sealed class MessageAttachmentUiState(open val attachmentId: String) {
    data class File(override val attachmentId: String, val url: String) : MessageAttachmentUiState(attachmentId)
    data class Image(override val attachmentId: String, val url: String) : MessageAttachmentUiState(attachmentId)
    data class VoiceNote(override val attachmentId: String, val amplitudeList: List<Int>) : MessageAttachmentUiState(attachmentId)
    data class Contact(override val attachmentId: String, val fileName: String) : MessageAttachmentUiState(attachmentId)
}