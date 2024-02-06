package com.lab.tb.distributed.helper

import com.lab.tb.distributed.entities.ApplicationContext
import com.lab.tb.distributed.model.AttachmentType

actual class AttachmentFileHelper {
    actual companion object {
        actual fun decodeToFile(
            type: AttachmentType,
            base64: String,
            fileName: String,
            context: ApplicationContext
        ): Boolean {
            TODO("Not yet implemented")
        }

        actual fun encodeToBase64(
            type: AttachmentType,
            fileName: String,
            context: ApplicationContext
        ): String {
            TODO("Not yet implemented")
        }
    }

}