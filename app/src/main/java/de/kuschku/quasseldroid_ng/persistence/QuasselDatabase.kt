package de.kuschku.quasseldroid_ng.persistence

import android.arch.lifecycle.LiveData
import android.arch.paging.DataSource
import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.*
import android.arch.persistence.room.migration.Migration
import android.content.Context
import android.support.annotation.IntRange
import android.support.v7.recyclerview.extensions.DiffCallback
import de.kuschku.libquassel.protocol.Message_Flag
import de.kuschku.libquassel.protocol.Message_Type
import de.kuschku.quasseldroid_ng.persistence.QuasselDatabase.DatabaseMessage
import de.kuschku.quasseldroid_ng.persistence.QuasselDatabase.Filtered
import org.threeten.bp.Instant

@Database(entities = [DatabaseMessage::class, Filtered::class], version = 3)
@TypeConverters(DatabaseMessage.MessageTypeConverters::class)
abstract class QuasselDatabase : RoomDatabase() {
  abstract fun message(): MessageDao
  abstract fun filtered(): FilteredDao

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
        type
      )}, flag=${Message_Flag.of(
        flag
      )}, bufferId=$bufferId, sender='$sender', senderPrefixes='$senderPrefixes', content='$content')"
    }

    object MessageDiffCallback : DiffCallback<DatabaseMessage>() {
      override fun areContentsTheSame(oldItem: QuasselDatabase.DatabaseMessage,
                                      newItem: QuasselDatabase.DatabaseMessage) = oldItem == newItem

      override fun areItemsTheSame(oldItem: QuasselDatabase.DatabaseMessage,
                                   newItem: QuasselDatabase.DatabaseMessage) = oldItem.messageId == newItem.messageId
    }
  }

  @Dao
  interface MessageDao {
    @Query("SELECT * FROM message WHERE messageId = :messageId")
    fun find(messageId: Int): DatabaseMessage?

    @Query("SELECT * FROM message WHERE bufferId = :bufferId ORDER BY messageId ASC")
    fun findByBufferId(bufferId: Int): List<DatabaseMessage>

    @Query(
      "SELECT * FROM message WHERE bufferId = :bufferId AND type & ~ :type > 0 ORDER BY messageId DESC"
    )
    fun findByBufferIdPaged(bufferId: Int, type: Int): DataSource.Factory<Int, DatabaseMessage>

    @Query("SELECT * FROM message WHERE bufferId = :bufferId ORDER BY messageId DESC LIMIT 1")
    fun findLastByBufferId(bufferId: Int): DatabaseMessage?

    @Query("SELECT * FROM message WHERE bufferId = :bufferId ORDER BY messageId ASC LIMIT 1")
    fun findFirstByBufferId(bufferId: Int): DatabaseMessage?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(vararg entities: DatabaseMessage)

    @Query("UPDATE message SET bufferId = :bufferId1 WHERE bufferId = :bufferId2")
    fun merge(@IntRange(from = 0) bufferId1: Int, @IntRange(from = 0) bufferId2: Int)

    @Query("SELECT count(*) FROM message WHERE bufferId = :bufferId")
    fun bufferSize(@IntRange(from = 0) bufferId: Int): Int

    @Query("DELETE FROM message")
    fun clearMessages()

    @Query("DELETE FROM message WHERE bufferId = :bufferId")
    fun clearMessages(@IntRange(from = 0) bufferId: Int)

    @Query(
      "DELETE FROM message WHERE bufferId = :bufferId AND messageId >= :first AND messageId <= :last"
    )
    fun clearMessages(@IntRange(from = 0) bufferId: Int, first: Int, last: Int)
  }

  @Entity(tableName = "filtered", primaryKeys = ["accountId", "bufferId"])
  data class Filtered(
    var accountId: Long,
    var bufferId: Int,
    var filtered: Int
  )

  @Dao
  interface FilteredDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun replace(vararg entities: Filtered)

    @Query(
      "SELECT filtered FROM filtered WHERE bufferId = :bufferId AND accountId = :accountId UNION SELECT 0 as filtered ORDER BY filtered DESC LIMIT 1"
    )
    fun get(accountId: Long, bufferId: Int): Int?

    @Query(
      "SELECT filtered FROM filtered WHERE bufferId = :bufferId AND accountId = :accountId UNION SELECT 0 as filtered ORDER BY filtered DESC LIMIT 1"
    )
    fun listen(accountId: Long, bufferId: Int): LiveData<Int>

    @Query("SELECT * FROM filtered WHERE accountId = :accountId")
    fun listen(accountId: Long): LiveData<List<Filtered>>

    @Query("DELETE FROM filtered")
    fun clear()

    @Query("DELETE FROM filtered WHERE accountId = :accountId")
    fun clear(accountId: Long)

    @Query("DELETE FROM filtered WHERE bufferId = :bufferId AND accountId = :accountId")
    fun clear(accountId: Long, bufferId: Int)
  }

  object Creator {
    private var database: QuasselDatabase? = null

    // For Singleton instantiation
    private val LOCK = Any()

    fun init(context: Context): QuasselDatabase {
      if (database == null) {
        synchronized(LOCK) {
          if (database == null) {
            database = Room.databaseBuilder(
              context.applicationContext,
              QuasselDatabase::class.java, DATABASE_NAME
            ).addMigrations(
              object : Migration(2, 3) {
                override fun migrate(database: SupportSQLiteDatabase) {
                  database.execSQL(
                    "CREATE TABLE filtered(bufferId INTEGER, accountId INTEGER, filtered INTEGER, PRIMARY KEY(accountId, bufferId));"
                  )
                }
              }
            ).build()
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

fun QuasselDatabase.MessageDao.clearMessages(
  @IntRange(from = 0) bufferId: Int,
  idRange: kotlin.ranges.IntRange
) {
  this.clearMessages(bufferId, idRange.first, idRange.last)
}