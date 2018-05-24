/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Koschinski
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

package de.kuschku.quasseldroid.util

import android.app.Activity
import android.text.Editable
import android.text.TextWatcher

abstract class TextValidator(private val activity: Activity,
                             private val errorListener: (String?) -> Unit,
                             private val error: String) : TextWatcher {
  override fun afterTextChanged(p0: Editable) {
    isValid = validate(p0)
    activity.runOnUiThread {
      errorListener(if (isValid) null else error)
    }
    onChanged()
  }

  protected open fun onChanged() = Unit

  override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit
  override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit

  abstract fun validate(text: Editable): Boolean
  var isValid = false
    private set
}
