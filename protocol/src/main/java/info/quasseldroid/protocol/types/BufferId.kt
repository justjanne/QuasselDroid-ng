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

package info.quasseldroid.protocol.types

private typealias BufferIdType = SignedIdType

inline class BufferId(override val id: BufferIdType) : SignedId<BufferIdType> {
  override fun toString() = "BufferId($id)"

  companion object {
    val MIN_VALUE = BufferId(BufferIdType.MIN_VALUE)
    val MAX_VALUE = BufferId(BufferIdType.MAX_VALUE)
  }
}
