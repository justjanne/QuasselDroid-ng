/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2019 Janne Mareike Koschinski
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

package de.kuschku.libquassel.protocol.message

import de.kuschku.libquassel.protocol.QVariant
import de.kuschku.libquassel.protocol.QVariantList
import de.kuschku.libquassel.protocol.Type
import de.kuschku.libquassel.protocol.primitive.serializer.StringSerializer
import de.kuschku.libquassel.protocol.value
import de.kuschku.libquassel.util.helper.deserializeString
import de.kuschku.libquassel.util.helper.serializeString
import java.nio.ByteBuffer

object RpcCallSerializer : SignalProxyMessageSerializer<SignalProxyMessage.RpcCall> {
  override fun serialize(data: SignalProxyMessage.RpcCall) = listOf(
    QVariant.of(RequestType.RpcCall.value, Type.Int),
    QVariant.of(data.slotName.serializeString(StringSerializer.UTF8), Type.QByteArray),
    *data.params.toTypedArray()
  )

  override fun deserialize(data: QVariantList) = SignalProxyMessage.RpcCall(
    data[0].value<ByteBuffer?>().deserializeString(StringSerializer.UTF8) ?: "",
    data.drop(1)
  )
}
