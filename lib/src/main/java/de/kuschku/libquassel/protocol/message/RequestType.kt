package de.kuschku.libquassel.protocol.message

enum class RequestType(val value: Int) {
  Invalid(0),
  Sync(1),
  RpcCall(2),
  InitRequest(3),
  InitData(4),
  HeartBeat(5),
  HeartBeatReply(6);

  companion object {
    private val byId = enumValues<RequestType>().associateBy(
      RequestType::value
    )

    fun of(value: Int) = byId[value] ?: Invalid
  }
}
