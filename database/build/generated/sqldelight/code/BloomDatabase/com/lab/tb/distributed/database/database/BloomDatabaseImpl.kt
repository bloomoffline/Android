package com.lab.tb.distributed.database.database

import com.lab.tb.distributed.database.BloomDatabase
import com.lab.tb.distributed.database.models.BleSettingsDb
import com.lab.tb.distributed.database.models.BleSettingsQueries
import com.lab.tb.distributed.database.models.ChatPresenceDb
import com.lab.tb.distributed.database.models.ChatPresenceQueries
import com.lab.tb.distributed.database.models.ChatUserDb
import com.lab.tb.distributed.database.models.ChatUserQueries
import com.lab.tb.distributed.database.models.MessageDb
import com.lab.tb.distributed.database.models.MessageQueries
import com.lab.tb.distributed.database.models.PrivatePartDb
import com.lab.tb.distributed.database.models.PrivatePartQueries
import com.lab.tb.distributed.database.models.ProfileDb
import com.lab.tb.distributed.database.models.ProfileQueries
import com.lab.tb.distributed.database.models.PublicPartDb
import com.lab.tb.distributed.database.models.PublicPartQueries
import com.squareup.sqldelight.Query
import com.squareup.sqldelight.TransacterImpl
import com.squareup.sqldelight.`internal`.copyOnWriteList
import com.squareup.sqldelight.db.SqlCursor
import com.squareup.sqldelight.db.SqlDriver
import kotlin.Any
import kotlin.Boolean
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Unit
import kotlin.collections.MutableList
import kotlin.reflect.KClass

internal val KClass<BloomDatabase>.schema: SqlDriver.Schema
  get() = BloomDatabaseImpl.Schema

internal fun KClass<BloomDatabase>.newInstance(driver: SqlDriver): BloomDatabase =
    BloomDatabaseImpl(driver)

