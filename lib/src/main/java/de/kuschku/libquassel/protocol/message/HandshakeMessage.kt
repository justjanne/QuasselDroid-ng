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

import de.kuschku.libquassel.protocol.Legacy_Features
import de.kuschku.libquassel.protocol.QVariantList
import de.kuschku.libquassel.protocol.QVariantMap
import de.kuschku.libquassel.protocol.value
import java.io.Serializable

sealed class HandshakeMessage : Serializable {
  class ClientInit(
    val clientVersion: String?, val buildDate: String?,
    val clientFeatures: Legacy_Features?, val featureList: List<String>
  ) : HandshakeMessage() {
    override fun toString(): String {
      return "ClientInit(clientVersion=$clientVersion, buildDate=$buildDate, clientFeatures=$clientFeatures, featureList=$featureList)"
    }
  }

  class ClientInitReject(val errorString: String?) : HandshakeMessage() {
    override fun toString(): String {
      return "ClientInitReject(errorString=$errorString)"
    }
  }

  class ClientInitAck(
    val coreFeatures: Legacy_Features?, val coreConfigured: Boolean?,
    val backendInfo: QVariantList?, val authenticatorInfo: QVariantList?,
    val featureList: List<String>
  ) : HandshakeMessage() {
    override fun toString(): String {
      return "ClientInitAck(coreFeatures=$coreFeatures, coreConfigured=$coreConfigured, backendInfo=$backendInfo, authenticatorInfo=$authenticatorInfo, featureList=$featureList)"
    }
  }

  class CoreSetupData(val adminUser: String?, val adminPassword: String?, val backend: String?,
                      val setupData: QVariantMap?, val authenticator: String?,
                      val authSetupData: QVariantMap?) :
    HandshakeMessage() {
    override fun toString(): String {
      return "CoreSetupData(adminUser=$adminUser, adminPassword=$adminPassword, backend=$backend, setupData=$setupData, authenticator=$authenticator, authSetupData=$authSetupData)"
    }
  }

  class CoreSetupReject(val errorString: String?) : HandshakeMessage() {
    override fun toString(): String {
      return "CoreSetupReject(errorString=$errorString)"
    }
  }

  class CoreSetupAck : HandshakeMessage() {
    override fun toString(): String {
      return "CoreSetupAck"
    }
  }

  class ClientLogin(val user: String?, val password: String?) : HandshakeMessage() {
    override fun toString(): String {
      return "ClientLogin"
    }
  }

  class ClientLoginReject(val errorString: String?) : HandshakeMessage() {
    override fun toString(): String {
      return "ClientLoginReject(errorString=$errorString)"
    }
  }

  class ClientLoginAck : HandshakeMessage() {
    override fun toString(): String {
      return "ClientLoginAck"
    }
  }

  class SessionInit(val identities: QVariantList?, val bufferInfos: QVariantList?,
                    val networkIds: QVariantList?) :
    HandshakeMessage() {
    override fun toString(): String {
      return "SessionInit"
    }
  }

  companion object :
    HandshakeMessageSerializer<HandshakeMessage> {
    override fun serialize(data: HandshakeMessage) = when (data) {
      is ClientInit        -> ClientInitSerializer.serialize(data)
      is ClientInitReject  -> ClientInitRejectSerializer.serialize(data)
      is ClientInitAck     -> ClientInitAckSerializer.serialize(data)
      is CoreSetupData     -> CoreSetupDataSerializer.serialize(data)
      is CoreSetupReject   -> CoreSetupRejectSerializer.serialize(data)
      is CoreSetupAck      -> CoreSetupAckSerializer.serialize(data)
      is ClientLogin       -> ClientLoginSerializer.serialize(data)
      is ClientLoginReject -> ClientLoginRejectSerializer.serialize(data)
      is ClientLoginAck    -> ClientLoginAckSerializer.serialize(data)
      is SessionInit       -> SessionInitSerializer.serialize(data)
    }

    override fun deserialize(data: QVariantMap): HandshakeMessage {
      val msgType = data["MsgType"].value<String?>()
      return when (msgType) {
        "ClientInit"        -> ClientInitSerializer.deserialize(data)
        "ClientInitReject"  -> ClientInitRejectSerializer.deserialize(data)
        "ClientInitAck"     -> ClientInitAckSerializer.deserialize(data)
        "CoreSetupData"     -> CoreSetupDataSerializer.deserialize(data)
        "CoreSetupReject"   -> CoreSetupRejectSerializer.deserialize(data)
        "CoreSetupAck"      -> CoreSetupAckSerializer.deserialize(data)
        "ClientLogin"       -> ClientLoginSerializer.deserialize(data)
        "ClientLoginReject" -> ClientLoginRejectSerializer.deserialize(data)
        "ClientLoginAck"    -> ClientLoginAckSerializer.deserialize(data)
        "SessionInit"       -> SessionInitSerializer.deserialize(data)
        else                -> throw IllegalArgumentException(
          "Invalid MsgType: $msgType"
        )
      }
    }
  }
}
