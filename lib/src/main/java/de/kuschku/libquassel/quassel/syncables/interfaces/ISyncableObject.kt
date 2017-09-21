package de.kuschku.libquassel.quassel.syncables.interfaces

import de.kuschku.libquassel.protocol.*

interface ISyncableObject {
  val objectName: String
  var identifier: String
  val className: String
  var initialized: Boolean
  fun SYNC(function: String, vararg arg: QVariant_)
  fun REQUEST(function: String, vararg arg: QVariant_)
  fun requestUpdate(properties: QVariantMap = toVariantMap()) {
    REQUEST(SLOT, ARG(properties, Type.QVariantMap))
  }

  fun update(properties: QVariantMap) {
    fromVariantMap(properties)
    SYNC(SLOT, ARG(properties, Type.QVariantMap))
  }

  fun init() {}

  fun fromVariantMap(properties: QVariantMap) = Unit
  fun toVariantMap(): QVariantMap = emptyMap()
}
