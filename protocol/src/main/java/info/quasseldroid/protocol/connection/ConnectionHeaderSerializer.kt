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

package info.quasseldroid.protocol.connection

import de.justjanne.bitflags.of
import de.justjanne.bitflags.toBits
import info.quasseldroid.protocol.features.FeatureSet
import info.quasseldroid.protocol.io.ChainedByteBuffer
import info.quasseldroid.protocol.serializers.primitive.Serializer
import info.quasseldroid.protocol.serializers.primitive.UByteSerializer
import info.quasseldroid.protocol.serializers.primitive.UIntSerializer
import java.nio.ByteBuffer

object ConnectionHeaderSerializer : Serializer<ConnectionHeader> {
  private const val magic: UInt = 0x42b3_3f00u
  private const val featureMask: UInt = 0x0000_00ffu
  private const val lastMagic: UByte = 0x80u

  private fun addMagic(data: UByte): UInt =
    magic or data.toUInt()

  private fun removeMagic(data: UInt): UByte =
    (data and featureMask).toUByte()

  private fun <T> writeList(
    buffer: ChainedByteBuffer,
    list: List<T>,
    featureSet: FeatureSet,
    f: (T) -> Unit
  ) {
    for (index in list.indices) {
      val isLast = index + 1 == list.size
      val magic = if (isLast) lastMagic else 0x00u
      UByteSerializer.serialize(buffer, magic, featureSet)
      f(list[index])
    }
  }

  private fun <T> readList(
    buffer: ByteBuffer,
    featureSet: FeatureSet,
    f: () -> T
  ) : List<T> {
    val list = mutableListOf<T>()
    while (true) {
      val isLast = UByteSerializer.deserialize(buffer, featureSet) != 0x00u.toUByte()
      list.add(f())
      if (isLast) {
        break
      }
    }
    return list
  }

  override fun serialize(buffer: ChainedByteBuffer, data: ConnectionHeader, featureSet: FeatureSet) {
    UIntSerializer.serialize(buffer, addMagic(data.features.toBits()), featureSet)
    writeList(buffer, data.versions, featureSet) {
      ProtocolMetaSerializer.serialize(buffer, it, featureSet)
    }
  }

  override fun deserialize(buffer: ByteBuffer, featureSet: FeatureSet) = ConnectionHeader(
    features = ProtocolFeature.of(
      removeMagic(UIntSerializer.deserialize(buffer, featureSet))
    ),
    versions = readList(buffer, featureSet) {
      ProtocolMetaSerializer.deserialize(buffer, featureSet)
    }
  )
}
