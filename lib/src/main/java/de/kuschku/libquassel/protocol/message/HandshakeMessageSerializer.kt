package de.kuschku.libquassel.protocol.message

import de.kuschku.libquassel.protocol.QVariantMap

interface HandshakeMessageSerializer<T : HandshakeMessage> {
  fun serialize(data: T): QVariantMap
  fun deserialize(data: QVariantMap): T
}
