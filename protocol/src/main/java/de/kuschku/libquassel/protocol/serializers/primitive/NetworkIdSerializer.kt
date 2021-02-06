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

package de.kuschku.libquassel.protocol.serializers.primitive

import de.kuschku.libquassel.protocol.features.FeatureSet
import de.kuschku.libquassel.protocol.io.ChainedByteBuffer
import de.kuschku.libquassel.protocol.types.BufferId
import de.kuschku.libquassel.protocol.types.IdentityId
import de.kuschku.libquassel.protocol.types.NetworkId
import de.kuschku.libquassel.protocol.variant.QuasselType
import java.nio.ByteBuffer

object NetworkIdSerializer : QuasselSerializer<NetworkId> {
  override val quasselType: QuasselType = QuasselType.NetworkId
  override val javaType: Class<out NetworkId> = NetworkId::class.java

  override fun serialize(buffer: ChainedByteBuffer, data: NetworkId, featureSet: FeatureSet) {
    IntSerializer.serialize(buffer, data.id, featureSet)
  }

  override fun deserialize(buffer: ByteBuffer, featureSet: FeatureSet): NetworkId {
    return NetworkId(IntSerializer.deserialize(buffer, featureSet))
  }

}
