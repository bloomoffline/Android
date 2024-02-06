package com.lab.tb.distributed.chat.android.data.message

import android.content.Context
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import com.lab.tb.distributed.ChatController
import com.lab.tb.distributed.base.UUID
import com.lab.tb.distributed.chat.android.manager.RecordManager
import com.lab.tb.distributed.chat.android.model.DraftAttachment
import com.lab.tb.distributed.chat.android.presentation.screen.chat.MessageAttachmentUiState
import com.lab.tb.distributed.model.AttachmentType
import com.lab.tb.distributed.model.ChatAttachment
import com.lab.tb.distributed.model.ChatMessage
import com.lab.tb.distributed.model.Content
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import java.util.Base64

class MessageRepositoryImpl(
    private val recordManager: RecordManager,
    private val dispatcher: CoroutineDispatcher,
): MessageRepository {
    private val draftAttachmentListStateFlow = MutableStateFlow(emptyList<DraftAttachment>())
    private val messageListStateFlow = MutableStateFlow(emptyList<ChatMessage>())
    private var replyingToMessageId : String? = null

    override fun getMessageList(messageId: String): Flow<List<ChatMessage>> {
        return ChatController.getInstance().roomMessage.map {
            it[messageId]?.message?.addedChatMessages ?: emptyList()
        }
    }

    override fun sendMessage(messageId: String, message: String, draftAttachmentList: List<DraftAttachment>) {
        val attachmentList = draftAttachmentList.mapNotNull { draft ->
            when (draft) {
                is DraftAttachment.Contact -> {
                    val newId = UUID.randomUUID()
                    val vcfContext = """
                        BEGIN:VCARD
                        VERSION:3.0
                        N:${draft.name}
                        FN:${draft.name}
                        TEL;type=CELL;type=VOICE;type=pref:${draft.phoneNumber}
                        EMAIL:${draft.email}
                        END:VCARD
                    """.trimIndent()
                    recordManager.createVcfFile("${draft.name}.vcf","contact/$newId", vcfContext)
                    ChatAttachment(
                        id = UUID.randomUUID(),
                        compression = 0,
                        content = Content(
                            type = "data",
                            data = Base64.getEncoder().encodeToString(vcfContext.toByteArray())
                        ),
                        type = AttachmentType.Contact.name,
                        name = "${draft.name}.vcf"
                    )
                }

                is DraftAttachment.VoiceNote -> {
                    val newId = UUID.randomUUID()
                    val bytes = draft.file?.readBytes()
                    recordManager.createNewRecordFile( "voiceNote.m4a","audio/$newId", bytes)

                    val encodedData = Base64.getEncoder().encodeToString(bytes)
                    ChatAttachment(
                        id = newId,
                        compression = 0,
                        content = Content(
                            type = "data",
                            data = encodedData
                        ),
                        type = AttachmentType.VoiceNote.name,
                        name = "voiceNote.m4a"
                    )
                }

                else -> {
                    null
                }
            }
        }

        ChatController.getInstance().sendMessage(messageId, message, replyingToMessageId, ArrayList(attachmentList))
        clearDraftAttachmentList()
    }

    override fun getDraftAttachmentList(): Flow<List<DraftAttachment>> {
        return draftAttachmentListStateFlow.asStateFlow()
    }

    override fun addDraftAttachment(draftAttachment: DraftAttachment) {
        draftAttachmentListStateFlow.update { it + draftAttachment }
    }

    override fun clearDraftAttachmentList() {
        draftAttachmentListStateFlow.update { emptyList() }
    }

    override fun saveReplyingToMessageId(messageId: String?) {
        replyingToMessageId = messageId
    }

    override fun deleteMessage(id: String, messageId: String) {
        ChatController.getInstance().deleteMessage(id, messageId)
    }

    override fun markAsUnread(messageId: String) {

    }

    override fun startRecording() {
        recordManager.startRecording()
    }

    override fun stopRecording() {
        recordManager.stopRecording()
        val recordedFile = recordManager.getRecordedFile()
        recordedFile?.let { nonNullRecordedFile ->
            addDraftAttachment(DraftAttachment.VoiceNote(nonNullRecordedFile))
        }
    }

    override fun startPlayingAudio(attachmentId: String, completion: () -> Unit) {
        recordManager.startPlayingAudio(attachmentId, completion)
    }

    override fun stopPlayingAudio(attachmentId: String) {
        recordManager.stopPlayingAudio()
    }

    override suspend fun loadAudioAmplitudes(attachmentId: String): List<Int> = withContext(dispatcher) {
        return@withContext recordManager.getAmplitudes(attachmentId)
    }
}