package de.kuschku.libquassel.quassel.syncables.interfaces

import de.kuschku.libquassel.protocol.ARG
import de.kuschku.libquassel.protocol.QVariantMap
import de.kuschku.libquassel.protocol.QVariant_
import de.kuschku.libquassel.protocol.Type
import de.kuschku.libquassel.session.SignalProxy

interface ISyncableObject {
  val objectName: String
  var identifier: Pair<String, String>
  val className: String
  var initialized: Boolean
  val proxy: SignalProxy

  fun requestUpdate(properties: QVariantMap = toVariantMap()) {
    REQUEST("requestUpdate", ARG(properties, Type.QVariantMap))
  }

  fun update(properties: QVariantMap) {
    fromVariantMap(properties)
    SYNC("update", ARG(properties, Type.QVariantMap))
  }

  fun init() {}

  fun fromVariantMap(properties: QVariantMap) = Unit
  fun toVariantMap(): QVariantMap = emptyMap()
}

/*inline*/ fun ISyncableObject.SYNC(function: String, vararg arg: QVariant_) {
  // Don’t transmit calls back that we just got from the network
  if (initialized && proxy.shouldSync(className, objectName, function))
    proxy.callSync(className, objectName, function, arg.toList())
}

/*inline*/ fun ISyncableObject.REQUEST(function: String, vararg arg: QVariant_) {
  // Don’t transmit calls back that we just got from the network
  if (initialized && proxy.shouldSync(className, objectName, function))
    proxy.callSync(className, objectName, function, arg.toList())
}
