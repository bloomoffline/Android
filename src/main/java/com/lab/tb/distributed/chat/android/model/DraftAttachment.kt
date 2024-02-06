package com.lab.tb.distributed.chat.android.model

import android.net.Uri

sealed class DraftAttachment {
    data class File(val uri: Uri) : DraftAttachment()
    data class Image(val uri: Uri) : DraftAttachment()
    data class VoiceNote(val file: java.io.File?) : DraftAttachment()
    data class Contact(val name: String, val phoneNumber: String, val email: String) : DraftAttachment()
}