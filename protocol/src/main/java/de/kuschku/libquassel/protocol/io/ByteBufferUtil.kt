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

package de.kuschku.libquassel.protocol.io

import java.nio.ByteBuffer

fun copyData(from: ByteBuffer, to: ByteBuffer, amount: Int = -1) {
  val actualAmount =
    if (amount >= 0) minOf(from.remaining(), to.remaining(), amount)
    else minOf(from.remaining(), to.remaining())
  for (i in 0 until actualAmount) {
    to.put(from.get())
  }/*
  if (actualAmount > 0) {
    val fromLimit = from.limit()
    val toLimit = to.limit()
    from.limit(from.position() + actualAmount)
    to.limit(to.position() + actualAmount)
    to.put(from)
    from.limit(fromLimit)
    to.limit(toLimit)
  }
  */
}

val alphabet = charArrayOf(
  '0', '1', '2', '3', '4', '5', '6', '7',
  '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
)

fun ByteBuffer.contentToString(): String {
  mark()
  var result = ""
  while (remaining() > 0) {
    val byte = get()
    val upperNibble = byte.toInt() shr 4
    val lowerNibble = byte.toInt() % 16
    result += alphabet[(upperNibble + 16) % 16]
    result += alphabet[(lowerNibble + 16) % 16]
  }
  reset()
  return result
}

fun ByteBuffer.print() = println(contentToString())

fun copyData(from: ByteBuffer, amount: Int) = ByteBuffer.allocateDirect(amount).also {
  if (amount > 0) {
    copyData(from, it, amount)
    it.clear()
  }
}
