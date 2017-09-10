package de.kuschku.quasseldroid_ng.quassel.syncables.interfaces

import de.kuschku.libquassel.annotations.Slot
import de.kuschku.libquassel.annotations.Syncable
import de.kuschku.quasseldroid_ng.protocol.ARG
import de.kuschku.quasseldroid_ng.protocol.QVariantMap
import de.kuschku.quasseldroid_ng.protocol.SLOT
import de.kuschku.quasseldroid_ng.protocol.Type

@Syncable(name = "IgnoreListManager")
interface IIgnoreListManager : ISyncableObject {
  fun initIgnoreList(): QVariantMap
  fun initSetIgnoreList(ignoreList: QVariantMap)
  @Slot
  fun addIgnoreListItem(type: Int, ignoreRule: String, isRegEx: Boolean, strictness: Int,
                        scope: Int, scopeRule: String, isActive: Boolean)

  @Slot
  fun removeIgnoreListItem(ignoreRule: String)

  @Slot
  fun requestAddIgnoreListItem(type: Int, ignoreRule: String, isRegEx: Boolean, strictness: Int,
                               scope: Int, scopeRule: String, isActive: Boolean) {
    REQUEST(SLOT, ARG(type, Type.Int), ARG(ignoreRule, Type.QString), ARG(isRegEx, Type.Bool),
            ARG(strictness, Type.Int), ARG(scope, Type.Int), ARG(scopeRule, Type.QString),
            ARG(isActive, Type.Bool))
  }

  @Slot
  fun requestRemoveIgnoreListItem(ignoreRule: String) {
    REQUEST(SLOT, ARG(ignoreRule, Type.QString))
  }

  @Slot
  fun requestToggleIgnoreRule(ignoreRule: String) {
    REQUEST(SLOT, ARG(ignoreRule, Type.QString))
  }

  @Slot
  fun toggleIgnoreRule(ignoreRule: String)

  @Slot
  override fun update(properties: QVariantMap) {
    super.update(properties)
  }
}
