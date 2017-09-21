package de.kuschku.libquassel.quassel.exceptions

data class UnknownMethodException(val className: String, val methodName: String) :
  QuasselException()
