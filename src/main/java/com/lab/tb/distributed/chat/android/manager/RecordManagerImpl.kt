package com.lab.tb.distributed.chat.android.manager

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import linc.com.amplituda.Amplituda
import linc.com.amplituda.Cache
import linc.com.amplituda.callback.AmplitudaErrorListener
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter

class RecordManagerImpl(
    private val context: Context
): RecordManager {

    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null
    private var amplituda: Amplituda = Amplituda(context)

    private var recordedFile: File? = null

    private fun createRecorder(): MediaRecorder {
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else MediaRecorder()
    }

    override fun startRecording() {
        val file = File(context.cacheDir, "voiceNote.m4a")

        createRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(FileOutputStream(file).fd)

            prepare()
            start()

            recorder = this
        }
        recordedFile = file
    }

    override fun stopRecording() {
        try {
            recorder?.stop()
        } catch (exception: Exception) {
          exception.printStackTrace()
        }
        recorder?.reset()
        recorder = null
    }

    override fun startPlayingAudio(attachmentId: String, completion: () -> Unit) {
        try {
            val audioFile = File(context.filesDir, "audio/${attachmentId}/voiceNote.m4a")
            MediaPlayer.create(context, audioFile.toUri()).apply {
                player = this
                start()
                setOnCompletionListener {
                    completion.invoke()
                }
            }
        } catch (ex: Exception) {
            println(ex.stackTrace)
        }
    }

    override suspend fun getAmplitudes(attachmentId: String): List<Int> = withContext(Dispatchers.IO) {
        return@withContext amplituda.processAudio("${context.filesDir}/audio/${attachmentId}/voiceNote.m4a", Cache.withParams(Cache.REFRESH))
            ?.get(AmplitudaErrorListener {
                it.printStackTrace()
            })
            ?.amplitudesAsList() ?: listOf()
    }

    override fun stopPlayingAudio() {
        player?.stop()
        player?.release()
        player = null
    }

    override fun createNewRecordFile(fileName: String ,filePath: String, byteArray: ByteArray?) {
        val dir = File(context.filesDir.path + "/$filePath")
        if (!dir.exists()) {
            dir.mkdirs()
        }

        val outputFile = File(dir.absolutePath, fileName)
        val fileOutputStream = FileOutputStream(outputFile)

        fileOutputStream.write(byteArray)
        fileOutputStream.close()
    }

    override fun createVcfFile(fileName: String, filePath: String, vcfContent: String) {
        val dir = File(context.filesDir.path + "/$filePath")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val vcfFile = File(dir, fileName)
        try {
            val writer = FileWriter(vcfFile)
            writer.append(vcfContent)
            writer.flush()
            writer.close()
        } catch (e: Exception) {
            print(e.stackTrace)
        }
    }

    override fun getRecordedFile(): File? = recordedFile
}