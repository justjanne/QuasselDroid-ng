package de.kuschku.quasseldroid.util.helper

import android.util.LruCache

inline fun <K, V> LruCache<K, V>.getOrPut(key: K, value: () -> V) = get(key) ?: value().let {
  put(key, it)
  it
}
