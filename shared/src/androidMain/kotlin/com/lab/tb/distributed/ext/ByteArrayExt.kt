

package com.lab.tb.distributed.ext

object ByteArrayExt {

    fun ByteArray.toUtf8String() = this.toString(Charsets.UTF_8)

    @OptIn(ExperimentalUnsignedTypes::class)
    fun ByteArray.toHexString() = asUByteArray().joinToString(separator = "") { it.toString(radix = 16).padStart(2, '0') }
}