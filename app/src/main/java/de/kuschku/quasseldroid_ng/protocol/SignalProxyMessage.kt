package de.kuschku.quasseldroid_ng.protocol

import de.kuschku.quasseldroid_ng.protocol.primitive.serializer.StringSerializer.UTF8
import de.kuschku.quasseldroid_ng.protocol.primitive.serializer.deserializeString
import de.kuschku.quasseldroid_ng.protocol.primitive.serializer.serializeString
import org.threeten.bp.Instant
import java.nio.ByteBuffer

enum class RequestType(val value: Int) {
  Invalid(0),
  Sync(1),
  RpcCall(2),
  InitRequest(3),
  InitData(4),
  HeartBeat(5),
  HeartBeatReply(6);

  companion object {
    private val byId = enumValues<RequestType>().associateBy(RequestType::value)
    fun of(value: Int) = byId[value] ?: Invalid
  }
}

sealed class SignalProxyMessage {
  class SyncMessage(val className: String, val objectName: String, val slotName: String,
                    val params: QVariantList) : SignalProxyMessage() {
    override fun toString(): String {
      return "SyncMessage::$className:${objectName.hashCode()}/$slotName"
    }
  }

  class RpcCall(val slotName: String, val params: QVariantList) : SignalProxyMessage() {
    override fun toString(): String {
      return "RpcCall::$slotName"
    }
  }

  class InitRequest(val className: String, val objectName: String) : SignalProxyMessage() {
    override fun toString(): String {
      return "InitRequest::$className:${objectName.hashCode()}"
    }
  }

  class InitData(val className: String, val objectName: String, val initData: QVariantMap) :
    SignalProxyMessage() {
    override fun toString(): String {
      return "InitData::$className:${objectName.hashCode()}"
    }
  }

  class HeartBeat(val timestamp: Instant) : SignalProxyMessage() {
    override fun toString(): String {
      return "HeartBeat::$timestamp"
    }
  }

  class HeartBeatReply(val timestamp: Instant) : SignalProxyMessage() {
    override fun toString(): String {
      return "HeartBeatReply::$timestamp"
    }
  }

  companion object : SignalProxyMessageSerializer<SignalProxyMessage> {
    override fun serialize(data: SignalProxyMessage) = when (data) {
      is SignalProxyMessage.SyncMessage    -> SyncMessageSerializer.serialize(data)
      is SignalProxyMessage.RpcCall        -> RpcCallSerializer.serialize(data)
      is SignalProxyMessage.InitRequest    -> InitRequestSerializer.serialize(data)
      is SignalProxyMessage.InitData       -> InitDataSerializer.serialize(data)
      is SignalProxyMessage.HeartBeat      -> HeartBeatSerializer.serialize(data)
      is SignalProxyMessage.HeartBeatReply -> HeartBeatReplySerializer.serialize(data)
    }

    override fun deserialize(data: QVariantList): SignalProxyMessage {
      val type = data.first().value(-1)
      return when (RequestType.of(type)) {
        RequestType.Sync           -> SyncMessageSerializer.deserialize(data.drop(1))
        RequestType.RpcCall        -> RpcCallSerializer.deserialize(data.drop(1))
        RequestType.InitRequest    -> InitRequestSerializer.deserialize(data.drop(1))
        RequestType.InitData       -> InitDataSerializer.deserialize(data.drop(1))
        RequestType.HeartBeat      -> HeartBeatSerializer.deserialize(data.drop(1))
        RequestType.HeartBeatReply -> HeartBeatReplySerializer.deserialize(data.drop(1))
        else                       -> throw IllegalArgumentException("Invalid MsgType: $type")
      }
    }

  }
}

