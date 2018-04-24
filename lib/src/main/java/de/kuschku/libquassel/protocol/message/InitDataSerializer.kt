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

package de.kuschku.libquassel.protocol.message

import de.kuschku.libquassel.protocol.*
import de.kuschku.libquassel.protocol.primitive.serializer.StringSerializer
import de.kuschku.libquassel.util.helpers.deserializeString
import de.kuschku.libquassel.util.helpers.serializeString
import java.nio.ByteBuffer

object InitDataSerializer : SignalProxyMessageSerializer<SignalProxyMessage.InitData> {
  override fun serialize(data: SignalProxyMessage.InitData) = listOf(
    QVariant.of<Any>(RequestType.InitData.value, Type.Int),
    QVariant.of<Any>(data.className.serializeString(StringSerializer.UTF8), Type.QByteArray),
    QVariant.of<Any>(data.objectName.serializeString(StringSerializer.UTF8), Type.QByteArray),
    QVariant.of<Any>(data.initData, Type.QVariantMap)
  )

  override fun deserialize(data: QVariantList) = SignalProxyMessage.InitData(
    data[0].value<ByteBuffer?>().deserializeString(StringSerializer.UTF8) ?: "",
    data[1].value<ByteBuffer?>().deserializeString(StringSerializer.UTF8) ?: "",
    data.drop(2).toVariantMap()
  )
}
