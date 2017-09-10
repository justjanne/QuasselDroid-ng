package de.kuschku.quasseldroid_ng.persistence

import android.arch.persistence.room.*
import android.content.Context
import android.support.annotation.IntRange
import de.kuschku.quasseldroid_ng.protocol.Message_Flag
import de.kuschku.quasseldroid_ng.protocol.Message_Flags
import de.kuschku.quasseldroid_ng.protocol.Message_Type
import de.kuschku.quasseldroid_ng.protocol.Message_Types
import de.kuschku.quasseldroid_ng.quassel.BufferInfo
import de.kuschku.quasseldroid_ng.util.Flag
import de.kuschku.quasseldroid_ng.util.Flags
import org.threeten.bp.Instant

@Database(entities = arrayOf(QuasselDatabase.Buffer::class, QuasselDatabase.Network::class,
                             QuasselDatabase.Message::class),
          version = 2)
@TypeConverters(QuasselDatabase.Message.MessageTypeConverters::class)
abstract class QuasselDatabase : RoomDatabase() {
  abstract fun networks(): NetworkDao
  abstract fun buffers(): BufferDao
  abstract fun message(): MessageDao

  @Entity(indices = arrayOf(Index("networkId")))
  class Buffer(
    @PrimaryKey var bufferId: Int,
    var networkId: Int,
    var type: Int,
    var groupId: Int,
    var bufferName: String
  )

  class RawMessage(
    messageId: Int,
    time: Instant,
    type: Message_Types,
    flag: Message_Flags,
    var bufferInfo: BufferInfo,
    sender: String,
    senderPrefixes: String,
    content: String
  ) : Message(messageId, time, type.toInt(), flag.toInt(), bufferInfo.bufferId, sender,
              senderPrefixes, content)

  @Entity
  open class Message(
    @PrimaryKey var messageId: Int,
    var time: Instant,
    var type: Int,
    var flag: Int,
    var bufferId: Int,
    var sender: String,
    var senderPrefixes: String,
    var content: String
  ) {
    enum class MessageType(override val bit: Int) : Flag<MessageType> {
      Plain(0x00001),
      Notice(0x00002),
      Action(0x00004),
      Nick(0x00008),
      Mode(0x00010),
      Join(0x00020),
      Part(0x00040),
      Quit(0x00080),
      Kick(0x00100),
      Kill(0x00200),
      Server(0x00400),
      Info(0x00800),
      Error(0x01000),
      DayChange(0x02000),
      Topic(0x04000),
      NetsplitJoin(0x08000),
      NetsplitQuit(0x10000),
      Invite(0x20000),
      Markerline(0x40000);

      companion object : Flags.Factory<MessageType> {
        override val NONE = MessageType.of()
        override fun of(bit: Int) = Flags.of<MessageType>(bit)
        override fun of(vararg flags: MessageType) = Flags.of(*flags)
      }
    }

    enum class MessageFlag(override val bit: Int) : Flag<MessageFlag> {
      Self(0x01),
      Highlight(0x02),
      Redirected(0x04),
      ServerMsg(0x08),
      Backlog(0x80);

      companion object : Flags.Factory<MessageFlag> {
        override val NONE = MessageFlag.of()
        override fun of(bit: Int) = Flags.of<MessageFlag>(bit)
        override fun of(vararg flags: MessageFlag) = Flags.of(*flags)
      }
    }

    class MessageTypeConverters {
      @TypeConverter
      fun convertInstant(value: Long) = Instant.ofEpochMilli(value)

      @TypeConverter
      fun convertInstant(value: Instant) = value.toEpochMilli()
    }

    override fun toString(): String {
      return "Message(messageId=$messageId, time=$time, type=${Message_Type.of(
        type)}, flag=${Message_Flag.of(
        flag)}, bufferId=$bufferId, sender='$sender', senderPrefixes='$senderPrefixes', content='$content')"
    }
  }

  @Entity
  class Network(
    @PrimaryKey var networkId: Int,
    var networkName: String
  )

  @Dao
  interface NetworkDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(vararg entities: QuasselDatabase.Network)

    @Query("SELECT * FROM network WHERE networkId = :networkId")
    fun findById(networkId: Int): QuasselDatabase.Network

    @Query("SELECT * FROM network")
    fun all(): List<QuasselDatabase.Network>

    @Delete
    fun delete(Network: QuasselDatabase.Network)

    @Query("DELETE FROM network")
    fun clear()
  }

  @Dao
  interface BufferDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(vararg entities: QuasselDatabase.Buffer)

    @Query("SELECT * FROM buffer WHERE networkId = :bufferId")
    fun findById(bufferId: Int): QuasselDatabase.Buffer

    @Query("SELECT * FROM buffer WHERE networkId = :networkId")
    fun findByNetwork(networkId: Int): List<QuasselDatabase.Buffer>

    @Query("DELETE FROM buffer WHERE networkId = :networkId")
    fun deleteByNetwork(networkId: Int)

    @Query("SELECT * FROM buffer")
    fun all(): List<QuasselDatabase.Buffer>

    @Delete
    fun delete(buffer: QuasselDatabase.Buffer)

    @Query("DELETE FROM buffer")
    fun clear()
  }

  @Dao
  interface MessageDao {
    @Query("SELECT * FROM message WHERE messageId = :messageId")
    fun find(messageId: Int): Message

    @Query("SELECT * FROM message WHERE bufferId = :bufferId")
    fun findByBufferId(bufferId: Int): List<Message>

    @Query("SELECT * FROM message WHERE bufferId = :bufferId ORDER BY messageId DESC LIMIT 1")
    fun findLastByBufferId(bufferId: Int): Message

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun save(entity: Message)

    @Query("UPDATE message SET bufferId = :bufferId1 WHERE bufferId = :bufferId2")
    fun merge(@IntRange(from = 0) bufferId1: Int, @IntRange(from = 0) bufferId2: Int)

    @Query("DELETE FROM message WHERE bufferId = :bufferId")
    fun clearBuffer(@IntRange(from = 0) bufferId: Int)
  }

  object Creator {
    private var database: QuasselDatabase? = null
      private set

    // For Singleton instantiation
    private val LOCK = Any()

    fun init(context: Context): QuasselDatabase {
      if (database == null) {
        synchronized(LOCK) {
          if (database == null) {
            database = Room.databaseBuilder(context.applicationContext,
                                            QuasselDatabase::class.java, DATABASE_NAME)
              .fallbackToDestructiveMigration()
              .build()
          }
        }
      }
      return database!!
    }
  }

  companion object {
    const val DATABASE_NAME = "persistence-clientData"
  }
}
