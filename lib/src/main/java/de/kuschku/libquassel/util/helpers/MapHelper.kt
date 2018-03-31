package de.kuschku.libquassel.util.helpers

fun <K, V> Map<K, V>.getOr(key: K, defValue: V) = this[key] ?: defValue

fun <K, V> MutableMap<K, V>.removeIfEqual(key: K, value: V): Boolean {
  if (!this.containsKey(key))
    return false

  if (this[key] != value)
    return false

  this.remove(key)
  return true
}
