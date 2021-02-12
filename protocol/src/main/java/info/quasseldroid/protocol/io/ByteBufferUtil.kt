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

package info.quasseldroid.protocol.io

import java.nio.ByteBuffer

fun copyData(from: ByteBuffer, to: ByteBuffer, desiredAmount: Int) {
  val limit = from.limit()
  val availableAmount = minOf(from.remaining(), to.remaining())
  val amount = minOf(availableAmount, desiredAmount)
  from.limit(from.position() + amount)
  to.put(from)
  from.limit(limit)
}

fun copyData(from: ByteBuffer, desiredAmount: Int): ByteBuffer {
  val to = ByteBuffer.allocate(minOf(from.remaining(), desiredAmount))
  copyData(from, to, desiredAmount)
  return to.flip()
}

fun ByteBuffer?.isEmpty() = this == null || !this.hasRemaining()

private val alphabet = charArrayOf(
  '0', '1', '2', '3', '4', '5', '6', '7',
  '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
)

fun ByteBuffer.contentToString(): String {
  val position = position()
  val limit = limit()
  var result = ""
  while (hasRemaining()) {
    val byte = get()
    val upperNibble = byte.toInt() shr 4
    val lowerNibble = byte.toInt() % 16
    result += alphabet[(upperNibble + 16) % 16]
    result += alphabet[(lowerNibble + 16) % 16]
  }
  limit(limit)
  position(position)
  return result
}
