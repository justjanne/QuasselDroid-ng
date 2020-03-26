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

package de.kuschku.quasseldroid.persistence.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import de.kuschku.quasseldroid.persistence.util.AccountId

@Entity(tableName = "Account")
data class Account(
  @PrimaryKey(autoGenerate = true)
  @ColumnInfo(name = "id")
  var rawId: Long,
  var host: String,
  var port: Int,
  var requireSsl: Boolean,
  var user: String,
  var pass: String,
  var name: String,
  var lastUsed: Long,
  var acceptedMissingFeatures: Boolean,
  var defaultFiltered: Int
) {
  inline var id
    get() = AccountId(rawId)
    set(value) {
      rawId = value.id
    }

  companion object {
    inline fun of(
      id: AccountId,
      host: String,
      port: Int,
      requireSsl: Boolean,
      user: String,
      pass: String,
      name: String,
      lastUsed: Long,
      acceptedMissingFeatures: Boolean,
      defaultFiltered: Int
    ) = Account(
      id.id,
      host,
      port,
      requireSsl,
      user,
      pass,
      name,
      lastUsed,
      acceptedMissingFeatures,
      defaultFiltered
    )
  }
}
