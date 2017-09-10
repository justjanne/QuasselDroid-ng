package de.kuschku.quasseldroid_ng.quassel.syncables

import de.kuschku.quasseldroid_ng.protocol.QVariant_
import de.kuschku.quasseldroid_ng.quassel.syncables.interfaces.ISyncableObject
import de.kuschku.quasseldroid_ng.session.SignalProxy

abstract class SyncableObject(
  protected val proxy: SignalProxy,
  final override val className: String
) : ISyncableObject {
  final override var objectName: String = ""
    private set
  override var identifier: String = "$className:"
  override var initialized: Boolean = false

  override fun SYNC(function: String, vararg arg: QVariant_) {
    if (initialized)
      proxy.callSync(className, objectName, function, arg.toList())
  }

  override fun REQUEST(function: String, vararg arg: QVariant_) {
    if (initialized)
      proxy.callSync(className, objectName, function, arg.toList())
  }

  protected fun renameObject(newName: String) {
    val oldName = objectName
    if (!initialized) {
      objectName = newName
      identifier = "$className:$objectName"
    } else if (oldName != newName) {
      objectName = newName
      identifier = "$className:$objectName"
      proxy.renameObject(this, newName, oldName)
    }
  }

  override fun toString() = identifier
}
