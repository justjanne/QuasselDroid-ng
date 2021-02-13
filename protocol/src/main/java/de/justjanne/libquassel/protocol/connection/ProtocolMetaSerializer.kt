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

import de.justjanne.libquassel.protocol.features.FeatureSet
import de.justjanne.libquassel.protocol.io.ChainedByteBuffer
import de.justjanne.libquassel.protocol.serializers.primitive.Serializer
import de.justjanne.libquassel.protocol.serializers.primitive.UByteSerializer
import de.justjanne.libquassel.protocol.serializers.primitive.UShortSerializer
import java.nio.ByteBuffer

object ProtocolMetaSerializer : Serializer<ProtocolMeta> {
  override fun serialize(buffer: ChainedByteBuffer, data: ProtocolMeta, featureSet: FeatureSet) {
    UShortSerializer.serialize(buffer, data.data, featureSet)
    UByteSerializer.serialize(buffer, data.version.value, featureSet)
  }

  override fun deserialize(buffer: ByteBuffer, featureSet: FeatureSet) = ProtocolMeta(
    UShortSerializer.deserialize(buffer, featureSet),
    ProtocolVersion.of(UByteSerializer.deserialize(buffer, featureSet))
  )
}
