package com.lab.tb.distributed.database.models

import kotlin.Long
import kotlin.String

public data class ChatUserDb(
  public val id: String,
  public val publicKey: String?,
  public val name: String?,
  public val logicalClock: Long?,
  public val displayName: String?
) {
  public override fun toString(): String = """
  |ChatUserDb [
  |  id: $id
  |  publicKey: $publicKey
  |  name: $name
  |  logicalClock: $logicalClock
  |  displayName: $displayName
  |]
  """.trimMargin()
}
