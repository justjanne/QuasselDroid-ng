package de.kuschku.libquassel.protocol

enum class QType(val typeName: String) {
  BufferId("BufferId"),
  BufferInfo("BufferInfo"),
  DccConfig_IpDetectionMode("DccConfig::IpDetectionMode"),
  DccConfig_PortSelectionMode("DccConfig::PortSelectionMode"),
  IrcUser("IrcUser"),
  IrcChannel("IrcChannel"),
  Identity("Identity"),
  IdentityId("IdentityId"),
  Message("Message"),
  MsgId("MsgId"),
  Network("Network"),
  NetworkId("NetworkId"),
  NetworkInfo("NetworkInfo"),
  Network_Server("Network::Server"),
  QHostAddress("QHostAddress")
}
