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
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import de.kuschku.quasseldroid.persistence.dao.AccountDao
import de.kuschku.quasseldroid.persistence.models.Account

@Database(entities = [(Account::class)], version = 4)
abstract class AccountDatabase : RoomDatabase() {
  abstract fun accounts(): AccountDao

  object Creator {
    private var database: AccountDatabase? = null

    // For Singleton instantiation
    private val LOCK = Any()

    fun init(context: Context): AccountDatabase {
      if (database == null) {
        synchronized(LOCK) {
          if (database == null) {
            database = Room.databaseBuilder(
              context.applicationContext,
              AccountDatabase::class.java,
              DATABASE_NAME
            ).addMigrations(
              object : Migration(1, 2) {
                override fun migrate(database: SupportSQLiteDatabase) {
                  database.execSQL("ALTER TABLE account ADD COLUMN acceptedMissingFeatures INTEGER NOT NULL DEFAULT 0;")
                }
              },
              object : Migration(2, 3) {
                override fun migrate(database: SupportSQLiteDatabase) {
                  database.execSQL("ALTER TABLE account ADD COLUMN defaultFiltered INTEGER NOT NULL DEFAULT 0;")
                }
              },
              object : Migration(3, 4) {
                override fun migrate(database: SupportSQLiteDatabase) {
                  database.execSQL("ALTER TABLE account ADD COLUMN requireSsl INTEGER NOT NULL DEFAULT 0;")
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
    const val DATABASE_NAME = "persistence-accounts"
  }
}
