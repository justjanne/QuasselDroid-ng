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

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.*
import android.arch.persistence.room.migration.Migration
import android.content.Context

@Database(entities = [(LegacyAccountDatabase.Account::class)], version = 4)
abstract class LegacyAccountDatabase : RoomDatabase() {
  abstract fun accounts(): AccountDao

  @Entity
  data class Account(
    @PrimaryKey
    var id: Long,
    var host: String,
    var port: Int,
    var user: String,
    var pass: String,
    var name: String
  )

  @Dao
  interface AccountDao {
    @Query("SELECT * FROM account")
    fun all(): List<Account>
  }

  object Creator {
    private var database: LegacyAccountDatabase? = null

    // For Singleton instantiation
    private val LOCK = Any()

    fun init(context: Context): LegacyAccountDatabase {
      if (database == null) {
        synchronized(LOCK) {
          if (database == null) {
            database = Room.databaseBuilder(
              context.applicationContext,
              LegacyAccountDatabase::class.java, DATABASE_NAME
            ).addMigrations(
              object : Migration(0, 1) {
                override fun migrate(database: SupportSQLiteDatabase) {
                  database.execSQL("DROP TABLE IF EXISTS cores")
                  database.execSQL("create table cores (_id integer primary key autoincrement, name text not null, server text not null, port integer not null);")

                  database.execSQL("DROP TABLE IF EXISTS user")
                  database.execSQL("CREATE TABLE user(userid integer primary key autoincrement, username text not null, password text not null, coreid integer not null unique, foreign key(coreid) references cores(_id) ON DELETE CASCADE ON UPDATE CASCADE)")
                }
              },
              object : Migration(1, 2) {
                override fun migrate(database: SupportSQLiteDatabase) {
                  database.execSQL("DROP TABLE IF EXISTS cores")
                  database.execSQL("create table cores (_id integer primary key autoincrement, name text not null, server text not null, port integer not null);")

                  database.execSQL("DROP TABLE IF EXISTS user")
                  database.execSQL("CREATE TABLE user(userid integer primary key autoincrement, username text not null, password text not null, coreid integer not null unique, foreign key(coreid) references cores(_id) ON DELETE CASCADE ON UPDATE CASCADE)")
                }
              },
              object : Migration(2, 3) {
                override fun migrate(database: SupportSQLiteDatabase) = Unit
              },
              object : Migration(3, 4) {
                override fun migrate(database: SupportSQLiteDatabase) {
                  database.execSQL("CREATE TABLE IF NOT EXISTS `Account` (`id` INTEGER NOT NULL, `host` TEXT NOT NULL, `port` INTEGER NOT NULL, `user` TEXT NOT NULL, `pass` TEXT NOT NULL, `name` TEXT NOT NULL, PRIMARY KEY(`id`))")
                  database.execSQL("INSERT INTO Account (id, host, port, user, pass, name) SELECT _id AS id, server, port, coalesce(username, ''), coalesce(password, ''), name FROM cores LEFT JOIN user ON user.coreid = cores._id")

                  database.execSQL("DROP TABLE IF EXISTS cores")
                  database.execSQL("DROP TABLE IF EXISTS user")
                }
              }
            ).allowMainThreadQueries().build()
          }
        }
      }
      return database!!
    }
  }

  companion object {
    const val DATABASE_NAME = "data"
  }
}
