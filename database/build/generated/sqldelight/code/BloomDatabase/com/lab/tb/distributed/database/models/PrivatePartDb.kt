package com.lab.tb.distributed.database.models

import kotlin.Long
import kotlin.String

public data class PrivatePartDb(
  public val id: String,
  public val name: String?,
  public val deviceId: Long?,
  public val registrationId: Long?,
  public val identityKeyPair: String?,
  public val preKeys: String?,
  public val signedPreKey: String?
) {
  public override fun toString(): String = """
  |PrivatePartDb [
  |  id: $id
  |  name: $name
  |  deviceId: $deviceId
  |  registrationId: $registrationId
  |  identityKeyPair: $identityKeyPair
  |  preKeys: $preKeys
  |  signedPreKey: $signedPreKey
  |]
  """.trimMargin()
}
