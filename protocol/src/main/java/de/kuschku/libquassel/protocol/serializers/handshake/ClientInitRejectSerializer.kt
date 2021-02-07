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

package de.kuschku.libquassel.protocol.serializers.handshake

import de.kuschku.libquassel.protocol.messages.handshake.ClientInitReject
import de.kuschku.libquassel.protocol.variant.QVariantMap
import de.kuschku.libquassel.protocol.variant.QtType
import de.kuschku.libquassel.protocol.variant.into
import de.kuschku.libquassel.protocol.variant.qVariant

object ClientInitRejectSerializer : HandshakeSerializer<ClientInitReject> {
  override val type: String = "ClientInitReject"
  override val javaType: Class<out ClientInitReject> = ClientInitReject::class.java

  override fun serialize(data: ClientInitReject) = mapOf(
    "Error" to qVariant(data.errorString, QtType.QString)
  )

  override fun deserialize(data: QVariantMap) = ClientInitReject(
    errorString = data["Error"].into()
  )
}