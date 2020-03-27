/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2020 Janne Mareike Koschinski
 * Copyright (c) 2020 The Quassel Project
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

package de.kuschku.libquassel.util

import org.threeten.bp.Instant
import java.nio.charset.Charset
import java.util.*

private val random = Random()

fun Any?.randomBoolean(): Boolean = random.nextBoolean()

fun Any?.randomByte(): Byte = random.nextInt().toByte()
fun Any?.randomUByte(): UByte = random.nextInt().toUByte()

fun Any?.randomShort(): Short = random.nextInt().toShort()
fun Any?.randomUShort(): UShort = random.nextInt().toUShort()

fun Any?.randomInt(): Int = random.nextInt()
fun Any?.randomUInt(): UInt = random.nextInt().toUInt()

fun Any?.randomLong(): Long = random.nextLong()
fun Any?.randomULong(): ULong = random.nextLong().toULong()

fun Any?.randomString(): String = UUID.randomUUID().toString()

fun Any?.randomInstant(): Instant = Instant.ofEpochMilli(randomLong())

fun <T> Any?.randomOf(vararg elements: T): T = elements[random.nextInt(elements.size)]
fun <T> Any?.randomOf(elements: List<T>): T = elements[random.nextInt(elements.size)]

fun Any?.randomCharset(): Charset = randomOf(*Charset.availableCharsets().values.toTypedArray())
