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

import de.justjanne.bitflags.Flag
import de.justjanne.bitflags.Flags
import de.justjanne.bitflags.toEnumSet

enum class BufferActivity(
  override val value: UInt,
) : Flag<UInt> {
  NoActivity(0x00u),
  OtherActivity(0x01u),
  NewMessage(0x02u),
  Highlight(0x04u);

  companion object : Flags<UInt, BufferActivity> {
    private val values = values().associateBy(BufferActivity::value)
    override val all: BufferActivities = values.values.toEnumSet()
  }
}

typealias BufferActivities = Set<BufferActivity>