private class BloomDatabaseImpl(
  driver: SqlDriver
) : TransacterImpl(driver), BloomDatabase {
  public override val bleSettingsQueries: BleSettingsQueriesImpl = BleSettingsQueriesImpl(this,
      driver)

  public override val chatPresenceQueries: ChatPresenceQueriesImpl = ChatPresenceQueriesImpl(this,
      driver)

  public override val chatUserQueries: ChatUserQueriesImpl = ChatUserQueriesImpl(this, driver)

  public override val messageQueries: MessageQueriesImpl = MessageQueriesImpl(this, driver)

  public override val privatePartQueries: PrivatePartQueriesImpl = PrivatePartQueriesImpl(this,
      driver)

  public override val profileQueries: ProfileQueriesImpl = ProfileQueriesImpl(this, driver)

  public override val publicPartQueries: PublicPartQueriesImpl = PublicPartQueriesImpl(this, driver)

  public object Schema : SqlDriver.Schema {
    public override val version: Int
      get() = 2

    public override fun create(driver: SqlDriver): Unit {
      driver.execute(null, """
          |CREATE TABLE PrivatePartDb (
          |    id TEXT PRIMARY KEY,
          |    name TEXT,
          |    deviceId INTEGER,
          |    registrationId INTEGER,
          |    identityKeyPair TEXT,
          |    preKeys TEXT,
          |    signedPreKey TEXT
          |)
          """.trimMargin(), 0)
      driver.execute(null, """
          |CREATE TABLE ProfileDb (
          |    id TEXT PRIMARY KEY,
          |    presence TEXT
          |)
          """.trimMargin(), 0)
      driver.execute(null, """
          |CREATE TABLE ChatPresenceDb (
          |    id TEXT PRIMARY KEY,
          |    user TEXT,
          |    status TEXT,
          |    info TEXT
          |)
          """.trimMargin(), 0)
      driver.execute(null, """
          |CREATE TABLE MessageDb (
          |    id TEXT PRIMARY KEY,
          |    timestamp INTEGER,
          |    sourceUserId TEXT,
          |    destinationUserId TEXT,
          |    addedChatMessages TEXT,
          |    updatedPresences TEXT,
          |    deletedChatMessages TEXT,
          |    messageRequest TEXT,
          |    logicalClock INTEGER,
          |    isPin INTEGER,
          |    isNewNotification INTEGER,
          |    chatChannel TEXT
          |)
          """.trimMargin(), 0)
      driver.execute(null, """
          |CREATE TABLE PublicPartDb (
          |    id TEXT PRIMARY KEY,
          |    registrationId INTEGER,
          |    name TEXT,
          |    deviceId INTEGER,
          |    preKeyId INTEGER,
          |    preKeyPublicKey TEXT,
          |    signedPreKeyId INTEGER,
          |    signedPreKeyPublicKey TEXT,
          |    signedPreKeySignature TEXT,
          |    identityKeyPairPublicKey TEXT
          |)
          """.trimMargin(), 0)
      driver.execute(null, """
          |CREATE TABLE BleSettingsDb (
          |    id TEXT PRIMARY KEY,
          |    advertisingEnabled INTEGER,
          |    scanningEnabled INTEGER,
          |    monitorSignalStrengthInterval INTEGER,
          |    enableNotification INTEGER,
          |    showRoomAndMessagesPreviews INTEGER
          |)
          """.trimMargin(), 0)
      driver.execute(null, """
          |CREATE TABLE ChatUserDb (
          |    id TEXT PRIMARY KEY,
          |    publicKey TEXT,
          |    name TEXT,
          |    logicalClock INTEGER,
          |    displayName TEXT
          |)
          """.trimMargin(), 0)
    }

    public override fun migrate(
      driver: SqlDriver,
      oldVersion: Int,
      newVersion: Int
    ): Unit {
      if (oldVersion <= 1 && newVersion > 1) {
        driver.execute(null, "DROP TABLE IF EXISTS PublicPartDb", 0)
        driver.execute(null, "DROP TABLE IF EXISTS PrivatePartDb", 0)
        driver.execute(null, """
            |CREATE TABLE PrivatePartDb (
            |    id TEXT PRIMARY KEY,
            |    name TEXT,
            |    deviceId INTEGER,
            |    registrationId INTEGER,
            |    identityKeyPair TEXT,
            |    preKeys TEXT,
            |    signedPreKey TEXT
            |)
            """.trimMargin(), 0)
        driver.execute(null, """
            |CREATE TABLE PublicPartDb (
            |    id TEXT PRIMARY KEY,
            |    registrationId INTEGER,
            |    name TEXT,
            |    deviceId INTEGER,
            |    preKeyId INTEGER,
            |    preKeyPublicKey TEXT,
            |    signedPreKeyId INTEGER,
            |    signedPreKeyPublicKey TEXT,
            |    signedPreKeySignature TEXT,
            |    identityKeyPairPublicKey TEXT
            |)
            """.trimMargin(), 0)
      }
    }
  }
}

