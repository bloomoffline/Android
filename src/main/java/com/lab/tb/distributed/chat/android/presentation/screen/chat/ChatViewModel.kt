package com.lab.tb.distributed.chat.android.presentation.screen.chat

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lab.tb.distributed.chat.android.data.message.MessageRepository
import com.lab.tb.distributed.chat.android.data.profile.ProfileRepository
import com.lab.tb.distributed.chat.android.model.DraftAttachment
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ChatViewModel(
    private val messageRepository: MessageRepository,
    profileRepository: ProfileRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val chatArgs = ChatArgs(savedStateHandle)

    val chatUiState: StateFlow<ChatUiState> = combine(
        messageRepository.getMessageList(chatArgs.messageId),
        messageRepository.getDraftAttachmentList(),
        profileRepository.getUserProfile()
    ) { messageList, draftAttachmentList ,userProfile ->
        val chatMessageBubbleList = messageList.map { message ->
            val messageId = message.id ?: ""
            val authorName = message.author?.displayName ?: ""
            val authorId = message.author?.id ?: ""
            val content = message.content?.data ?: ""
            val isMe = message.author?.id == userProfile.me.id
            val attachmentList = message.attachments?.map { chatAttachment ->
                when (chatAttachment.type) {
                    "VoiceNote" -> {
                        val attachmentId = chatAttachment.id
                        attachmentId?.let { nonNullAttachmentId ->
                            val audioAmplitudes = messageRepository.loadAudioAmplitudes(nonNullAttachmentId)
                            MessageAttachmentUiState.VoiceNote(attachmentId, audioAmplitudes)
                        } ?: MessageAttachmentUiState.File("", "")
                    }

                    "Contact" -> {
                        val name = chatAttachment.name ?: ""
                        val attachmentId = chatAttachment.id
                        attachmentId?.let { nonNullAttachmentId ->
                            MessageAttachmentUiState.Contact(nonNullAttachmentId, name)
                        } ?: MessageAttachmentUiState.File("", "")
                    }

                    else -> { MessageAttachmentUiState.File("","")}
                }
            } ?: emptyList()
            MessageBubbleUiState(
                id = messageId,
                authorName = authorName,
                authorId = authorId,
                content = content,
                messageContentList = attachmentList,
                isMe = isMe
            )
        }
        ChatUiState.Success(chatArgs.roomName, chatMessageBubbleList, draftAttachmentList, userProfile.me)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ChatUiState.Empty
    )

    fun sendMessage(message: String, context: Context, draftAttachmentList: List<DraftAttachment>) =
        viewModelScope.launch {
            messageRepository.sendMessage(chatArgs.messageId, message, draftAttachmentList)
        }

    fun deleteMessage(messageId: String) = viewModelScope.launch {
        messageRepository.deleteMessage(chatArgs.messageId, messageId)
    }

    fun String.decodeHex(): ByteArray {
        check(length % 2 == 0) { "Must have an even length" }

        val byteIterator = chunkedSequence(2)
            .map { it.toInt(16).toByte() }
            .iterator()

        return ByteArray(length / 2) { byteIterator.next() }
    }

    fun saveReplyingToMessageId(messageId: String? = null) = viewModelScope.launch {
        messageRepository.saveReplyingToMessageId(messageId)
    }

    fun markAsUnread(messageId: String) = viewModelScope.launch {
        messageRepository.markAsUnread(messageId)
    }

    fun addDraftAttachment(draftAttachment: DraftAttachment) = viewModelScope.launch {
        messageRepository.addDraftAttachment(draftAttachment)
    }

    fun clearDraftAttachmentList() = viewModelScope.launch {
        messageRepository.clearDraftAttachmentList()
    }

    fun startRecording() = viewModelScope.launch {
        messageRepository.startRecording()
    }

    fun stopRecording() = viewModelScope.launch {
        messageRepository.stopRecording()
    }

    fun startAudio(attachmentId: String, completion: () -> Unit) = viewModelScope.launch {
        messageRepository.startPlayingAudio(attachmentId, completion)
    }

    fun stopAudio(attachmentId: String) = viewModelScope.launch {
        messageRepository.stopPlayingAudio(attachmentId)
    }
}