package de.kuschku.quasseldroid_ng.persistence

import android.arch.persistence.room.*
import android.content.Context
import android.support.annotation.IntRange
import de.kuschku.libquassel.protocol.Message_Flag
import de.kuschku.libquassel.protocol.Message_Type
import org.threeten.bp.Instant

@Database(entities = arrayOf(QuasselDatabase.Buffer::class, QuasselDatabase.Network::class,
                             QuasselDatabase.DatabaseMessage::class),
          version = 2)
@TypeConverters(QuasselDatabase.DatabaseMessage.MessageTypeConverters::class)
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

  @Entity(tableName = "message")
  open class DatabaseMessage(
    @PrimaryKey var messageId: Int,
    var time: Instant,
    var type: Int,
    var flag: Int,
    var bufferId: Int,
    var sender: String,
    var senderPrefixes: String,
    var content: String
  ) {
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
    fun find(messageId: Int): DatabaseMessage

    @Query("SELECT * FROM message WHERE bufferId = :bufferId")
    fun findByBufferId(bufferId: Int): List<DatabaseMessage>

    @Query("SELECT * FROM message WHERE bufferId = :bufferId ORDER BY messageId DESC LIMIT 1")
    fun findLastByBufferId(bufferId: Int): DatabaseMessage

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun save(entity: DatabaseMessage)

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
