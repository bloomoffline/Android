package com.lab.tb.distributed.database.models

import com.squareup.sqldelight.Query
import com.squareup.sqldelight.Transacter
import kotlin.Any
import kotlin.Long
import kotlin.String
import kotlin.Unit

public interface PublicPartQueries : Transacter {
  public fun <T : Any> selectById(id: String, mapper: (
    id: String,
    registrationId: Long?,
    name: String?,
    deviceId: Long?,
    preKeyId: Long?,
    preKeyPublicKey: String?,
    signedPreKeyId: Long?,
    signedPreKeyPublicKey: String?,
    signedPreKeySignature: String?,
    identityKeyPairPublicKey: String?
  ) -> T): Query<T>

  public fun selectById(id: String): Query<PublicPartDb>

  public fun <T : Any> selectAll(mapper: (
    id: String,
    registrationId: Long?,
    name: String?,
    deviceId: Long?,
    preKeyId: Long?,
    preKeyPublicKey: String?,
    signedPreKeyId: Long?,
    signedPreKeyPublicKey: String?,
    signedPreKeySignature: String?,
    identityKeyPairPublicKey: String?
  ) -> T): Query<T>

  public fun selectAll(): Query<PublicPartDb>

  public fun insert(PublicPartDb: PublicPartDb): Unit

  public fun removeById(id: String): Unit

  public fun removeAll(): Unit
}
