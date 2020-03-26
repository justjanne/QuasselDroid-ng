/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2020 Janne Mareike Koschinski
 * Copyright (c) 2020 The Quassel Project
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
import de.kuschku.quasseldroid.persistence.util.AccountId
import io.reactivex.Flowable

@Dao
interface AccountDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun save(vararg entities: Account)

  @Insert(onConflict = OnConflictStrategy.IGNORE)
  fun _create(vararg entities: Account): Array<Long>

  @Query("SELECT * FROM account WHERE id = :id")
  fun _findById(id: Long): Account?

  @Query("SELECT * FROM account WHERE id = :id")
  fun _listen(id: Long): LiveData<Account?>

  @Query("SELECT IFNULL(t.defaultFiltered, :defaultValue) FROM (SELECT defaultFiltered FROM account WHERE id = :id UNION SELECT NULL ORDER BY defaultFiltered DESC LIMIT 1) t")
  fun _listenDefaultFiltered(id: Long, defaultValue: Int): Flowable<Int>

  @Query("SELECT * FROM account ORDER BY lastUsed DESC")
  fun all(): LiveData<List<Account>>

  @Delete
  fun delete(account: Account)

  @Query("UPDATE account SET defaultFiltered = :defaultFiltered WHERE id = :id")
  fun _setFiltered(id: Long, defaultFiltered: Int)

  @Query("DELETE FROM account")
  fun clear()
}


fun AccountDao.create(vararg entities: Account): List<AccountId> =
  _create(*entities).map(::AccountId)

fun AccountDao.findById(id: AccountId): Account? =
  _findById(id.id)

fun AccountDao.listen(id: AccountId): LiveData<Account?> =
  _listen(id.id)

fun AccountDao.listenDefaultFiltered(id: AccountId, defaultValue: Int): Flowable<Int> =
  _listenDefaultFiltered(id.id, defaultValue)

fun AccountDao.setFiltered(id: AccountId, defaultFiltered: Int) =
  _setFiltered(id.id, defaultFiltered)
