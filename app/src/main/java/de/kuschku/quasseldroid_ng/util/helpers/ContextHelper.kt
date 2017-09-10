package de.kuschku.quasseldroid_ng.util.helpers

import android.content.Context
import android.os.Build

inline fun <reified T> Context.systemService(): T = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
  getSystemService(T::class.java)
} else {
  getSystemService(T::class.java.simpleName) as T
}
