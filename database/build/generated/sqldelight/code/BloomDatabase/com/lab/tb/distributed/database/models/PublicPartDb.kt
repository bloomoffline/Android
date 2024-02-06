package com.lab.tb.distributed.database.models

import kotlin.Long
import kotlin.String

public data class PublicPartDb(
  public val id: String,
  public val registrationId: Long?,
  public val name: String?,
  public val deviceId: Long?,
  public val preKeyId: Long?,
  public val preKeyPublicKey: String?,
  public val signedPreKeyId: Long?,
  public val signedPreKeyPublicKey: String?,
  public val signedPreKeySignature: String?,
  public val identityKeyPairPublicKey: String?
) {
  public override fun toString(): String = """
  |PublicPartDb [
  |  id: $id
  |  registrationId: $registrationId
  |  name: $name
  |  deviceId: $deviceId
  |  preKeyId: $preKeyId
  |  preKeyPublicKey: $preKeyPublicKey
  |  signedPreKeyId: $signedPreKeyId
  |  signedPreKeyPublicKey: $signedPreKeyPublicKey
  |  signedPreKeySignature: $signedPreKeySignature
  |  identityKeyPairPublicKey: $identityKeyPairPublicKey
  |]
  """.trimMargin()
}
