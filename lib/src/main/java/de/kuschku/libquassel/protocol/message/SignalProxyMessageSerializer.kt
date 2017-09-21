package de.kuschku.libquassel.protocol.message

import de.kuschku.libquassel.protocol.QVariantList

interface SignalProxyMessageSerializer<T : SignalProxyMessage> {
  fun serialize(data: T): QVariantList
  fun deserialize(data: QVariantList): T
}
