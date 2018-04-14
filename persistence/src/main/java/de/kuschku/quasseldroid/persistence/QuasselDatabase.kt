package de.kuschku.quasseldroid.persistence

import android.arch.lifecycle.LiveData
import android.arch.paging.DataSource
import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.*
import android.arch.persistence.room.migration.Migration
import android.content.Context
import android.support.annotation.IntRange
import de.kuschku.libquassel.protocol.Message_Flag
import de.kuschku.libquassel.protocol.Message_Type
import de.kuschku.libquassel.protocol.MsgId
import de.kuschku.quasseldroid.persistence.QuasselDatabase.DatabaseMessage
import de.kuschku.quasseldroid.persistence.QuasselDatabase.Filtered
import io.reactivex.Flowable
import org.threeten.bp.Instant

@Database(entities = [DatabaseMessage::class, Filtered::class], version = 8)
@TypeConverters(DatabaseMessage.MessageTypeConverters::class)
abstract class QuasselDatabase : RoomDatabase() {
  abstract fun message(): MessageDao
  abstract fun filtered(): FilteredDao

  @Entity(tableName = "message", indices = [Index("bufferId"), Index("ignored")])
  data class DatabaseMessage(
    @PrimaryKey var messageId: Int,
    var time: Instant,
    var type: Int,
    var flag: Int,
    var bufferId: Int,
    var sender: String,
    var senderPrefixes: String,
    var realName: String,
    var avatarUrl: String,
    var content: String,
    var ignored: Boolean
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
      )}, bufferId=$bufferId, sender='$sender', senderPrefixes='$senderPrefixes', realName='$realName', avatarUrl='$avatarUrl', content='$content')"
    }
  }

  @Dao
  interface MessageDao {
    @Query("SELECT * FROM message")
    fun all(): List<DatabaseMessage>

    @Query("SELECT * FROM message WHERE messageId = :messageId")
    fun find(messageId: Int): DatabaseMessage?

    @Query("SELECT * FROM message WHERE bufferId = :bufferId ORDER BY messageId ASC")
    fun findByBufferId(bufferId: Int): List<DatabaseMessage>

    @Query("SELECT * FROM message WHERE bufferId = :bufferId AND type & ~ :type > 0 AND ignored = 0 ORDER BY messageId DESC")
    fun findByBufferIdPaged(bufferId: Int, type: Int): DataSource.Factory<Int, DatabaseMessage>

    @Query("SELECT * FROM message WHERE bufferId = :bufferId ORDER BY messageId DESC LIMIT 1")
    fun findLastByBufferId(bufferId: Int): DatabaseMessage?

    @Query("SELECT * FROM message WHERE bufferId = :bufferId ORDER BY messageId DESC LIMIT 1")
    fun lastMsgId(bufferId: Int): LiveData<DatabaseMessage>

    @Query("SELECT messageId FROM message WHERE bufferId = :bufferId ORDER BY messageId ASC LIMIT 1")
    fun firstMsgId(bufferId: Int): Flowable<MsgId>

    @Query("SELECT messageId FROM message WHERE bufferId = :bufferId AND type & ~ :type > 0 AND ignored = 0 ORDER BY messageId ASC LIMIT 1")
    fun firstVisibleMsgId(bufferId: Int, type: Int): MsgId?

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

    @Query(
      "SELECT filtered FROM filtered WHERE bufferId = :bufferId AND accountId = :accountId UNION SELECT 0 as filtered ORDER BY filtered DESC LIMIT 1"
    )
    fun listenRx(accountId: Long, bufferId: Int): Flowable<Int>

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
              },
              object : Migration(3, 4) {
                override fun migrate(database: SupportSQLiteDatabase) {
                  database.execSQL(
                    "ALTER TABLE message ADD followUp INT DEFAULT 0 NOT NULL;"
                  )
                }
              },
              object : Migration(4, 5) {
                override fun migrate(database: SupportSQLiteDatabase) {
                  database.execSQL("drop table message;")
                  database.execSQL("create table message (messageId INTEGER not null primary key, time INTEGER not null, type INTEGER not null, flag INTEGER not null, bufferId INTEGER not null, sender TEXT not null, senderPrefixes TEXT not null, content TEXT not null);")
                }
              },
              object : Migration(5, 6) {
                override fun migrate(database: SupportSQLiteDatabase) {
                  database.execSQL("drop table message;")
                  database.execSQL("create table message (messageId INTEGER not null primary key, time INTEGER not null, type INTEGER not null, flag INTEGER not null, bufferId INTEGER not null, sender TEXT not null, senderPrefixes TEXT not null, realName TEXT not null, avatarUrl TEXT not null, content TEXT not null);")
                }
              },
              object : Migration(6, 7) {
                override fun migrate(database: SupportSQLiteDatabase) {
                  database.execSQL("drop table message;")
                  database.execSQL("create table message (messageId INTEGER not null primary key, time INTEGER not null, type INTEGER not null, flag INTEGER not null, bufferId INTEGER not null, sender TEXT not null, senderPrefixes TEXT not null, realName TEXT not null, avatarUrl TEXT not null, content TEXT not null, ignored INTEGER not null);")
                }
              },
              object : Migration(7, 8) {
                override fun migrate(database: SupportSQLiteDatabase) {
                  database.execSQL("CREATE INDEX index_message_bufferId ON message(bufferId);")
                  database.execSQL("CREATE INDEX index_message_ignored ON message(ignored);")
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
