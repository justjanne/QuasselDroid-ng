package de.kuschku.libquassel.quassel.exceptions

data class WrongObjectTypeException(val obj: Any?, val type: String) : QuasselException()
