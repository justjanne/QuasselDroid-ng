/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Koschinski
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

package de.kuschku.libquassel.util.helpers

import de.kuschku.libquassel.protocol.primitive.serializer.StringSerializer
import java.nio.ByteBuffer

fun ByteBuffer.copyTo(target: ByteBuffer) {
  while (target.remaining() > 8)
    target.putLong(this.long)
  while (target.hasRemaining())
    target.put(this.get())
}

fun ByteBuffer?.deserializeString(serializer: StringSerializer) = if (this == null) {
  null
} else {
  serializer.deserializeAll(this)
}

fun ByteBuffer.hexDump() {
  val target = ByteBuffer.allocate(this.capacity())
  this.clear()
  this.copyTo(target)
  target.array().hexDump()
}
