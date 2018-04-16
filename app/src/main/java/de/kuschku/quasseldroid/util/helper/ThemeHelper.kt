package de.kuschku.quasseldroid.util.helper

import android.content.res.Resources
import android.content.res.TypedArray

inline fun <R> Resources.Theme.styledAttributes(vararg attributes: Int, f: TypedArray.() -> R) =
  this.obtainStyledAttributes(attributes).run {
    f()
  }

inline fun <R> TypedArray.use(block: (TypedArray) -> R): R {
  val result = block(this)
  recycle()
  return result
}
