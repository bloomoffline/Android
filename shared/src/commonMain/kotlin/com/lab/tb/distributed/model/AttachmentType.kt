package com.lab.tb.distributed.model

enum class AttachmentType(value: String) {
    File("File"),
    Image("Image"),
    Contact("Contact"),
    VoiceNote("VoiceNote");

    companion object {
        fun get(value: String?): AttachmentType {
            return when (value) {
                "File" -> File
                "Image" -> Image
                "Contact" -> Contact
                "VoiceNote" -> VoiceNote
                else -> throw Exception("No AttachmentType found for $value")
            }
        }
    }
}