/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Koschinski
 * Copyright (c) 2018 The Quassel Project
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.quasseldroid.persistence

import android.content.Context
import androidx.annotation.IntRange
import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import de.kuschku.libquassel.protocol.*
import de.kuschku.quasseldroid.persistence.QuasselDatabase.*
import io.reactivex.Flowable
import org.threeten.bp.Instant

@Database(entities = [MessageData::class, Filtered::class, SslValidityWhitelistEntry::class, SslHostnameWhitelistEntry::class, NotificationData::class],
          version = 19)
@TypeConverters(MessageTypeConverter::class)
abstract class QuasselDatabase : RoomDatabase() {
  abstract fun message(): MessageDao
  abstract fun filtered(): FilteredDao
  abstract fun validityWhitelist(): SslValidityWhitelistDao
  abstract fun hostnameWhitelist(): SslHostnameWhitelistDao
  abstract fun notifications(): NotificationDao

  @Entity(tableName = "message", indices = [Index("bufferId"), Index("ignored")])
  data class MessageData(
    @PrimaryKey var messageId: MsgId,
    var time: Instant,
    var type: Message_Types,
    var flag: Message_Flags,
    var bufferId: BufferId,
    var networkId: NetworkId,
    var sender: String,
    var senderPrefixes: String,
    var realName: String,
    var avatarUrl: String,
    var content: String,
    var ignored: Boolean
  )

  @Dao
  interface MessageDao {
    @Query("SELECT * FROM message")
    fun all(): List<MessageData>

    @Query("SELECT DISTINCT bufferId FROM message")
    fun buffers(): List<BufferId>

    @Query("SELECT * FROM message WHERE messageId = :messageId")
    fun find(messageId: Int): MessageData?

    @Query("SELECT * FROM message WHERE bufferId = :bufferId ORDER BY messageId ASC")
    fun findByBufferId(bufferId: Int): List<MessageData>

    @Query("SELECT * FROM message WHERE bufferId = :bufferId AND type & ~ :type > 0 AND ignored = 0 ORDER BY messageId DESC")
    fun findByBufferIdPaged(bufferId: Int, type: Int): DataSource.Factory<Int, MessageData>

    @Query("SELECT * FROM message WHERE bufferId = :bufferId ORDER BY messageId DESC LIMIT 1")
    fun findLastByBufferId(bufferId: Int): MessageData?

    @Query("SELECT * FROM message WHERE bufferId = :bufferId ORDER BY messageId DESC LIMIT 1")
    fun lastMsgId(bufferId: Int): LiveData<MessageData>

    @Query("SELECT messageId FROM message WHERE bufferId = :bufferId ORDER BY messageId ASC LIMIT 1")
    fun firstMsgId(bufferId: Int): Flowable<MsgId>

    @Query("SELECT messageId FROM message WHERE bufferId = :bufferId AND type & ~ :type > 0 AND ignored = 0 ORDER BY messageId ASC LIMIT 1")
    fun firstVisibleMsgId(bufferId: Int, type: Int): MsgId?

    @Query("SELECT * FROM message WHERE bufferId = :bufferId ORDER BY messageId ASC LIMIT 1")
    fun findFirstByBufferId(bufferId: Int): MessageData?

    @Query("SELECT EXISTS(SELECT 1 FROM message WHERE bufferId = :bufferId AND type & ~ :type > 0 AND ignored = 0)")
    fun hasVisibleMessages(bufferId: Int, type: Int): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(vararg entities: MessageData)

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
    var bufferId: BufferId,
    var filtered: Int
  )

  @Dao
  interface FilteredDao {
    @Query("SELECT DISTINCT bufferId FROM filtered WHERE accountId = :accountId")
    fun buffers(accountId: Long): List<BufferId>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun replace(vararg entities: Filtered)

    @Query("UPDATE filtered SET filtered = :filtered WHERE accountId = :accountId AND bufferId = :bufferId")
    fun setFiltered(accountId: Long, bufferId: Int, filtered: Int)

    @Query("SELECT filtered FROM filtered WHERE bufferId = :bufferId AND accountId = :accountId UNION SELECT :defaultValue as filtered ORDER BY filtered DESC LIMIT 1")
    fun get(accountId: Long, bufferId: Int, defaultValue: Int): Int

    @Query("SELECT filtered FROM filtered WHERE bufferId = :bufferId AND accountId = :accountId UNION SELECT :defaultValue as filtered ORDER BY filtered DESC LIMIT 1")
    fun listen(accountId: Long, bufferId: Int, defaultValue: Int): LiveData<Int>

