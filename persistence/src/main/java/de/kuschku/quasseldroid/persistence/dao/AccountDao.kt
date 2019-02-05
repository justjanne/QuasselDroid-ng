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

package de.kuschku.quasseldroid.persistence.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import de.kuschku.quasseldroid.persistence.models.Account
import io.reactivex.Flowable

@Dao
interface AccountDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun save(vararg entities: Account)

  @Insert(onConflict = OnConflictStrategy.IGNORE)
  fun create(vararg entities: Account): Array<Long>

  @Query("SELECT * FROM account WHERE id = :id")
  fun findById(id: Long): Account?

  @Query("SELECT * FROM account WHERE id = :id")
  fun listen(id: Long): LiveData<Account?>

  @Query("SELECT IFNULL(t.defaultFiltered, :defaultValue) FROM (SELECT defaultFiltered FROM account WHERE id = :id UNION SELECT NULL ORDER BY defaultFiltered DESC LIMIT 1) t")
  fun listenDefaultFiltered(id: Long, defaultValue: Int): Flowable<Int>

  @Query("SELECT * FROM account ORDER BY lastUsed DESC")
  fun all(): LiveData<List<Account>>

  @Delete
  fun delete(account: Account)

  @Query("UPDATE account SET defaultFiltered = :defaultFiltered WHERE id = :id")
  fun setFiltered(id: Long, defaultFiltered: Int)

  @Query("DELETE FROM account")
  fun clear()
}
