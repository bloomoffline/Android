package com.lab.tb.distributed.database.models

import com.squareup.sqldelight.Query
import com.squareup.sqldelight.Transacter
import kotlin.Any
import kotlin.String
import kotlin.Unit

public interface ChatPresenceQueries : Transacter {
  public fun <T : Any> selectById(id: String, mapper: (
    id: String,
    user: String?,
    status: String?,
    info: String?
  ) -> T): Query<T>

  public fun selectById(id: String): Query<ChatPresenceDb>

  public fun <T : Any> selectAll(mapper: (
    id: String,
    user: String?,
    status: String?,
    info: String?
  ) -> T): Query<T>

  public fun selectAll(): Query<ChatPresenceDb>

  public fun insert(ChatPresenceDb: ChatPresenceDb): Unit

  public fun removeById(id: String): Unit

  public fun removeAll(): Unit
}
