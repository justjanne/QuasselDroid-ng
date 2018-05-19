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

import android.arch.lifecycle.LiveData
import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.*
import android.arch.persistence.room.migration.Migration
import android.content.Context

@Database(entities = [(AccountDatabase.Account::class)], version = 2)
abstract class AccountDatabase : RoomDatabase() {
  abstract fun accounts(): AccountDao

  @Entity
  data class Account(
    @PrimaryKey(autoGenerate = true)
    var id: Long,
    var host: String,
    var port: Int,
    var user: String,
    var pass: String,
    var name: String,
    var lastUsed: Long,
    var acceptedMissingFeatures: Boolean
  )

  @Dao
  interface AccountDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(vararg entities: AccountDatabase.Account)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun create(vararg entities: AccountDatabase.Account): Array<Long>

    @Query("SELECT * FROM account WHERE id = :id")
    fun findById(id: Long): AccountDatabase.Account?

    @Query("SELECT * FROM account ORDER BY lastUsed DESC")
    fun all(): LiveData<List<Account>>

    @Delete
    fun delete(account: AccountDatabase.Account)

    @Query("DELETE FROM account")
    fun clear()
  }

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
              AccountDatabase::class.java, DATABASE_NAME
            ).addMigrations(
              object : Migration(1, 2) {
                override fun migrate(database: SupportSQLiteDatabase) {
                  database.execSQL("ALTER TABLE account ADD COLUMN acceptedMissingFeatures INTEGER NOT NULL DEFAULT 0;")
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
