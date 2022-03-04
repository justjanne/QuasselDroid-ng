package de.justjanne.quasseldroid.persistence

import androidx.paging.LoadType
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.withTransaction
import de.justjanne.bitflags.of
import de.justjanne.bitflags.toBits
import de.justjanne.libquassel.client.syncables.ClientBacklogManager
import de.justjanne.libquassel.protocol.models.BufferInfo
import de.justjanne.libquassel.protocol.models.Message
import de.justjanne.libquassel.protocol.models.flags.MessageFlag
import de.justjanne.libquassel.protocol.models.flags.MessageType
import de.justjanne.libquassel.protocol.models.ids.BufferId
import de.justjanne.libquassel.protocol.models.ids.MsgId
import de.justjanne.libquassel.protocol.models.ids.SignedId64Type
import de.justjanne.libquassel.protocol.models.ids.SignedIdType
import de.justjanne.libquassel.protocol.models.ids.isValid
import de.justjanne.libquassel.protocol.variant.QVariant_
import de.justjanne.libquassel.protocol.variant.into
import org.intellij.lang.annotations.Language
import org.threeten.bp.Instant

class QuasselRemoteMediator<Key : Any>(
  private val bufferId: BufferId,
  private val database: AppDatabase,
  private val backlogManager: ClientBacklogManager,
  private val pageSize: Int = 50
) : RemoteMediator<Key, MessageModel>() {

  private suspend fun loadAround(bufferId: BufferId, messageId: MsgId): List<Message> =
    loadBefore(bufferId, messageId) + loadAfter(bufferId, messageId)

  private suspend fun loadBefore(bufferId: BufferId, messageId: MsgId) =
    backlogManager.backlog(bufferId, last = messageId, limit = pageSize)
      .mapNotNull<QVariant_, Message>(QVariant_::into)

  private suspend fun loadAfter(bufferId: BufferId, messageId: MsgId) =
    backlogManager.backlogForward(bufferId, first = messageId, limit = pageSize)
      .mapNotNull<QVariant_, Message>(QVariant_::into)

  override suspend fun load(
    loadType: LoadType,
    state: PagingState<Key, MessageModel>
  ): MediatorResult {
    val loadKey: MsgId = when (loadType) {
      LoadType.REFRESH ->
        state.anchorPosition?.let { anchorPosition ->
          state.closestItemToPosition(anchorPosition)?.messageId?.let(::MsgId)
        } ?: MsgId(-1)
      LoadType.PREPEND ->
        state.firstItemOrNull()?.messageId?.let(::MsgId)
        ?: return MediatorResult.Success(endOfPaginationReached = true)
      LoadType.APPEND ->
        state.lastItemOrNull()?.messageId?.let(::MsgId)
          ?: return MediatorResult.Success(endOfPaginationReached = true)
    }

    val newMessages: List<Message> = when (loadType) {
      LoadType.REFRESH ->
        if (loadKey.isValid()) loadAround(bufferId, loadKey)
        else loadBefore(bufferId, loadKey)
      LoadType.PREPEND -> loadBefore(bufferId, loadKey)
      LoadType.APPEND -> loadAfter(bufferId, loadKey)
    }

    database.withTransaction {
      if (loadType == LoadType.REFRESH) {
        database.messageDao().delete(bufferId.id)
      }

      database.messageDao().insert(newMessages.map(::MessageModel))
    }

    return MediatorResult.Success(
      endOfPaginationReached = newMessages.isEmpty()
    )
  }
}

@Database(entities = [MessageModel::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
  abstract fun messageDao(): MessageDao
}

object Converters {
  @TypeConverter
  fun fromInstant(value: Instant): Long = value.toEpochMilli()

  @TypeConverter
  fun toInstant(value: Long): Instant = Instant.ofEpochMilli(value)
}

@Dao
interface MessageDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(vararg models: MessageModel)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(models: Collection<MessageModel>)

  @Query("SELECT * FROM message WHERE bufferId = :bufferId")
  fun pagingSource(bufferId: SignedIdType): PagingSource<Int, MessageModel>

  @Language("RoomSql")
  @Query("DELETE FROM message WHERE bufferId = :bufferId")
  suspend fun delete(bufferId: SignedIdType)

  @Query("DELETE FROM message")
  suspend fun delete()
}

@Entity(tableName = "message")
data class MessageModel(
  /**
   * Id of the message
   */
  @PrimaryKey
  val messageId: SignedId64Type,
  /**
   * Timestamp at which the message was sent
   */
  val time: Instant,
  /**
   * Message type
   */
  val type: Int,
  /**
   * Set flags on the message
   */
  val flag: Int,
  /**
   * Metadata of the buffer the message was received in
   */
  @ColumnInfo(index = true)
  val bufferId: SignedIdType,
  /**
   * `nick!ident@host` of the sender
   */
  val sender: String,
  /**
   * Channel role prefixes of the sender
   */
  val senderPrefixes: String,
  /**
   * Realname of the sender
   */
  val realName: String,
  /**
   * Avatar of the sender
   */
  val avatarUrl: String,
  /**
   * Message content
   */
  val content: String
) {
  constructor(message: Message) : this(
    messageId = message.messageId.id,
    time = message.time,
    type = message.type.toBits().toInt(),
    flag = message.flag.toBits().toInt(),
    bufferId = message.bufferInfo.bufferId.id,
    sender = message.sender,
    senderPrefixes = message.senderPrefixes,
    realName = message.realName,
    avatarUrl = message.avatarUrl,
    content = message.content
  )

  fun toMessage() = Message(
    messageId = MsgId(messageId),
    time = time,
    type = MessageType.of(type.toUInt()),
    flag = MessageFlag.of(flag.toUInt()),
    bufferInfo = BufferInfo(
      bufferId = BufferId(bufferId)
    ),
    sender = sender,
    senderPrefixes = senderPrefixes,
    realName = realName,
    avatarUrl = avatarUrl,
    content = content
  )
}
