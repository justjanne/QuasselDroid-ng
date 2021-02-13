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

package de.justjanne.libquassel.protocol.serializers.handshake

import de.justjanne.libquassel.protocol.messages.handshake.SessionInit
import de.justjanne.libquassel.protocol.variant.QVariantMap
import de.justjanne.libquassel.protocol.variant.QtType
import de.justjanne.libquassel.protocol.variant.into
import de.justjanne.libquassel.protocol.variant.qVariant

object SessionInitSerializer : HandshakeSerializer<SessionInit> {
  override val type: String = "SessionInit"
  override val javaType: Class<out SessionInit> = SessionInit::class.java

  override fun serialize(data: SessionInit) = mapOf(
    "MsgType" to qVariant(type, QtType.QString),
    "SessionState" to qVariant(mapOf(
      "BufferInfos" to qVariant(data.bufferInfos, QtType.QVariantList),
      "NetworkIds" to qVariant(data.networkIds, QtType.QVariantList),
      "Identities" to qVariant(data.identities, QtType.QVariantList),
    ), QtType.QVariantMap)
  )

  override fun deserialize(data: QVariantMap) = data["SessionState"].into<QVariantMap>().let {
    SessionInit(
      bufferInfos = it?.get("BufferInfos").into(),
      networkIds = it?.get("NetworkIds").into(),
      identities = it?.get("Identities").into(),
    )
  }
}
