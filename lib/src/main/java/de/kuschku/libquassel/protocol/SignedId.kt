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

package de.kuschku.libquassel.protocol

typealias SignedId = Int
typealias SignedId64 = Long

typealias MsgId_Type = SignedId64

inline class MsgId(val id: MsgId_Type) : Comparable<MsgId> {
  override fun compareTo(other: MsgId) = id.compareTo(other.id)
  inline fun isValidId() = id > 0

  override fun toString(): String {
    return "MsgId($id)"
  }

  companion object {
    val MIN_VALUE = MsgId(MsgId_Type.MIN_VALUE)
    val MAX_VALUE = MsgId(MsgId_Type.MAX_VALUE)
  }
}

typealias NetworkId_Type = SignedId

inline class NetworkId(val id: NetworkId_Type) : Comparable<NetworkId> {
  override fun compareTo(other: NetworkId) = id.compareTo(other.id)
  inline fun isValidId() = id > 0

  override fun toString(): String {
    return "NetworkId($id)"
  }

  companion object {
    val MIN_VALUE = NetworkId(NetworkId_Type.MIN_VALUE)
    val MAX_VALUE = NetworkId(NetworkId_Type.MAX_VALUE)
  }
}

typealias BufferId_Type = SignedId

inline class BufferId(val id: BufferId_Type) : Comparable<BufferId> {
  override fun compareTo(other: BufferId) = id.compareTo(other.id)
  inline fun isValidId() = id > 0

  override fun toString(): String {
    return "BufferId($id)"
  }

  companion object {
    val MIN_VALUE = BufferId(BufferId_Type.MIN_VALUE)
    val MAX_VALUE = BufferId(BufferId_Type.MAX_VALUE)
  }
}

typealias IdentityId_Type = SignedId

inline class IdentityId(val id: IdentityId_Type) : Comparable<IdentityId> {
  override fun compareTo(other: IdentityId) = id.compareTo(other.id)
  inline fun isValidId() = id > 0

  override fun toString(): String {
    return "IdentityId($id)"
  }

  companion object {
    val MIN_VALUE = IdentityId(IdentityId_Type.MIN_VALUE)
    val MAX_VALUE = IdentityId(IdentityId_Type.MAX_VALUE)
  }
}
