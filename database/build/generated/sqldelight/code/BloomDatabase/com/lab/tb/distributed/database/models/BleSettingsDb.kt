package com.lab.tb.distributed.database.models

import kotlin.Boolean
import kotlin.Long
import kotlin.String

public data class BleSettingsDb(
  public val id: String,
  public val advertisingEnabled: Boolean?,
  public val scanningEnabled: Boolean?,
  public val monitorSignalStrengthInterval: Long?,
  public val enableNotification: Boolean?,
  public val showRoomAndMessagesPreviews: Boolean?
) {
  public override fun toString(): String = """
  |BleSettingsDb [
  |  id: $id
  |  advertisingEnabled: $advertisingEnabled
  |  scanningEnabled: $scanningEnabled
  |  monitorSignalStrengthInterval: $monitorSignalStrengthInterval
  |  enableNotification: $enableNotification
  |  showRoomAndMessagesPreviews: $showRoomAndMessagesPreviews
  |]
  """.trimMargin()
}
