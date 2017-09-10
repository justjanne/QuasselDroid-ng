package de.kuschku.quasseldroid_ng.util.helpers

fun <K, V> Map<K, V>.getOr(key: K, defValue: V)
  = this[key] ?: defValue
