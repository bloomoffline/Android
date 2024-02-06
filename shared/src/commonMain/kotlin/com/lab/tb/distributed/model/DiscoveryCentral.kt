package com.lab.tb.distributed.model

class DiscoveryCentral {
    private val nl = '\n'.code.toByte()

    var incomingData: MutableList<Byte> = mutableListOf()

    fun dequeueLine() : ByteArray? {
        val lineData = mutableListOf<Byte>()
        incomingData.forEach { b ->
            if (b == nl) {
                incomingData.subList(0, lineData.size + 1).clear()
                return lineData.toByteArray()
            } else {
                lineData.add(b)
            }
        }
        return null
    }

}