private class PrivatePartQueriesImpl(
  private val database: BloomDatabaseImpl,
  private val driver: SqlDriver
) : TransacterImpl(driver), PrivatePartQueries {
  internal val selectById: MutableList<Query<*>> = copyOnWriteList()

  internal val selectAll: MutableList<Query<*>> = copyOnWriteList()

  public override fun <T : Any> selectById(id: String, mapper: (
    id: String,
    name: String?,
    deviceId: Long?,
    registrationId: Long?,
    identityKeyPair: String?,
    preKeys: String?,
    signedPreKey: String?
  ) -> T): Query<T> = SelectByIdQuery(id) { cursor ->
    mapper(
      cursor.getString(0)!!,
      cursor.getString(1),
      cursor.getLong(2),
      cursor.getLong(3),
      cursor.getString(4),
      cursor.getString(5),
      cursor.getString(6)
    )
  }

  public override fun selectById(id: String): Query<PrivatePartDb> = selectById(id) { id_, name,
      deviceId, registrationId, identityKeyPair, preKeys, signedPreKey ->
    PrivatePartDb(
      id_,
      name,
      deviceId,
      registrationId,
      identityKeyPair,
      preKeys,
      signedPreKey
    )
  }

  public override fun <T : Any> selectAll(mapper: (
    id: String,
    name: String?,
    deviceId: Long?,
    registrationId: Long?,
    identityKeyPair: String?,
    preKeys: String?,
    signedPreKey: String?
  ) -> T): Query<T> = Query(-155026277, selectAll, driver, "PrivatePart.sq", "selectAll",
      "SELECT * FROM PrivatePartDb") { cursor ->
    mapper(
      cursor.getString(0)!!,
      cursor.getString(1),
      cursor.getLong(2),
      cursor.getLong(3),
      cursor.getString(4),
      cursor.getString(5),
      cursor.getString(6)
    )
  }

  public override fun selectAll(): Query<PrivatePartDb> = selectAll { id, name, deviceId,
      registrationId, identityKeyPair, preKeys, signedPreKey ->
    PrivatePartDb(
      id,
      name,
      deviceId,
      registrationId,
      identityKeyPair,
      preKeys,
      signedPreKey
    )
  }

  public override fun insert(PrivatePartDb: PrivatePartDb): Unit {
    driver.execute(951417059, """
    |INSERT OR REPLACE INTO PrivatePartDb
    |VALUES (?, ?, ?, ?, ?, ?, ?)
    """.trimMargin(), 7) {
      bindString(1, PrivatePartDb.id)
      bindString(2, PrivatePartDb.name)
      bindLong(3, PrivatePartDb.deviceId)
      bindLong(4, PrivatePartDb.registrationId)
      bindString(5, PrivatePartDb.identityKeyPair)
      bindString(6, PrivatePartDb.preKeys)
      bindString(7, PrivatePartDb.signedPreKey)
    }
    notifyQueries(951417059, {database.privatePartQueries.selectById +
        database.privatePartQueries.selectAll})
  }

  public override fun removeById(id: String): Unit {
    driver.execute(-2051246176, """
    |DELETE FROM PrivatePartDb
    |WHERE id = ?
    """.trimMargin(), 1) {
      bindString(1, id)
    }
    notifyQueries(-2051246176, {database.privatePartQueries.selectById +
        database.privatePartQueries.selectAll})
  }

  public override fun removeAll(): Unit {
    driver.execute(-1451643885, """DELETE FROM PrivatePartDb""", 0)
    notifyQueries(-1451643885, {database.privatePartQueries.selectById +
        database.privatePartQueries.selectAll})
  }

  private inner class SelectByIdQuery<out T : Any>(
    public val id: String,
    mapper: (SqlCursor) -> T
  ) : Query<T>(selectById, mapper) {
    public override fun execute(): SqlCursor = driver.executeQuery(-510805992, """
    |SELECT *
    |FROM PrivatePartDb
    |WHERE id = ?
    """.trimMargin(), 1) {
      bindString(1, id)
    }

    public override fun toString(): String = "PrivatePart.sq:selectById"
  }
}

private class ProfileQueriesImpl(
  private val database: BloomDatabaseImpl,
  private val driver: SqlDriver
) : TransacterImpl(driver), ProfileQueries {
  internal val selectById: MutableList<Query<*>> = copyOnWriteList()

  internal val selectAll: MutableList<Query<*>> = copyOnWriteList()

  public override fun <T : Any> selectById(id: String, mapper: (id: String,
      presence: String?) -> T): Query<T> = SelectByIdQuery(id) { cursor ->
    mapper(
      cursor.getString(0)!!,
      cursor.getString(1)
    )
  }

  public override fun selectById(id: String): Query<ProfileDb> = selectById(id) { id_, presence ->
    ProfileDb(
      id_,
      presence
    )
  }

  public override fun <T : Any> selectAll(mapper: (id: String, presence: String?) -> T): Query<T> =
      Query(1079106280, selectAll, driver, "Profile.sq", "selectAll", "SELECT * FROM ProfileDb") {
      cursor ->
    mapper(
      cursor.getString(0)!!,
      cursor.getString(1)
    )
  }

  public override fun selectAll(): Query<ProfileDb> = selectAll { id, presence ->
    ProfileDb(
      id,
      presence
    )
  }

  public override fun insert(ProfileDb: ProfileDb): Unit {
    driver.execute(114984374, """
    |INSERT OR REPLACE INTO ProfileDb
    |VALUES (?, ?)
    """.trimMargin(), 2) {
      bindString(1, ProfileDb.id)
      bindString(2, ProfileDb.presence)
    }
    notifyQueries(114984374, {database.profileQueries.selectById +
        database.profileQueries.selectAll})
  }

  public override fun removeById(id: String): Unit {
    driver.execute(1847124723, """
    |DELETE FROM ProfileDb
    |WHERE id = ?
    """.trimMargin(), 1) {
      bindString(1, id)
    }
    notifyQueries(1847124723, {database.profileQueries.selectById +
        database.profileQueries.selectAll})
  }

  public override fun removeAll(): Unit {
    driver.execute(-217511328, """DELETE FROM ProfileDb""", 0)
    notifyQueries(-217511328, {database.profileQueries.selectById +
        database.profileQueries.selectAll})
  }

  private inner class SelectByIdQuery<out T : Any>(
    public val id: String,
    mapper: (SqlCursor) -> T
  ) : Query<T>(selectById, mapper) {
    public override fun execute(): SqlCursor = driver.executeQuery(-907402389, """
    |SELECT *
    |FROM ProfileDb
    |WHERE id = ?
    """.trimMargin(), 1) {
      bindString(1, id)
    }

    public override fun toString(): String = "Profile.sq:selectById"
  }
}

