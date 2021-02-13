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

import info.quasseldroid.protocol.messages.handshake.ClientLogin
import info.quasseldroid.protocol.variant.QVariantMap
import info.quasseldroid.protocol.variant.QtType
import info.quasseldroid.protocol.variant.into
import info.quasseldroid.protocol.variant.qVariant

object ClientLoginSerializer : HandshakeSerializer<ClientLogin> {
  override val type: String = "ClientLogin"
  override val javaType: Class<out ClientLogin> = ClientLogin::class.java

  override fun serialize(data: ClientLogin) = mapOf(
    "MsgType" to qVariant(type, QtType.QString),
    "User" to qVariant(data.user, QtType.QString),
    "Password" to qVariant(data.password, QtType.QString)
  )

  override fun deserialize(data: QVariantMap) = ClientLogin(
    user = data["User"].into(),
    password = data["Password"].into(),
  )
}
