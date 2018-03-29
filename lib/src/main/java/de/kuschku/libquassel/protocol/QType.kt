package de.kuschku.libquassel.protocol

import de.kuschku.libquassel.protocol.primitive.serializer.*

enum class QType(val typeName: String, val serializer: Serializer<*>,
                 val type: Type = Type.UserType) {
  BufferId("BufferId", IntSerializer),
  BufferInfo("BufferInfo", BufferInfoSerializer),
  DccConfig_IpDetectionMode("DccConfig::IpDetectionMode", DccConfig_IpDetectionModeSerializer),
  DccConfig_PortSelectionMode("DccConfig::PortSelectionMode",
                              DccConfig_PortSelectionModeSerializer),
  IrcUser("IrcUser", VariantMapSerializer),
  IrcChannel("IrcChannel", VariantMapSerializer),
  Identity("Identity", VariantMapSerializer),
  IdentityId("IdentityId", IntSerializer),
  Message("Message", MessageSerializer),
  MsgId("MsgId", IntSerializer),
  NetworkId("NetworkId", IntSerializer),
  NetworkInfo("NetworkInfo", VariantMapSerializer),
  Network_Server("Network::Server", VariantMapSerializer),
  QHostAddress("QHostAddress", HostAddressSerializer),
  PeerPtr("PeerPtr", LongSerializer, type = Type.Long);

  override fun toString() = "QType($typeName, $type)"

  companion object {
    private val map = values().associateBy(QType::typeName)
    fun of(name: String) = map[name]
  }
}
