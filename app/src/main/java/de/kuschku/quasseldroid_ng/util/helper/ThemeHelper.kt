package de.kuschku.quasseldroid_ng.util.helper

import android.content.res.Resources
import android.content.res.TypedArray

inline fun Resources.Theme.styledAttributes(vararg attributes: Int, f: TypedArray.() -> Unit) {
  this.obtainStyledAttributes(attributes).use {
    it.apply(f)
  }
}

inline fun <R> TypedArray.use(block: (TypedArray) -> R): R {
  val result = block(this)
  recycle()
  return result
}