private class ChatPresenceQueriesImpl(
  private val database: BloomDatabaseImpl,
  private val driver: SqlDriver
) : TransacterImpl(driver), ChatPresenceQueries {
  internal val selectById: MutableList<Query<*>> = copyOnWriteList()

  internal val selectAll: MutableList<Query<*>> = copyOnWriteList()

  public override fun <T : Any> selectById(id: String, mapper: (
    id: String,
    user: String?,
    status: String?,
    info: String?
  ) -> T): Query<T> = SelectByIdQuery(id) { cursor ->
    mapper(
      cursor.getString(0)!!,
      cursor.getString(1),
      cursor.getString(2),
      cursor.getString(3)
    )
  }

  public override fun selectById(id: String): Query<ChatPresenceDb> = selectById(id) { id_, user,
      status, info ->
    ChatPresenceDb(
      id_,
      user,
      status,
      info
    )
  }

  public override fun <T : Any> selectAll(mapper: (
    id: String,
    user: String?,
    status: String?,
    info: String?
  ) -> T): Query<T> = Query(-1833405206, selectAll, driver, "ChatPresence.sq", "selectAll",
      "SELECT * FROM ChatPresenceDb") { cursor ->
    mapper(
      cursor.getString(0)!!,
      cursor.getString(1),
      cursor.getString(2),
      cursor.getString(3)
    )
  }

  public override fun selectAll(): Query<ChatPresenceDb> = selectAll { id, user, status, info ->
    ChatPresenceDb(
      id,
      user,
      status,
      info
    )
  }

  public override fun insert(ChatPresenceDb: ChatPresenceDb): Unit {
    driver.execute(2006973172, """
    |INSERT OR REPLACE INTO ChatPresenceDb
    |VALUES (?, ?, ?, ?)
    """.trimMargin(), 4) {
      bindString(1, ChatPresenceDb.id)
      bindString(2, ChatPresenceDb.user)
      bindString(3, ChatPresenceDb.status)
      bindString(4, ChatPresenceDb.info)
    }
    notifyQueries(2006973172, {database.chatPresenceQueries.selectAll +
        database.chatPresenceQueries.selectById})
  }

  public override fun removeById(id: String): Unit {
    driver.execute(1753581873, """
    |DELETE FROM ChatPresenceDb
    |WHERE id = ?
    """.trimMargin(), 1) {
      bindString(1, id)
    }
    notifyQueries(1753581873, {database.chatPresenceQueries.selectAll +
        database.chatPresenceQueries.selectById})
  }

  public override fun removeAll(): Unit {
    driver.execute(1164944482, """DELETE FROM ChatPresenceDb""", 0)
    notifyQueries(1164944482, {database.chatPresenceQueries.selectAll +
        database.chatPresenceQueries.selectById})
  }

  private inner class SelectByIdQuery<out T : Any>(
    public val id: String,
    mapper: (SqlCursor) -> T
  ) : Query<T>(selectById, mapper) {
    public override fun execute(): SqlCursor = driver.executeQuery(-1000945239, """
    |SELECT *
    |FROM ChatPresenceDb
    |WHERE id = ?
    """.trimMargin(), 1) {
      bindString(1, id)
    }

    public override fun toString(): String = "ChatPresence.sq:selectById"
  }
}

