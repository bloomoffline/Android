package com.lab.tb.distributed.database.models

import com.squareup.sqldelight.Query
import com.squareup.sqldelight.Transacter
import kotlin.Any
import kotlin.Boolean
import kotlin.Long
import kotlin.String
import kotlin.Unit

public interface BleSettingsQueries : Transacter {
  public fun <T : Any> selectById(id: String, mapper: (
    id: String,
    advertisingEnabled: Boolean?,
    scanningEnabled: Boolean?,
    monitorSignalStrengthInterval: Long?,
    enableNotification: Boolean?,
    showRoomAndMessagesPreviews: Boolean?
  ) -> T): Query<T>

  public fun selectById(id: String): Query<BleSettingsDb>

  public fun <T : Any> selectAll(mapper: (
    id: String,
    advertisingEnabled: Boolean?,
    scanningEnabled: Boolean?,
    monitorSignalStrengthInterval: Long?,
    enableNotification: Boolean?,
    showRoomAndMessagesPreviews: Boolean?
  ) -> T): Query<T>

  public fun selectAll(): Query<BleSettingsDb>

  public fun insert(BleSettingsDb: BleSettingsDb): Unit

  public fun removeById(id: String): Unit

  public fun removeAll(): Unit
}
