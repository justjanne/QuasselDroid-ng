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

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import de.kuschku.quasseldroid.persistence.models.SslValidityWhitelistEntry

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
