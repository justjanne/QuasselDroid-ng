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

package de.kuschku.quasseldroid.util.ui

import android.annotation.SuppressLint
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View

class DoubleClickHelper(view: View) : View.OnTouchListener {
  var doubleClickListener: (() -> Unit)? = null

  private val gestureDetector = GestureDetector(
    view.context,
    object : GestureDetector.SimpleOnGestureListener() {
      override fun onDoubleTap(e: MotionEvent?): Boolean {
        doubleClickListener?.invoke()
        return true
      }

      override fun onDoubleTapEvent(e: MotionEvent?): Boolean {
        return true
      }
    }
  )

  @SuppressLint("ClickableViewAccessibility")
  override fun onTouch(v: View?, event: MotionEvent?) = gestureDetector.onTouchEvent(event)
}
