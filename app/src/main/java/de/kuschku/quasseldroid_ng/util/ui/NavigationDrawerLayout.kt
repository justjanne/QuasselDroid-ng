/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.kuschku.quasseldroid_ng.util.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.widget.FrameLayout
import de.kuschku.quasseldroid_ng.R

class NavigationDrawerLayout @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
  private var mInsetForeground: Drawable? = null
  private var mInsets: Rect? = null
  private val mTempRect = Rect()
  private val maxWidth: Int

  init {
    val a = context.obtainStyledAttributes(
      attrs,
      R.styleable.ScrimInsetsFrameLayout, defStyleAttr,
      R.style.Widget_Design_ScrimInsetsFrameLayout
    )
    mInsetForeground = a.getDrawable(R.styleable.ScrimInsetsFrameLayout_insetForeground)
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
      ViewCompat.postInvalidateOnAnimation(this@NavigationDrawerLayout)
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
