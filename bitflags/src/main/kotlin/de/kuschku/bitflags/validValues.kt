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

import java.util.*

@JvmName("validValuesUByte")
inline fun <reified T> Flags<UByte, T>.validValues(): EnumSet<T>
  where T : Flag<UByte>, T : Enum<T> =
  all.filter { it.value != 0.toUByte() }.toEnumSet()

@JvmName("validValuesByte")
inline fun <reified T> Flags<Byte, T>.validValues(): EnumSet<T>
  where T : Flag<Byte>, T : Enum<T> =
  all.filter { it.value != 0.toByte() }.toEnumSet()

@JvmName("validValuesUShort")
inline fun <reified T> Flags<UShort, T>.validValues(): EnumSet<T>
  where T : Flag<UShort>, T : Enum<T> =
  all.filter { it.value != 0.toUShort() }.toEnumSet()

@JvmName("validValuesShort")
inline fun <reified T> Flags<Short, T>.validValues(): EnumSet<T>
  where T : Flag<Short>, T : Enum<T> =
  all.filter { it.value != 0.toShort() }.toEnumSet()

@JvmName("validValuesUInt")
inline fun <reified T> Flags<UInt, T>.validValues(): EnumSet<T>
  where T : Flag<UInt>, T : Enum<T> =
  all.filter { it.value != 0u }.toEnumSet()

@JvmName("validValuesInt")
inline fun <reified T> Flags<Int, T>.validValues(): EnumSet<T>
  where T : Flag<Int>, T : Enum<T> =
  all.filter { it.value != 0 }.toEnumSet()

@JvmName("validValuesULong")
inline fun <reified T> Flags<ULong, T>.validValues(): EnumSet<T>
  where T : Flag<ULong>, T : Enum<T> =
  all.filter { it.value != 0uL }.toEnumSet()

@JvmName("validValuesLong")
inline fun <reified T> Flags<Long, T>.validValues(): EnumSet<T>
  where T : Flag<Long>, T : Enum<T> =
  all.filter { it.value != 0L }.toEnumSet()
