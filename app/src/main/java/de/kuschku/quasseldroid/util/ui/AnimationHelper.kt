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
