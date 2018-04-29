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

package de.kuschku.quasseldroid.ui.chat.messages

import android.graphics.Canvas
import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import de.kuschku.quasseldroid.R
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle
import org.threeten.bp.temporal.ChronoUnit

class DayChangeItemDecoration(
  private val adapter: MessageAdapter,
  private val textSize: Int
) :
  RecyclerView.ItemDecoration() {
  private val dayChangeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
  private val bounds = Rect()

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
      if (child.getTag(R.id.tag_daychange) == true) {
        parent.getDecoratedBoundsWithMargins(child, bounds)
        val bottom = bounds.bottom + Math.round(child.translationY)
        val top = bounds.top + Math.round(child.translationY)
        val layout = child.getTag(R.id.tag_daychange_layout) as View
        c.save()
        c.clipRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat())
        c.translate(left.toFloat(), top.toFloat())
        layout.draw(c)
        c.restore()
      }
    }
    c.restore()
  }

  private fun fixLayoutSize(view: View, parent: ViewGroup) {
    val widthSpec = View.MeasureSpec.makeMeasureSpec(
      parent.width,
      View.MeasureSpec.EXACTLY
    )
    val heightSpec = View.MeasureSpec.makeMeasureSpec(
      parent.height,
      View.MeasureSpec.UNSPECIFIED
    )

    val childWidthSpec = ViewGroup.getChildMeasureSpec(
      widthSpec,
      parent.paddingLeft + parent.paddingRight,
      view.layoutParams.width
    )
    val childHeightSpec = ViewGroup.getChildMeasureSpec(
      heightSpec,
      parent.paddingTop + parent.paddingBottom,
      view.layoutParams.height
    )

    view.measure(childWidthSpec, childHeightSpec)
    view.layout(0, 0, view.measuredWidth, view.measuredHeight)
  }

  override fun getItemOffsets(outRect: Rect, v: View, parent: RecyclerView,
                              state: RecyclerView.State) {
    adapter[parent.getChildAdapterPosition(v)]?.let {
      if (it.hasDayChange) {
        if (v.getTag(R.id.tag_daychange_layout) == null) {
          val layout = LayoutInflater.from(parent.context).inflate(
            R.layout.widget_chatmessage_daychange, parent, false
          )
          val content = layout.findViewById<TextView>(R.id.combined)
          content?.text = dayChangeFormatter.format(
            it.content.time.atZone(ZoneId.systemDefault()).truncatedTo(ChronoUnit.DAYS)
          )
          content?.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize.toFloat())
          fixLayoutSize(layout, parent)

          v.setTag(R.id.tag_daychange_layout, layout)
          v.setTag(R.id.tag_daychange_content, content)
        }
        v.setTag(R.id.tag_daychange, true)
        val layout = v.getTag(R.id.tag_daychange_layout) as View
        outRect.set(0, layout.measuredHeight, 0, 0)
      } else {
        v.setTag(R.id.tag_daychange, false)
      }
    }
  }
}
