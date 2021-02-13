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

package de.justjanne.libquassel.protocol.types

import de.justjanne.bitflags.Flag
import de.justjanne.bitflags.Flags
import de.justjanne.bitflags.toEnumSet

enum class MessageType(
  override val value: UInt,
) : Flag<UInt> {
  Plain(0x00001u),
  Notice(0x00002u),
  Action(0x00004u),
  Nick(0x00008u),
  Mode(0x00010u),
  Join(0x00020u),
  Part(0x00040u),
  Quit(0x00080u),
  Kick(0x00100u),
  Kill(0x00200u),
  Server(0x00400u),
  Info(0x00800u),
  Error(0x01000u),
  DayChange(0x02000u),
  Topic(0x04000u),
  NetsplitJoin(0x08000u),
  NetsplitQuit(0x10000u),
  Invite(0x20000u),
  Markerline(0x40000u);

  companion object : Flags<UInt, MessageType> {
    private val values = values().associateBy(MessageType::value)
    override val all: MessageTypes = values.values.toEnumSet()
  }
}

typealias MessageTypes = Set<MessageType>
