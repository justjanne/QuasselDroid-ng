/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2019 Janne Koschinski
 * Copyright (c) 2019 The Quassel Project
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package de.kuschku.quasseldroid.persistence.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import de.kuschku.quasseldroid.persistence.dao.*
import de.kuschku.quasseldroid.persistence.models.*
import de.kuschku.quasseldroid.persistence.util.MessageTypeConverter

@Database(entities = [MessageData::class, Filtered::class, SslValidityWhitelistEntry::class, SslHostnameWhitelistEntry::class, NotificationData::class],
          version = 19)
@TypeConverters(MessageTypeConverter::class)
abstract class QuasselDatabase : RoomDatabase() {
  abstract fun message(): MessageDao
  abstract fun filtered(): FilteredDao
  abstract fun validityWhitelist(): SslValidityWhitelistDao
  abstract fun hostnameWhitelist(): SslHostnameWhitelistDao
  abstract fun notifications(): NotificationDao

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
              QuasselDatabase::class.java,
              DATABASE_NAME
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