private class MessageQueriesImpl(
  private val database: BloomDatabaseImpl,
  private val driver: SqlDriver
) : TransacterImpl(driver), MessageQueries {
  internal val selectById: MutableList<Query<*>> = copyOnWriteList()

  internal val selectAll: MutableList<Query<*>> = copyOnWriteList()

  public override fun <T : Any> selectById(id: String, mapper: (
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
  ) -> T): Query<T> = SelectByIdQuery(id) { cursor ->
    mapper(
      cursor.getString(0)!!,
      cursor.getLong(1),
      cursor.getString(2),
      cursor.getString(3),
      cursor.getString(4),
      cursor.getString(5),
      cursor.getString(6),
      cursor.getString(7),
      cursor.getLong(8),
      cursor.getLong(9)?.let { it == 1L },
      cursor.getLong(10)?.let { it == 1L },
      cursor.getString(11)
    )
  }

  public override fun selectById(id: String): Query<MessageDb> = selectById(id) { id_, timestamp,
      sourceUserId, destinationUserId, addedChatMessages, updatedPresences, deletedChatMessages,
      messageRequest, logicalClock, isPin, isNewNotification, chatChannel ->
    MessageDb(
      id_,
      timestamp,
      sourceUserId,
      destinationUserId,
      addedChatMessages,
      updatedPresences,
      deletedChatMessages,
      messageRequest,
      logicalClock,
      isPin,
      isNewNotification,
      chatChannel
    )
  }

  public override fun <T : Any> selectAll(mapper: (
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
  ) -> T): Query<T> = Query(1960127818, selectAll, driver, "Message.sq", "selectAll",
      "SELECT * FROM MessageDb") { cursor ->
    mapper(
      cursor.getString(0)!!,
      cursor.getLong(1),
      cursor.getString(2),
      cursor.getString(3),
      cursor.getString(4),
      cursor.getString(5),
      cursor.getString(6),
      cursor.getString(7),
      cursor.getLong(8),
      cursor.getLong(9)?.let { it == 1L },
      cursor.getLong(10)?.let { it == 1L },
      cursor.getString(11)
    )
  }

  public override fun selectAll(): Query<MessageDb> = selectAll { id, timestamp, sourceUserId,
      destinationUserId, addedChatMessages, updatedPresences, deletedChatMessages, messageRequest,
      logicalClock, isPin, isNewNotification, chatChannel ->
    MessageDb(
      id,
      timestamp,
      sourceUserId,
      destinationUserId,
      addedChatMessages,
      updatedPresences,
      deletedChatMessages,
      messageRequest,
      logicalClock,
      isPin,
      isNewNotification,
      chatChannel
    )
  }

  public override fun insert(MessageDb: MessageDb): Unit {
    driver.execute(-443212140, """
    |INSERT OR REPLACE INTO MessageDb
    |VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    """.trimMargin(), 12) {
      bindString(1, MessageDb.id)
      bindLong(2, MessageDb.timestamp)
      bindString(3, MessageDb.sourceUserId)
      bindString(4, MessageDb.destinationUserId)
      bindString(5, MessageDb.addedChatMessages)
      bindString(6, MessageDb.updatedPresences)
      bindString(7, MessageDb.deletedChatMessages)
      bindString(8, MessageDb.messageRequest)
      bindLong(9, MessageDb.logicalClock)
      bindLong(10, MessageDb.isPin?.let { if (it) 1L else 0L })
      bindLong(11, MessageDb.isNewNotification?.let { if (it) 1L else 0L })
      bindString(12, MessageDb.chatChannel)
    }
    notifyQueries(-443212140, {database.messageQueries.selectById +
        database.messageQueries.selectAll})
  }

  public override fun removeById(id: String): Unit {
    driver.execute(-905978671, """
    |DELETE FROM MessageDb
    |WHERE id = ?
    """.trimMargin(), 1) {
      bindString(1, id)
    }
    notifyQueries(-905978671, {database.messageQueries.selectById +
        database.messageQueries.selectAll})
  }

  public override fun removeAll(): Unit {
    driver.execute(663510210, """DELETE FROM MessageDb""", 0)
    notifyQueries(663510210, {database.messageQueries.selectById +
        database.messageQueries.selectAll})
  }

  private inner class SelectByIdQuery<out T : Any>(
    public val id: String,
    mapper: (SqlCursor) -> T
  ) : Query<T>(selectById, mapper) {
    public override fun execute(): SqlCursor = driver.executeQuery(634461513, """
    |SELECT *
    |FROM MessageDb
    |WHERE id = ?
    """.trimMargin(), 1) {
      bindString(1, id)
    }

    public override fun toString(): String = "Message.sq:selectById"
  }
}

