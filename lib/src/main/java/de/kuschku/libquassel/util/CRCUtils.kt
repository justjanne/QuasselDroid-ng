/*
 * QuasselDroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Koschinski
 * Copyright (c) 2018 Ken Børge Viktil
 * Copyright (c) 2018 Magnus Fjell
 * Copyright (c) 2018 Martin Sandsmark
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
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

object CRCUtils {
  fun qChecksum(data: ByteArray): Int {
    var crc = 0xffff
    val crcHighBitMask = 0x8000

    for (b in data) {
      val c = reflect(b.toInt(), 8)
      var j = 0x80
      while (j > 0) {
        var highBit = crc and crcHighBitMask
        crc = crc shl 1
        if (c and j > 0) {
          highBit = highBit xor crcHighBitMask
        }
        if (highBit > 0) {
          crc = crc xor 0x1021
        }
        j = j shr 1
      }
    }

    crc = reflect(crc, 16)
    crc = crc xor 0xffff
    crc = crc and 0xffff

    return crc
  }

  private fun reflect(crc: Int, n: Int): Int {
    var j = 1
    var crcout = 0
    var i = 1 shl n - 1
    while (i > 0) {
      if (crc and i > 0) {
        crcout = crcout or j
      }
      j = j shl 1
      i = i shr 1
    }
    return crcout
  }
}
