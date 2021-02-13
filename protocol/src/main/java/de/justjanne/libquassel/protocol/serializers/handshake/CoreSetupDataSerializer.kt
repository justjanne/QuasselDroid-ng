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

import de.justjanne.libquassel.protocol.messages.handshake.CoreSetupData
import de.justjanne.libquassel.protocol.variant.QVariantMap
import de.justjanne.libquassel.protocol.variant.QtType
import de.justjanne.libquassel.protocol.variant.into
import de.justjanne.libquassel.protocol.variant.qVariant

object CoreSetupDataSerializer : HandshakeSerializer<CoreSetupData> {
  override val type: String = "CoreSetupData"
  override val javaType: Class<out CoreSetupData> = CoreSetupData::class.java

  override fun serialize(data: CoreSetupData) = mapOf(
    "MsgType" to qVariant(type, QtType.QString),
    "SetupData" to qVariant(mapOf(
      "AdminUser" to qVariant(data.adminUser, QtType.QString),
      "AdminPasswd" to qVariant(data.adminPassword, QtType.QString),
      "Backend" to qVariant(data.backend, QtType.QString),
      "ConnectionProperties" to qVariant(data.setupData, QtType.QVariantMap),
      "Authenticator" to qVariant(data.authenticator, QtType.QString),
      "AuthProperties" to qVariant(data.authSetupData, QtType.QVariantMap),
    ), QtType.QVariantMap)
  )

  override fun deserialize(data: QVariantMap) =
    data["SetupData"].into<QVariantMap>().let {
      CoreSetupData(
        adminUser = it?.get("AdminUser").into(),
        adminPassword = it?.get("AdminPasswd").into(),
        backend = it?.get("Backend").into(),
        setupData = it?.get("ConnectionProperties").into(),
        authenticator = it?.get("Authenticator").into(),
        authSetupData = it?.get("AuthProperties").into()
      )
    }
}