private class PublicPartQueriesImpl(
  private val database: BloomDatabaseImpl,
  private val driver: SqlDriver
) : TransacterImpl(driver), PublicPartQueries {
  internal val selectById: MutableList<Query<*>> = copyOnWriteList()

  internal val selectAll: MutableList<Query<*>> = copyOnWriteList()

  public override fun <T : Any> selectById(id: String, mapper: (
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
  ) -> T): Query<T> = SelectByIdQuery(id) { cursor ->
    mapper(
      cursor.getString(0)!!,
      cursor.getLong(1),
      cursor.getString(2),
      cursor.getLong(3),
      cursor.getLong(4),
      cursor.getString(5),
      cursor.getLong(6),
      cursor.getString(7),
      cursor.getString(8),
      cursor.getString(9)
    )
  }

  public override fun selectById(id: String): Query<PublicPartDb> = selectById(id) { id_,
      registrationId, name, deviceId, preKeyId, preKeyPublicKey, signedPreKeyId,
      signedPreKeyPublicKey, signedPreKeySignature, identityKeyPairPublicKey ->
    PublicPartDb(
      id_,
      registrationId,
      name,
      deviceId,
      preKeyId,
      preKeyPublicKey,
      signedPreKeyId,
      signedPreKeyPublicKey,
      signedPreKeySignature,
      identityKeyPairPublicKey
    )
  }

  public override fun <T : Any> selectAll(mapper: (
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
  ) -> T): Query<T> = Query(-1620776063, selectAll, driver, "PublicPart.sq", "selectAll",
      "SELECT * FROM PublicPartDb") { cursor ->
    mapper(
      cursor.getString(0)!!,
      cursor.getLong(1),
      cursor.getString(2),
      cursor.getLong(3),
      cursor.getLong(4),
      cursor.getString(5),
      cursor.getLong(6),
      cursor.getString(7),
      cursor.getString(8),
      cursor.getString(9)
    )
  }

  public override fun selectAll(): Query<PublicPartDb> = selectAll { id, registrationId, name,
      deviceId, preKeyId, preKeyPublicKey, signedPreKeyId, signedPreKeyPublicKey,
      signedPreKeySignature, identityKeyPairPublicKey ->
    PublicPartDb(
      id,
      registrationId,
      name,
      deviceId,
      preKeyId,
      preKeyPublicKey,
      signedPreKeyId,
      signedPreKeyPublicKey,
      signedPreKeySignature,
      identityKeyPairPublicKey
    )
  }

  public override fun insert(PublicPartDb: PublicPartDb): Unit {
    driver.execute(983950269, """
    |INSERT OR REPLACE INTO PublicPartDb
    |VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    """.trimMargin(), 10) {
      bindString(1, PublicPartDb.id)
      bindLong(2, PublicPartDb.registrationId)
      bindString(3, PublicPartDb.name)
      bindLong(4, PublicPartDb.deviceId)
      bindLong(5, PublicPartDb.preKeyId)
      bindString(6, PublicPartDb.preKeyPublicKey)
      bindLong(7, PublicPartDb.signedPreKeyId)
      bindString(8, PublicPartDb.signedPreKeyPublicKey)
      bindString(9, PublicPartDb.signedPreKeySignature)
      bindString(10, PublicPartDb.identityKeyPairPublicKey)
    }
    notifyQueries(983950269, {database.publicPartQueries.selectAll +
        database.publicPartQueries.selectById})
  }

  public override fun removeById(id: String): Unit {
    driver.execute(-244849286, """
    |DELETE FROM PublicPartDb
    |WHERE id = ?
    """.trimMargin(), 1) {
      bindString(1, id)
    }
    notifyQueries(-244849286, {database.publicPartQueries.selectAll +
        database.publicPartQueries.selectById})
  }

  public override fun removeAll(): Unit {
    driver.execute(1377573625, """DELETE FROM PublicPartDb""", 0)
    notifyQueries(1377573625, {database.publicPartQueries.selectAll +
        database.publicPartQueries.selectById})
  }

  private inner class SelectByIdQuery<out T : Any>(
    public val id: String,
    mapper: (SqlCursor) -> T
  ) : Query<T>(selectById, mapper) {
    public override fun execute(): SqlCursor = driver.executeQuery(1295590898, """
    |SELECT *
    |FROM PublicPartDb
    |WHERE id = ?
    """.trimMargin(), 1) {
      bindString(1, id)
    }

    public override fun toString(): String = "PublicPart.sq:selectById"
  }
}

