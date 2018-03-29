package de.kuschku.libquassel.protocol

import de.kuschku.libquassel.protocol.primitive.serializer.StringSerializer
import de.kuschku.libquassel.quassel.BufferInfo
import de.kuschku.libquassel.quassel.LegacyFeature
import de.kuschku.libquassel.quassel.ProtocolFeature
import de.kuschku.libquassel.quassel.syncables.interfaces.INetwork
import de.kuschku.libquassel.util.Flags
import de.kuschku.libquassel.util.ShortFlags
import de.kuschku.libquassel.util.helpers.deserializeString
import java.nio.ByteBuffer

typealias QStringList = List<String?>
typealias All_ = Any?
typealias QVariant_ = QVariant<*>
typealias QVariantMap = Map<String, QVariant_>
typealias QVariantList = List<QVariant_>

typealias IdentityId = Int
typealias BufferId = Int
typealias MsgId = Int
typealias NetworkId = Int

typealias Message_Type = Message.MessageType
typealias Message_Types = Flags<Message_Type>

typealias Message_Flag = Message.MessageFlag
typealias Message_Flags = Flags<Message_Flag>

typealias Legacy_Feature = LegacyFeature
typealias Legacy_Features = Flags<Legacy_Feature>

typealias Protocol_Feature = ProtocolFeature
typealias Protocol_Features = Flags<Protocol_Feature>

typealias Network_ChannelModeType = INetwork.ChannelModeType
typealias Network_ChannelModeTypes = Flags<Network_ChannelModeType>

typealias Buffer_Type = BufferInfo.Type
typealias Buffer_Types = ShortFlags<Buffer_Type>

typealias Buffer_Activity = BufferInfo.Activity
typealias Buffer_Activities = Flags<Buffer_Activity>

typealias UByte = Byte
typealias UShort = Short
typealias UInt = Int
typealias ULong = Long

inline fun <T> ARG(data: T?, type: Type) = QVariant.of(data, type)
inline fun <T> ARG(data: T?, type: QType) = QVariant.of(data, type)

fun QVariantList.toVariantMap(): QVariantMap {
  val map = HashMap<String, QVariant_>()
  var i = 0
  while (i < size) {
    val key = get(i).value<ByteBuffer?>().deserializeString(StringSerializer.UTF8) ?: ""
    val value = get(i + 1)
    map[key] = value
    i += 2
  }
  return map
}

fun <K, V> List<Map<K, V>>.transpose(): Map<K, List<V>> {
  val result = mutableMapOf<K, MutableList<V>>()
  forEach { map ->
    map.entries.forEach { (key, value) ->
      result.getOrPut(key, ::mutableListOf).add(value)
    }
  }
  return result
}
