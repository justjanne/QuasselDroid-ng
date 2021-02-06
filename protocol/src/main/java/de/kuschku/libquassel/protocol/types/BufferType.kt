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

import de.kuschku.bitflags.Flag
import de.kuschku.bitflags.Flags

enum class BufferType(
  override val value: UShort,
): Flag<UShort> {
  Invalid(0x00u),
  Status(0x01u),
  Channel(0x02u),
  Query(0x04u),
  Group(0x08u);

  companion object : Flags<UShort, BufferType> {
    private val values = values().associateBy(BufferType::value)
    override fun get(value: UShort) = values[value]
    override fun all() = values.values
  }
}

typealias BufferTypes = Set<BufferType>
