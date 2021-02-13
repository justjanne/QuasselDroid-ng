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

package de.justjanne.libquassel.protocol.connection

import de.justjanne.bitflags.of
import de.justjanne.bitflags.toBits
import de.justjanne.libquassel.protocol.features.FeatureSet
import de.justjanne.libquassel.protocol.io.ChainedByteBuffer
import de.justjanne.libquassel.protocol.serializers.primitive.Serializer
import de.justjanne.libquassel.protocol.serializers.primitive.UByteSerializer
import java.nio.ByteBuffer

object ProtocolInfoSerializer : Serializer<ProtocolInfo> {
  override fun serialize(buffer: ChainedByteBuffer, data: ProtocolInfo, featureSet: FeatureSet) {
    UByteSerializer.serialize(buffer, data.flags.toBits(), featureSet)
    ProtocolMetaSerializer.serialize(buffer, data.meta, featureSet)
  }

  override fun deserialize(buffer: ByteBuffer, featureSet: FeatureSet) = ProtocolInfo(
    ProtocolFeature.of(UByteSerializer.deserialize(buffer, featureSet)),
    ProtocolMetaSerializer.deserialize(buffer, featureSet)
  )
}
