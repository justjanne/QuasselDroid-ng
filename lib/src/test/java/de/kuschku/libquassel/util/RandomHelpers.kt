/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Koschinski
 * Copyright (c) 2018 Ken Børge Viktil
 * Copyright (c) 2018 Magnus Fjell
 * Copyright (c) 2018 Martin Sandsmark
 * Copyright (c) 2018 The Quassel Project
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.libquassel.util

import de.kuschku.libquassel.protocol.UByte
import de.kuschku.libquassel.protocol.UInt
import de.kuschku.libquassel.protocol.UShort
import java.nio.charset.Charset
import java.util.*

val random = Random()

fun Any?.randomBoolean(): Boolean = random.nextBoolean()

fun Any?.randomByte(): Byte = random.nextInt(2 shl 8).toByte()
fun Any?.randomUByte(): UByte = random.nextInt(Byte.MAX_VALUE.toInt()).toByte()

fun Any?.randomShort(): Short = random.nextInt(2 shl 16).toShort()
fun Any?.randomUShort(): UShort = random.nextInt(Short.MAX_VALUE.toInt()).toShort()

fun Any?.randomInt(): Int = random.nextInt()
fun Any?.randomUInt(): UInt = random.nextInt(Int.MAX_VALUE)

fun Any?.randomLong(): Long = random.nextLong()

fun Any?.randomString(): String = UUID.randomUUID().toString()

fun <T> Any?.randomOf(vararg elements: T): T = elements[random.nextInt(elements.size)]

fun Any?.randomCharset(): Charset = randomOf(*Charset.availableCharsets().values.toTypedArray())
