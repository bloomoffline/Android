package com.lab.tb.distributed.database.models

import com.squareup.sqldelight.Query
import com.squareup.sqldelight.Transacter
import kotlin.Any
import kotlin.Long
import kotlin.String
import kotlin.Unit

public interface PrivatePartQueries : Transacter {
  public fun <T : Any> selectById(id: String, mapper: (
    id: String,
    name: String?,
    deviceId: Long?,
    registrationId: Long?,
    identityKeyPair: String?,
    preKeys: String?,
    signedPreKey: String?
  ) -> T): Query<T>

  public fun selectById(id: String): Query<PrivatePartDb>

  public fun <T : Any> selectAll(mapper: (
    id: String,
    name: String?,
    deviceId: Long?,
    registrationId: Long?,
    identityKeyPair: String?,
    preKeys: String?,
    signedPreKey: String?
  ) -> T): Query<T>

  public fun selectAll(): Query<PrivatePartDb>

  public fun insert(PrivatePartDb: PrivatePartDb): Unit

  public fun removeById(id: String): Unit

  public fun removeAll(): Unit
}
