package com.lab.tb.distributed.database

import com.lab.tb.distributed.database.database.newInstance
import com.lab.tb.distributed.database.database.schema
import com.lab.tb.distributed.database.models.BleSettingsQueries
import com.lab.tb.distributed.database.models.ChatPresenceQueries
import com.lab.tb.distributed.database.models.ChatUserQueries
import com.lab.tb.distributed.database.models.MessageQueries
import com.lab.tb.distributed.database.models.PrivatePartQueries
import com.lab.tb.distributed.database.models.ProfileQueries
import com.lab.tb.distributed.database.models.PublicPartQueries
import com.squareup.sqldelight.Transacter
import com.squareup.sqldelight.db.SqlDriver

public interface BloomDatabase : Transacter {
  public val bleSettingsQueries: BleSettingsQueries

  public val chatPresenceQueries: ChatPresenceQueries

  public val chatUserQueries: ChatUserQueries

  public val messageQueries: MessageQueries

  public val privatePartQueries: PrivatePartQueries

  public val profileQueries: ProfileQueries

  public val publicPartQueries: PublicPartQueries

  public companion object {
    public val Schema: SqlDriver.Schema
      get() = BloomDatabase::class.schema

    public operator fun invoke(driver: SqlDriver): BloomDatabase =
        BloomDatabase::class.newInstance(driver)
  }
}
