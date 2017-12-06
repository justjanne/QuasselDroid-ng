package de.kuschku.quasseldroid_ng.persistence

import android.arch.paging.LivePagedListProvider
import android.arch.persistence.room.*
import android.content.Context
import android.support.annotation.IntRange
import de.kuschku.libquassel.protocol.Message_Flag
import de.kuschku.libquassel.protocol.Message_Type
import org.threeten.bp.Instant

@Database(entities = arrayOf(QuasselDatabase.DatabaseMessage::class), version = 2)
@TypeConverters(QuasselDatabase.DatabaseMessage.MessageTypeConverters::class)
abstract class QuasselDatabase : RoomDatabase() {
  abstract fun message(): MessageDao

  @Entity(tableName = "message")
  data class DatabaseMessage(
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
      fun convertInstant(value: Long): Instant = Instant.ofEpochMilli(value)

      @TypeConverter
      fun convertInstant(value: Instant) = value.toEpochMilli()
    }

    override fun toString(): String {
      return "Message(messageId=$messageId, time=$time, type=${Message_Type.of(
        type)}, flag=${Message_Flag.of(
        flag)}, bufferId=$bufferId, sender='$sender', senderPrefixes='$senderPrefixes', content='$content')"
    }
  }

  @Dao
  interface MessageDao {
    @Query("SELECT * FROM message WHERE messageId = :messageId")
    fun find(messageId: Int): DatabaseMessage?

    @Query("SELECT * FROM message WHERE bufferId = :bufferId ORDER BY messageId ASC")
    fun findByBufferId(bufferId: Int): List<DatabaseMessage>

    @Query("SELECT * FROM message WHERE bufferId = :bufferId ORDER BY messageId ASC")
    fun findByBufferIdPaged(bufferId: Int): LivePagedListProvider<Int, DatabaseMessage>

    @Query("SELECT * FROM message WHERE bufferId = :bufferId ORDER BY messageId DESC LIMIT 1")
    fun findLastByBufferId(bufferId: Int): DatabaseMessage?

    @Query("SELECT * FROM message WHERE bufferId = :bufferId ORDER BY messageId ASC LIMIT 1")
    fun findFirstByBufferId(bufferId: Int): DatabaseMessage?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(vararg entities: DatabaseMessage)

    @Query("UPDATE message SET bufferId = :bufferId1 WHERE bufferId = :bufferId2")
    fun merge(@IntRange(from = 0) bufferId1: Int, @IntRange(from = 0) bufferId2: Int)

    @Query("DELETE FROM message")
    fun clearMessages()

    @Query("DELETE FROM message WHERE bufferId = :bufferId")
    fun clearMessages(@IntRange(from = 0) bufferId: Int)

    @Query("DELETE FROM message WHERE bufferId = :bufferId AND messageId >= :first AND messageId <= :last")
    fun clearMessages(@IntRange(from = 0) bufferId: Int, first: Int, last: Int)
  }

  object Creator {
    private var database: QuasselDatabase? = null

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

fun QuasselDatabase.MessageDao.clearMessages(@IntRange(from = 0) bufferId: Int, idRange: kotlin.ranges.IntRange) {
  this.clearMessages(bufferId, idRange.first, idRange.last)
}