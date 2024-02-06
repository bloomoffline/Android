package com.lab.tb.distributed.crypto.entity

import org.whispersystems.libsignal.SignalProtocolAddress


abstract class BaseEncryptedEntity protected constructor(
    val registrationId: Int,
    val signalProtocolAddress: SignalProtocolAddress
)