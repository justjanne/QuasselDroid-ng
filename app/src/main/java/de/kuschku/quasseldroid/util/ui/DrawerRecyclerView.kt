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

package de.kuschku.quasseldroid.util.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.support.v4.view.ViewCompat
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import de.kuschku.quasseldroid.R

class DrawerRecyclerView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {
  private var mInsetForeground: Drawable? = null
  private var mInsets: Rect? = null
  private val mTempRect = Rect()
  private val maxWidth: Int

  init {
    val a = context.obtainStyledAttributes(
      attrs,
      R.styleable.DrawerRecyclerView, defStyleAttr,
      R.style.Widget_DrawerRecyclerView
    )
    mInsetForeground = a.getDrawable(R.styleable.DrawerRecyclerView_insetBackground)
    a.recycle()
    setWillNotDraw(true) // No need to draw until the insets are adjusted
    ViewCompat.setOnApplyWindowInsetsListener(
      this
    ) { _, insets ->
      if (null == mInsets) {
        mInsets = Rect()
      }
      mInsets!!.set(
        insets.systemWindowInsetLeft,
        insets.systemWindowInsetTop,
        insets.systemWindowInsetRight,
        insets.systemWindowInsetBottom
      )
      setPadding(
        insets.systemWindowInsetLeft,
        insets.systemWindowInsetTop,
        insets.systemWindowInsetRight,
        insets.systemWindowInsetBottom
      )
      setWillNotDraw(!insets.hasSystemWindowInsets() || mInsetForeground == null)
      ViewCompat.postInvalidateOnAnimation(this@DrawerRecyclerView)
      insets.consumeSystemWindowInsets()
    }

    maxWidth = context.resources.getDimensionPixelSize(R.dimen.navigation_drawer_max_width)
  }

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    super.onMeasure(
      MeasureSpec.makeMeasureSpec(
        Math.min(MeasureSpec.getSize(widthMeasureSpec), maxWidth),
        MeasureSpec.getMode(widthMeasureSpec)
      ),
      heightMeasureSpec
    )
  }

  override fun draw(canvas: Canvas) {
    super.draw(canvas)
    val width = width
    val height = height
    if (mInsets != null && mInsetForeground != null) {
      val sc = canvas.save()
      canvas.translate(scrollX.toFloat(), scrollY.toFloat())
      // Top
      mTempRect.set(0, 0, width, mInsets!!.top)
      mInsetForeground!!.bounds = mTempRect
      mInsetForeground!!.draw(canvas)
      // Bottom
      mTempRect.set(0, height - mInsets!!.bottom, width, height)
      mInsetForeground!!.bounds = mTempRect
      mInsetForeground!!.draw(canvas)
      // Left
      mTempRect.set(0, mInsets!!.top, mInsets!!.left, height - mInsets!!.bottom)
      mInsetForeground!!.bounds = mTempRect
      mInsetForeground!!.draw(canvas)
      // Right
      mTempRect.set(width - mInsets!!.right, mInsets!!.top, width, height - mInsets!!.bottom)
      mInsetForeground!!.bounds = mTempRect
      mInsetForeground!!.draw(canvas)
      canvas.restoreToCount(sc)
    }
  }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    if (mInsetForeground != null) {
      mInsetForeground!!.callback = this
    }
  }

  override fun onDetachedFromWindow() {
    super.onDetachedFromWindow()
    if (mInsetForeground != null) {
      mInsetForeground!!.callback = null
    }
  }
}
