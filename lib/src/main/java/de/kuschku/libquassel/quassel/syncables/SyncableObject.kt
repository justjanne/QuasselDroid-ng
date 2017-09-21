package de.kuschku.libquassel.quassel.syncables

import de.kuschku.libquassel.quassel.syncables.interfaces.ISyncableObject
import de.kuschku.libquassel.session.SignalProxy

abstract class SyncableObject(
  override val proxy: SignalProxy,
  final override val className: String
) : ISyncableObject {
  final override var objectName: String = ""
    private set
  override var identifier: String = "$className:"
  override var initialized: Boolean = false

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
