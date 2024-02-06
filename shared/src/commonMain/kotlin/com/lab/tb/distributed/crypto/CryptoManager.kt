package com.lab.tb.distributed.crypto

import com.lab.tb.distributed.model.PrivatePart
import com.lab.tb.distributed.model.PublicPart

expect class CryptoManager {
    companion object {
        fun generatePrivatePart(id: String): PrivatePart?

        fun generatePublicPart(id: String): PublicPart?
        fun decrypt(sessionId: String, message: String): String
        fun encrypt(sessionId: String, message: String): String
        fun initSession(
            sessionId: String,
            privatePart: PrivatePart,
            publicPart: PublicPart
        )
    }

}