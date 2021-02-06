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

interface Flags<T, U> where U: Flag<T>, U: Enum<U> {
  operator fun get(value: T): U?
  fun all(): Collection<U>
}

inline fun <reified T> Flags<*, T>.of(
  vararg values: T
) where T: Flag<*>, T: Enum<T> = values.toEnumSet()
inline fun <reified T> Flags<*, T>.of(
  values: Collection<T>
) where T: Flag<*>, T: Enum<T> = values.toEnumSet()

inline fun <reified T: Enum<T>> Array<out T>.toEnumSet() =
  EnumSet.noneOf(T::class.java).apply {
    addAll(this@toEnumSet)
  }

inline fun <reified T: Enum<T>> Collection<T>.toEnumSet() =
  EnumSet.noneOf(T::class.java).apply {
    addAll(this@toEnumSet)
  }
