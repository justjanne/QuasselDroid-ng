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
@file:Suppress("NOTHING_TO_INLINE")

package de.kuschku.quasseldroid.persistence.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import de.kuschku.libquassel.protocol.BufferId
import de.kuschku.libquassel.protocol.BufferId_Type
import de.kuschku.quasseldroid.persistence.models.Filtered

@Dao
interface FilteredDao {
  @Query("SELECT DISTINCT bufferId FROM filtered WHERE accountId = :accountId")
  fun _buffers(accountId: Long): List<BufferId_Type>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun replace(vararg entities: Filtered)

  @Query("UPDATE filtered SET filtered = :filtered WHERE accountId = :accountId AND bufferId = :bufferId")
  fun _setFiltered(accountId: Long, bufferId: BufferId_Type, filtered: Int)

  @Query("SELECT IFNULL(t.filtered, :defaultValue) FROM (SELECT filtered FROM filtered WHERE bufferId = :bufferId AND accountId = :accountId UNION SELECT NULL ORDER BY filtered DESC LIMIT 1) t")
  fun _get(accountId: Long, bufferId: BufferId_Type, defaultValue: Int): Int

  @Query("SELECT IFNULL(t.filtered, :defaultValue) FROM (SELECT filtered FROM filtered WHERE bufferId = :bufferId AND accountId = :accountId UNION SELECT NULL ORDER BY filtered DESC LIMIT 1) t")
  fun _listen(accountId: Long, bufferId: BufferId_Type, defaultValue: Int): LiveData<Int>

  @Query("SELECT * FROM filtered WHERE accountId = :accountId")
  fun listen(accountId: Long): LiveData<List<Filtered>>

  @Query("DELETE FROM filtered")
  fun clear()

  @Query("DELETE FROM filtered WHERE accountId = :accountId")
  fun clear(accountId: Long)

  @Query("DELETE FROM filtered WHERE bufferId = :bufferId AND accountId = :accountId")
  fun _clear(accountId: Long, bufferId: BufferId_Type)
}

inline fun FilteredDao.buffers(accountId: Long) =
  _buffers(accountId).map(::BufferId)

inline fun FilteredDao.setFiltered(accountId: Long, bufferId: BufferId, filtered: Int) =
  _setFiltered(accountId, bufferId.id, filtered)

inline fun FilteredDao.get(accountId: Long, bufferId: BufferId, defaultValue: Int) =
  _get(accountId, bufferId.id, defaultValue)

inline fun FilteredDao.listen(accountId: Long, bufferId: BufferId, defaultValue: Int) =
  _listen(accountId, bufferId.id, defaultValue)

inline fun FilteredDao.clear(accountId: Long, bufferId: BufferId) =
  _clear(accountId, bufferId.id)