private class BleSettingsQueriesImpl(
  private val database: BloomDatabaseImpl,
  private val driver: SqlDriver
) : TransacterImpl(driver), BleSettingsQueries {
  internal val selectById: MutableList<Query<*>> = copyOnWriteList()

  internal val selectAll: MutableList<Query<*>> = copyOnWriteList()

  public override fun <T : Any> selectById(id: String, mapper: (
    id: String,
    advertisingEnabled: Boolean?,
    scanningEnabled: Boolean?,
    monitorSignalStrengthInterval: Long?,
    enableNotification: Boolean?,
    showRoomAndMessagesPreviews: Boolean?
  ) -> T): Query<T> = SelectByIdQuery(id) { cursor ->
    mapper(
      cursor.getString(0)!!,
      cursor.getLong(1)?.let { it == 1L },
      cursor.getLong(2)?.let { it == 1L },
      cursor.getLong(3),
      cursor.getLong(4)?.let { it == 1L },
      cursor.getLong(5)?.let { it == 1L }
    )
  }

  public override fun selectById(id: String): Query<BleSettingsDb> = selectById(id) { id_,
      advertisingEnabled, scanningEnabled, monitorSignalStrengthInterval, enableNotification,
      showRoomAndMessagesPreviews ->
    BleSettingsDb(
      id_,
      advertisingEnabled,
      scanningEnabled,
      monitorSignalStrengthInterval,
      enableNotification,
      showRoomAndMessagesPreviews
    )
  }

  public override fun <T : Any> selectAll(mapper: (
    id: String,
    advertisingEnabled: Boolean?,
    scanningEnabled: Boolean?,
    monitorSignalStrengthInterval: Long?,
    enableNotification: Boolean?,
    showRoomAndMessagesPreviews: Boolean?
  ) -> T): Query<T> = Query(1481231123, selectAll, driver, "BleSettings.sq", "selectAll",
      "SELECT * FROM BleSettingsDb") { cursor ->
    mapper(
      cursor.getString(0)!!,
      cursor.getLong(1)?.let { it == 1L },
      cursor.getLong(2)?.let { it == 1L },
      cursor.getLong(3),
      cursor.getLong(4)?.let { it == 1L },
      cursor.getLong(5)?.let { it == 1L }
    )
  }

  public override fun selectAll(): Query<BleSettingsDb> = selectAll { id, advertisingEnabled,
      scanningEnabled, monitorSignalStrengthInterval, enableNotification,
      showRoomAndMessagesPreviews ->
    BleSettingsDb(
      id,
      advertisingEnabled,
      scanningEnabled,
      monitorSignalStrengthInterval,
      enableNotification,
      showRoomAndMessagesPreviews
    )
  }

  public override fun insert(BleSettingsDb: BleSettingsDb): Unit {
    driver.execute(-868385429, """
    |INSERT OR REPLACE INTO BleSettingsDb
    |VALUES (?, ?, ?, ?, ?, ?)
    """.trimMargin(), 6) {
      bindString(1, BleSettingsDb.id)
      bindLong(2, BleSettingsDb.advertisingEnabled?.let { if (it) 1L else 0L })
      bindLong(3, BleSettingsDb.scanningEnabled?.let { if (it) 1L else 0L })
      bindLong(4, BleSettingsDb.monitorSignalStrengthInterval)
      bindLong(5, BleSettingsDb.enableNotification?.let { if (it) 1L else 0L })
      bindLong(6, BleSettingsDb.showRoomAndMessagesPreviews?.let { if (it) 1L else 0L })
    }
    notifyQueries(-868385429, {database.bleSettingsQueries.selectById +
        database.bleSettingsQueries.selectAll})
  }

  public override fun removeById(id: String): Unit {
    driver.execute(1428092968, """
    |DELETE FROM BleSettingsDb
    |WHERE id = ?
    """.trimMargin(), 1) {
      bindString(1, id)
    }
    notifyQueries(1428092968, {database.bleSettingsQueries.selectById +
        database.bleSettingsQueries.selectAll})
  }

  public override fun removeAll(): Unit {
    driver.execute(184613515, """DELETE FROM BleSettingsDb""", 0)
    notifyQueries(184613515, {database.bleSettingsQueries.selectById +
        database.bleSettingsQueries.selectAll})
  }

  private inner class SelectByIdQuery<out T : Any>(
    public val id: String,
    mapper: (SqlCursor) -> T
  ) : Query<T>(selectById, mapper) {
    public override fun execute(): SqlCursor = driver.executeQuery(-1326434144, """
    |SELECT *
    |FROM BleSettingsDb
    |WHERE id = ?
    """.trimMargin(), 1) {
      bindString(1, id)
    }

    public override fun toString(): String = "BleSettings.sq:selectById"
  }
}

