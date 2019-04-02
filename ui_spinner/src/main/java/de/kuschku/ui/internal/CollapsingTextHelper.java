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

package de.kuschku.ui.internal;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;

import de.kuschku.ui.resources.MaterialResources;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.core.math.MathUtils;
import androidx.core.text.TextDirectionHeuristicsCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import de.kuschku.ui.spinner.R;

/**
 * Helper class for rendering and animating collapsed text.
 */
public final class CollapsingTextHelper {
  private final View view;
  private final Rect collapsedBounds;
  private final RectF currentBounds;
  private final TextPaint textPaint;
  private final TextPaint tmpPaint;
  private boolean drawTitle;
  private int collapsedTextGravity = Gravity.CENTER_VERTICAL;
  private float collapsedTextSize = 15;
  private ColorStateList collapsedTextColor;
  private float collapsedDrawY;
  private float collapsedDrawX;
  private float currentDrawX;
  private float currentDrawY;
  private Typeface collapsedTypeface;
  private CharSequence text;
  private CharSequence textToDraw;
  private boolean isRtl;
  private int[] state;
  private boolean boundsChanged;

  public CollapsingTextHelper(View view) {
    this.view = view;

    textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.SUBPIXEL_TEXT_FLAG);
    tmpPaint = new TextPaint(textPaint);

