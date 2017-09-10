package de.kuschku.quasseldroid_ng.protocol

import de.kuschku.quasseldroid_ng.util.Flags


sealed class HandshakeMessage {
  class ClientInit(val clientVersion: String?, val buildDate: String?,
                   val clientFeatures: Quassel_Features?) : HandshakeMessage() {
    override fun toString(): String {
      return "ClientInit(clientVersion=$clientVersion, buildDate=$buildDate, clientFeatures=$clientFeatures)"
    }
  }

  class ClientInitReject(val errorString: String?) : HandshakeMessage() {
    override fun toString(): String {
      return "ClientInitReject(errorString=$errorString)"
    }
  }

  class ClientInitAck(val coreFeatures: Quassel_Features?, val coreConfigured: Boolean?,
                      val backendInfo: QVariantList?,
                      val authenticatorInfo: QVariantList?) : HandshakeMessage() {
    override fun toString(): String {
      return "ClientInitAck(coreFeatures=$coreFeatures, coreConfigured=$coreConfigured, backendInfo=$backendInfo, authenticatorInfo=$authenticatorInfo)"
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
      return "SessionInit(identities=$identities, bufferInfos=$bufferInfos, networkIds=$networkIds)"
    }
  }

  companion object : HandshakeMessageSerializer<HandshakeMessage> {
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

object ClientInitSerializer : HandshakeMessageSerializer<HandshakeMessage.ClientInit> {
  override fun serialize(data: HandshakeMessage.ClientInit) = mapOf(
    "MsgType" to QVariant_(data::class.java.simpleName, Type.QString),
    "ClientVersion" to QVariant_(data.clientVersion, Type.QString),
    "ClientDate" to QVariant_(data.buildDate, Type.QString),
    "ClientFeatures" to QVariant_(data.clientFeatures?.toInt(), Type.UInt)
  )

  override fun deserialize(data: QVariantMap) = HandshakeMessage.ClientInit(
    clientVersion = data["ClientVersion"].value(),
    buildDate = data["ClientDate"].value(),
    clientFeatures = Flags.of(data["ClientFeatures"].value(0))
  )
}

object ClientInitRejectSerializer : HandshakeMessageSerializer<HandshakeMessage.ClientInitReject> {
  override fun serialize(data: HandshakeMessage.ClientInitReject) = mapOf(
    "MsgType" to QVariant_(data::class.java.simpleName, Type.QString),
    "Error" to QVariant_(data.errorString, Type.QString)
  )

  override fun deserialize(data: QVariantMap) = HandshakeMessage.ClientInitReject(
    errorString = data["Error"].value()
  )
}

object ClientInitAckSerializer : HandshakeMessageSerializer<HandshakeMessage.ClientInitAck> {
  override fun serialize(data: HandshakeMessage.ClientInitAck) = mapOf(
    "MsgType" to QVariant_(data::class.java.simpleName, Type.QString),
    "CoreFeatures" to QVariant_(data.coreFeatures?.toInt(), Type.UInt),
    "StorageBackends" to QVariant_(data.backendInfo, Type.QVariantList),
    "Authenticator" to QVariant_(data.authenticatorInfo, Type.QVariantList),
    "Configured" to QVariant_(data.coreConfigured, Type.Bool)
  )

  override fun deserialize(data: QVariantMap) = HandshakeMessage.ClientInitAck(
    coreFeatures = Flags.of(data["CoreFeatures"].value(0)),
    backendInfo = data["StorageBackends"].value(),
    authenticatorInfo = data["Authenticators"].value(),
    coreConfigured = data["Configured"].value()
  )
}

object CoreSetupDataSerializer : HandshakeMessageSerializer<HandshakeMessage.CoreSetupData> {
  override fun serialize(data: HandshakeMessage.CoreSetupData) = mapOf(
    "MsgType" to QVariant_(data::class.java.simpleName, Type.QString),
    "SetupData" to QVariant_(mapOf(
      "AdminUser" to QVariant_(data.adminUser, Type.QString),
      "AdminPasswd" to QVariant_(data.adminPassword, Type.QString),
      "Backend" to QVariant_(data.backend, Type.QString),
      "ConnectionProperties" to QVariant_(data.setupData, Type.QVariantMap),
      "Authenticator" to QVariant_(data.authenticator, Type.QString),
      "AuthProperties" to QVariant_(data.authSetupData, Type.QVariantMap)
    ), Type.QVariantMap)
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

object CoreSetupRejectSerializer : HandshakeMessageSerializer<HandshakeMessage.CoreSetupReject> {
  override fun serialize(data: HandshakeMessage.CoreSetupReject) = mapOf(
    "MsgType" to QVariant_(data::class.java.simpleName, Type.QString),
    "Error" to QVariant_(data.errorString, Type.QString)
  )

  override fun deserialize(data: QVariantMap) = HandshakeMessage.CoreSetupReject(
    errorString = data["Error"].value()
  )
}

object CoreSetupAckSerializer : HandshakeMessageSerializer<HandshakeMessage.CoreSetupAck> {
  override fun serialize(data: HandshakeMessage.CoreSetupAck) = mapOf(
    "MsgType" to QVariant_(data::class.java.simpleName, Type.QString)
  )

  override fun deserialize(data: QVariantMap) = HandshakeMessage.CoreSetupAck()
}

object ClientLoginSerializer : HandshakeMessageSerializer<HandshakeMessage.ClientLogin> {
  override fun serialize(data: HandshakeMessage.ClientLogin) = mapOf(
    "MsgType" to QVariant_(data::class.java.simpleName, Type.QString),
    "User" to QVariant_(data.user, Type.QString),
    "Password" to QVariant_(data.password, Type.QString)
  )

  override fun deserialize(data: QVariantMap) = HandshakeMessage.ClientLogin(
    user = data["User"].value(),
    password = data["Password"].value()
  )
}

object ClientLoginRejectSerializer :
  HandshakeMessageSerializer<HandshakeMessage.ClientLoginReject> {
  override fun serialize(data: HandshakeMessage.ClientLoginReject) = mapOf(
    "MsgType" to QVariant_(data::class.java.simpleName, Type.QString),
    "Error" to QVariant_(data.errorString, Type.QString)
  )

  override fun deserialize(data: QVariantMap) = HandshakeMessage.ClientLoginReject(
    errorString = data["Error"].value()
  )
}

object ClientLoginAckSerializer : HandshakeMessageSerializer<HandshakeMessage.ClientLoginAck> {
  override fun serialize(data: HandshakeMessage.ClientLoginAck) = mapOf(
    "MsgType" to QVariant_(data::class.java.simpleName, Type.QString)
  )

  override fun deserialize(data: QVariantMap) = HandshakeMessage.ClientLoginAck()
}

object SessionInitSerializer : HandshakeMessageSerializer<HandshakeMessage.SessionInit> {
  override fun serialize(data: HandshakeMessage.SessionInit) = mapOf(
    "MsgType" to QVariant_(data::class.java.simpleName, Type.QString),
    "SessionState" to QVariant_(mapOf(
      "BufferInfos" to QVariant_(data.bufferInfos, Type.QVariantList),
      "NetworkIds" to QVariant_(data.networkIds, Type.QVariantList),
      "Identities" to QVariant_(data.identities, Type.QVariantList)
    ), Type.QVariantMap)
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

interface HandshakeMessageSerializer<T : HandshakeMessage> {
  fun serialize(data: T): QVariantMap
  fun deserialize(data: QVariantMap): T
}
