/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Koschinski
 * Copyright (c) 2018 Ken Børge Viktil
 * Copyright (c) 2018 Magnus Fjell
 * Copyright (c) 2018 Martin Sandsmark
 * Copyright (c) 2018 The Quassel Project
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
