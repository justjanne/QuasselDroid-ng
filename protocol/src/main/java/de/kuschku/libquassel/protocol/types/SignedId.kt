/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2021 Janne Mareike Koschinski
 * Copyright (c) 2021 The Quassel Project
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

package de.kuschku.libquassel.protocol.types

import java.io.Serializable

typealias SignedIdType = Int
typealias SignedId64Type = Long

interface SignedId<T> : Serializable, Comparable<SignedId<T>>
  where T : Number, T : Comparable<T> {
  val id: T

  override fun compareTo(other: SignedId<T>): Int {
    return id.compareTo(other.id)
  }
}

@Suppress("NOTHING_TO_INLINE")
@JvmName("isValidId")
inline fun SignedId<SignedIdType>.isValid() = id > 0

@Suppress("NOTHING_TO_INLINE")
@JvmName("isValidId64")
inline fun SignedId<SignedId64Type>.isValid() = id > 0




