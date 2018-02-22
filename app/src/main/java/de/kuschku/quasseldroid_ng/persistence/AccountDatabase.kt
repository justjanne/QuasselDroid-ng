package de.kuschku.quasseldroid_ng.persistence

import android.arch.paging.DataSource
import android.arch.persistence.room.*
import android.content.Context

@Database(entities = [(AccountDatabase.Account::class)], version = 1)
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
    var lastUsed: Long
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
    fun all(): DataSource.Factory<Int, Account>

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
            )
              .build()
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
