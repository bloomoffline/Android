package com.lab.tb.distributed.database.models

import kotlin.Boolean
import kotlin.Long
import kotlin.String

public data class MessageDb(
  public val id: String,
  public val timestamp: Long?,
  public val sourceUserId: String?,
  public val destinationUserId: String?,
  public val addedChatMessages: String?,
  public val updatedPresences: String?,
  public val deletedChatMessages: String?,
  public val messageRequest: String?,
  public val logicalClock: Long?,
  public val isPin: Boolean?,
  public val isNewNotification: Boolean?,
  public val chatChannel: String?
) {
  public override fun toString(): String = """
  |MessageDb [
  |  id: $id
  |  timestamp: $timestamp
  |  sourceUserId: $sourceUserId
  |  destinationUserId: $destinationUserId
  |  addedChatMessages: $addedChatMessages
  |  updatedPresences: $updatedPresences
  |  deletedChatMessages: $deletedChatMessages
  |  messageRequest: $messageRequest
  |  logicalClock: $logicalClock
  |  isPin: $isPin
  |  isNewNotification: $isNewNotification
  |  chatChannel: $chatChannel
  |]
  """.trimMargin()
}
