package com.lab.tb.distributed.database.models

import com.squareup.sqldelight.Query
import com.squareup.sqldelight.Transacter
import kotlin.Any
import kotlin.String
import kotlin.Unit

public interface ProfileQueries : Transacter {
  public fun <T : Any> selectById(id: String, mapper: (id: String, presence: String?) -> T):
      Query<T>

  public fun selectById(id: String): Query<ProfileDb>

  public fun <T : Any> selectAll(mapper: (id: String, presence: String?) -> T): Query<T>

  public fun selectAll(): Query<ProfileDb>

  public fun insert(ProfileDb: ProfileDb): Unit

  public fun removeById(id: String): Unit

  public fun removeAll(): Unit
}
