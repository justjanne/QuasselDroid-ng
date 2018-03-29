package de.kuschku.libquassel.quassel.syncables

import de.kuschku.libquassel.protocol.QVariant
import de.kuschku.libquassel.protocol.QVariantMap
import de.kuschku.libquassel.protocol.Type
import de.kuschku.libquassel.protocol.valueOr
import de.kuschku.libquassel.quassel.syncables.interfaces.IIgnoreListManager
import de.kuschku.libquassel.session.SignalProxy

class IgnoreListManager constructor(
  proxy: SignalProxy
) : SyncableObject(proxy, "IgnoreListManager"), IIgnoreListManager {
  override fun toVariantMap(): QVariantMap = mapOf(
    "IgnoreList" to QVariant.of(initIgnoreList(), Type.QVariantMap)
  )

  override fun fromVariantMap(properties: QVariantMap) {
    initSetIgnoreList(properties["IgnoreList"].valueOr(::emptyMap))
  }

  override fun initIgnoreList(): QVariantMap = emptyMap()

  override fun initSetIgnoreList(ignoreList: QVariantMap) {
  }

  override fun addIgnoreListItem(type: Int, ignoreRule: String, isRegEx: Boolean, strictness: Int,
                                 scope: Int, scopeRule: String, isActive: Boolean) {
  }

  override fun removeIgnoreListItem(ignoreRule: String) {
  }

  override fun toggleIgnoreRule(ignoreRule: String) {
  }

}
