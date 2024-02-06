package com.lab.tb.distributed.helper

import com.lab.tb.distributed.entities.ApplicationContext
import com.lab.tb.distributed.model.AttachmentType
import java.io.File
import java.io.FileOutputStream
import java.util.Base64


actual class AttachmentFileHelper {
    actual companion object {
        actual fun decodeToFile(chatMessageId: String, type: AttachmentType, base64: String, fileName: String, context: ApplicationContext): Boolean {
            try {
                val folder = when (type) {
                    AttachmentType.File -> "file"
                    AttachmentType.Image -> "image"
                    AttachmentType.Contact -> "contact"
                    AttachmentType.VoiceNote -> "audio"
                }
                val dir = File(context.filesDir.path + "/$folder/$chatMessageId")
                if (!dir.exists()) {
                    dir.mkdirs()
                }

                val bytes = Base64.getDecoder().decode(base64)
                val outputFile = File(dir.absolutePath, fileName)
                val fileOutputStream = FileOutputStream(outputFile)

                fileOutputStream.write(bytes)
                fileOutputStream.close()
            } catch (ex:Exception) {
                println("Error occurred: ${ex.stackTraceToString()}")
                return false
            }

            return true
        }

        actual fun encodeToBase64(type: AttachmentType, fileName: String, context: ApplicationContext): String {
            val folder = when (type) {
                AttachmentType.File -> "file"
                AttachmentType.Image -> "image"
                AttachmentType.Contact -> "contact"
                AttachmentType.VoiceNote -> "audio"
            }
            val dir = File(context.filesDir.path + "/$folder")
            if (!dir.exists()){
                dir.mkdir();
            }

            val file = File(dir.absolutePath, fileName)
            val bytes = file.readBytes()
            return Base64.getEncoder().encodeToString(bytes)
        }
    }


}