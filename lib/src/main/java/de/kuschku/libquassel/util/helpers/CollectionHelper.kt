/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2019 Janne Koschinski
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

package de.kuschku.libquassel.util.helpers


/**
 * Returns the sum of all elements in the collection.
 */
@kotlin.jvm.JvmName("sumOfUByte")
fun Iterable<UByte>.sum(): UInt {
  var sum: UInt = 0u
  for (element in this) {
    sum += element
  }
  return sum
}

/**
 * Returns the sum of all elements in the collection.
 */
@kotlin.jvm.JvmName("sumOfUShort")
fun Iterable<UShort>.sum(): UInt {
  var sum: UInt = 0u
  for (element in this) {
    sum += element
  }
  return sum
}

/**
 * Returns the sum of all elements in the collection.
 */
@kotlin.jvm.JvmName("sumOfUInt")
fun Iterable<UInt>.sum(): UInt {
  var sum: UInt = 0u
  for (element in this) {
    sum += element
  }
  return sum
}

/**
 * Returns the sum of all elements in the collection.
 */
@kotlin.jvm.JvmName("sumOfULong")
fun Iterable<ULong>.sum(): ULong {
  var sum: ULong = 0uL
  for (element in this) {
    sum += element
  }
  return sum
}
