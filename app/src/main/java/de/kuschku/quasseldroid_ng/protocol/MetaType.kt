package de.kuschku.quasseldroid_ng.protocol

import de.kuschku.quasseldroid_ng.protocol.primitive.serializer.*
import org.threeten.bp.LocalTime
import org.threeten.bp.OffsetDateTime

data class MetaType<T>(val klass: Class<T>, val type: Type, val serializer: Serializer<T>? = null,
                       val name: String = type.serializableName) {
  companion object {
    private val byName = HashMap<String, MetaType<*>>()
    private val byType = HashMap<Type, MetaType<*>>()

    private inline fun <reified T : Any?> addType(type: Type,
                                                  serializer: Serializer<T>? = null) {
      val metaType = MetaType(T::class.java, type, serializer, type.serializableName)
      byName[metaType.name] = metaType
      byType[metaType.type] = metaType
    }

    private inline fun <reified T : Any?> addType(type: Type, name: String,
                                                  serializer: Serializer<T>? = null) {
      val metaType = MetaType(T::class.java, type, serializer, name)
      byName[metaType.name] = metaType
    }

    private inline fun <reified T : Any?> addType(type: Type, name: QType,
                                                  serializer: Serializer<T>? = null) {
      addType(type, name.typeName, serializer)
    }

    init {
      addType(Type.Void, VoidSerializer)
      addType(Type.Bool, BoolSerializer)
      addType(Type.Char, ByteSerializer)
      addType(Type.UChar, ByteSerializer)
      addType(Type.Short, ShortSerializer)
      addType(Type.UShort, ShortSerializer)
      addType(Type.Int, IntSerializer)
      addType(Type.UInt, IntSerializer)
      addType(Type.Long, LongSerializer)
      addType(Type.ULong, LongSerializer)

      addType(Type.QTime, TimeSerializer)
      addType(Type.QDateTime, DateTimeSerializer)
      addType(Type.QChar, CharSerializer)
      addType(Type.QString, StringSerializer.UTF16)
      addType(Type.QByteArray, ByteArraySerializer)
      addType(Type.QStringList, StringListSerializer)
      addType(Type.QVariantList, VariantListSerializer)
      addType(Type.QVariantMap, VariantMapSerializer)
      addType(Type.QVariant, VariantSerializer)

      addType(Type.UserType, QType.BufferId, IntSerializer)
      addType(Type.UserType, QType.BufferInfo, BufferInfoSerializer)
      addType(Type.UserType, QType.DccConfig_IpDetectionMode, DccConfig_IpDetectionModeSerializer)
      addType(Type.UserType, QType.DccConfig_PortSelectionMode,
              DccConfig_PortSelectionModeSerializer)
      addType(Type.UserType, QType.IrcUser, VariantMapSerializer)
      addType(Type.UserType, QType.IrcChannel, VariantMapSerializer)
      addType(Type.UserType, QType.Identity, VariantMapSerializer)
      addType(Type.UserType, QType.IdentityId, IntSerializer)
      addType(Type.UserType, QType.MsgId, IntSerializer)
      addType(Type.UserType, QType.Message, MessageSerializer)
      addType(Type.UserType, QType.NetworkId, IntSerializer)
      addType(Type.UserType, QType.NetworkInfo, VariantMapSerializer)
      addType(Type.UserType, QType.Network_Server, VariantMapSerializer)
      addType(Type.UserType, QType.QHostAddress, HostAddressSerializer)

      addType(Type.QByteArray, "UTF8String", StringSerializer.UTF8)
      addType(Type.QByteArray, "CString", StringSerializer.C)

      addType(Type.Long, "PeerPtr", LongSerializer)
    }

    fun <T : Any?> get(key: String?): MetaType<T> =
      byName[key] as MetaType<T>? ?: throw(IllegalArgumentException("Type does not exist: $key"))


    fun <T : Any?> get(key: Type?): MetaType<T> =
      byType[key] as MetaType<T>? ?: throw(IllegalArgumentException("Type does not exist: $key"))

    fun <T : Any?> get(data: T): MetaType<T>? = when (data) {
      equals(null)      -> get(Type.Void)
      is Boolean        -> get<Boolean>(Type.Bool)
      is Byte           -> get<Byte>(Type.Char)
      is Short          -> get<Short>(Type.Short)
      is Int            -> get<Int>(Type.Int)
      is Long           -> get<Long>(Type.Long)
      is LocalTime      -> get<LocalTime>(Type.QTime)
      is OffsetDateTime -> get<OffsetDateTime>(Type.QDateTime)
      is String         -> get<String?>(Type.QString)
      is QVariant<*>    -> get<QVariant<*>>(Type.QVariant)
      is List<*>        -> if (data.isEmpty() || data[0] !is String) {
        get<List<*>>(Type.QVariantList)
      } else {
        get<QStringList>(Type.QStringList)
      }
      else              -> throw IllegalArgumentException("Unsupported type: $data")
    } as MetaType<T>
  }
}
