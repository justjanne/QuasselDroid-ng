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
import de.kuschku.bitflags.toEnumSet

enum class MessageFlag(
  override val value: UInt,
) : Flag<UInt> {
  Self(0x01u),
  Highlight(0x02u),
  Redirected(0x04u),
  ServerMsg(0x08u),
  Backlog(0x80u);

  companion object : Flags<UInt, MessageFlag> {
    private val values = values().associateBy(MessageFlag::value)
    override val all: MessageFlags = values.values.toEnumSet()
  }
}

typealias MessageFlags = Set<MessageFlag>