    collapsedBounds = new Rect();
    currentBounds = new RectF();
  }

  private static boolean rectEquals(Rect r, int left, int top, int right, int bottom) {
    return !(r.left != left || r.top != top || r.right != right || r.bottom != bottom);
  }

  private void setCollapsedBounds(int left, int top, int right, int bottom) {
    if (!rectEquals(collapsedBounds, left, top, right, bottom)) {
      collapsedBounds.set(left, top, right, bottom);
      boundsChanged = true;
      onBoundsChanged();
    }
  }

  public void setCollapsedBounds(Rect bounds) {
    setCollapsedBounds(bounds.left, bounds.top, bounds.right, bounds.bottom);
  }

  private float calculateCollapsedTextWidth() {
    if (text == null) {
      return 0;
    }
    getTextPaintCollapsed(tmpPaint);
    return tmpPaint.measureText(text, 0, text.length());
  }

  public float getCollapsedTextHeight() {
    getTextPaintCollapsed(tmpPaint);
    // Return collapsed height measured from the baseline.
    return -tmpPaint.ascent();
  }

  public void getCollapsedTextActualBounds(RectF bounds) {
    boolean isRtl = calculateIsRtl(text);

    bounds.left =
      !isRtl ? collapsedBounds.left : collapsedBounds.right - calculateCollapsedTextWidth();
    bounds.top = collapsedBounds.top;
    bounds.right = !isRtl ? bounds.left + calculateCollapsedTextWidth() : collapsedBounds.right;
    bounds.bottom = collapsedBounds.top + getCollapsedTextHeight();
  }

  private void getTextPaintCollapsed(TextPaint textPaint) {
    textPaint.setTextSize(collapsedTextSize);
    textPaint.setTypeface(collapsedTypeface);
  }

  private void onBoundsChanged() {
    drawTitle =
      collapsedBounds.width() > 0
        && collapsedBounds.height() > 0;
  }

  public void setCollapsedTextGravity(int gravity) {
    if (collapsedTextGravity != gravity) {
      collapsedTextGravity = gravity;
      recalculate();
    }
  }

  public void setCollapsedTextAppearance(int resId) {
    Context context = view.getContext();
    TypedArray a = context.obtainStyledAttributes(resId, R.styleable.TextAppearance);
    ColorStateList textColor = MaterialResources.getColorStateList(context, a, R.styleable.TextAppearance_android_textColor);
    if (textColor != null) {
      collapsedTextColor = textColor;
    }
    float textSize = a.getDimension(R.styleable.TextAppearance_android_textSize, 0f);
    if (textSize != 0) {
      collapsedTextSize = textSize;
    }
    a.recycle();


    recalculate();
  }

  public void setTypefaces(Typeface typeface) {
    boolean collapsedFontChanged = setCollapsedTypefaceInternal(typeface);
    if (collapsedFontChanged) {
      recalculate();
    }
  }

  @SuppressWarnings("ReferenceEquality") // Matches the Typeface comparison in TextView
  private boolean setCollapsedTypefaceInternal(Typeface typeface) {
    // Explicit Typeface setting cancels pending async fetch, if any, to avoid old font overriding
    // already updated one when async op comes back after a while.
    if (collapsedTypeface != typeface) {
      collapsedTypeface = typeface;
      return true;
    }
    return false;
  }

  public final boolean setState(final int[] state) {
    this.state = state;

    if (isStateful()) {
      recalculate();
      return true;
    }

    return false;
  }

  private boolean isStateful() {
    return (collapsedTextColor != null && collapsedTextColor.isStateful());
  }

  private void calculateOffsets() {
    currentBounds.left = collapsedBounds.left;
    currentBounds.top = collapsedDrawY;
    currentBounds.right = collapsedBounds.right;
    currentBounds.bottom = collapsedBounds.bottom;
    currentDrawX = collapsedDrawX;
    currentDrawY = collapsedDrawY;

    setInterpolatedTextSize();

    textPaint.setColor(getCurrentCollapsedTextColor());

    ViewCompat.postInvalidateOnAnimation(view);
  }

  @ColorInt
  public int getCurrentCollapsedTextColor() {
    return getCurrentColor(collapsedTextColor);
  }

  @ColorInt
  private int getCurrentColor(@Nullable ColorStateList colorStateList) {
    if (colorStateList == null) {
      return 0;
    }
    if (state != null) {
      return colorStateList.getColorForState(state, 0);
    }
    return colorStateList.getDefaultColor();
  }

  private void calculateBaseOffsets() {
    // We then calculate the collapsed text size, using the same logic
    calculateUsingTextSize();
    float width =
      textToDraw != null ? textPaint.measureText(textToDraw, 0, textToDraw.length()) : 0;
    final int collapsedAbsGravity =
      GravityCompat.getAbsoluteGravity(
        collapsedTextGravity,
        isRtl ? ViewCompat.LAYOUT_DIRECTION_RTL : ViewCompat.LAYOUT_DIRECTION_LTR);
    switch (collapsedAbsGravity & Gravity.VERTICAL_GRAVITY_MASK) {
      case Gravity.BOTTOM:
        collapsedDrawY = collapsedBounds.bottom;
        break;
      case Gravity.TOP:
        collapsedDrawY = collapsedBounds.top - textPaint.ascent();
        break;
      case Gravity.CENTER_VERTICAL:
      default:
        float textHeight = textPaint.descent() - textPaint.ascent();
        float textOffset = (textHeight / 2) - textPaint.descent();
        collapsedDrawY = collapsedBounds.centerY() + textOffset;
        break;
    }
    switch (collapsedAbsGravity & GravityCompat.RELATIVE_HORIZONTAL_GRAVITY_MASK) {
      case Gravity.CENTER_HORIZONTAL:
        collapsedDrawX = collapsedBounds.centerX() - (width / 2);
        break;
      case Gravity.RIGHT:
        collapsedDrawX = collapsedBounds.right - width;
        break;
      case Gravity.LEFT:
      default:
        collapsedDrawX = collapsedBounds.left;
        break;
    }
  }

  public void draw(Canvas canvas) {
    final int saveCount = canvas.save();

    if (textToDraw != null && drawTitle) {
      float x = currentDrawX;
      float y = currentDrawY;

      canvas.drawText(textToDraw, 0, textToDraw.length(), x, y, textPaint);
    }

    canvas.restoreToCount(saveCount);
  }

  private boolean calculateIsRtl(CharSequence text) {
    final boolean defaultIsRtl =
      ViewCompat.getLayoutDirection(view) == ViewCompat.LAYOUT_DIRECTION_RTL;
    return (defaultIsRtl
      ? TextDirectionHeuristicsCompat.FIRSTSTRONG_RTL
      : TextDirectionHeuristicsCompat.FIRSTSTRONG_LTR)
      .isRtl(text, 0, text.length());
  }

  private void setInterpolatedTextSize() {
    calculateUsingTextSize();
    ViewCompat.postInvalidateOnAnimation(view);
  }

  @SuppressWarnings("ReferenceEquality") // Matches the Typeface comparison in TextView
  private void calculateUsingTextSize() {
    if (text == null) {
      return;
    }

    final float collapsedWidth = collapsedBounds.width();

    boolean updateDrawText = false;

    final float availableWidth;
    availableWidth = collapsedWidth;

    if (availableWidth > 0) {
      updateDrawText = boundsChanged;
      boundsChanged = false;
    }

    if (textToDraw == null || updateDrawText) {
      textPaint.setTextSize(collapsedTextSize);
      textPaint.setTypeface(collapsedTypeface);

      // If we don't currently have text to draw, or the text size has changed, ellipsize...
      final CharSequence title =
        TextUtils.ellipsize(text, textPaint, availableWidth, TextUtils.TruncateAt.END);
      if (!TextUtils.equals(title, textToDraw)) {
        textToDraw = title;
        isRtl = calculateIsRtl(textToDraw);
      }
    }
  }

  public void recalculate() {
    if (view.getHeight() > 0 && view.getWidth() > 0) {
      // If we've already been laid out, calculate everything now otherwise we'll wait
      // until a layout
      calculateBaseOffsets();
      calculateOffsets();
    }
  }

  /**
   * Set the title to display
   */
  public void setText(CharSequence text) {
    if (text == null || !TextUtils.equals(this.text, text)) {
      this.text = text;
      textToDraw = null;
      recalculate();
    }
  }

  public ColorStateList getCollapsedTextColor() {
    return collapsedTextColor;
  }

  public void setCollapsedTextColor(ColorStateList textColor) {
    if (collapsedTextColor != textColor) {
      collapsedTextColor = textColor;
      recalculate();
    }
  }
}
