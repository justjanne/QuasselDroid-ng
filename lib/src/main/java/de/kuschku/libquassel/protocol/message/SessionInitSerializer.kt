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

package de.kuschku.libquassel.protocol.message

import de.kuschku.libquassel.protocol.QVariant
import de.kuschku.libquassel.protocol.QVariantMap
import de.kuschku.libquassel.protocol.Type
import de.kuschku.libquassel.protocol.value

object SessionInitSerializer : HandshakeMessageSerializer<HandshakeMessage.SessionInit> {
  override fun serialize(data: HandshakeMessage.SessionInit) = mapOf(
    "MsgType" to QVariant.of("SessionInit", Type.QString),
    "SessionState" to QVariant.of(mapOf(
      "BufferInfos" to QVariant.of(data.bufferInfos, Type.QVariantList),
      "NetworkIds" to QVariant.of(data.networkIds, Type.QVariantList),
      "Identities" to QVariant.of(data.identities, Type.QVariantList)
    ), Type.QVariantMap
    )
  )

  override fun deserialize(data: QVariantMap): HandshakeMessage.SessionInit {
    val setupData = data["SessionState"].value<QVariantMap?>()
    return HandshakeMessage.SessionInit(
      bufferInfos = setupData?.get("BufferInfos").value(),
      networkIds = setupData?.get("NetworkIds").value(),
      identities = setupData?.get("Identities").value()
    )
  }
}
