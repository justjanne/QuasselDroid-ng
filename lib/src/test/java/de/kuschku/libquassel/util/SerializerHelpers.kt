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

package de.kuschku.libquassel.util

import de.kuschku.libquassel.protocol.primitive.serializer.Serializer
import de.kuschku.libquassel.quassel.QuasselFeatures
import de.kuschku.libquassel.util.nio.ChainedByteBuffer

fun <T> roundTrip(serializer: Serializer<T>, value: T,
                  features: QuasselFeatures = QuasselFeatures.all()): T {
  val chainedBuffer = ChainedByteBuffer(
    direct = false
  )
  serializer.serialize(chainedBuffer, value, features)
  val buffer = chainedBuffer.toBuffer()
  return serializer.deserialize(buffer, features)
}
