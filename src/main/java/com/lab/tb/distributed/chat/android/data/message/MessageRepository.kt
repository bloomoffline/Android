package com.lab.tb.distributed.chat.android.data.message

import com.lab.tb.distributed.chat.android.model.DraftAttachment
import com.lab.tb.distributed.model.ChatMessage
import kotlinx.coroutines.flow.Flow

interface MessageRepository {
    fun getMessageList(messageId: String): Flow<List<ChatMessage>>
    fun sendMessage(messageId: String, message: String, draftAttachmentList: List<DraftAttachment>)
    fun getDraftAttachmentList(): Flow<List<DraftAttachment>>
    fun addDraftAttachment(draftAttachment: DraftAttachment)
    fun clearDraftAttachmentList()
    fun saveReplyingToMessageId(messageId: String?)
    fun deleteMessage(id: String, messageId: String)
    fun markAsUnread(messageId: String)
    fun startRecording()
    fun stopRecording()
    fun startPlayingAudio(attachmentId: String, completion: () -> Unit)
    fun stopPlayingAudio(attachmentId: String)
    suspend fun loadAudioAmplitudes(attachmentId: String): List<Int>
}