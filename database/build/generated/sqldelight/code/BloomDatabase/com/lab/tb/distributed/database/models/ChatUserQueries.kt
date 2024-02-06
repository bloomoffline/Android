package com.lab.tb.distributed.database.models

import com.squareup.sqldelight.Query
import com.squareup.sqldelight.Transacter
import kotlin.Any
import kotlin.Long
import kotlin.String
import kotlin.Unit

public interface ChatUserQueries : Transacter {
  public fun <T : Any> selectById(id: String, mapper: (
    id: String,
    publicKey: String?,
    name: String?,
    logicalClock: Long?,
    displayName: String?
  ) -> T): Query<T>

  public fun selectById(id: String): Query<ChatUserDb>

  public fun <T : Any> selectAll(mapper: (
    id: String,
    publicKey: String?,
    name: String?,
    logicalClock: Long?,
    displayName: String?
  ) -> T): Query<T>

  public fun selectAll(): Query<ChatUserDb>

  public fun insert(ChatUserDb: ChatUserDb): Unit

  public fun removeById(id: String): Unit

  public fun removeAll(): Unit
}
