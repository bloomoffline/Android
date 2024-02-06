package com.lab.tb.distributed.database.models

import kotlin.String

public data class ChatPresenceDb(
  public val id: String,
  public val user: String?,
  public val status: String?,
  public val info: String?
) {
  public override fun toString(): String = """
  |ChatPresenceDb [
  |  id: $id
  |  user: $user
  |  status: $status
  |  info: $info
  |]
  """.trimMargin()
}
