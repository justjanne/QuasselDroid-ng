package de.kuschku.quasseldroid_ng.quassel.syncables

import de.kuschku.quasseldroid_ng.protocol.QVariantMap
import de.kuschku.quasseldroid_ng.protocol.QVariant_
import de.kuschku.quasseldroid_ng.protocol.Type
import de.kuschku.quasseldroid_ng.protocol.value
import de.kuschku.quasseldroid_ng.quassel.syncables.interfaces.ICoreInfo
import de.kuschku.quasseldroid_ng.session.SignalProxy

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
