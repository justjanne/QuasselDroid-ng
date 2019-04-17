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

package de.kuschku.ui.spinner;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.widget.TextViewCompat;
import androidx.customview.view.AbsSavedState;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.LinkedHashSet;

import de.kuschku.ui.color.MaterialColors;
import de.kuschku.ui.internal.CollapsingTextHelper;
import de.kuschku.ui.internal.DescendantOffsetUtils;
import de.kuschku.ui.resources.MaterialResources;
import de.kuschku.ui.shape.MaterialShapeDrawable;
import de.kuschku.ui.shape.ShapeAppearanceModel;

import static de.kuschku.ui.internal.ThemeEnforcement.createThemedContext;

/**
 * Layout which wraps a {@link android.widget.Spinner}, or descendant to show a floating label when
 * the hint is hidden.
 *
 * <p>Also supports:
 *
 * <ul>
 * <li>Showing an error via {@link #setErrorEnabled(boolean)} and {@link #setError(CharSequence)}
 * <li>Showing helper text via {@link #setHelperTextEnabled(boolean)} and {@link
 * #setHelperText(CharSequence)}
 * </ul>
 *
 * <p>An example usage is as so:</p>
 *
 * <pre>
 * &lt;de.kuschku.ui.spinner.MaterialSpinnerLayout
 *         android:layout_width=&quot;match_parent&quot;
 *         android:layout_height=&quot;wrap_content&quot;
 *         android:hint=&quot;@string/form_username&quot;&gt;
 *
 *     &lt;android.widget.Spinner
 *             android:layout_width=&quot;match_parent&quot;
 *             android:layout_height=&quot;wrap_content&quot;/&gt;
 *
 * &lt;/de.kuschku.ui.spinner.MaterialSpinnerLayout&gt;
 * </pre>
 *
 * <p><strong>Note:</strong> The actual view hierarchy present under MaterialSpinnerLayout is
 * <strong>NOT</strong> guaranteed to match the view hierarchy as written in XML. As a result, calls
 * to getParent() on children of the MaterialSpinnerLayout -- such as a Spinner -- may not return the
 * MaterialSpinnerLayout itself, but rather an intermediate View. If you need to access a View
 * directly, set an {@code android:id} and use {@link View#findViewById(int)}.
 */
public class MaterialSpinnerLayout extends LinearLayout {

  public static final int BOX_BACKGROUND_NONE = 0;
  public static final int BOX_BACKGROUND_FILLED = 1;
  public static final int BOX_BACKGROUND_OUTLINE = 2;
  private static final int DEF_STYLE_RES = R.style.Widget_Design_MaterialSpinnerLayout;
  final CollapsingTextHelper collapsingTextHelper = new CollapsingTextHelper(this);
  private final FrameLayout inputFrame;
  private final IndicatorViewController indicatorViewController = new IndicatorViewController(this);
  private final ShapeAppearanceModel shapeAppearanceModel;
  private final ShapeAppearanceModel cornerAdjustedShapeAppearanceModel;
  private final int boxLabelCutoutPaddingPx;
  private final int boxCollapsedPaddingTopPx;
  private final int boxStrokeWidthDefaultPx;
  private final int boxStrokeWidthFocusedPx;
  private final Rect tmpRect = new Rect();
  private final Rect tmpBoundsRect = new Rect();
  private final RectF tmpRectF = new RectF();
  private final LinkedHashSet<OnSpinnerAttachedListener> spinnerAttachedListeners =
    new LinkedHashSet<>();
  @ColorInt
  private final int defaultStrokeColor;
  @ColorInt
  private final int hoveredStrokeColor;
  @ColorInt
  private final int disabledFilledBackgroundColor;
  @ColorInt
  private final int hoveredFilledBackgroundColor;
  @ColorInt
  private final int disabledColor;
  Spinner spinner;
  private boolean hintEnabled;
  private CharSequence hint;
  private boolean isProvidingHint;
  private MaterialShapeDrawable boxBackground;
  private MaterialShapeDrawable boxUnderline;
  @BoxBackgroundMode
  private int boxBackgroundMode;
  private int boxStrokeWidthPx;
  @ColorInt
  private int boxStrokeColor;
  @ColorInt
  private int boxBackgroundColor;
  private Typeface typeface;
  private ColorStateList defaultHintTextColor;
  private ColorStateList focusedTextColor;
  @ColorInt
  private int focusedStrokeColor;
  @ColorInt
  private int defaultFilledBackgroundColor;
  private boolean hintAnimationEnabled;
  private boolean inDrawableStateChanged;

  public MaterialSpinnerLayout(Context context) {
    this(context, null);
  }

