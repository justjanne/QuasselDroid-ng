package de.kuschku.quasseldroid_ng

abstract class QuasselException : Exception()
data class ObjectNotFoundException(val className: String, val objectName: String) :
  QuasselException()

data class WrongObjectTypeException(val obj: Any?, val type: String) : QuasselException()
data class UnknownMethodException(val className: String, val methodName: String) :
  QuasselException()
