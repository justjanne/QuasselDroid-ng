package de.kuschku.libquassel.session

import de.kuschku.libquassel.protocol.QType
import de.kuschku.libquassel.protocol.QVariant_
import de.kuschku.libquassel.protocol.Type
import de.kuschku.libquassel.protocol.message.SignalProxyMessage
import de.kuschku.libquassel.quassel.exceptions.ObjectNotFoundException
import de.kuschku.libquassel.quassel.syncables.interfaces.ISyncableObject

class ObjectStorage(private val proxy: SignalProxy) {
  private val objectTree: MutableMap<String, ISyncableObject> = HashMap()

  fun add(obj: ISyncableObject) = objectTree.put(obj.identifier, obj)

  fun remove(obj: ISyncableObject) = objectTree.remove(obj.identifier)

  fun rename(className: String, new: String, old: String) {
    val obj = get(className, old) ?: throw ObjectNotFoundException(className, old)
    rename(obj, new, old)
  }

  fun rename(obj: ISyncableObject, new: String, old: String) {
    objectTree.put("${obj.className}:$new", obj)
    objectTree.remove("${obj.className}:$old")
    proxy.dispatch(
      SignalProxyMessage.RpcCall("__objectRenamed__", listOf(
        QVariant_(obj.className, Type.QString), QVariant_(new, Type.QString),
        QVariant_(old, Type.QString))
      ))
  }

  fun get(className: QType, objectName: String) = get(className.typeName, objectName)
  fun get(className: String, objectName: String) = objectTree["$className:$objectName"]

  fun clear() = objectTree.clear()
}
