package com.lab.tb.distributed.base

expect class UUID {
    companion object {
        fun randomUUID() : String
    }
}