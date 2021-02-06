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

package de.kuschku.bitflags

import kotlin.experimental.or

@JvmName("toByteFlag")
fun Set<Flag<Byte>>?.toBits(): Byte = this?.fold(0.toByte()) { acc, el ->
  acc or el.value
} ?: 0.toByte()

@JvmName("toUByteFlag")
fun Set<Flag<UByte>>?.toBits(): UByte = this?.fold(0.toUByte()) { acc, el ->
  acc or el.value
} ?: 0.toUByte()

@JvmName("toShortFlag")
fun Set<Flag<Short>>?.toBits(): Short = this?.fold(0.toShort()) { acc, el ->
  acc or el.value
} ?: 0.toShort()

@JvmName("toUShortFlag")
fun Set<Flag<UShort>>?.toBits(): UShort = this?.fold(0.toUShort()) { acc, el ->
  acc or el.value
} ?: 0.toUShort()

@JvmName("toIntFlag")
fun Set<Flag<Int>>?.toBits(): Int = this?.fold(0) { acc, el ->
  acc or el.value
} ?: 0

@JvmName("toUIntFlag")
fun Set<Flag<UInt>>?.toBits(): UInt = this?.fold(0.toUInt()) { acc, el ->
  acc or el.value
} ?: 0u

@JvmName("toLongFlag")
fun Set<Flag<Long>>?.toBits(): Long = this?.fold(0.toLong()) { acc, el ->
  acc or el.value
} ?: 0L

@JvmName("toULongFlag")
fun Set<Flag<ULong>>?.toBits(): ULong = this?.fold(0.toULong()) { acc, el ->
  acc or el.value
} ?: 0uL
