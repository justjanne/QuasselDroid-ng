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

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import de.kuschku.quasseldroid.persistence.models.SslHostnameWhitelistEntry

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
