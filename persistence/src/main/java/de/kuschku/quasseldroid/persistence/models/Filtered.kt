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

package de.kuschku.quasseldroid.persistence.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import de.kuschku.libquassel.protocol.BufferId
import de.kuschku.libquassel.protocol.BufferId_Type

@Entity(tableName = "filtered", primaryKeys = ["accountId", "bufferId"])
data class Filtered(
  var accountId: Long,
  @ColumnInfo(name = "bufferId")
  var rawBufferId: BufferId_Type,
  var filtered: Int
) {
  inline val bufferId
    get() = BufferId(rawBufferId)

  companion object {
    inline fun of(
      accountId: Long,
      bufferId: BufferId,
      filtered: Int
    ) = Filtered(
      accountId,
      bufferId.id,
      filtered
    )
  }
}
