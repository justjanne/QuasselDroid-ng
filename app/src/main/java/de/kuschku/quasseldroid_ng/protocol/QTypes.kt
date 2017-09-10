package de.kuschku.quasseldroid_ng.protocol

import de.kuschku.quasseldroid_ng.persistence.QuasselDatabase
import de.kuschku.quasseldroid_ng.protocol.primitive.serializer.StringSerializer
import de.kuschku.quasseldroid_ng.protocol.primitive.serializer.deserializeString
import de.kuschku.quasseldroid_ng.protocol.primitive.serializer.serializeString
import de.kuschku.quasseldroid_ng.quassel.BufferInfo
import de.kuschku.quasseldroid_ng.quassel.ProtocolFeature
import de.kuschku.quasseldroid_ng.quassel.QuasselFeature
import de.kuschku.quasseldroid_ng.quassel.syncables.interfaces.INetwork
import de.kuschku.quasseldroid_ng.util.Flags
import de.kuschku.quasseldroid_ng.util.ShortFlags
import java.nio.ByteBuffer

typealias QStringList = List<String?>
typealias All_ = Any?
typealias QVariant_ = QVariant<All_>
typealias QVariantMap = Map<String, QVariant_>
typealias QVariantList = List<QVariant_>

typealias IdentityId = Int
typealias BufferId = Int
typealias MsgId = Int
typealias NetworkId = Int

typealias Message_Type = QuasselDatabase.Message.MessageType
typealias Message_Types = Flags<Message_Type>

typealias Message_Flag = QuasselDatabase.Message.MessageFlag
typealias Message_Flags = Flags<Message_Flag>

typealias Quassel_Feature = QuasselFeature
typealias Quassel_Features = Flags<Quassel_Feature>

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

inline val SLOT
  get() = Throwable().stackTrace.first().methodName

typealias ARG = QVariant_

fun QVariantMap.toVariantList(): QVariantList =
  entries.flatMap { (key, value) ->
    listOf(QVariant_(key.serializeString(StringSerializer.UTF8), Type.QByteArray), value)
  }

fun QVariantList.toVariantMap(): QVariantMap =
  (0 until size step 2).map {
    Pair(
      get(it).value<ByteBuffer?>().deserializeString(StringSerializer.UTF8) ?: "",
      get(it + 1)
    )
  }.toMap()

fun <K, V> List<Map<K, V>>.transpose(): Map<K, List<V>> {
  val result = mutableMapOf<K, MutableList<V>>()
  forEach { map ->
    map.entries.forEach { (key, value) ->
      result.getOrPut(key, ::mutableListOf).add(value)
    }
  }
  return result
}


fun <K, V> Map<K, List<V>>.transpose(): List<Map<K, V>> {
  val result = MutableList(values.map(List<*>::size).max() ?: 0) { mutableMapOf<K, V>() }
  this.entries.forEach { (key, values) ->
    values.forEachIndexed { index, value ->
      result[index][key] = value
    }
  }
  return result
}

fun nickFromMask(mask: String): String {
  val (nick, _, _) = splitHostMask(mask)
  return nick
}

fun userFromMask(mask: String): String {
  val (_, user, _) = splitHostMask(mask)
  return user
}

fun hostFromMask(mask: String): String {
  val (_, _, host) = splitHostMask(mask)
  return host
}

fun splitHostMask(mask: String): Triple<String, String, String> {
  if (!mask.contains("@"))
    return Triple(mask, "", "")

  val (userPart, host) = mask.split("@", limit = 2)
  if (!userPart.contains("!"))
    return Triple(mask, "", host)

  val (nick, user) = userPart.split('!', limit = 2)
  return Triple(nick, user, host)
}
