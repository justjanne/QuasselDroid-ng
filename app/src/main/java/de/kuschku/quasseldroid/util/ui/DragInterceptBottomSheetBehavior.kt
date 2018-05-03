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

import android.content.Context
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.CoordinatorLayout
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class DragInterceptBottomSheetBehavior<V : View> : BottomSheetBehavior<V> {
  constructor() : super()
  constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

  var allowDragging = true

  override fun onInterceptTouchEvent(parent: CoordinatorLayout?, child: V,
                                     event: MotionEvent?): Boolean {
    if (!allowDragging) return false
    return super.onInterceptTouchEvent(parent, child, event)
  }

  companion object {
    /**
     * A utility function to get the [BottomSheetBehavior] associated with the `view`.
     *
     * @param view The [View] with [BottomSheetBehavior].
     * @return The [BottomSheetBehavior] associated with the `view`.
     */
    fun <V : View> from(view: V?): DragInterceptBottomSheetBehavior<V> {
      val params = view?.layoutParams as? CoordinatorLayout.LayoutParams
                   ?: throw IllegalArgumentException("The view is not a child of CoordinatorLayout")
      val behavior = params.behavior as? DragInterceptBottomSheetBehavior<*>
                     ?: throw IllegalArgumentException("The view is not associated with BottomSheetBehavior")
      return behavior as DragInterceptBottomSheetBehavior<V>
    }
  }
}
