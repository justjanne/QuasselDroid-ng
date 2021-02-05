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

package de.kuschku.quasseldroid.protocol

import kotlin.experimental.or


interface Flag<T> {
  val value: T
}

@JvmName("toByteFlag")
fun Set<Flag<Byte>>.toFlag(): Byte = fold(0.toByte()) { acc, el ->
  acc or el.value
}

@JvmName("toUByteFlag")
fun Set<Flag<UByte>>.toFlag(): UByte = fold(0.toUByte()) { acc, el ->
  acc or el.value
}

@JvmName("toShortFlag")
fun Set<Flag<Short>>.toFlag(): Short = fold(0.toShort()) { acc, el ->
  acc or el.value
}

@JvmName("toUShortFlag")
fun Set<Flag<UShort>>.toFlag(): UShort = fold(0.toUShort()) { acc, el ->
  acc or el.value
}

@JvmName("toIntFlag")
fun Set<Flag<Int>>.toFlag(): Int = fold(0) { acc, el ->
  acc or el.value
}

@JvmName("toUIntFlag")
fun Set<Flag<UInt>>.toFlag(): UInt = fold(0.toUInt()) { acc, el ->
  acc or el.value
}

@JvmName("toLongFlag")
fun Set<Flag<Long>>.toFlag(): Long = fold(0.toLong()) { acc, el ->
  acc or el.value
}

@JvmName("toULongFlag")
fun Set<Flag<ULong>>.toFlag(): ULong = fold(0.toULong()) { acc, el ->
  acc or el.value
}