    @Query("SELECT * FROM filtered WHERE accountId = :accountId")
    fun listen(accountId: Long): LiveData<List<Filtered>>

    @Query("DELETE FROM filtered")
    fun clear()

    @Query("DELETE FROM filtered WHERE accountId = :accountId")
    fun clear(accountId: Long)

    @Query("DELETE FROM filtered WHERE bufferId = :bufferId AND accountId = :accountId")
    fun clear(accountId: Long, bufferId: Int)
  }

  @Entity(tableName = "ssl_validity_whitelist")
  data class SslValidityWhitelistEntry(
    @PrimaryKey
    var fingerprint: String,
    var ignoreDate: Boolean
  )

  @Dao
  interface SslValidityWhitelistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(vararg entities: SslValidityWhitelistEntry)

    @Query("SELECT * FROM ssl_validity_whitelist")
    fun all(): List<SslValidityWhitelistEntry>

    @Query("SELECT * FROM ssl_validity_whitelist WHERE fingerprint = :fingerprint")
    fun find(fingerprint: String): SslValidityWhitelistEntry?

    @Query("DELETE FROM ssl_validity_whitelist WHERE fingerprint = :fingerprint")
    fun delete(fingerprint: String)

    @Query("DELETE FROM ssl_validity_whitelist")
    fun clear()
  }

  @Entity(tableName = "ssl_hostname_whitelist", primaryKeys = ["fingerprint", "hostname"])
  data class SslHostnameWhitelistEntry(
    var fingerprint: String,
    var hostname: String
  )

  @Dao
  interface SslHostnameWhitelistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(vararg entities: SslHostnameWhitelistEntry)

    @Query("SELECT * FROM ssl_hostname_whitelist")
    fun all(): List<SslHostnameWhitelistEntry>

    @Query("SELECT * FROM ssl_hostname_whitelist WHERE fingerprint = :fingerprint AND hostname = :hostname")
    fun find(fingerprint: String, hostname: String): SslHostnameWhitelistEntry?

    @Query("DELETE FROM ssl_hostname_whitelist WHERE fingerprint = :fingerprint AND hostname = :hostname")
    fun delete(fingerprint: String, hostname: String)

    @Query("DELETE FROM ssl_hostname_whitelist")
    fun clear()
  }

  @Entity(tableName = "notification", indices = [Index("bufferId")])
  data class NotificationData(
    @PrimaryKey var messageId: MsgId,
    var creationTime: Instant,
    var time: Instant,
    var type: Message_Types,
    var flag: Message_Flags,
    var bufferId: BufferId,
    var bufferName: String,
    var bufferType: Buffer_Types,
    var networkId: NetworkId,
    var networkName: String,
    var sender: String,
    var senderPrefixes: String,
    var realName: String,
    var avatarUrl: String,
    var content: String,
    var ownNick: String,
    var ownIdent: String,
    var ownRealName: String,
    var ownAvatarUrl: String,
    var hidden: Boolean
  )

  @Dao
  interface NotificationDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun save(vararg entities: NotificationData)

    @Query("SELECT DISTINCT bufferId FROM notification")
    fun buffers(): List<BufferId>

    @Query("SELECT * FROM notification WHERE hidden = 0 ORDER BY time ASC")
    fun all(): List<NotificationData>

    @Query("SELECT * FROM notification WHERE bufferId = :bufferId AND hidden = 0 ORDER BY time ASC")
    fun all(bufferId: BufferId): List<NotificationData>

    @Query("UPDATE notification SET hidden = 1 WHERE bufferId = :bufferId AND messageId <= :messageId")
    fun markHidden(bufferId: BufferId, messageId: MsgId)

    @Query("UPDATE notification SET hidden = 1 WHERE bufferId = :bufferId AND flag & 2 = 0")
    fun markHiddenNormal(bufferId: BufferId)

    @Query("DELETE FROM notification WHERE bufferId = :bufferId AND messageId <= :messageId")
    fun markRead(bufferId: BufferId, messageId: MsgId)

    @Query("DELETE FROM notification WHERE bufferId = :bufferId AND flag & 2 = 0")
    fun markReadNormal(bufferId: BufferId)

    @Query("DELETE FROM notification")
    fun clear()
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
              },
              object : Migration(8, 9) {
                override fun migrate(database: SupportSQLiteDatabase) {
                  database.execSQL("create table ssl_exception (accountId INTEGER not null, certificateFingerprint TEXT not null, ignoreValidityDate INTEGER not null, primary key(accountId, certificateFingerprint));")
                }
              },
              object : Migration(9, 10) {
                override fun migrate(database: SupportSQLiteDatabase) {
                  database.execSQL("drop table ssl_exception;")
                  database.execSQL("create table ssl_exception (accountId INTEGER not null, hostName TEXT not null, certificateFingerprint TEXT not null, ignoreValidityDate INTEGER not null, primary key(accountId, hostName, certificateFingerprint));")
                }
              },
              object : Migration(10, 11) {
                override fun migrate(database: SupportSQLiteDatabase) {
                  database.execSQL("drop table ssl_exception;")
                  database.execSQL("create table ssl_exception (accountId INTEGER not null, certificateFingerprint TEXT not null, ignoreValidityDate INTEGER not null, primary key(accountId, certificateFingerprint));")
                }
              },
              object : Migration(11, 12) {
                override fun migrate(database: SupportSQLiteDatabase) {
                  database.execSQL("drop table ssl_exception;")
                  database.execSQL("create table ssl_validity_whitelist (fingerprint TEXT not null, ignoreDate INTEGER not null, primary key(fingerprint));")
                  database.execSQL("create table ssl_hostname_whitelist (fingerprint TEXT not null, hostname TEXT not null, primary key(fingerprint, hostname));")
                }
              },
              object : Migration(12, 13) {
                override fun migrate(database: SupportSQLiteDatabase) {
                  database.execSQL("drop table ssl_validity_whitelist;")
                  database.execSQL("drop table ssl_hostname_whitelist;")
                  database.execSQL("create table ssl_validity_whitelist (fingerprint TEXT not null, ignoreDate INTEGER not null, primary key(fingerprint));")
                  database.execSQL("create table ssl_hostname_whitelist (fingerprint TEXT not null, hostname TEXT not null, primary key(fingerprint, hostname));")
                }
              },
              object : Migration(13, 14) {
                override fun migrate(database: SupportSQLiteDatabase) {
                  database.execSQL("CREATE TABLE IF NOT EXISTS `notification` (`messageId` INTEGER NOT NULL, `time` INTEGER NOT NULL, `type` INTEGER NOT NULL, `flag` INTEGER NOT NULL, `bufferId` INTEGER NOT NULL, `bufferName` TEXT NOT NULL, `bufferType` INTEGER NOT NULL, `networkId` INTEGER NOT NULL, `sender` TEXT NOT NULL, `senderPrefixes` TEXT NOT NULL, `realName` TEXT NOT NULL, `avatarUrl` TEXT NOT NULL, `content` TEXT NOT NULL, PRIMARY KEY(`messageId`));")
                  database.execSQL("CREATE  INDEX `index_notification_bufferId` ON `notification` (`bufferId`);")
                }
              },
              object : Migration(14, 15) {
                override fun migrate(database: SupportSQLiteDatabase) {
                  database.execSQL("ALTER TABLE message ADD networkId INT DEFAULT 0 NOT NULL;")
                }
              },
              object : Migration(15, 16) {
                override fun migrate(database: SupportSQLiteDatabase) {
                  database.execSQL("ALTER TABLE `notification` ADD `creationTime` INT DEFAULT 0 NOT NULL;")
                }
              },
              object : Migration(16, 17) {
                override fun migrate(database: SupportSQLiteDatabase) {
                  database.execSQL("ALTER TABLE `notification` ADD `ownNick` TEXT DEFAULT '' NOT NULL;")
                  database.execSQL("ALTER TABLE `notification` ADD `ownIdent` TEXT DEFAULT '' NOT NULL;")
                  database.execSQL("ALTER TABLE `notification` ADD `ownRealName` TEXT DEFAULT '' NOT NULL;")
                  database.execSQL("ALTER TABLE `notification` ADD `ownAvatarUrl` TEXT DEFAULT '' NOT NULL;")
                }
              },
              object : Migration(17, 18) {
                override fun migrate(database: SupportSQLiteDatabase) {
                  database.execSQL("ALTER TABLE `notification` ADD `hidden` INT DEFAULT 0 NOT NULL;")
                }
              },
              object : Migration(18, 19) {
                override fun migrate(database: SupportSQLiteDatabase) {
                  database.execSQL("ALTER TABLE `notification` ADD `networkName` TEXT DEFAULT '' NOT NULL;")
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
