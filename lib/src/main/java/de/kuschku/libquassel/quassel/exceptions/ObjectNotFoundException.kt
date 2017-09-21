package de.kuschku.libquassel.quassel.exceptions

data class ObjectNotFoundException(val className: String, val objectName: String) :
  QuasselException()
