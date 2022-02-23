/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2020 Janne Mareike Koschinski
 * Copyright (c) 2020 The Quassel Project
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
import de.kuschku.libquassel.protocol.QVariantMap
import de.kuschku.libquassel.protocol.QtType
import de.kuschku.libquassel.protocol.value

object CoreSetupDataSerializer : HandshakeMessageSerializer<HandshakeMessage.CoreSetupData> {
  override fun serialize(data: HandshakeMessage.CoreSetupData) = mapOf(
    "MsgType" to QVariant.of("CoreSetupData", QtType.QString),
    "SetupData" to QVariant.of(mapOf(
      "AdminUser" to QVariant.of(data.adminUser, QtType.QString),
      "AdminPasswd" to QVariant.of(data.adminPassword, QtType.QString),
      "Backend" to QVariant.of(data.backend, QtType.QString),
      "ConnectionProperties" to QVariant.of(data.setupData, QtType.QVariantMap),
      "Authenticator" to QVariant.of(data.authenticator, QtType.QString),
      "AuthProperties" to QVariant.of(data.authSetupData, QtType.QVariantMap)
    ), QtType.QVariantMap
    )
  )

  override fun deserialize(data: QVariantMap): HandshakeMessage.CoreSetupData {
    val setupData = data["SetupData"].value<QVariantMap?>()
    return HandshakeMessage.CoreSetupData(
      adminUser = setupData?.get("AdminUser").value(),
      adminPassword = setupData?.get("AdminPasswd").value(),
      backend = setupData?.get("Backend").value(),
      setupData = setupData?.get("ConnectionProperties").value(),
      authenticator = setupData?.get("Authenticator").value(),
      authSetupData = setupData?.get("AuthProperties").value()
    )
  }
}
