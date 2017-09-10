package de.kuschku.quasseldroid_ng.quassel.syncables.interfaces.invokers

import de.kuschku.quasseldroid_ng.UnknownMethodException
import de.kuschku.quasseldroid_ng.WrongObjectTypeException
import de.kuschku.quasseldroid_ng.protocol.QVariantList

interface Invoker<out T> {
  val className: String
  @Throws(WrongObjectTypeException::class, UnknownMethodException::class)
  fun invoke(on: Any?, method: String, params: QVariantList)
}
