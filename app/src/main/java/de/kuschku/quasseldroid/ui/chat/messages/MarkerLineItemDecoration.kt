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

package de.kuschku.quasseldroid.ui.chat.messages

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.support.annotation.AttrRes
import android.support.annotation.DimenRes
import android.support.v7.widget.RecyclerView
import android.view.View
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.util.helper.styledAttributes

class MarkerLineItemDecoration(
  private val adapter: MessageAdapter,
  context: Context,
  @DimenRes height: Int,
  @AttrRes markerlineColor: Int
) : RecyclerView.ItemDecoration() {
  private val bounds = Rect()
  private val height = context.resources.getDimensionPixelSize(height)
  private val color = context.theme.styledAttributes(markerlineColor) {
    Paint().apply {
      color = getColor(0, 0)
    }
  }

  override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
    c.save()
    val left: Int
    val right: Int
    if (parent.clipToPadding) {
      left = parent.paddingLeft
      right = parent.width - parent.paddingRight
      c.clipRect(left, parent.paddingTop, right, parent.height - parent.paddingBottom)
    } else {
      left = 0
      right = parent.width
    }

    val childCount = parent.childCount
    for (i in 0 until childCount) {
      val child = parent.getChildAt(i)
      if (child.getTag(R.id.tag_markerline) == true) {
        parent.getDecoratedBoundsWithMargins(child, bounds)
        val bottom = bounds.bottom + Math.round(child.translationY)
        val top = bottom - height
        c.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), color)
      }
    }
    c.restore()
  }

  override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView,
                              state: RecyclerView.State) {
    adapter[parent.getChildAdapterPosition(view)]?.let {
      if (it.isMarkerLine) {
        view.setTag(R.id.tag_markerline, true)
        outRect.set(0, 0, height, 0)
      } else {
        view.setTag(R.id.tag_markerline, false)
      }
    }
  }
}
