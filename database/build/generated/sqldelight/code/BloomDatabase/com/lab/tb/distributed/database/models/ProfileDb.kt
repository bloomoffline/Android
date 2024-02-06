package com.lab.tb.distributed.database.models

import kotlin.String

public data class ProfileDb(
  public val id: String,
  public val presence: String?
) {
  public override fun toString(): String = """
  |ProfileDb [
  |  id: $id
  |  presence: $presence
  |]
  """.trimMargin()
}
