package de.kuschku.libquassel.quassel.syncables

import de.kuschku.libquassel.protocol.QVariantMap
import de.kuschku.libquassel.protocol.QVariant_
import de.kuschku.libquassel.protocol.Type
import de.kuschku.libquassel.protocol.value
import de.kuschku.libquassel.quassel.syncables.interfaces.ICoreInfo
import de.kuschku.libquassel.session.SignalProxy

class CoreInfo constructor(
  proxy: SignalProxy
) : SyncableObject(proxy, "CoreInfo"), ICoreInfo {
  override fun toVariantMap() = initProperties()

  override fun fromVariantMap(properties: QVariantMap) {
    initSetProperties(properties)
  }

  override fun initProperties(): QVariantMap = mapOf(
    "coreData" to QVariant_(coreData(), Type.QVariantMap)
  )

  override fun initSetProperties(properties: QVariantMap) {
    setCoreData(properties["coreData"].value(coreData()))
  }

  override fun setCoreData(data: QVariantMap) {
    _coreData = data
    super.setCoreData(data)
  }

  fun coreData() = _coreData

  private var _coreData: QVariantMap = emptyMap()
}
