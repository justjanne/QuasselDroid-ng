/*
 * Copyright 2017 The Android Open Source Project
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

package de.kuschku.ui.shape;

import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Region.Op;
import android.graphics.drawable.Drawable;

import androidx.annotation.ColorInt;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.ObjectsCompat;

import org.jetbrains.annotations.NotNull;

import de.kuschku.ui.graphics.drawable.TintAwareDrawable;

/**
 * Base drawable class for Material Shapes that handles shadows, elevation, scale and color for a
 * generated path.
 */
public class MaterialShapeDrawable extends Drawable
  implements TintAwareDrawable, ShapeAppearanceModel.OnChangedListener {

  private static final Paint clearPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
  // Pre-allocated objects that are re-used several times during path computation and rendering.
  private final Matrix matrix = new Matrix();
  private final Path path = new Path();
  private final Path pathInsetByStroke = new Path();
  private final RectF rectF = new RectF();
  private final RectF insetRectF = new RectF();
  private final Region transparentRegion = new Region();
  private final Region scratchRegion = new Region();
  private final Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
  private final Paint strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
  private final ShapeAppearancePathProvider pathProvider = new ShapeAppearancePathProvider();
  private MaterialShapeDrawableState drawableState;
  // Inter-method state.
  private boolean pathDirty;
  private ShapeAppearanceModel strokeShapeAppearance;
  @Nullable
  private PorterDuffColorFilter tintFilter;
  @Nullable
  private PorterDuffColorFilter strokeTintFilter;

  public MaterialShapeDrawable() {
    this(new ShapeAppearanceModel());
  }

  /**
   * @param shapeAppearanceModel the {@link ShapeAppearanceModel} containing the path that will be
   *                             rendered in this drawable.
   */
  public MaterialShapeDrawable(ShapeAppearanceModel shapeAppearanceModel) {
    this(new MaterialShapeDrawableState(shapeAppearanceModel));
  }

  private MaterialShapeDrawable(MaterialShapeDrawableState drawableState) {
    this.drawableState = drawableState;
    strokePaint.setStyle(Style.STROKE);
    fillPaint.setStyle(Style.FILL);
    clearPaint.setColor(Color.WHITE);
    clearPaint.setXfermode(new PorterDuffXfermode(Mode.DST_OUT));
    updateTintFilter();
    updateColorsForState(getState());

    // Listens for modifications made in the ShapeAppearanceModel, and requests a redraw if the
    // ShapeAppearanceModel has changed.
    drawableState.shapeAppearanceModel.addOnChangedListener(this);
  }

  private static int modulateAlpha(int paintAlpha, int alpha) {
    int scale = alpha + (alpha >>> 7); // convert to 0..256
    return (paintAlpha * scale) >>> 8;
  }

  @Nullable
  @Override
  public ConstantState getConstantState() {
    return drawableState;
  }

  @NonNull
  @Override
  public Drawable mutate() {
    drawableState = new MaterialShapeDrawableState(drawableState);
    return this;
  }

  /**
   * Get the {@link ShapeAppearanceModel} containing the path that will be rendered in this
   * drawable.
   *
   * @return the current model.
   */
  @NonNull
  private ShapeAppearanceModel getShapeAppearanceModel() {
    return drawableState.shapeAppearanceModel;
  }

  /**
   * Set the {@link ShapeAppearanceModel} containing the path that will be rendered in this
   * drawable.
   *
   * @param shapeAppearanceModel the desired model.
   */
  public void setShapeAppearanceModel(@NonNull ShapeAppearanceModel shapeAppearanceModel) {
    drawableState.shapeAppearanceModel.removeOnChangedListener(this);
    drawableState.shapeAppearanceModel = shapeAppearanceModel;
    shapeAppearanceModel.addOnChangedListener(this);
    invalidateSelf();
  }

  /**
   * Set the color used for the fill.
   *
   * @param fillColor the color set on the {@link Paint} object responsible for the fill.
   */
  public void setFillColor(@Nullable ColorStateList fillColor) {
    if (drawableState.fillColor != fillColor) {
      drawableState.fillColor = fillColor;
      onStateChange(getState());
    }
  }

  /**
   * Set the color used for the stroke.
   *
   * @param strokeColor the color set on the {@link Paint} object responsible for the stroke.
   */
  private void setStrokeColor(@Nullable ColorStateList strokeColor) {
    if (drawableState.strokeColor != strokeColor) {
      drawableState.strokeColor = strokeColor;
      onStateChange(getState());
    }
  }

  @Override
  public void setTintMode(@Nullable PorterDuff.Mode tintMode) {
    if (drawableState.tintMode != tintMode) {
      drawableState.tintMode = tintMode;
      updateTintFilter();
      invalidateSelfIgnoreShape();
    }
  }

  @Override
  public void setTintList(@Nullable ColorStateList tintList) {
    drawableState.tintList = tintList;
    updateTintFilter();
    invalidateSelfIgnoreShape();
  }

  @Override
  public void setTint(@ColorInt int tintColor) {
    setTintList(ColorStateList.valueOf(tintColor));
  }

  /**
   * Set the shape's stroke width and stroke color.
   *
   * @param strokeWidth a float for the width of the stroke.
   * @param strokeColor an int representing the Color to use for the shape's stroke.
   */
  public void setStroke(float strokeWidth, @ColorInt int strokeColor) {
    setStrokeWidth(strokeWidth);
    setStrokeColor(ColorStateList.valueOf(strokeColor));
  }

  /**
   * Set the stroke width used by the shape's paint.
   *
   * @param strokeWidth desired stroke width.
   */
  private void setStrokeWidth(float strokeWidth) {
    drawableState.strokeWidth = strokeWidth;
    invalidateSelf();
  }

  @Override
  public int getOpacity() {
    // OPAQUE or TRANSPARENT are possible, but the complexity of determining this based on the
    // shape model outweighs the optimizations gained.
    return PixelFormat.TRANSLUCENT;
  }

  @Override
  public void setAlpha(@IntRange(from = 0, to = 255) int alpha) {
    if (drawableState.alpha != alpha) {
      drawableState.alpha = alpha;
      invalidateSelfIgnoreShape();
    }
  }

  @Override
  public void setColorFilter(@Nullable ColorFilter colorFilter) {
    drawableState.colorFilter = colorFilter;
    invalidateSelfIgnoreShape();
  }

  @Override
  public Region getTransparentRegion() {
    Rect bounds = getBounds();
    transparentRegion.set(bounds);
    calculatePath(getBoundsAsRectF(), path);
    scratchRegion.setPath(path, transparentRegion);
    transparentRegion.op(scratchRegion, Op.DIFFERENCE);
    return transparentRegion;
  }

  private RectF getBoundsAsRectF() {
    Rect bounds = getBounds();
    rectF.set(bounds.left, bounds.top, bounds.right, bounds.bottom);
    return rectF;
  }

  @Override
  public void onShapeAppearanceModelChanged() {
    invalidateSelf();
  }

  @Override
  public void invalidateSelf() {
    pathDirty = true;
    super.invalidateSelf();
  }

  /**
   * Invalidate without recalculating the path associated with this shape. This is useful if the
   * shape has stayed the same but we still need to be redrawn, such as when the color has changed.
   */
  private void invalidateSelfIgnoreShape() {
    super.invalidateSelf();
  }

  /**
   * Returns whether the shape has a fill.
   */
  private boolean hasFill() {
    return drawableState.paintStyle == Style.FILL_AND_STROKE
      || drawableState.paintStyle == Style.FILL;
  }

  /**
   * Returns whether the shape has a stroke with a positive width.
   */
  private boolean hasStroke() {
    return (drawableState.paintStyle == Style.FILL_AND_STROKE
      || drawableState.paintStyle == Style.STROKE)
      && strokePaint.getStrokeWidth() > 0;
  }

  @Override
  protected void onBoundsChange(@NonNull Rect bounds) {
    pathDirty = true;
    super.onBoundsChange(bounds);
  }

  @Override
  public void draw(@NotNull Canvas canvas) {
    fillPaint.setColorFilter(tintFilter);
    final int prevAlpha = fillPaint.getAlpha();
    fillPaint.setAlpha(modulateAlpha(prevAlpha, drawableState.alpha));

    strokePaint.setColorFilter(strokeTintFilter);
    strokePaint.setStrokeWidth(drawableState.strokeWidth);

    final int prevStrokeAlpha = strokePaint.getAlpha();
    strokePaint.setAlpha(modulateAlpha(prevStrokeAlpha, drawableState.alpha));

    if (pathDirty) {
      calculateStrokePath();
      calculatePath(getBoundsAsRectF(), path);
      pathDirty = false;
    }

    if (hasFill()) {
      drawFillShape(canvas);
    }
    if (hasStroke()) {
      drawStrokeShape(canvas);
    }

    fillPaint.setAlpha(prevAlpha);
    strokePaint.setAlpha(prevStrokeAlpha);
  }

  /**
   * Draw the path or try to draw a round rect if possible.
   */
  private void drawShape(
    Canvas canvas,
    Paint paint,
    Path path,
    ShapeAppearanceModel shapeAppearanceModel,
    RectF bounds) {
    if (shapeAppearanceModel.isRoundRect()) {
      float cornerSize = shapeAppearanceModel.getTopRightCorner().getCornerSize();
      canvas.drawRoundRect(bounds, cornerSize, cornerSize, paint);
    } else {
      canvas.drawPath(path, paint);
    }
  }

  private void drawFillShape(Canvas canvas) {
    drawShape(canvas, fillPaint, path, drawableState.shapeAppearanceModel, getBoundsAsRectF());
  }

  private void drawStrokeShape(Canvas canvas) {
    drawShape(
      canvas, strokePaint, pathInsetByStroke, strokeShapeAppearance, getBoundsInsetByStroke());
  }

  /**
   * @deprecated see {@link ShapeAppearancePathProvider}
   */
  @Deprecated
  public void getPathForSize(int width, int height, Path path) {
    calculatePathForSize(new RectF(0, 0, width, height), path);
  }

  /**
   * @deprecated see {@link ShapeAppearancePathProvider}
   */
  @Deprecated
  public void getPathForSize(Rect bounds, Path path) {
    calculatePathForSize(new RectF(bounds), path);
  }

  private void calculatePathForSize(RectF bounds, Path path) {
    pathProvider.calculatePath(
      drawableState.shapeAppearanceModel,
      drawableState.interpolation,
      bounds,
      null,
      path);
  }

  /**
   * Calculates the path that can be used to draw the stroke entirely inside the shape
   */
  private void calculateStrokePath() {
    strokeShapeAppearance = new ShapeAppearanceModel(getShapeAppearanceModel());
    float cornerSizeTopLeft = strokeShapeAppearance.getTopLeftCorner().cornerSize;
    float cornerSizeTopRight = strokeShapeAppearance.getTopRightCorner().cornerSize;
    float cornerSizeBottomRight = strokeShapeAppearance.getBottomRightCorner().cornerSize;
    float cornerSizeBottomLeft = strokeShapeAppearance.getBottomLeftCorner().cornerSize;

    // Adjust corner radius in order to draw the stroke so that the corners of the background are
    // drawn on top of the edges.
    strokeShapeAppearance.setCornerRadii(
      adjustCornerSizeForStrokeSize(cornerSizeTopLeft),
      adjustCornerSizeForStrokeSize(cornerSizeTopRight),
      adjustCornerSizeForStrokeSize(cornerSizeBottomRight),
      adjustCornerSizeForStrokeSize(cornerSizeBottomLeft));

    pathProvider.calculatePath(
      strokeShapeAppearance,
      drawableState.interpolation,
      getBoundsInsetByStroke(),
      pathInsetByStroke);
  }

  private float adjustCornerSizeForStrokeSize(float cornerSize) {
    float adjustedCornerSize = cornerSize - getStrokeInsetLength();
    return Math.max(adjustedCornerSize, 0);
  }

  private void calculatePath(RectF bounds, Path path) {
    calculatePathForSize(bounds, path);
    if (drawableState.scale == 1f) {
      return;
    }
    matrix.reset();
    matrix.setScale(
      drawableState.scale, drawableState.scale, bounds.width() / 2.0f, bounds.height() / 2.0f);
    path.transform(matrix);
  }

  private boolean updateTintFilter() {
    PorterDuffColorFilter originalTintFilter = tintFilter;
    PorterDuffColorFilter originalStrokeTintFilter = strokeTintFilter;
    tintFilter =
      calculateTintFilter(
        drawableState.tintList,
        drawableState.tintMode
      );
    strokeTintFilter =
      calculateTintFilter(
        drawableState.strokeTintList,
        drawableState.tintMode
      );
    return !ObjectsCompat.equals(originalTintFilter, tintFilter)
      || !ObjectsCompat.equals(originalStrokeTintFilter, strokeTintFilter);
  }

  @Nullable
  private PorterDuffColorFilter calculateTintFilter(ColorStateList tintList, PorterDuff.Mode tintMode) {
    return tintList == null || tintMode == null
      ? null
      : new PorterDuffColorFilter(tintList.getColorForState(getState(), Color.TRANSPARENT), tintMode);
  }

  @Override
  public boolean isStateful() {
    return super.isStateful()
      || (drawableState.tintList != null && drawableState.tintList.isStateful())
      || (drawableState.strokeTintList != null && drawableState.strokeTintList.isStateful())
      || (drawableState.strokeColor != null && drawableState.strokeColor.isStateful())
      || (drawableState.fillColor != null && drawableState.fillColor.isStateful());
  }

  @Override
  protected boolean onStateChange(@NonNull int[] state) {
    boolean paintColorChanged = updateColorsForState(state);
    boolean tintFilterChanged = updateTintFilter();
    boolean invalidateSelf = paintColorChanged || tintFilterChanged;
    if (invalidateSelf) {
      invalidateSelf();
    }
    return invalidateSelf;
  }

  private boolean updateColorsForState(int[] state) {
    boolean invalidateSelf = false;

    if (drawableState.fillColor != null) {
      final int previousFillColor = fillPaint.getColor();
      final int newFillColor = drawableState.fillColor.getColorForState(state, previousFillColor);
      if (previousFillColor != newFillColor) {
        fillPaint.setColor(newFillColor);
        invalidateSelf = true;
      }
    }

    if (drawableState.strokeColor != null) {
      final int previousStrokeColor = strokePaint.getColor();
      final int newStrokeColor =
        drawableState.strokeColor.getColorForState(state, previousStrokeColor);
      if (previousStrokeColor != newStrokeColor) {
        strokePaint.setColor(newStrokeColor);
        invalidateSelf = true;
      }
    }

    return invalidateSelf;
  }

  private float getStrokeInsetLength() {
    if (hasStroke()) {
      return strokePaint.getStrokeWidth() / 2.0f;
    }
    return 0f;
  }

  private RectF getBoundsInsetByStroke() {
    RectF rectF = getBoundsAsRectF();
    float inset = getStrokeInsetLength();
    insetRectF.set(
      rectF.left + inset, rectF.top + inset, rectF.right - inset, rectF.bottom - inset);
    return insetRectF;
  }

  static final class MaterialShapeDrawableState extends ConstantState {

    @NonNull
    ShapeAppearanceModel shapeAppearanceModel;

    @Nullable
    ColorFilter colorFilter;
    @Nullable
    ColorStateList fillColor = null;
    @Nullable
    ColorStateList strokeColor = null;
    @Nullable
    ColorStateList strokeTintList = null;
    @Nullable
    ColorStateList tintList = null;
    @Nullable
    PorterDuff.Mode tintMode = PorterDuff.Mode.SRC_IN;

    float scale = 1f;
    float interpolation = 1f;
    float strokeWidth;

    int alpha = 255;

    boolean useTintColorForShadow = false;

    Style paintStyle = Style.FILL_AND_STROKE;

    MaterialShapeDrawableState(@NotNull ShapeAppearanceModel shapeAppearanceModel) {
      this.shapeAppearanceModel = shapeAppearanceModel;
    }

    MaterialShapeDrawableState(MaterialShapeDrawableState orig) {
      shapeAppearanceModel = orig.shapeAppearanceModel;
      strokeWidth = orig.strokeWidth;
      colorFilter = orig.colorFilter;
      fillColor = orig.fillColor;
      strokeColor = orig.strokeColor;
      tintMode = orig.tintMode;
      tintList = orig.tintList;
      alpha = orig.alpha;
      scale = orig.scale;
      useTintColorForShadow = orig.useTintColorForShadow;
      interpolation = orig.interpolation;
      strokeTintList = orig.strokeTintList;
      paintStyle = orig.paintStyle;
    }

    @NonNull
    @Override
    public Drawable newDrawable() {
      return new MaterialShapeDrawable(this);
    }

    @Override
    public int getChangingConfigurations() {
      return 0;
    }
  }
}
