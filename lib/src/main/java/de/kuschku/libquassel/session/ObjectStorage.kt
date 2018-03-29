package de.kuschku.libquassel.session

import de.kuschku.libquassel.protocol.QType
import de.kuschku.libquassel.protocol.QVariant
import de.kuschku.libquassel.protocol.Type
import de.kuschku.libquassel.protocol.message.SignalProxyMessage
import de.kuschku.libquassel.quassel.exceptions.ObjectNotFoundException
import de.kuschku.libquassel.quassel.syncables.interfaces.ISyncableObject

class ObjectStorage(private val proxy: SignalProxy) {
  private val objectTree: MutableMap<Pair<String, String>, ISyncableObject> = HashMap()

  fun add(obj: ISyncableObject) {
    objectTree[obj.identifier] = obj
    if (get(obj.className, obj.objectName) != obj) {
      throw IllegalStateException("Object should be existing")
    }
  }

  fun remove(obj: ISyncableObject) {
    objectTree.remove(obj.identifier)
    if (get(obj.className, obj.objectName) == obj) {
      throw IllegalStateException("Object should not be existing")
    }
  }

  fun rename(className: String, new: String, old: String) {
    val obj = get(className, old)
    if (obj != null) {
      rename(obj, new, old)
    } else {
      throw ObjectNotFoundException(className, old)
    }
  }

  fun rename(obj: ISyncableObject, new: String, old: String) {
    objectTree[Pair(obj.className, new)] = obj
    objectTree.remove(Pair(obj.className, old), obj)
    if (get(obj.className, new) != obj) {
      throw IllegalStateException("Object should be existing")
    }
    if (get(obj.className, old) == obj) {
      throw IllegalStateException("Object should not be referenced by the old name")
    }
    if (proxy.shouldRpc("__objectRenamed__")) {
      proxy.dispatch(
        SignalProxyMessage.RpcCall(
          "__objectRenamed__",
          listOf(
            QVariant.of(obj.className, Type.QString), QVariant.of(new, Type.QString),
            QVariant.of(old, Type.QString)
          )
        )
      )
    }
  }

  fun get(className: QType, objectName: String) = get(className.typeName, objectName)
  fun get(className: String, objectName: String) = objectTree[Pair(className, objectName)]

  fun clear() = objectTree.clear()
}