object SyncMessageSerializer : SignalProxyMessageSerializer<SignalProxyMessage.SyncMessage> {
  override fun serialize(data: SignalProxyMessage.SyncMessage): QVariantList = listOf(
    QVariant_(RequestType.Sync.value, Type.Int),
    QVariant_(data.className.serializeString(UTF8), Type.QByteArray),
    QVariant_(data.objectName.serializeString(UTF8), Type.QByteArray),
    QVariant_(data.slotName.serializeString(UTF8), Type.QByteArray),
    *data.params.toTypedArray()
  )

  override fun deserialize(data: QVariantList) = SignalProxyMessage.SyncMessage(
    data[0].value<ByteBuffer?>().deserializeString(UTF8) ?: "",
    data[1].value<ByteBuffer?>().deserializeString(UTF8) ?: "",
    data[2].value<ByteBuffer?>().deserializeString(UTF8) ?: "",
    data.drop(3)
  )
}

object RpcCallSerializer : SignalProxyMessageSerializer<SignalProxyMessage.RpcCall> {
  override fun serialize(data: SignalProxyMessage.RpcCall) = listOf(
    QVariant_(RequestType.RpcCall.value, Type.Int),
    QVariant_(data.slotName.serializeString(UTF8), Type.QByteArray),
    *data.params.toTypedArray()
  )

  override fun deserialize(data: QVariantList) = SignalProxyMessage.RpcCall(
    data[0].value<ByteBuffer?>().deserializeString(UTF8) ?: "",
    data.drop(1)
  )
}

object InitRequestSerializer : SignalProxyMessageSerializer<SignalProxyMessage.InitRequest> {
  override fun serialize(data: SignalProxyMessage.InitRequest) = listOf(
    QVariant_(RequestType.InitRequest.value, Type.Int),
    QVariant_(data.className.serializeString(UTF8), Type.QByteArray),
    QVariant_(data.objectName.serializeString(UTF8), Type.QByteArray)
  )

  override fun deserialize(data: QVariantList) = SignalProxyMessage.InitRequest(
    data[0].value<ByteBuffer?>().deserializeString(UTF8) ?: "",
    data[1].value<ByteBuffer?>().deserializeString(UTF8) ?: ""
  )
}

object InitDataSerializer : SignalProxyMessageSerializer<SignalProxyMessage.InitData> {
  override fun serialize(data: SignalProxyMessage.InitData) = listOf(
    QVariant_(RequestType.InitData.value, Type.Int),
    QVariant_(data.className.serializeString(UTF8), Type.QByteArray),
    QVariant_(data.objectName.serializeString(UTF8), Type.QByteArray),
    QVariant_(data.initData, Type.QVariantMap)
  )

  override fun deserialize(data: QVariantList) = SignalProxyMessage.InitData(
    data[0].value<ByteBuffer?>().deserializeString(UTF8) ?: "",
    data[1].value<ByteBuffer?>().deserializeString(UTF8) ?: "",
    data.drop(2).toVariantMap()
  )
}

object HeartBeatSerializer : SignalProxyMessageSerializer<SignalProxyMessage.HeartBeat> {
  override fun serialize(data: SignalProxyMessage.HeartBeat) = listOf(
    QVariant_(RequestType.HeartBeat.value, Type.Int),
    QVariant_(data.timestamp, Type.QDateTime)
  )

  override fun deserialize(data: QVariantList) = SignalProxyMessage.HeartBeat(
    data[0].value(Instant.EPOCH)
  )
}

object HeartBeatReplySerializer : SignalProxyMessageSerializer<SignalProxyMessage.HeartBeatReply> {
  override fun serialize(data: SignalProxyMessage.HeartBeatReply) = listOf(
    QVariant_(RequestType.HeartBeatReply.value, Type.Int),
    QVariant_(data.timestamp, Type.QDateTime)
  )

  override fun deserialize(data: QVariantList) = SignalProxyMessage.HeartBeatReply(
    data[0].value(Instant.EPOCH)
  )
}

interface SignalProxyMessageSerializer<T : SignalProxyMessage> {
  fun serialize(data: T): QVariantList
  fun deserialize(data: QVariantList): T
}
