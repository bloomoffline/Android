package com.lab.tb.distributed

interface ChatTransport {
    fun broadcast(raw: String)
    fun onReceive(handler: (String) -> Unit)
}