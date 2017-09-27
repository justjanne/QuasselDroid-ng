package de.kuschku.quasseldroid_ng.util.helper

import android.content.SharedPreferences

fun SharedPreferences.editApply(f: SharedPreferences.Editor.() -> Unit) {
  val editor = this.edit()
  editor.f()
  editor.apply()
}

fun SharedPreferences.editCommit(f: SharedPreferences.Editor.() -> Unit) {
  val editor = this.edit()
  editor.f()
  editor.commit()
}
