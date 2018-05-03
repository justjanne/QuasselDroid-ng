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

package de.kuschku.quasseldroid.util.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.LruCache;
import android.view.Gravity;
import android.view.View;

import de.kuschku.quasseldroid.R;

/**
 * Shadow view based on the {@code ScrimUtil.java} class from the Muzei App. Take a look at
 * <a href="https://plus.google.com/+RomanNurik/posts/2QvHVFWrHZf">this post</a> from Roman
 * Nurik for more details. Find the source code
 * <a href="https://github.com/romannurik/muzei/blob/master/main/src/main/java/com/google/android/apps/muzei/util/ScrimUtil.java">here</a>.
 */
public class ShadowView extends View {

  private static final LruCache<Integer, Drawable> cubicGradientScrimCache = new LruCache<>(10);

  public ShadowView(Context context) {
    super(context);
    initialize(context, null, 0, 0);
  }

  public ShadowView(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    initialize(context, attrs, 0, 0);
  }

  public ShadowView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initialize(context, attrs, defStyleAttr, 0);
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  public ShadowView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    initialize(context, attrs, defStyleAttr, defStyleRes);
  }

  /**
   * Initializes the view.
   *
   * @param context      The Context the view is running in, through which it can
   *                     access the current theme, resources, etc.
   * @param attrs        The attributes of the XML tag that is inflating the view.
   * @param defStyleAttr An attribute in the current theme that contains a
   *                     reference to a style resource that supplies default values for
   *                     the view. Can be 0 to not look for defaults.
   * @param defStyleRes  A resource identifier of a style resource that
   *                     supplies default values for the view, used only if
   *                     defStyleAttr is 0 or can not be found in the theme. Can be 0
   *                     to not look for defaults.
   * @see View(Context, AttributeSet, int)
   */
  private void initialize(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    int gravity = Gravity.TOP;

    // Get the attributes.
    TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ShadowView, defStyleAttr, defStyleRes);
    try {
      if (a != null) {
        gravity = a.getInt(R.styleable.ShadowView_android_gravity, gravity);
      }

    } finally {
      if (a != null) a.recycle();
    }

    // Set the gradient as background.
    setBackground(makeCubicGradientScrimDrawable(0x44000000, 8, gravity));
  }

  /**
   * Creates an approximated cubic gradient using a multi-stop linear gradient.
   */
  @SuppressLint("RtlHardcoded")
  private Drawable makeCubicGradientScrimDrawable(int baseColor, int numStops, int gravity) {

    // Generate a cache key by hashing together the inputs, based on the method described in the Effective Java book
    int cacheKeyHash = baseColor;
    cacheKeyHash = 31 * cacheKeyHash + numStops;
    cacheKeyHash = 31 * cacheKeyHash + gravity;

    Drawable cachedGradient = cubicGradientScrimCache.get(cacheKeyHash);
    if (cachedGradient != null) {
      return cachedGradient;
    }

    numStops = Math.max(numStops, 2);

    PaintDrawable paintDrawable = new PaintDrawable();
    paintDrawable.setShape(new RectShape());

    final int[] stopColors = new int[numStops];

    int red = Color.red(baseColor);
    int green = Color.green(baseColor);
    int blue = Color.blue(baseColor);
    int alpha = Color.alpha(baseColor);

    for (int i = 0; i < numStops; i++) {
      float x = i * 1f / (numStops - 1);
      float opacity = constrain(0, 1, (float) Math.pow(x, 3));
      stopColors[i] = Color.argb((int) (alpha * opacity), red, green, blue);
    }

    final float x0, x1, y0, y1;
    switch (gravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
      case Gravity.LEFT:
        x0 = 1;
        x1 = 0;
        break;
      case Gravity.RIGHT:
        x0 = 0;
        x1 = 1;
        break;
      default:
        x0 = 0;
        x1 = 0;
        break;
    }
    switch (gravity & Gravity.VERTICAL_GRAVITY_MASK) {
      case Gravity.TOP:
        y0 = 1;
        y1 = 0;
        break;
      case Gravity.BOTTOM:
        y0 = 0;
        y1 = 1;
        break;
      default:
        y0 = 0;
        y1 = 0;
        break;
    }

    paintDrawable.setShaderFactory(new ShapeDrawable.ShaderFactory() {
      @Override
      public Shader resize(int width, int height) {
        return new LinearGradient(
          width * x0,
          height * y0,
          width * x1,
          height * y1,
          stopColors, null,
          Shader.TileMode.CLAMP);
      }
    });

    cubicGradientScrimCache.put(cacheKeyHash, paintDrawable);
    return paintDrawable;
  }

  private float constrain(float min, float max, float v) {
    return Math.max(min, Math.min(max, v));
  }

}
