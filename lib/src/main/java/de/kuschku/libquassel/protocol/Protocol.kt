/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2019 Janne Mareike Koschinski
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

enum class Protocol(private val value: UByte) {
  Legacy(0x01u),
  Datastream(0x02u);

  fun toByte() = value.toByte()
  fun toChar() = value.toInt().toChar()
  fun toDouble() = value.toInt().toDouble()
  fun toFloat() = value.toInt().toFloat()
  fun toInt() = value.toInt()
  fun toLong() = value.toLong()
  fun toShort() = value.toShort()
  fun toUByte() = value.toUByte()
  fun toUInt() = value.toUInt()
  fun toULong() = value.toULong()
  fun toUShort() = value.toUShort()
}
