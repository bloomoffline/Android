package com.lab.tb.distributed.database.models

import com.squareup.sqldelight.Query
import com.squareup.sqldelight.Transacter
import kotlin.Any
import kotlin.Boolean
import kotlin.Long
import kotlin.String
import kotlin.Unit

public interface MessageQueries : Transacter {
  public fun <T : Any> selectById(id: String, mapper: (
    id: String,
    timestamp: Long?,
    sourceUserId: String?,
    destinationUserId: String?,
    addedChatMessages: String?,
    updatedPresences: String?,
    deletedChatMessages: String?,
    messageRequest: String?,
    logicalClock: Long?,
    isPin: Boolean?,
    isNewNotification: Boolean?,
    chatChannel: String?
  ) -> T): Query<T>

  public fun selectById(id: String): Query<MessageDb>

  public fun <T : Any> selectAll(mapper: (
    id: String,
    timestamp: Long?,
    sourceUserId: String?,
    destinationUserId: String?,
    addedChatMessages: String?,
    updatedPresences: String?,
    deletedChatMessages: String?,
    messageRequest: String?,
    logicalClock: Long?,
    isPin: Boolean?,
    isNewNotification: Boolean?,
    chatChannel: String?
  ) -> T): Query<T>

  public fun selectAll(): Query<MessageDb>

  public fun insert(MessageDb: MessageDb): Unit

  public fun removeById(id: String): Unit

  public fun removeAll(): Unit
}
