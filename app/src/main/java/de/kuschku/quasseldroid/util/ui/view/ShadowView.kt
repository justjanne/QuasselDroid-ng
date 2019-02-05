/*
 * Copyright 2014 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.kuschku.quasseldroid.util.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.drawable.Drawable
import android.graphics.drawable.PaintDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.os.Build
import android.util.AttributeSet
import android.util.LruCache
import android.view.Gravity
import android.view.View
import androidx.annotation.RequiresApi
import de.kuschku.quasseldroid.R

/**
 * Shadow view based on the `ScrimUtil.java` class from the Muzei App. Take a look at
 * [this post](https://plus.google.com/+RomanNurik/posts/2QvHVFWrHZf) from Roman
 * Nurik for more details. Find the source code
 * [here](https://github.com/romannurik/muzei/blob/master/main/src/main/java/com/google/android/apps/muzei/util/ScrimUtil.java).
 */
class ShadowView : View {
  constructor(context: Context) : super(context) {
    initialize(context, null, 0, 0)
  }

  constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
    initialize(context, attrs, 0, 0)
  }

  constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    super(context, attrs, defStyleAttr) {
    initialize(context, attrs, defStyleAttr, 0)
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) :
    super(context, attrs, defStyleAttr, defStyleRes) {
    initialize(context, attrs, defStyleAttr, defStyleRes)
  }

  /**
   * Initializes the view.
   *
   * @param context      The Context the view is running in, through which it can
   * access the current theme, resources, etc.
   * @param attrs        The attributes of the XML tag that is inflating the view.
   * @param defStyleAttr An attribute in the current theme that contains a
   * reference to a style resource that supplies default values for
   * the view. Can be 0 to not look for defaults.
   * @param defStyleRes  A resource identifier of a style resource that
   * supplies default values for the view, used only if
   * defStyleAttr is 0 or can not be found in the theme. Can be 0
   * to not look for defaults.
   * @see View
   */
  private fun initialize(context: Context, attrs: AttributeSet?, defStyleAttr: Int,
                         defStyleRes: Int) {
    var gravity = Gravity.TOP

    // Get the attributes.
    val a = context.obtainStyledAttributes(attrs, R.styleable.ShadowView, defStyleAttr, defStyleRes)
    try {
      if (a != null) {
        gravity = a.getInt(R.styleable.ShadowView_android_gravity, gravity)
      }

    } finally {
      a?.recycle()
    }

    // Set the gradient as background.
    background = makeCubicGradientScrimDrawable(0x44000000, 8, gravity)
  }

  /**
   * Creates an approximated cubic gradient using a multi-stop linear gradient.
   */
  @SuppressLint("RtlHardcoded")
  private fun makeCubicGradientScrimDrawable(baseColor: Int, numStops: Int,
                                             gravity: Int): Drawable {
    // Generate a cache key by hashing together the inputs, based on the method described in the Effective Java book
    val cacheKeyHash = ((baseColor) * 31 + numStops) * 31 + gravity
    val cachedGradient = cubicGradientScrimCache.get(cacheKeyHash)
    if (cachedGradient != null) {
      return cachedGradient
    }

    val paintDrawable = PaintDrawable()
    paintDrawable.shape = RectShape()

    val red = Color.red(baseColor)
    val green = Color.green(baseColor)
    val blue = Color.blue(baseColor)
    val alpha = Color.alpha(baseColor)

    val stopColors = IntArray(numStops) {
      val x = it * 1f / (numStops - 1)
      val opacity = constrain(0f, 1f, Math.pow(x.toDouble(), 3.0).toFloat())
      Color.argb((alpha * opacity).toInt(), red, green, blue)
    }

    val x0: Float
    val x1: Float
    val y0: Float
    val y1: Float
    when (gravity and Gravity.HORIZONTAL_GRAVITY_MASK) {
      Gravity.LEFT  -> {
        x0 = 1f
        x1 = 0f
      }
      Gravity.RIGHT -> {
        x0 = 0f
        x1 = 1f
      }
      else          -> {
        x0 = 0f
        x1 = 0f
      }
    }
    when (gravity and Gravity.VERTICAL_GRAVITY_MASK) {
      Gravity.TOP    -> {
        y0 = 1f
        y1 = 0f
      }
      Gravity.BOTTOM -> {
        y0 = 0f
        y1 = 1f
      }
      else           -> {
        y0 = 0f
        y1 = 0f
      }
    }

    paintDrawable.shaderFactory = ShadowShaderFactory(
      x0,
      y0,
      x1,
      y1,
      stopColors)
    cubicGradientScrimCache.put(cacheKeyHash, paintDrawable)
    return paintDrawable
  }

  private fun constrain(min: Float, max: Float, v: Float): Float {
    return Math.max(min, Math.min(max, v))
  }

  class ShadowShaderFactory(
    private val x0: Float, private val y0: Float,
    private val x1: Float, private val y1: Float,
    private val stopColors: IntArray
  ) : ShapeDrawable.ShaderFactory() {
    override fun resize(width: Int, height: Int) = LinearGradient(
      width * x0,
      height * y0,
      width * x1,
      height * y1,
      stopColors, null,
      Shader.TileMode.CLAMP)
  }

  companion object {
    private val cubicGradientScrimCache = LruCache<Int, Drawable>(10)
  }
}
