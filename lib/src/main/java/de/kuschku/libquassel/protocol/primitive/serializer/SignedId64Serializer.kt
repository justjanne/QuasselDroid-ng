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

package de.kuschku.libquassel.protocol.primitive.serializer

import de.kuschku.libquassel.protocol.SignedId64
import de.kuschku.libquassel.quassel.ExtendedFeature
import de.kuschku.libquassel.quassel.QuasselFeatures
import de.kuschku.libquassel.util.nio.ChainedByteBuffer
import java.nio.ByteBuffer

object SignedId64Serializer : Serializer<SignedId64> {
  override fun serialize(buffer: ChainedByteBuffer, data: SignedId64, features: QuasselFeatures) {
    if (features.hasFeature(ExtendedFeature.LongMessageId))
      buffer.putLong(data)
    else
      buffer.putInt(data.toInt())
  }

  override fun deserialize(buffer: ByteBuffer, features: QuasselFeatures): SignedId64 {
    return if (features.hasFeature(ExtendedFeature.LongMessageId))
      buffer.long
    else
      buffer.int.toLong()
  }
}
