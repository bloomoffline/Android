
package com.lab.tb.distributed.helper

import com.lab.tb.distributed.entities.ApplicationContext
import com.lab.tb.distributed.model.AttachmentType

expect class AttachmentFileHelper() {
    companion object {
        fun decodeToFile(chatMessageId: String, type: AttachmentType, base64: String, fileName: String, context: ApplicationContext): Boolean

        fun encodeToBase64(type: AttachmentType, fileName: String, context: ApplicationContext): String
    }

}