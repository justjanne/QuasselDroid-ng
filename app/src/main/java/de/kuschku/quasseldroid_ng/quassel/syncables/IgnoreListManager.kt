package de.kuschku.quasseldroid_ng.quassel.syncables

import de.kuschku.quasseldroid_ng.protocol.QVariantMap
import de.kuschku.quasseldroid_ng.protocol.QVariant_
import de.kuschku.quasseldroid_ng.protocol.Type
import de.kuschku.quasseldroid_ng.protocol.valueOr
import de.kuschku.quasseldroid_ng.quassel.syncables.interfaces.IIgnoreListManager
import de.kuschku.quasseldroid_ng.session.SignalProxy

class IgnoreListManager constructor(
  proxy: SignalProxy
) : SyncableObject(proxy, "IgnoreListManager"), IIgnoreListManager {
  override fun toVariantMap(): QVariantMap = mapOf(
    "IgnoreList" to QVariant_(initIgnoreList(), Type.QVariantMap)
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