private class ChatUserQueriesImpl(
  private val database: BloomDatabaseImpl,
  private val driver: SqlDriver
) : TransacterImpl(driver), ChatUserQueries {
  internal val selectById: MutableList<Query<*>> = copyOnWriteList()

  internal val selectAll: MutableList<Query<*>> = copyOnWriteList()

  public override fun <T : Any> selectById(id: String, mapper: (
    id: String,
    publicKey: String?,
    name: String?,
    logicalClock: Long?,
    displayName: String?
  ) -> T): Query<T> = SelectByIdQuery(id) { cursor ->
    mapper(
      cursor.getString(0)!!,
      cursor.getString(1),
      cursor.getString(2),
      cursor.getLong(3),
      cursor.getString(4)
    )
  }

  public override fun selectById(id: String): Query<ChatUserDb> = selectById(id) { id_, publicKey,
      name, logicalClock, displayName ->
    ChatUserDb(
      id_,
      publicKey,
      name,
      logicalClock,
      displayName
    )
  }

  public override fun <T : Any> selectAll(mapper: (
    id: String,
    publicKey: String?,
    name: String?,
    logicalClock: Long?,
    displayName: String?
  ) -> T): Query<T> = Query(-1888887014, selectAll, driver, "ChatUser.sq", "selectAll",
      "SELECT * FROM ChatUserDb") { cursor ->
    mapper(
      cursor.getString(0)!!,
      cursor.getString(1),
      cursor.getString(2),
      cursor.getLong(3),
      cursor.getString(4)
    )
  }

  public override fun selectAll(): Query<ChatUserDb> = selectAll { id, publicKey, name,
      logicalClock, displayName ->
    ChatUserDb(
      id,
      publicKey,
      name,
      logicalClock,
      displayName
    )
  }

  public override fun insert(ChatUserDb: ChatUserDb): Unit {
    driver.execute(-1769849148, """
    |INSERT OR REPLACE INTO ChatUserDb
    |VALUES (?, ?, ?, ?, ?)
    """.trimMargin(), 5) {
      bindString(1, ChatUserDb.id)
      bindString(2, ChatUserDb.publicKey)
      bindString(3, ChatUserDb.name)
      bindLong(4, ChatUserDb.logicalClock)
      bindString(5, ChatUserDb.displayName)
    }
    notifyQueries(-1769849148, {database.chatUserQueries.selectAll +
        database.chatUserQueries.selectById})
  }

  public override fun removeById(id: String): Unit {
    driver.execute(33645825, """
    |DELETE FROM ChatUserDb
    |WHERE id = ?
    """.trimMargin(), 1) {
      bindString(1, id)
    }
    notifyQueries(33645825, {database.chatUserQueries.selectAll +
        database.chatUserQueries.selectById})
  }

  public override fun removeAll(): Unit {
    driver.execute(1109462674, """DELETE FROM ChatUserDb""", 0)
    notifyQueries(1109462674, {database.chatUserQueries.selectAll +
        database.chatUserQueries.selectById})
  }

  private inner class SelectByIdQuery<out T : Any>(
    public val id: String,
    mapper: (SqlCursor) -> T
  ) : Query<T>(selectById, mapper) {
    public override fun execute(): SqlCursor = driver.executeQuery(1574086009, """
    |SELECT *
    |FROM ChatUserDb
    |WHERE id = ?
    """.trimMargin(), 1) {
      bindString(1, id)
    }

    public override fun toString(): String = "ChatUser.sq:selectById"
  }
}
