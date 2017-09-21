package de.kuschku.libquassel.quassel.syncables.interfaces.invokers

import de.kuschku.libquassel.protocol.QVariantList
import de.kuschku.libquassel.quassel.exceptions.UnknownMethodException
import de.kuschku.libquassel.quassel.exceptions.WrongObjectTypeException

interface Invoker<out T> {
  val className: String
  @Throws(WrongObjectTypeException::class, UnknownMethodException::class)
  fun invoke(on: Any?, method: String, params: QVariantList)
}
