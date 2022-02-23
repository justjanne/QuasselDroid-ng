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

package de.kuschku.quasseldroid.persistence.util

import java.io.Serializable

typealias AccountId_Type = Long

@JvmInline
value class AccountId(val id: AccountId_Type) : Comparable<AccountId>, Serializable {
  override fun compareTo(other: AccountId) = id.compareTo(other.id)
  inline fun isValidId() = id >= 0

  override fun toString(): String {
    return "AccountId($id)"
  }

  companion object {
    val MIN_VALUE = AccountId(AccountId_Type.MIN_VALUE)
    val MAX_VALUE = AccountId(AccountId_Type.MAX_VALUE)
  }
}
