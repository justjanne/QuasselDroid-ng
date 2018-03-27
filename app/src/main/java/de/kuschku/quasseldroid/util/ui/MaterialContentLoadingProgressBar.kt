package de.kuschku.quasseldroid.util.ui

/*
 * Copyright (C) 2013 The Android Open Source Project
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

import android.content.Context
import android.util.AttributeSet
import android.view.View
import me.zhanghai.android.materialprogressbar.MaterialProgressBar

/**
 * ContentLoadingProgressBar implements a ProgressBar that waits a minimum time to be
 * dismissed before showing. Once visible, the progress bar will be visible for
 * a minimum amount of time to avoid "flashes" in the UI when an event could take
 * a largely variable time to complete (from none, to a user perceivable amount)
 */
class MaterialContentLoadingProgressBar @JvmOverloads constructor(context: Context,
                                                                  attrs: AttributeSet? = null) :
  MaterialProgressBar(context, attrs, 0) {
  private var mStartTime: Long = -1
  private var mPostedHide = false
  private var mPostedShow = false
  private var mDismissed = false

  private val mDelayedHide = Runnable {
    mPostedHide = false
    mStartTime = -1
    visibility = View.GONE
  }

  private val mDelayedShow = Runnable {
    mPostedShow = false
    if (!mDismissed) {
      mStartTime = System.currentTimeMillis()
      visibility = View.VISIBLE
    }
  }

  public override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    removeCallbacks()
  }

  public override fun onDetachedFromWindow() {
    super.onDetachedFromWindow()
    removeCallbacks()
  }

  private fun removeCallbacks() {
    removeCallbacks(mDelayedHide)
    removeCallbacks(mDelayedShow)
  }

  /**
   * Hide the progress view if it is visible. The progress view will not be
   * hidden until it has been shown for at least a minimum show time. If the
   * progress view was not yet visible, cancels showing the progress view.
   */
  fun hide() {
    mDismissed = true
    removeCallbacks(mDelayedShow)
    val diff = System.currentTimeMillis() - mStartTime
    if (diff >= MIN_SHOW_TIME || mStartTime == -1L) {
      // The progress spinner has been shown long enough
      // OR was not shown yet. If it wasn't shown yet,
      // it will just never be shown.
      visibility = View.GONE
    } else {
      // The progress spinner is shown, but not long enough,
      // so put a delayed message in to hide it when its been
      // shown long enough.
      if (!mPostedHide) {
        postDelayed(mDelayedHide, MIN_SHOW_TIME - diff)
        mPostedHide = true
      }
    }
  }

  /**
   * Show the progress view after waiting for a minimum delay. If
   * during that time, hide() is called, the view is never made visible.
   */
  fun show() {
    // Reset the start time.
    mStartTime = -1
    mDismissed = false
    removeCallbacks(mDelayedHide)
    if (!mPostedShow) {
      postDelayed(mDelayedShow, MIN_DELAY.toLong())
      mPostedShow = true
    }
  }

  companion object {
    private const val MIN_SHOW_TIME = 500 // ms
    private const val MIN_DELAY = 500 // ms
  }
}
