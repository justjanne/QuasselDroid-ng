/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2019 Janne Koschinski
 * Copyright (c) 2019 The Quassel Project
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.quasseldroid.util.ui

import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation
import kotlin.math.roundToLong

object AnimationHelper {
  fun expand(v: ViewGroup) {
    v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    val targetHeight = v.measuredHeight

    // Older versions of android (pre API 21) cancel animations for views with a height of 0.
    v.layoutParams.height = 1
    v.visibility = View.VISIBLE

    v.clearAnimation()
    v.startAnimation(object : Animation() {
      init {
        duration = (targetHeight / v.context.resources.displayMetrics.density).roundToLong()
      }

      override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
        v.layoutParams.height = if (interpolatedTime == 1f)
          ViewGroup.LayoutParams.WRAP_CONTENT
        else
          (targetHeight * interpolatedTime).toInt()
        v.alpha = interpolatedTime
        v.requestLayout()
      }

      override fun willChangeBounds(): Boolean {
        return true
      }
    })
  }

  fun collapse(v: ViewGroup) {
    val initialHeight = v.measuredHeight
    v.clearAnimation()
    v.startAnimation(object : Animation() {
      init {
        duration = (initialHeight / v.context.resources.displayMetrics.density).roundToLong()
      }

      override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
        if (interpolatedTime == 1f) {
          v.visibility = View.GONE
        } else {
          v.layoutParams.height = initialHeight - (initialHeight * interpolatedTime).toInt()
          v.alpha = 1 - interpolatedTime
          v.requestLayout()
        }
      }

      override fun willChangeBounds(): Boolean {
        return true
      }
    })
  }
}
