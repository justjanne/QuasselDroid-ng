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

package info.quasseldroid.protocol.serializers.handshake

import de.justjanne.bitflags.of
import de.justjanne.bitflags.toBits
import info.quasseldroid.protocol.features.LegacyFeature
import info.quasseldroid.protocol.features.QuasselFeatureName
import info.quasseldroid.protocol.messages.handshake.ClientInitAck
import info.quasseldroid.protocol.messages.handshake.ClientLoginAck
import info.quasseldroid.protocol.variant.*

object ClientLoginAckSerializer : HandshakeSerializer<ClientLoginAck> {
  override val type: String = "ClientLoginAck"
  override val javaType: Class<out ClientLoginAck> = ClientLoginAck::class.java

  override fun serialize(data: ClientLoginAck) = mapOf(
    "MsgType" to qVariant(type, QtType.QString),
  )

  override fun deserialize(data: QVariantMap) = ClientLoginAck
}
