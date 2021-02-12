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

package info.quasseldroid.protocol.serializers.primitive

import info.quasseldroid.protocol.features.FeatureSet
import info.quasseldroid.protocol.features.QuasselFeature
import info.quasseldroid.protocol.io.ChainedByteBuffer
import info.quasseldroid.protocol.types.MsgId
import info.quasseldroid.protocol.variant.QuasselType
import java.nio.ByteBuffer

object MsgIdSerializer : QuasselSerializer<MsgId> {
  override val quasselType: QuasselType = QuasselType.MsgId
  override val javaType: Class<out MsgId> = MsgId::class.java

  override fun serialize(buffer: ChainedByteBuffer, data: MsgId, featureSet: FeatureSet) {
    if (featureSet.hasFeature(QuasselFeature.LongMessageId)) {
      LongSerializer.serialize(buffer, data.id, featureSet)
    } else {
      IntSerializer.serialize(buffer, data.id.toInt(), featureSet)
    }
  }

  override fun deserialize(buffer: ByteBuffer, featureSet: FeatureSet): MsgId {
    return if (featureSet.hasFeature(QuasselFeature.LongMessageId)) {
      MsgId(LongSerializer.deserialize(buffer, featureSet))
    } else {
      MsgId(IntSerializer.deserialize(buffer, featureSet).toLong())
    }
  }

}
