package de.kuschku.libquassel.util.helpers

fun <K, V> Map<K, V>.getOr(key: K, defValue: V)
  = this[key] ?: defValue