  public MaterialSpinnerLayout(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, R.attr.md_materialSpinnerStyle);
  }

  public MaterialSpinnerLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(createThemedContext(context, attrs, defStyleAttr, DEF_STYLE_RES), attrs, defStyleAttr);
    // Ensure we are using the correctly themed context rather than the context that was passed in.
    context = getContext();

    setOrientation(VERTICAL);
    setWillNotDraw(false);
    setAddStatesFromChildren(true);

    inputFrame = new FrameLayout(context);
    inputFrame.setAddStatesFromChildren(true);
    addView(inputFrame);

    collapsingTextHelper.setCollapsedTextGravity(Gravity.TOP | GravityCompat.START);

    final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MaterialSpinnerLayout, defStyleAttr, DEF_STYLE_RES);

    hintEnabled = a.getBoolean(R.styleable.MaterialSpinnerLayout_md_hintEnabled, true);
    setHint(a.getText(R.styleable.MaterialSpinnerLayout_android_hint));
    hintAnimationEnabled = a.getBoolean(R.styleable.MaterialSpinnerLayout_md_hintAnimationEnabled, true);

    shapeAppearanceModel = new ShapeAppearanceModel(context, attrs, defStyleAttr, DEF_STYLE_RES);
    cornerAdjustedShapeAppearanceModel = new ShapeAppearanceModel(shapeAppearanceModel);

    boxLabelCutoutPaddingPx =
      context
        .getResources()
        .getDimensionPixelOffset(R.dimen.md_mtrl_spinner_box_label_cutout_padding);
    boxCollapsedPaddingTopPx =
      a.getDimensionPixelOffset(R.styleable.MaterialSpinnerLayout_md_boxCollapsedPaddingTop, 0);

    boxStrokeWidthDefaultPx =
      context
        .getResources()
        .getDimensionPixelSize(R.dimen.md_mtrl_spinner_box_stroke_width_default);
    boxStrokeWidthFocusedPx =
      context
        .getResources()
        .getDimensionPixelSize(R.dimen.md_mtrl_spinner_box_stroke_width_focused);
    boxStrokeWidthPx = boxStrokeWidthDefaultPx;

    float boxCornerRadiusTopStart =
      a.getDimension(R.styleable.MaterialSpinnerLayout_md_boxCornerRadiusTopStart, -1f);
    float boxCornerRadiusTopEnd =
      a.getDimension(R.styleable.MaterialSpinnerLayout_md_boxCornerRadiusTopEnd, -1f);
    float boxCornerRadiusBottomEnd =
      a.getDimension(R.styleable.MaterialSpinnerLayout_md_boxCornerRadiusBottomEnd, -1f);
    float boxCornerRadiusBottomStart =
      a.getDimension(R.styleable.MaterialSpinnerLayout_md_boxCornerRadiusBottomStart, -1f);
    if (boxCornerRadiusTopStart >= 0) {
      shapeAppearanceModel.getTopLeftCorner().setCornerSize(boxCornerRadiusTopStart);
    }
    if (boxCornerRadiusTopEnd >= 0) {
      shapeAppearanceModel.getTopRightCorner().setCornerSize(boxCornerRadiusTopEnd);
    }
    if (boxCornerRadiusBottomEnd >= 0) {
      shapeAppearanceModel.getBottomRightCorner().setCornerSize(boxCornerRadiusBottomEnd);
    }
    if (boxCornerRadiusBottomStart >= 0) {
      shapeAppearanceModel.getBottomLeftCorner().setCornerSize(boxCornerRadiusBottomStart);
    }
    adjustCornerSizeForStrokeWidth();

    ColorStateList filledBackgroundColorStateList =
      MaterialResources.getColorStateList(
        context, a, R.styleable.MaterialSpinnerLayout_md_boxBackgroundColor);
    if (filledBackgroundColorStateList != null) {
      defaultFilledBackgroundColor = filledBackgroundColorStateList.getDefaultColor();
      boxBackgroundColor = defaultFilledBackgroundColor;
      if (filledBackgroundColorStateList.isStateful()) {
        disabledFilledBackgroundColor =
          filledBackgroundColorStateList.getColorForState(
            new int[]{-android.R.attr.state_enabled}, -1);
        hoveredFilledBackgroundColor =
          filledBackgroundColorStateList.getColorForState(
            new int[]{android.R.attr.state_hovered}, -1);
      } else {
        ColorStateList mtrlFilledBackgroundColorStateList =
          AppCompatResources.getColorStateList(context, R.color.md_mtrl_filled_background_color);
        disabledFilledBackgroundColor =
          mtrlFilledBackgroundColorStateList.getColorForState(
            new int[]{-android.R.attr.state_enabled}, -1);
        hoveredFilledBackgroundColor =
          mtrlFilledBackgroundColorStateList.getColorForState(
            new int[]{android.R.attr.state_hovered}, -1);
      }
    } else {
      boxBackgroundColor = Color.TRANSPARENT;
      defaultFilledBackgroundColor = Color.TRANSPARENT;
      disabledFilledBackgroundColor = Color.TRANSPARENT;
      hoveredFilledBackgroundColor = Color.TRANSPARENT;
    }

    if (a.hasValue(R.styleable.MaterialSpinnerLayout_android_textColorHint)) {
      defaultHintTextColor =
        focusedTextColor = a.getColorStateList(R.styleable.MaterialSpinnerLayout_android_textColorHint);
    }

    ColorStateList boxStrokeColorStateList =
      MaterialResources.getColorStateList(context, a, R.styleable.MaterialSpinnerLayout_md_boxStrokeColor);
    if (boxStrokeColorStateList != null && boxStrokeColorStateList.isStateful()) {
      defaultStrokeColor = boxStrokeColorStateList.getDefaultColor();
      disabledColor =
        boxStrokeColorStateList.getColorForState(new int[]{-android.R.attr.state_enabled}, -1);
      hoveredStrokeColor =
        boxStrokeColorStateList.getColorForState(new int[]{android.R.attr.state_hovered}, -1);
      focusedStrokeColor =
        boxStrokeColorStateList.getColorForState(new int[]{android.R.attr.state_focused}, -1);
    } else {
      // If attribute boxStrokeColor is not a color state list but only a single value, its value
      // will be applied to the box's focus state.
      focusedStrokeColor =
        a.getColor(R.styleable.MaterialSpinnerLayout_md_boxStrokeColor, Color.TRANSPARENT);
      defaultStrokeColor =
        ContextCompat.getColor(context, R.color.md_mtrl_spinner_default_box_stroke_color);
      disabledColor = ContextCompat.getColor(context, R.color.md_mtrl_spinner_disabled_color);
      hoveredStrokeColor =
        ContextCompat.getColor(context, R.color.md_mtrl_spinner_hovered_box_stroke_color);
    }

    final int hintAppearance = a.getResourceId(R.styleable.MaterialSpinnerLayout_md_hintTextAppearance, -1);
    if (hintAppearance != -1) {
      setHintTextAppearance(a.getResourceId(R.styleable.MaterialSpinnerLayout_md_hintTextAppearance, 0));
    }

    final int errorTextAppearance =
      a.getResourceId(R.styleable.MaterialSpinnerLayout_md_errorTextAppearance, 0);
    final boolean errorEnabled = a.getBoolean(R.styleable.MaterialSpinnerLayout_md_errorEnabled, false);

    final int helperTextTextAppearance =
      a.getResourceId(R.styleable.MaterialSpinnerLayout_md_helperTextTextAppearance, 0);
    final boolean helperTextEnabled =
      a.getBoolean(R.styleable.MaterialSpinnerLayout_md_helperTextEnabled, false);
    final CharSequence helperText = a.getText(R.styleable.MaterialSpinnerLayout_md_helperText);

    setHelperTextEnabled(helperTextEnabled);
    setHelperText(helperText);
    setHelperTextTextAppearance(helperTextTextAppearance);
    setErrorEnabled(errorEnabled);
    setErrorTextAppearance(errorTextAppearance);

    if (a.hasValue(R.styleable.MaterialSpinnerLayout_md_errorTextColor)) {
      setErrorTextColor(a.getColorStateList(R.styleable.MaterialSpinnerLayout_md_errorTextColor));
    }
    if (a.hasValue(R.styleable.MaterialSpinnerLayout_md_helperTextTextColor)) {
      setHelperTextColor(a.getColorStateList(R.styleable.MaterialSpinnerLayout_md_helperTextTextColor));
    }
    if (a.hasValue(R.styleable.MaterialSpinnerLayout_md_hintTextColor)) {
      setHintTextColor(a.getColorStateList(R.styleable.MaterialSpinnerLayout_md_hintTextColor));
    }

    setBoxBackgroundMode(
      a.getInt(R.styleable.MaterialSpinnerLayout_md_boxBackgroundMode, BOX_BACKGROUND_NONE));
    a.recycle();

    // For accessibility, consider MaterialSpinnerLayout itself to be a simple container for a
    // Spinner, and do not expose it to accessibility services.
    ViewCompat.setImportantForAccessibility(this, ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO);
  }

  private static void recursiveSetEnabled(final ViewGroup vg, final boolean enabled) {
    for (int i = 0, count = vg.getChildCount(); i < count; i++) {
      final View child = vg.getChildAt(i);
      child.setEnabled(enabled);
      if (child instanceof ViewGroup) {
        recursiveSetEnabled((ViewGroup) child, enabled);
      }
    }
  }

  @Override
  public void addView(View child, int index, final ViewGroup.LayoutParams params) {
    if (child instanceof Spinner) {
      // Make sure that the Spinner is vertically at the bottom, so that it sits on the
      // Spinner's underline
      FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(params);
      flp.gravity = Gravity.CENTER_VERTICAL | (flp.gravity & ~Gravity.VERTICAL_GRAVITY_MASK);
      inputFrame.addView(child, flp);

      // Now use the Spinner's LayoutParams as our own and update them to make enough space
      // for the label
      inputFrame.setLayoutParams(params);
      updateInputLayoutMargins();

      setSpinner((Spinner) child);
    } else {
      // Carry on adding the View...
      super.addView(child, index, params);
    }
  }

  @NonNull
  private Drawable getBoxBackground() {
    if (boxBackgroundMode == BOX_BACKGROUND_FILLED || boxBackgroundMode == BOX_BACKGROUND_OUTLINE) {
      return boxBackground;
    }
    throw new IllegalStateException();
  }

  /**
   * Get the box background mode (filled, outline, or none).
   *
   * <p>May be one of {@link #BOX_BACKGROUND_NONE}, {@link #BOX_BACKGROUND_FILLED}, or {@link
   * #BOX_BACKGROUND_OUTLINE}.
   */
  @BoxBackgroundMode
  public int getBoxBackgroundMode() {
    return boxBackgroundMode;
  }

  /**
   * Set the box background mode (filled, outline, or none).
   *
   * <p>May be one of {@link #BOX_BACKGROUND_NONE}, {@link #BOX_BACKGROUND_FILLED}, or {@link
   * #BOX_BACKGROUND_OUTLINE}.
   *
   * <p>Note: This method defines MaterialSpinnerLayout's internal behavior (for example, it allows the
   * hint to be displayed inline with the stroke in a cutout), but doesn't set all attributes that
   * are set in the styles provided for the box background modes. To achieve the look of an outlined
   * or filled text field, supplement this method with other methods that modify the box, such as
   * {@link #setBoxStrokeColor(int)} and {@link #setBoxBackgroundColor(int)}.
   *
   * @param boxBackgroundMode box's background mode
   * @throws IllegalArgumentException if boxBackgroundMode is not a @BoxBackgroundMode constant
   */
  public void setBoxBackgroundMode(@BoxBackgroundMode int boxBackgroundMode) {
    if (boxBackgroundMode == this.boxBackgroundMode) {
      return;
    }
    this.boxBackgroundMode = boxBackgroundMode;
    if (spinner != null) {
      onApplyBoxBackgroundMode();
    }
  }

  private void onApplyBoxBackgroundMode() {
    assignBoxBackgroundByMode();
    setSpinnerBoxBackground();
    updatespinnerBoxState();
    if (boxBackgroundMode != BOX_BACKGROUND_NONE) {
      updateInputLayoutMargins();
    }
  }

  private void assignBoxBackgroundByMode() {
    switch (boxBackgroundMode) {
      case BOX_BACKGROUND_FILLED:
        boxBackground = new MaterialShapeDrawable(shapeAppearanceModel);
        boxUnderline = new MaterialShapeDrawable();
        break;
      case BOX_BACKGROUND_OUTLINE:
        if (hintEnabled && !(boxBackground instanceof CutoutDrawable)) {
          boxBackground = new CutoutDrawable(shapeAppearanceModel);
        } else {
          boxBackground = new MaterialShapeDrawable(shapeAppearanceModel);
        }
        boxUnderline = null;
        break;
      case BOX_BACKGROUND_NONE:
        boxBackground = null;
        boxUnderline = null;
        break;
      default:
        throw new IllegalArgumentException(
          boxBackgroundMode + " is illegal; only @BoxBackgroundMode constants are supported.");
    }
  }

  private void setSpinnerBoxBackground() {
    // Set the Spinner background to boxBackground if we should use that as the box background.
    if (shouldUseSpinnerBackgroundForBoxBackground()) {
      Object tag = spinner.getTag(R.id.md_spinner_background);
      Drawable viewBackground;
      if (tag instanceof Drawable) {
        viewBackground = (Drawable) tag;
      } else {
        viewBackground = spinner.getBackground();
        spinner.setTag(R.id.md_spinner_background, viewBackground);
      }

      Drawable finalBackground;
      if (viewBackground != null) {
        finalBackground = new LayerDrawable(new Drawable[]{
          boxBackground,
          viewBackground
        });
      } else {
        finalBackground = boxBackground;
      }

      ViewCompat.setBackground(spinner, finalBackground);
    }
  }

  private boolean shouldUseSpinnerBackgroundForBoxBackground() {
    // When the text field's Spinner's background is null, use the Spinner's background for the
    // box background.
    return spinner != null
      && boxBackground != null
      && boxBackgroundMode != BOX_BACKGROUND_NONE;
  }

  /**
   * Returns the box's stroke color.
   *
   * @return the color used for the box's stroke
   * @see #setBoxStrokeColor(int)
   */
  public int getBoxStrokeColor() {
    return focusedStrokeColor;
  }

  /**
   * Set the outline box's stroke color.
   *
   * <p>Calling this method when not in outline box mode will do nothing.
   *
   * @param boxStrokeColor the color to use for the box's stroke
   * @see #getBoxStrokeColor()
   */
  public void setBoxStrokeColor(@ColorInt int boxStrokeColor) {
    if (focusedStrokeColor != boxStrokeColor) {
      focusedStrokeColor = boxStrokeColor;
      updatespinnerBoxState();
    }
  }

  /**
   * Set the resource used for the filled box's background color.
   *
   * @param boxBackgroundColorId the resource to use for the box's background color
   */
  public void setBoxBackgroundColorResource(@ColorRes int boxBackgroundColorId) {
    setBoxBackgroundColor(ContextCompat.getColor(getContext(), boxBackgroundColorId));
  }

  /**
   * Returns the box's background color.
   *
   * @return the color used for the box's background
   * @see #setBoxBackgroundColor(int)
   */
  public int getBoxBackgroundColor() {
    return boxBackgroundColor;
  }

  /**
   * Set the filled box's background color.
   *
   * @param boxBackgroundColor the color to use for the filled box's background
   * @see #getBoxBackgroundColor()
   */
  public void setBoxBackgroundColor(@ColorInt int boxBackgroundColor) {
    if (this.boxBackgroundColor != boxBackgroundColor) {
      this.boxBackgroundColor = boxBackgroundColor;
      defaultFilledBackgroundColor = boxBackgroundColor;
      applyBoxAttributes();
    }
  }

  /**
   * Set the resources used for the box's corner radii.
   *
   * @param boxCornerRadiusTopStartId    the resource to use for the box's top start corner radius
   * @param boxCornerRadiusTopEndId      the resource to use for the box's top end corner radius
   * @param boxCornerRadiusBottomEndId   the resource to use for the box's bottom end corner radius
   * @param boxCornerRadiusBottomStartId the resource to use for the box's bottom start corner
   *                                     radius
   */
  public void setBoxCornerRadiiResources(
    @DimenRes int boxCornerRadiusTopStartId,
    @DimenRes int boxCornerRadiusTopEndId,
    @DimenRes int boxCornerRadiusBottomEndId,
    @DimenRes int boxCornerRadiusBottomStartId) {
    setBoxCornerRadii(
      getContext().getResources().getDimension(boxCornerRadiusTopStartId),
      getContext().getResources().getDimension(boxCornerRadiusTopEndId),
      getContext().getResources().getDimension(boxCornerRadiusBottomStartId),
      getContext().getResources().getDimension(boxCornerRadiusBottomEndId));
  }

  /**
   * Set the box's corner radii.
   *
   * @param boxCornerRadiusTopStart    the value to use for the box's top start corner radius
   * @param boxCornerRadiusTopEnd      the value to use for the box's top end corner radius
   * @param boxCornerRadiusBottomEnd   the value to use for the box's bottom end corner radius
   * @param boxCornerRadiusBottomStart the value to use for the box's bottom start corner radius
   * @see #getBoxCornerRadiusTopStart()
   * @see #getBoxCornerRadiusTopEnd()
   * @see #getBoxCornerRadiusBottomEnd()
   * @see #getBoxCornerRadiusBottomStart()
   */
  public void setBoxCornerRadii(
    float boxCornerRadiusTopStart,
    float boxCornerRadiusTopEnd,
    float boxCornerRadiusBottomStart,
    float boxCornerRadiusBottomEnd) {
    if (shapeAppearanceModel.getTopLeftCorner().getCornerSize() != boxCornerRadiusTopStart
      || shapeAppearanceModel.getTopRightCorner().getCornerSize() != boxCornerRadiusTopEnd
      || shapeAppearanceModel.getBottomRightCorner().getCornerSize() != boxCornerRadiusBottomEnd
      || shapeAppearanceModel.getBottomLeftCorner().getCornerSize()
      != boxCornerRadiusBottomStart) {
      shapeAppearanceModel.getTopLeftCorner().setCornerSize(boxCornerRadiusTopStart);
      shapeAppearanceModel.getTopRightCorner().setCornerSize(boxCornerRadiusTopEnd);
      shapeAppearanceModel.getBottomRightCorner().setCornerSize(boxCornerRadiusBottomEnd);
      shapeAppearanceModel.getBottomLeftCorner().setCornerSize(boxCornerRadiusBottomStart);
      applyBoxAttributes();
    }
  }

  /**
   * Returns the box's top start corner radius.
   *
   * @return the value used for the box's top start corner radius
   * @see #setBoxCornerRadii(float, float, float, float)
   */
  public float getBoxCornerRadiusTopStart() {
    return shapeAppearanceModel.getTopLeftCorner().getCornerSize();
  }

  /**
   * Returns the box's top end corner radius.
   *
   * @return the value used for the box's top end corner radius
   * @see #setBoxCornerRadii(float, float, float, float)
   */
  public float getBoxCornerRadiusTopEnd() {
    return shapeAppearanceModel.getTopRightCorner().getCornerSize();
  }

  /**
   * Returns the box's bottom end corner radius.
   *
   * @return the value used for the box's bottom end corner radius
   * @see #setBoxCornerRadii(float, float, float, float)
   */
  public float getBoxCornerRadiusBottomEnd() {
    return shapeAppearanceModel.getBottomLeftCorner().getCornerSize();
  }

  /**
   * Returns the box's bottom start corner radius.
   *
   * @return the value used for the box's bottom start corner radius
   * @see #setBoxCornerRadii(float, float, float, float)
   */
  public float getBoxCornerRadiusBottomStart() {
    return shapeAppearanceModel.getBottomRightCorner().getCornerSize();
  }

  /**
   * Adjust the corner size based on the stroke width to maintain GradientDrawable's behavior.
   * MaterialShapeDrawable internally adjusts the corner size so that the corner size does not
   * depend on the stroke width. GradientDrawable does not account for stroke width, so this causes
   * a visual diff when migrating from GradientDrawable to MaterialShapeDrawable. This method
   * reverts the corner size adjustment in MaterialShapeDrawable to maintain the visual behavior
   * from GradientDrawable for now.
   */
  private void adjustCornerSizeForStrokeWidth() {
    float strokeInset = boxBackgroundMode == BOX_BACKGROUND_OUTLINE ? boxStrokeWidthPx / 2f : 0;
    if (strokeInset <= 0f) {
      return; // Only adjust the corner size if there's a stroke inset.
    }

    float cornerRadiusTopLeft = shapeAppearanceModel.getTopLeftCorner().getCornerSize();
    cornerAdjustedShapeAppearanceModel
      .getTopLeftCorner()
      .setCornerSize(cornerRadiusTopLeft + strokeInset);

    float cornerRadiusTopRight = shapeAppearanceModel.getTopRightCorner().getCornerSize();
    cornerAdjustedShapeAppearanceModel
      .getTopRightCorner()
      .setCornerSize(cornerRadiusTopRight + strokeInset);

    float cornerRadiusBottomRight = shapeAppearanceModel.getBottomRightCorner().getCornerSize();
    cornerAdjustedShapeAppearanceModel
      .getBottomRightCorner()
      .setCornerSize(cornerRadiusBottomRight + strokeInset);

    float cornerRadiusBottomLeft = shapeAppearanceModel.getBottomLeftCorner().getCornerSize();
    cornerAdjustedShapeAppearanceModel
      .getBottomLeftCorner()
      .setCornerSize(cornerRadiusBottomLeft + strokeInset);

    ensureCornerAdjustedShapeAppearanceModel();
  }

  private void ensureCornerAdjustedShapeAppearanceModel() {
    if (boxBackgroundMode != BOX_BACKGROUND_NONE
      && getBoxBackground() instanceof MaterialShapeDrawable) {
      ((MaterialShapeDrawable) getBoxBackground())
        .setShapeAppearanceModel(cornerAdjustedShapeAppearanceModel);
    }
  }

  /**
   * Returns the typeface used for the hint and any label views (such as counter and error views).
   */
  @Nullable
  public Typeface getTypeface() {
    return typeface;
  }

  /**
   * Set the typeface to use for the hint and any label views (such as counter and error views).
   *
   * @param typeface typeface to use, or {@code null} to use the default.
   */
  @SuppressWarnings("ReferenceEquality") // Matches the Typeface comparison in TextView
  public void setTypeface(@Nullable Typeface typeface) {
    if (typeface != this.typeface) {
      this.typeface = typeface;

      collapsingTextHelper.setTypefaces(typeface);
      indicatorViewController.setTypefaces(typeface);
    }
  }

  private void updateInputLayoutMargins() {
    // Create/update the LayoutParams so that we can add enough top margin
    // to the Spinner to make room for the label.
    if (boxBackgroundMode != BOX_BACKGROUND_FILLED) {
      final LayoutParams lp = (LayoutParams) inputFrame.getLayoutParams();
      final int newTopMargin = calculateLabelMarginTop();

      if (newTopMargin != lp.topMargin) {
        lp.topMargin = newTopMargin;
        inputFrame.requestLayout();
      }
    }
  }

  @Override
  public int getBaseline() {
    if (spinner != null) {
      return spinner.getBaseline() + getPaddingTop() + calculateLabelMarginTop();
    } else {
      return super.getBaseline();
    }
  }

  void updateLabelState(boolean animate) {
    updateLabelState(animate, false);
  }

  private void updateLabelState(boolean animate, boolean force) {
    final boolean isEnabled = isEnabled();
    final boolean hasFocus = spinner != null && spinner.hasFocus();
    final boolean errorShouldBeShown = indicatorViewController.errorShouldBeShown();

    // Set the expanded and collapsed labels to the default text color.
    if (defaultHintTextColor != null) {
      collapsingTextHelper.setCollapsedTextColor(defaultHintTextColor);
    }

    // Set the collapsed and expanded label text colors based on the current state.
    if (!isEnabled) {
      collapsingTextHelper.setCollapsedTextColor(ColorStateList.valueOf(disabledColor));
    } else if (errorShouldBeShown) {
      collapsingTextHelper.setCollapsedTextColor(indicatorViewController.getErrorViewTextColors());
    } else if (hasFocus && focusedTextColor != null) {
      collapsingTextHelper.setCollapsedTextColor(focusedTextColor);
    } // If none of these states apply, leave the expanded and collapsed colors as they are.

    // We should be showing the label so do so if it isn't already
    if (isEnabled() && (hasFocus || errorShouldBeShown) && force) {
      if (cutoutEnabled()) {
        openCutout();
      }
    }
  }

  /**
   * Returns the {@link Spinner} used for text input.
   */
  @Nullable
  public Spinner getSpinner() {
    return spinner;
  }

  private void setSpinner(Spinner spinner) {
    // If we already have an Spinner, throw an exception
    if (this.spinner != null) {
      throw new IllegalArgumentException("We already have an Spinner, can only have one");
    }

    this.spinner = spinner;
    onApplyBoxBackgroundMode();

    final int spinnerGravity = this.spinner.getGravity();
    collapsingTextHelper.setCollapsedTextGravity(
      Gravity.TOP | (spinnerGravity & ~Gravity.VERTICAL_GRAVITY_MASK));

    updateSpinnerBackground();

    indicatorViewController.adjustIndicatorPadding();
    dispatchOnSpinnerAttached();

    // Update the label visibility with no animation, but force a state change
    updateLabelState(false, true);
  }

  private void setHintInternal(CharSequence hint) {
    if (!TextUtils.equals(hint, this.hint)) {
      this.hint = hint;
      collapsingTextHelper.setText(hint);
      // Reset the cutout to make room for a larger hint.
      openCutout();
    }
  }

  /**
   * Returns the hint which is displayed in the floating label, if enabled.
   *
   * @return the hint, or null if there isn't one set, or the hint is not enabled.
   */
  @Nullable
  public CharSequence getHint() {
    return hintEnabled ? hint : null;
  }

  /**
   * Set the hint to be displayed in the floating label, if enabled.
   *
   * @see #setHintEnabled(boolean)
   */
  public void setHint(@Nullable CharSequence hint) {
    if (hintEnabled) {
      setHintInternal(hint);
      sendAccessibilityEvent(AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED);
    }
  }

  /**
   * Returns whether the floating label functionality is enabled or not in this layout.
   *
   * @see #setHintEnabled(boolean)
   */
  public boolean isHintEnabled() {
    return hintEnabled;
  }

  /**
   * Sets whether the floating label functionality is enabled or not in this layout.
   *
   * <p>If enabled, any non-empty hint in the child Spinner will be moved into the floating hint,
   * and its existing hint will be cleared. If disabled, then any non-empty floating hint in this
   * layout will be moved into the Spinner, and this layout's hint will be cleared.
   *
   * @see #setHint(CharSequence)
   * @see #isHintEnabled()
   */
  public void setHintEnabled(boolean enabled) {
    if (enabled != hintEnabled) {
      hintEnabled = enabled;
      if (!hintEnabled) {
        // Ensures a child Spinner provides its internal hint, not this MaterialSpinnerLayout's.
        isProvidingHint = false;
        // Now clear out any set hint
        setHintInternal(null);
      } else {
        isProvidingHint = true;
      }

      // Now update the Spinner top margin
      if (spinner != null) {
        updateInputLayoutMargins();
      }
    }
  }

  /**
   * Returns whether or not this layout is actively managing a child {@link Spinner}'s hint.
   */
  boolean isProvidingHint() {
    return isProvidingHint;
  }

  /**
   * Sets the collapsed hint text color, size, style from the specified TextAppearance resource.
   */
  public void setHintTextAppearance(@StyleRes int resId) {
    collapsingTextHelper.setCollapsedTextAppearance(resId);
    focusedTextColor = collapsingTextHelper.getCollapsedTextColor();

    if (spinner != null) {
      updateLabelState(false);
      // Text size might have changed so update the top margin
      updateInputLayoutMargins();
    }
  }

  /**
   * Gets the collapsed hint text color.
   */
  @Nullable
  public ColorStateList getHintTextColor() {
    return collapsingTextHelper.getCollapsedTextColor();
  }

  /**
   * Sets the collapsed hint text color from the specified ColorStateList resource.
   */
  public void setHintTextColor(@Nullable ColorStateList hintTextColor) {
    if (collapsingTextHelper.getCollapsedTextColor() != hintTextColor) {
      collapsingTextHelper.setCollapsedTextColor(hintTextColor);
      focusedTextColor = hintTextColor;

      if (spinner != null) {
        updateLabelState(false);
      }
    }
  }

  /**
   * Returns the text color used by the hint in both the collapsed and expanded states, or null if
   * no color has been set.
   */
  @Nullable
  public ColorStateList getDefaultHintTextColor() {
    return defaultHintTextColor;
  }

  /**
   * Sets the text color used by the hint in both the collapsed and expanded states.
   */
  public void setDefaultHintTextColor(@Nullable ColorStateList textColor) {
    defaultHintTextColor = textColor;
    focusedTextColor = textColor;

    if (spinner != null) {
      updateLabelState(false);
    }
  }

  /**
   * Sets the text color and size for the error message from the specified TextAppearance resource.
   */
  public void setErrorTextAppearance(@StyleRes int errorTextAppearance) {
    indicatorViewController.setErrorTextAppearance(errorTextAppearance);
  }

  /**
   * Sets the text color used by the error message in all states.
   */
  public void setErrorTextColor(@Nullable ColorStateList errorTextColor) {
    indicatorViewController.setErrorViewTextColor(errorTextColor);
  }

  /**
   * Returns the text color used by the error message in current state.
   */
  @ColorInt
  public int getErrorCurrentTextColors() {
    return indicatorViewController.getErrorViewCurrentTextColor();
  }

  /**
   * Sets the text color and size for the helper text from the specified TextAppearance resource.
   */
  public void setHelperTextTextAppearance(@StyleRes int helperTextTextAppearance) {
    indicatorViewController.setHelperTextAppearance(helperTextTextAppearance);
  }

  /**
   * Sets the text color used by the helper text in all states.
   */
  public void setHelperTextColor(@Nullable ColorStateList helperTextColor) {
    indicatorViewController.setHelperTextViewTextColor(helperTextColor);
  }

  /**
   * Returns whether the error functionality is enabled or not in this layout.
   *
   * @see #setErrorEnabled(boolean)
   */
  public boolean isErrorEnabled() {
    return indicatorViewController.isErrorEnabled();
  }

  /**
   * Whether the error functionality is enabled or not in this layout. Enabling this functionality
   * before setting an error message via {@link #setError(CharSequence)}, will mean that this layout
   * will not change size when an error is displayed.
   */
  public void setErrorEnabled(boolean enabled) {
    indicatorViewController.setErrorEnabled(enabled);
  }

  /**
   * Returns whether the helper text functionality is enabled or not in this layout.
   *
   * @see #setHelperTextEnabled(boolean)
   */
  public boolean isHelperTextEnabled() {
    return indicatorViewController.isHelperTextEnabled();
  }

  /**
   * Whether the helper text functionality is enabled or not in this layout. Enabling this
   * functionality before setting a helper message via {@link #setHelperText(CharSequence)} will
   * mean that this layout will not change size when a helper message is displayed.
   */
  public void setHelperTextEnabled(boolean enabled) {
    indicatorViewController.setHelperTextEnabled(enabled);
  }

  /**
   * Returns the text color used by the helper text in the current states.
   */
  @ColorInt
  public int getHelperTextCurrentTextColor() {
    return indicatorViewController.getHelperTextViewCurrentTextColor();
  }

  @Override
  public void setEnabled(boolean enabled) {
    // Since we're set to addStatesFromChildren, we need to make sure that we set all
    // children to enabled/disabled otherwise any enabled children will wipe out our disabled
    // drawable state
    recursiveSetEnabled(this, enabled);
    super.setEnabled(enabled);
  }

  void setTextAppearanceCompatWithErrorFallback(TextView textView, @StyleRes int textAppearance) {
    boolean useDefaultColor = false;
    try {
      TextViewCompat.setTextAppearance(textView, textAppearance);

      if (VERSION.SDK_INT >= VERSION_CODES.M
        && textView.getTextColors().getDefaultColor() == Color.MAGENTA) {
        // Caused by our theme not extending from Theme.Design*. On API 23 and
        // above, unresolved theme attrs result in MAGENTA rather than an exception.
        // Flag so that we use a decent default
        useDefaultColor = true;
      }
    } catch (Exception e) {
      // Caused by our theme not extending from Theme.Design*. Flag so that we use
      // a decent default
      useDefaultColor = true;
    }
    if (useDefaultColor) {
      // Probably caused by our theme not extending from Theme.Design*. Instead
      // we manually set something appropriate
      TextViewCompat.setTextAppearance(textView, R.style.TextAppearance_AppCompat_Caption);
      textView.setTextColor(ContextCompat.getColor(getContext(), R.color.md_design_error));
    }
  }

  private int calculateLabelMarginTop() {
    if (!hintEnabled) {
      return 0;
    }

    switch (boxBackgroundMode) {
      case BOX_BACKGROUND_OUTLINE:
        return (int) (collapsingTextHelper.getCollapsedTextHeight() / 2);
      case BOX_BACKGROUND_FILLED:
      case BOX_BACKGROUND_NONE:
        return (int) collapsingTextHelper.getCollapsedTextHeight();
      default:
        return 0;
    }
  }

  private Rect calculateCollapsedTextBounds(Rect rect) {
    if (spinner == null) {
      throw new IllegalStateException();
    }
    Rect bounds = tmpBoundsRect;

    bounds.bottom = rect.bottom;
    switch (boxBackgroundMode) {
      case BOX_BACKGROUND_OUTLINE:
        bounds.left = rect.left + spinner.getPaddingLeft();
        bounds.top = rect.top - calculateLabelMarginTop();
        bounds.right = rect.right - spinner.getPaddingRight();
        return bounds;
      case BOX_BACKGROUND_FILLED:
        bounds.left = rect.left + spinner.getPaddingLeft();
        bounds.top = rect.top + boxCollapsedPaddingTopPx;
        bounds.right = rect.right - spinner.getPaddingRight();
        return bounds;
      case BOX_BACKGROUND_NONE:
      default:
        bounds.left = rect.left + spinner.getPaddingLeft();
        bounds.top = getPaddingTop();
        bounds.right = rect.right - spinner.getPaddingRight();
        return bounds;
    }
  }

  /*
   * Calculates the box background color that should be set.
   *
   * The filled text field has a surface layer with value {@code ?attr/colorSurface} underneath its
   * background that is taken into account when calculating the background color.
   */
  private int calculateBoxBackgroundColor() {
    int backgroundColor = boxBackgroundColor;
    if (boxBackgroundMode == BOX_BACKGROUND_FILLED) {
      int surfaceLayerColor = MaterialColors.INSTANCE.getColor(this, R.attr.colorSurface, Color.TRANSPARENT);
      backgroundColor = MaterialColors.INSTANCE.layer(surfaceLayerColor, boxBackgroundColor);
    }
    return backgroundColor;
  }

  private void applyBoxAttributes() {
    if (boxBackground == null) {
      return;
    }

    if (canDrawOutlineStroke()) {
      boxBackground.setStroke(boxStrokeWidthPx, boxStrokeColor);
    }

    boxBackground.setFillColor(ColorStateList.valueOf(calculateBoxBackgroundColor()));
    applyBoxUnderlineAttributes();
    invalidate();
  }

  private void applyBoxUnderlineAttributes() {
    // Exit if the underline is not being drawn by MaterialSpinnerLayout.
    if (boxUnderline == null) {
      return;
    }

    if (canDrawStroke()) {
      boxUnderline.setFillColor(ColorStateList.valueOf(boxStrokeColor));
    }
    invalidate();
  }

  private boolean canDrawOutlineStroke() {
    return boxBackgroundMode == BOX_BACKGROUND_OUTLINE && canDrawStroke();
  }

  private boolean canDrawStroke() {
    return boxStrokeWidthPx > -1 && boxStrokeColor != Color.TRANSPARENT;
  }

  void updateSpinnerBackground() {
    // Only update the color filter for the legacy text field, since we can directly change the
    // Paint colors of the MaterialShapeDrawable box background without having to use color filters.
    if (spinner == null || boxBackgroundMode != BOX_BACKGROUND_NONE) {
      return;
    }

    Drawable spinnerBackground = spinner.getBackground();
    if (spinnerBackground == null) {
      return;
    }

    spinnerBackground = spinnerBackground.mutate();

    if (indicatorViewController.errorShouldBeShown()) {
      // Set a color filter for the error color
      spinnerBackground.setColorFilter(
        new PorterDuffColorFilter(indicatorViewController.getErrorViewCurrentTextColor(), PorterDuff.Mode.SRC_IN)
      );
    } else {
      // Else reset the color filter and refresh the drawable state so that the
      // normal tint is used
      DrawableCompat.clearColorFilter(spinnerBackground);
      spinner.refreshDrawableState();
    }
  }

  @Override
  public Parcelable onSaveInstanceState() {
    Parcelable superState = super.onSaveInstanceState();
    SavedState ss = new SavedState(superState);
    if (indicatorViewController.errorShouldBeShown()) {
      ss.error = getError();
    }
    return ss;
  }

  @Override
  protected void onRestoreInstanceState(Parcelable state) {
    if (!(state instanceof SavedState)) {
      super.onRestoreInstanceState(state);
      return;
    }
    SavedState ss = (SavedState) state;
    super.onRestoreInstanceState(ss.getSuperState());
    setError(ss.error);
    requestLayout();
  }

  /**
   * Returns the error message that was set to be displayed with {@link #setError(CharSequence)}, or
   * <code>null</code> if no error was set or if error displaying is not enabled.
   *
   * @see #setError(CharSequence)
   */
  @Nullable
  public CharSequence getError() {
    return indicatorViewController.isErrorEnabled() ? indicatorViewController.getErrorText() : null;
  }

  /**
   * Sets an error message that will be displayed below our {@link Spinner}. If the {@code error} is
   * {@code null}, the error message will be cleared.
   *
   * <p>If the error functionality has not been enabled via {@link #setErrorEnabled(boolean)}, then
   * it will be automatically enabled if {@code error} is not empty.
   *
   * @param errorText Error message to display, or null to clear
   * @see #getError()
   */
  public void setError(@Nullable final CharSequence errorText) {
    if (!indicatorViewController.isErrorEnabled()) {
      if (TextUtils.isEmpty(errorText)) {
        // If error isn't enabled, and the error is empty, just return
        return;
      }
      // Else, we'll assume that they want to enable the error functionality
      setErrorEnabled(true);
    }

    if (!TextUtils.isEmpty(errorText)) {
      indicatorViewController.showError(errorText);
    } else {
      indicatorViewController.hideError();
    }
  }

  /**
   * Returns the helper message that was set to be displayed with {@link
   * #setHelperText(CharSequence)}, or <code>null</code> if no helper text was set or if helper text
   * functionality is not enabled.
   *
   * @see #setHelperText(CharSequence)
   */
  @Nullable
  public CharSequence getHelperText() {
    return indicatorViewController.isHelperTextEnabled()
      ? indicatorViewController.getHelperText()
      : null;
  }

  /**
   * Sets a helper message that will be displayed below the {@link Spinner}. If the {@code helper}
   * is {@code null}, the helper text functionality will be disabled and the helper message will be
   * hidden.
   *
   * <p>If the helper text functionality has not been enabled via {@link
   * #setHelperTextEnabled(boolean)}, then it will be automatically enabled if {@code helper} is not
   * empty.
   *
   * @param helperText Helper text to display
   * @see #getHelperText()
   */
  public void setHelperText(@Nullable final CharSequence helperText) {
    // If helper text is null, disable helper if it's enabled.
    if (TextUtils.isEmpty(helperText)) {
      if (isHelperTextEnabled()) {
        setHelperTextEnabled(false);
      }
    } else {
      if (!isHelperTextEnabled()) {
        setHelperTextEnabled(true);
      }
      indicatorViewController.showHelper(helperText);
    }
  }

  /**
   * Returns whether any hint state changes, due to being focused or non-empty text, are animated.
   *
   * @see #setHintAnimationEnabled(boolean)
   */
  public boolean isHintAnimationEnabled() {
    return hintAnimationEnabled;
  }

  /**
   * Set whether any hint state changes, due to being focused or non-empty text, are animated.
   *
   * @see #isHintAnimationEnabled()
   */
  public void setHintAnimationEnabled(boolean enabled) {
    hintAnimationEnabled = enabled;
  }

  /**
   * Add a {@link OnSpinnerAttachedListener} that will be invoked when the edit text is attached,
   * or from this method if the Spinner is already present.
   *
   * <p>Components that add a listener should take care to remove it when finished via {@link
   * #removeOnSpinnerAttachedListener(OnSpinnerAttachedListener)}.
   *
   * @param listener listener to add
   */
  public void addOnSpinnerAttachedListener(OnSpinnerAttachedListener listener) {
    spinnerAttachedListeners.add(listener);
    if (spinner != null) {
      listener.onSpinnerAttached();
    }
  }

  /**
   * Remove the given {@link OnSpinnerAttachedListener} that was previously added via {@link
   * #addOnSpinnerAttachedListener(OnSpinnerAttachedListener)}.
   *
   * @param listener listener to remove
   */
  public void removeOnSpinnerAttachedListener(OnSpinnerAttachedListener listener) {
    spinnerAttachedListeners.remove(listener);
  }

  /**
   * Remove all previously added {@link OnSpinnerAttachedListener}s.
   */
  public void clearOnSpinnerAttachedListeners() {
    spinnerAttachedListeners.clear();
  }

  private void dispatchOnSpinnerAttached() {
    for (OnSpinnerAttachedListener listener : spinnerAttachedListeners) {
      listener.onSpinnerAttached();
    }
  }

  @Override
  protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    super.onLayout(changed, left, top, right, bottom);

    if (spinner != null) {
      Rect rect = tmpRect;
      DescendantOffsetUtils.getDescendantRect(this, spinner, rect);
      updateBoxUnderlineBounds(rect);

      if (hintEnabled) {
        collapsingTextHelper.setCollapsedBounds(calculateCollapsedTextBounds(rect));
        collapsingTextHelper.recalculate();

        // If the label should be collapsed, set the cutout bounds on the CutoutDrawable to make
        // sure it draws with a cutout in draw().
        if (cutoutEnabled()) {
          openCutout();
        }
      }
    }
  }

  private void updateBoxUnderlineBounds(Rect bounds) {
    if (boxUnderline != null) {
      int top = bounds.bottom - boxStrokeWidthFocusedPx;
      boxUnderline.setBounds(bounds.left, top, bounds.right, bounds.bottom);
    }
  }

  @Override
  public void draw(Canvas canvas) {
    super.draw(canvas);
    drawHint(canvas);
    drawBoxUnderline(canvas);
  }

  private void drawHint(Canvas canvas) {
    if (hintEnabled) {
      collapsingTextHelper.draw(canvas);
    }
  }

  private void drawBoxUnderline(Canvas canvas) {
    if (boxUnderline != null) {
      // Draw using the current boxStrokeWidth.
      Rect underlineBounds = boxUnderline.getBounds();
      underlineBounds.top = underlineBounds.bottom - boxStrokeWidthPx;
      boxUnderline.draw(canvas);
    }
  }

  private boolean cutoutEnabled() {
    return hintEnabled && !TextUtils.isEmpty(hint) && boxBackground instanceof CutoutDrawable;
  }

  private void openCutout() {
    if (!cutoutEnabled()) {
      return;
    }
    final RectF cutoutBounds = tmpRectF;
    collapsingTextHelper.getCollapsedTextActualBounds(cutoutBounds);
    applyCutoutPadding(cutoutBounds);
    // Offset the cutout bounds by the MaterialSpinnerLayout's left padding to ensure that the cutout is
    // inset relative to the MaterialSpinnerLayout's bounds.
    cutoutBounds.offset(-getPaddingLeft(), 0);
    ((CutoutDrawable) boxBackground).setCutout(cutoutBounds);
  }

  private void closeCutout() {
    if (cutoutEnabled()) {
      ((CutoutDrawable) boxBackground).removeCutout();
    }
  }

  private void applyCutoutPadding(RectF cutoutBounds) {
    cutoutBounds.left -= boxLabelCutoutPaddingPx;
    cutoutBounds.top -= boxLabelCutoutPaddingPx;
    cutoutBounds.right += boxLabelCutoutPaddingPx;
    cutoutBounds.bottom += boxLabelCutoutPaddingPx;
  }

  @VisibleForTesting
  boolean cutoutIsOpen() {
    return cutoutEnabled() && ((CutoutDrawable) boxBackground).hasCutout();
  }

  @Override
  protected void drawableStateChanged() {
    if (inDrawableStateChanged) {
      // Some of the calls below will update the drawable state of child views. Since we're
      // using addStatesFromChildren we can get into infinite recursion, hence we'll just
      // exit in this instance
      return;
    }

    inDrawableStateChanged = true;

    super.drawableStateChanged();

    final int[] state = getDrawableState();
    boolean changed = collapsingTextHelper.setState(state);

    // Drawable state has changed so see if we need to update the label
    updateLabelState(ViewCompat.isLaidOut(this) && isEnabled());
    updateSpinnerBackground();
    updatespinnerBoxState();

    if (changed) {
      invalidate();
    }

    inDrawableStateChanged = false;
  }

  void updatespinnerBoxState() {
    if (boxBackground == null || boxBackgroundMode == BOX_BACKGROUND_NONE) {
      return;
    }

    final boolean hasFocus = isFocused() || (spinner != null && spinner.hasFocus());
    final boolean isHovered = isHovered() || (spinner != null && spinner.isHovered());

    // Update the text box's stroke color based on the current state.
    if (!isEnabled()) {
      boxStrokeColor = disabledColor;
    } else if (indicatorViewController.errorShouldBeShown()) {
      boxStrokeColor = indicatorViewController.getErrorViewCurrentTextColor();
    } else if (hasFocus) {
      boxStrokeColor = focusedStrokeColor;
    } else if (isHovered) {
      boxStrokeColor = hoveredStrokeColor;
    } else {
      boxStrokeColor = defaultStrokeColor;
    }

    // Update the text box's stroke width based on the current state.
    if ((isHovered || hasFocus) && isEnabled()) {
      boxStrokeWidthPx = boxStrokeWidthFocusedPx;
      adjustCornerSizeForStrokeWidth();
    } else {
      boxStrokeWidthPx = boxStrokeWidthDefaultPx;
      adjustCornerSizeForStrokeWidth();
    }

    // Update the text box's background color based on the current state.
    if (boxBackgroundMode == BOX_BACKGROUND_FILLED) {
      if (!isEnabled()) {
        boxBackgroundColor = disabledFilledBackgroundColor;
      } else if (isHovered) {
        boxBackgroundColor = hoveredFilledBackgroundColor;
      } else {
        boxBackgroundColor = defaultFilledBackgroundColor;
      }
    }

    applyBoxAttributes();
  }

  @VisibleForTesting
  final boolean isHelperTextDisplayed() {
    return indicatorViewController.helperTextIsDisplayed();
  }

  @VisibleForTesting
  final int getHintCurrentCollapsedTextColor() {
    return collapsingTextHelper.getCurrentCollapsedTextColor();
  }

  @VisibleForTesting
  final float getHintCollapsedTextHeight() {
    return collapsingTextHelper.getCollapsedTextHeight();
  }

  @VisibleForTesting
  final int getErrorTextCurrentColor() {
    return indicatorViewController.getErrorViewCurrentTextColor();
  }

  /**
   * Values for box background mode. There is either a filled background, an outline background, or
   * no background.
   */
  @IntDef({BOX_BACKGROUND_NONE, BOX_BACKGROUND_FILLED, BOX_BACKGROUND_OUTLINE})
  @Retention(RetentionPolicy.SOURCE)
  public @interface BoxBackgroundMode {
  }

  /**
   * Callback interface invoked when the view's {@link Spinner} is attached, or from {@link
   * #addOnSpinnerAttachedListener(OnSpinnerAttachedListener)} if the edit text is already present.
   *
   * @see #addOnSpinnerAttachedListener(OnSpinnerAttachedListener)
   */
  public interface OnSpinnerAttachedListener {

    /**
     * Called when the {@link Spinner} is attached, or from {@link
     * #addOnSpinnerAttachedListener(OnSpinnerAttachedListener)} if the edit text is already
     * present.
     */
    void onSpinnerAttached();
  }

  static class SavedState extends AbsSavedState {
    public static final Creator<SavedState> CREATOR =
      new ClassLoaderCreator<SavedState>() {
        @Override
        public SavedState createFromParcel(Parcel in, ClassLoader loader) {
          return new SavedState(in, loader);
        }

        @Override
        public SavedState createFromParcel(Parcel in) {
          return new SavedState(in, null);
        }

        @Override
        public SavedState[] newArray(int size) {
          return new SavedState[size];
        }
      };
    CharSequence error;

    SavedState(Parcelable superState) {
      super(superState);
    }

    SavedState(Parcel source, ClassLoader loader) {
      super(source, loader);
      error = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(source);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
      super.writeToParcel(dest, flags);
      TextUtils.writeToParcel(error, dest, flags);
    }

    @NotNull
    @Override
    public String toString() {
      return "MaterialSpinnerLayout.SavedState{"
        + Integer.toHexString(System.identityHashCode(this))
        + " error="
        + error
        + "}";
    }
  }
}
