/*
 * Copyright (C) 2018 The Android Open Source Project
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
package de.kuschku.ui.color

import android.content.Context
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils
import de.kuschku.ui.resources.MaterialAttributes

/**
 * A utility class for common color variants used in Material themes.
 */
object MaterialColors {
  /**
   * Returns the color int for the provided theme color attribute, or the default value if the
   * attribute is not set in the current theme, using the `view`'s [Context].
   */
  @ColorInt
  fun getColor(
    view: View, @AttrRes colorAttributeResId: Int, @ColorInt defaultValue: Int): Int {
    return getColor(view.context, colorAttributeResId, defaultValue)
  }

  /**
   * Returns the color int for the provided theme color attribute, or the default value if the
   * attribute is not set in the current theme.
   */
  @ColorInt
  private fun getColor(
    context: Context, @AttrRes colorAttributeResId: Int, @ColorInt defaultValue: Int): Int {
    val typedValue = MaterialAttributes.resolveAttribute(context, colorAttributeResId)
    return typedValue?.data ?: defaultValue
  }

  /**
   * Calculates a color that represents the layering of the `overlayColor` on top of the
   * `backgroundColor`.
   */
  @ColorInt
  fun layer(@ColorInt backgroundColor: Int, @ColorInt overlayColor: Int): Int {
    return ColorUtils.compositeColors(overlayColor, backgroundColor)
  }
}
