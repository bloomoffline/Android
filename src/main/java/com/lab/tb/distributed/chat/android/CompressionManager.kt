/*
package com.lab.tb.distributed.chat.android

import java.nio.ByteBuffer

object CompressionManager {

    init {
        System.loadLibrary("mehdkmm")
    }

    fun decode(data: ByteArray): String {
         val decodeDstArray: ByteBuffer = ByteBuffer.allocateDirect(999)
         val decodeSrcArray: ByteBuffer = ByteBuffer.allocateDirect(999)
        decode(decodeSrcArray.put(data), decodeDstArray)
        val decodedArray = ByteArray(decodeDstArray.remaining())
        decodeDstArray.get(decodedArray, 0, decodedArray.size)
        return decodedArray.filter { it.toInt() != 0 }.toByteArray().toString(Charsets.UTF_8)
    }

    fun encode(data: String): ByteArray {
        val encodeDstArray: ByteBuffer = ByteBuffer.allocateDirect(999)
        val encodeSrcArray: ByteBuffer = ByteBuffer.allocateDirect(999)
        val dataByteArray = data.toByteArray()
        encode(encodeSrcArray.put(dataByteArray), encodeDstArray)
        val encodedArray = ByteArray(encodeDstArray.remaining())
        encodeDstArray.get(encodedArray, 0, encodedArray.size)
        return encodedArray
    }

    private external fun decode(srcArray: ByteBuffer, dstArray: ByteBuffer)


    private external fun encode(srcArray: ByteBuffer, dstArray: ByteBuffer)
}
*/
