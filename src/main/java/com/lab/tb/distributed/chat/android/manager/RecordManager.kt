package com.lab.tb.distributed.chat.android.manager

import java.io.File

interface RecordManager {
    fun startRecording()
    fun stopRecording()
    fun startPlayingAudio(attachmentId: String, completion: () -> Unit)
    fun stopPlayingAudio()
    fun getRecordedFile(): File?
    suspend fun getAmplitudes(attachmentId: String): List<Int>
    fun createNewRecordFile(fileName: String ,filePath: String, byteArray: ByteArray?)
    fun createVcfFile(fileName: String ,filePath: String, vcfContent: String)
}