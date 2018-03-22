package de.kuschku.quasseldroid.util.helper

import android.annotation.SuppressLint
import android.content.SharedPreferences

fun SharedPreferences.editApply(f: SharedPreferences.Editor.() -> Unit) {
  val editor = this.edit()
  editor.f()
  editor.apply()
}

@SuppressLint("ApplySharedPref")
fun SharedPreferences.editCommit(f: SharedPreferences.Editor.() -> Unit) {
  val editor = this.edit()
  editor.f()
  editor.commit()
}
